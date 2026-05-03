package com.client.xvideos.l.featured.downloader

import com.client.xvideos.common.AppPath
import com.client.xvideos.common.kdownloader.KDownloader
import com.client.xvideos.common.kdownloader.Status
import com.client.xvideos.common.util.getFolderSize
import com.client.xvideos.common.util.toPrettyCount3
import com.client.xvideos.l.model.lMediaDownloadHeaders
import com.client.xvideos.l.model.lMediaUserAgent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import kotlin.random.Random

@OptIn(FlowPreview::class)
class DownloaderAlbum(
    val albumName: String,
    private val kDownloader: KDownloader,
    private val scope: CoroutineScope,
    ) {

    /**
     * Общее количество файлов в альбоме
     */
    var fileCountRaw = MutableStateFlow(0)

    /**
     * Количество скачанных файлов
     */
    var fileCountDownloaded = MutableStateFlow(0)

    /**
     * Общее количество ошибок загрузки
     */
    var fileCountError = MutableStateFlow(0)

    var folderSize = MutableStateFlow(0L)

    val isDownloading = MutableStateFlow(false)

    var stop = MutableStateFlow(false)


    fun stop() {
        //Отстановить закачку
        //stop.value = true
        kDownloader.cancel(albumName)
    }


    private val updatingGetSizeChannel =
        Channel<Long>(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)


    init {
        Timber.i("!!!! DownloaderAlbum init albumName: $albumName")

        scope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    updatingGetSizeChannel.receive()
                    val size = getFolderSize(File(AppPath.l_downloaded_albums, albumName))
                    folderSize.value = size
                    Timber.i("---- DownloaderAlbum updating size albumName: $albumName size :${folderSize.value.toPrettyCount3()}")
                    delay(1000)
                } catch (e: Exception) {
                    Timber.e(e, "Error updating folder size for $albumName")
                }
            }
        }

        requestAlbumSizeUpdate()

        //val size = getFolderSize(File(AppPath.downloaded_albums_l, albumName))
        //folderSize.value = size

        val dir = File(AppPath.l_downloaded_albums + "/" + albumName)
        dir.mkdirs()
        // Список имён файлов, которые уже есть
        val existingFiles = dir.listFiles()?.map { it.name }?.toSet() ?: emptySet()
        fileCountDownloaded.value = existingFiles.size



    }

    fun requestAlbumSizeUpdate() {
        updatingGetSizeChannel.trySend(Random.nextLong())
    }

    // Добавить в класс DownloaderAlbum
    data class DeletionProgress(
        val isDeleting: Boolean = false,
        val current: Int = 0,
        val total: Int = 0,
        val currentFileName: String = ""
    )

    val deletionProgress = MutableStateFlow(DeletionProgress())

    fun deleteAlbum(onStart: () -> Unit = {}, onComplete: () -> Unit = {}) {

        scope.launch(Dispatchers.IO.limitedParallelism(1)) {
            withContext(Dispatchers.Main) { onStart() }

            val dir = File(AppPath.l_downloaded_albums + "/" + albumName)

            if (dir.exists()) {
                val files = dir.listFiles() ?: emptyArray()
                val totalFiles = files.size

                deletionProgress.value = DeletionProgress(
                    isDeleting = true,
                    total = totalFiles
                )

                files.forEachIndexed { index, file ->
                    try {
                        deletionProgress.value = deletionProgress.value.copy(
                            current = index + 1,
                            currentFileName = file.name
                        )

                        file.delete()
                        delay(10)
                    } catch (e: Exception) {
                        Timber.e(e, "Error deleting file: ${file.name}")
                    }
                }

                dir.delete()
            }

            deletionProgress.value = DeletionProgress(isDeleting = false)
            requestAlbumSizeUpdate()
            fileCountDownloaded.value = 0

            withContext(Dispatchers.Main) { onComplete() }
        }
    }

    private val downloadSemaphore = Semaphore(1)

    private val singleThreadDispatcher = Dispatchers.IO.limitedParallelism(1)

    suspend fun saveAlbums(listUrl: List<String>, albumName: String) {

        downloadSemaphore.withPermit {

            withContext(singleThreadDispatcher) {

                stop.value = false

                fileCountError.value = 0
                fileCountDownloaded.value = 0
                fileCountRaw.value = 0

                requestAlbumSizeUpdate()

                val dir = File(AppPath.l_downloaded_albums + "/" + albumName)
                dir.mkdirs()

                fileCountRaw.value = listUrl.size

                // Список имён файлов, которые уже есть
                val existingFiles = dir.listFiles()?.map { it.name }?.toSet() ?: emptySet()

                fileCountDownloaded.value = existingFiles.size

                // Фильтруем список URL — оставляем только те, которых нет на диске
                val urlsToDownload = listUrl.filter { url ->
                    val fileName = url.substringAfterLast('/').substringBefore('?')
                    fileName !in existingFiles
                }

                //isDownloading.value = fileCountRaw.value >= fileCountDownloaded.value + fileCountError.value
                val s = kDownloader.getStatusesByTag(albumName)
                isDownloading.value = s.any { it.second == Status.RUNNING }

                urlsToDownload.distinct().forEach { item ->

                    if (stop.value) {
                        isDownloading.value = false
                        return@forEach
                    }

                    delay(50)

                    val fileName = item.substringAfterLast('/').substringBefore('?')

                    val request = kDownloader
                        .newRequestBuilder(item, dir.absolutePath, fileName)
                        .headers(lMediaDownloadHeaders())
                        .userAgent(lMediaUserAgent())
                        .tag(albumName)
                        .build()

                    // Using all of these lambdas is not mandatory. for example - you can only use onStart or onProgress also
                    kDownloader.enqueue(
                        request,

                        onCompleted = {
                            Timber.d(">>> Download onCompleted $fileName")
                            fileCountDownloaded.update { it + 1 }
                            requestAlbumSizeUpdate()

                            //isDownloading.value = fileCountRaw.value >= fileCountDownloaded.value + fileCountError.value
                            val s = kDownloader.getStatusesByTag(albumName)
                            isDownloading.value = s.any { it.second == Status.RUNNING }

                        },
                        onError = {
                            Timber.e(">>> Download onError $fileName")
                            fileCountError.update { it + 1 }
                            requestAlbumSizeUpdate()
                            val s = kDownloader.getStatusesByTag(albumName)
                            isDownloading.value = s.any { it.second == Status.RUNNING }

                            //isDownloading.value = fileCountRaw.value >= fileCountDownloaded.value + fileCountError.value
                        }

                    )


                }
            }
        }
    }

}
