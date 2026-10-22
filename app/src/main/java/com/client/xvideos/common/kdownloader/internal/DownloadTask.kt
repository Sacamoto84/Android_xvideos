package com.client.xvideos.common.kdownloader.internal

import com.client.xvideos.common.kdownloader.database.AppDbHelper
import com.client.xvideos.common.kdownloader.database.DbHelper
import com.client.xvideos.common.kdownloader.database.DownloadModel
import com.client.xvideos.common.kdownloader.httpclient.DefaultHttpClient
import com.client.xvideos.common.kdownloader.httpclient.HttpClient
import com.client.xvideos.common.kdownloader.internal.stream.FileDownloadOutputStream
import com.client.xvideos.common.kdownloader.internal.stream.FileDownloadRandomAccessFile
import com.client.xvideos.common.kdownloader.Constants
import com.client.xvideos.common.kdownloader.Status
import com.client.xvideos.common.kdownloader.utils.getPath
import com.client.xvideos.common.kdownloader.utils.getRedirectedConnectionIfAny
import com.client.xvideos.common.kdownloader.utils.getTempPath
import com.client.xvideos.common.kdownloader.utils.renameFileName
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection

class DownloadTask(
    private val req: DownloadRequest,
    private val dbHelper: DbHelper
) {

    private var responseCode = 0
    private var totalBytes: Long = 0
    private var inputStream: InputStream? = null
    // Nullable, а не lateinit: при ошибке до открытия файла/соединения finally не должен падать.
    private var outputStream: FileDownloadOutputStream? = null

    private var tempPath: String = ""
    private var httpClient: HttpClient? = null
    private var isResumeSupported = true

    private var lastSyncTime: Long = 0
    private var lastSyncBytes: Long = 0

    private var eTag: String = ""

    private val dbScope = CoroutineScope(SupervisorJob() + Dispatchers.IO.limitedParallelism(1) +
            CoroutineExceptionHandler { _, e -> Timber.e(e, "KDownloader: ошибка записи в БД загрузок") })

    companion object {
        private const val TIME_GAP_FOR_SYNC: Long = 2000
        private const val MIN_BYTES_FOR_SYNC: Long = 65536
        private const val BUFFER_SIZE = 1024 * 4
        private const val MAX_PARALLEL_DOWNLOADS = 4

        // Общий на все задачи: DownloadTask создаётся на каждый запрос, поэтому
        // семафор-поле экземпляра ничего не ограничивал.
        private val downloadSemaphore = Semaphore(MAX_PARALLEL_DOWNLOADS)
    }

    suspend inline fun run(
        crossinline onStart: () -> Unit = {},
        crossinline onProgress: (value: Int) -> Unit = { _ -> },
        crossinline onError: (error: String) -> Unit = { _ -> },
        crossinline onCompleted: () -> Unit = {},
        crossinline onPause: () -> Unit = {}
    ) = run(object : DownloadRequest.Listener {
        override fun onStart() = onStart()

        override fun onProgress(value: Int) = onProgress(value)

        override fun onError(error: String) = onError(error)

        override fun onCompleted() = onCompleted()

        override fun onPause() = onPause()
    })

    private suspend fun createAndInsertNewModel() {

        dbScope.launch {
            dbHelper.insert(
                DownloadModel(
                    id = req.downloadId,
                    url = req.url,
                    totalBytes = req.totalBytes,
                    eTag = eTag
                )
            )
        }
    }

    private suspend fun removeNoMoreNeededModelFromDatabase() {
        dbScope.launch {
            dbHelper.remove(req.downloadId)
        }
    }


    suspend fun run(listener: DownloadRequest.Listener) {
        downloadSemaphore.withPermit {
            withContext(Dispatchers.IO.limitedParallelism(1)) {
                try {
                    tempPath = getTempPath(req.dirPath, req.fileName)
                    var file = File(tempPath)

                    var model = getDownloadModelIfAlreadyPresentInDatabase()

                    if (model == null && file.exists() && dbHelper is AppDbHelper) {
                        if (!deleteTempFile()) {
                            // Старый temp не удалился — пишем рядом: "id.mp4.temp" -> "id2.mp4.temp".
                            // Делим только имя файла: в пути каталога тоже бывают точки.
                            val name = file.name
                            val dot = name.indexOf('.')
                            val altName = if (dot > 0) name.substring(0, dot) + "2" + name.substring(dot) else name + "2"
                            file = File(file.parentFile, altName)
                            tempPath = file.path
                        }
                    }

                    if (model != null) {
                        if (file.exists()) {
                            req.totalBytes = (model.totalBytes)
                            req.downloadedBytes = (model.downloadedBytes)
                        } else {
                            removeNoMoreNeededModelFromDatabase()
                            req.downloadedBytes = 0
                            req.totalBytes = 0
                            model = null
                        }
                    }

                    // use the url to download the file with HTTP Client
                    var client = DefaultHttpClient().clone()
                    httpClient = client

                    req.status = Status.RUNNING

                    listener.onStart()

                    client.connect(req)

                    eTag = client.getResponseHeader(Constants.ETAG)

                    if (checkIfFreshStartRequiredAndStart(model)) {
                        model = null
                    }

                    client = getRedirectedConnectionIfAny(httpClient ?: client, req)
                    httpClient = client
                    responseCode = client.getResponseCode()

                    if (!isSuccessful()) {
                        req.status = Status.FAILED
                        listener.onError("Wrong link (HTTP $responseCode)")
                        return@withContext
                    }

                    setResumeSupportedOrNot()

                    totalBytes = req.totalBytes

                    if (!isResumeSupported) {
                        deleteTempFile()
                        req.downloadedBytes = 0
                    }

                    if (totalBytes == 0L) {
                        totalBytes = client.getContentLength()
                        req.totalBytes = (totalBytes)
                    }

                    if (isResumeSupported && model == null) {
                        createAndInsertNewModel()
                    }

                    val input = client.getInputStream()
                    inputStream = input
                    if (input == null) {
                        req.status = Status.FAILED
                        listener.onError("Empty response")
                        return@withContext
                    }

                    val buff = ByteArray(BUFFER_SIZE)

                    if (!file.exists()) {
                        val parentFile = file.parentFile
                        if (parentFile != null && !parentFile.exists()) {
                            if (parentFile.mkdirs()) {
                                file.createNewFile()
                            }
                        } else {
                            file.createNewFile()
                        }
                    }

                    val output = FileDownloadRandomAccessFile.Companion.create(file)
                    this@DownloadTask.outputStream = output

                    if (req.status === Status.CANCELLED) {
                        deleteTempFile()
                        req.reset()
                        listener.onError("Cancelled")
                        return@withContext
                    } else if (req.status === Status.PAUSED) {
                        sync(output)
                        listener.onPause()
                        return@withContext
                    }

                    if (isResumeSupported && req.downloadedBytes != 0L) {
                        output.seek(req.downloadedBytes)
                    }

                    do {
                        val byteCount = input.read(buff, 0, BUFFER_SIZE)
                        if (byteCount == -1) {
                            break
                        }

                        if (req.status === Status.CANCELLED) {
                            deleteTempFile()
                            req.reset()
                            listener.onError("Cancelled")
                            return@withContext
                        } else if (req.status === Status.PAUSED) {
                            sync(output)
                            listener.onPause()
                            return@withContext
                        }

                        if (!isActive) {
                            deleteTempFile()
                            req.reset()
                            break
                        }
                        if (!req.job.isActive) {
                            deleteTempFile()
                            req.reset()
                            break
                        }
                        output.write(buff, 0, byteCount)
                        req.downloadedBytes = req.downloadedBytes + byteCount
                        syncIfRequired(output)

                        var progress = 0
                        if (totalBytes > 0) {
                            progress = ((req.downloadedBytes * 100) / totalBytes).toInt()
                        }
                        listener.onProgress(progress)
                    } while (true)

                    if (req.status === Status.CANCELLED) {
                        deleteTempFile()
                        req.reset()
                        listener.onError("Cancelled")
                        return@withContext
                    } else if (req.status === Status.PAUSED) {
                        sync(output)
                        listener.onPause()
                        return@withContext
                    }

                    val path = getPath(req.dirPath, req.fileName)
                    renameFileName(tempPath, path)
                    listener.onCompleted()
                    req.status = Status.COMPLETED
                    return@withContext
                } catch (e: CancellationException) {
                    deleteTempFile()
                    req.reset()
                    // При отмене через cancel() слушатель уже получил "Cancelled" — не дублируем.
                    if (req.status != Status.CANCELLED) {
                        req.status = Status.FAILED
                        listener.onError("Cancelled")
                    }
                    throw e
                } catch (e: Exception) {
                    if (!isResumeSupported) {
                        deleteTempFile()
                        req.reset()
                    }
                    req.status = Status.FAILED
                    listener.onError(e.toString())
                    return@withContext
                } finally {
                    closeAllSafely()
                }
            }
        }
    }

    private fun setResumeSupportedOrNot() {
        isResumeSupported = (responseCode == HttpURLConnection.HTTP_PARTIAL)
    }

    private fun deleteTempFile(): Boolean {
        val file = File(tempPath)
        if (file.exists()) {
            return file.delete()
        }
        return false
    }

    @Throws(IOException::class, IllegalAccessException::class)
    private suspend fun checkIfFreshStartRequiredAndStart(model: DownloadModel?): Boolean {
        if (responseCode == Constants.HTTP_RANGE_NOT_SATISFIABLE || isETagChanged(model)) {
            if (model != null) {
                removeNoMoreNeededModelFromDatabase()
            }
            deleteTempFile()
            req.downloadedBytes = 0
            req.totalBytes = 0
            httpClient?.close()
            val client = DefaultHttpClient().clone()
            httpClient = client
            client.connect(req)
            val redirected = getRedirectedConnectionIfAny(client, req)
            httpClient = redirected
            responseCode = redirected.getResponseCode()
            return true
        }
        return false
    }

    private fun isETagChanged(model: DownloadModel?): Boolean {
        return (!(eTag.isEmpty() || model == null || model.eTag.isEmpty())
                && model.eTag != eTag)
    }

    private suspend fun getDownloadModelIfAlreadyPresentInDatabase(): DownloadModel? {
        return withContext(Dispatchers.IO) {
            dbHelper.find(req.downloadId)
        }
    }

    private suspend fun closeAllSafely() {

        try {
            httpClient?.close()
        } catch (e: Exception) {
            Timber.w(e, "KDownloader: close http")
        }

        try {
            inputStream?.close()
        } catch (e: IOException) {
            Timber.w(e, "KDownloader: close input")
        }

        val output = outputStream ?: return
        try {
            sync(output)
        } catch (e: Exception) {
            Timber.w(e, "KDownloader: sync")
        } finally {
            try {
                output.close()
            } catch (e: IOException) {
                Timber.w(e, "KDownloader: close output")
            }
        }
    }

    private suspend fun syncIfRequired(outputStream: FileDownloadOutputStream) {
        val currentBytes: Long = req.downloadedBytes
        val currentTime = System.currentTimeMillis()
        val bytesDelta: Long = currentBytes - lastSyncBytes
        val timeDelta: Long = currentTime - lastSyncTime
        if (bytesDelta > MIN_BYTES_FOR_SYNC && timeDelta > TIME_GAP_FOR_SYNC) {
            sync(outputStream)
            lastSyncBytes = currentBytes
            lastSyncTime = currentTime
        }
    }

    private suspend fun sync(outputStream: FileDownloadOutputStream) {
        var success: Boolean
        try {
            outputStream.flushAndSync()
            success = true
        } catch (e: IOException) {
            success = false
            Timber.w(e, "KDownloader: flush")
        }
        if (success && isResumeSupported) {
            dbHelper
                .updateProgress(
                    req.downloadId,
                    req.downloadedBytes,
                    System.currentTimeMillis()
                )
        }
    }

    private fun isSuccessful(): Boolean {
        return (responseCode >= HttpURLConnection.HTTP_OK
                && responseCode < HttpURLConnection.HTTP_MULT_CHOICE)
    }
}