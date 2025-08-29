package com.client.xvideos.l.featured.downloader

import com.client.common.AppPath
import com.client.common.util.getFolderSize
import com.client.xvideos.feature.kdownloader.KDownloader
import com.ketch.Ketch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DownloaderL @Inject constructor() {

    private val downloadDispatcher = Executors.newFixedThreadPool(8).asCoroutineDispatcher()
    val scope = CoroutineScope(SupervisorJob() + downloadDispatcher)

    val listDownloaderAlbum = mutableSetOf<DownloaderAlbum>()

    fun clear() {
        downloadDispatcher.close()
    }
}


@OptIn(FlowPreview::class)
class DownloaderAlbum(
    val albumName: String,
    private val kDownloader: KDownloader,
    private val scope: CoroutineScope,
    val ketch: Ketch
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

    private val triggerAlbumSize = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    init {
        Timber.i("!!!! DownloaderAlbum init albumName: $albumName")
        scope.launch {
            triggerAlbumSize
                .debounce(1000) // не чаще раза в 1000 мс
                .collectLatest {
                    val size = getFolderSize(File(AppPath.downloaded_albums_l, albumName))
                    folderSize.value = size
                }
        }

        val size = getFolderSize(File(AppPath.downloaded_albums_l, albumName))
        folderSize.value = size

        val dir = File(AppPath.downloaded_albums_l + "/" + albumName)
        dir.mkdirs()
        // Список имён файлов, которые уже есть
        val existingFiles = dir.listFiles()?.map { it.name }?.toSet() ?: emptySet()
        fileCountDownloaded.value = existingFiles.size

    }

    fun requestAlbumSizeUpdate() {
        triggerAlbumSize.tryEmit(Unit)
    }

    fun saveAlbums(listUrl: List<String>, albumName: String) {
        scope.launch {

            fileCountError.value = 0
            fileCountDownloaded.value = 0
            fileCountRaw.value = 0

            requestAlbumSizeUpdate()

            val dir = File(AppPath.downloaded_albums_l + "/" + albumName)
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



            urlsToDownload.distinct().forEach { item ->

                val fileName = item.substringAfterLast('/').substringBefore('?')

                val id = ketch.download(
                    url = item,
                    fileName = fileName,
                    path = dir.absolutePath,
                    tag = fileName,
                    metaData = fileName,
                    supportPauseResume = false
                )


                ketch.observeDownloadById(id)
                    .flowOn(Dispatchers.IO)
                    .collect { downloadModel ->
                        // use downloadModel
                        val status = downloadModel?.status
                        val id = downloadModel?.id
                        Timber.i("!!!! DownloaderAlbum id:$id status: $status")
                    }


//                val request = kDownloader
//                    .newRequestBuilder(item, dir.absolutePath, fileName)
//                    .tag(fileName)
//                    .build()
//
//                // Using all of these lambdas is not mandatory. for example - you can only use onStart or onProgress also
//                kDownloader.enqueue(
//                    request,
//                    onStart = {
//                        Timber.d(">>> Download Started $fileName")
//                        //requestAlbumSizeUpdate()
//                    },
//                    onProgress = {
//                    },
//                    onCompleted = {
//                        Timber.d(">>> Download onCompleted $fileName")
//                        fileCountDownloaded.update { it + 1 }
//                        //requestAlbumSizeUpdate()
//                    },
//                    onError = {
//                        Timber.e(">>> Download onError $fileName")
//                        fileCountError.update { it + 1 }
//                        //requestAlbumSizeUpdate()
//                    },
//                    onPause = {
//                    }
//                )


            }
        }

    }

}