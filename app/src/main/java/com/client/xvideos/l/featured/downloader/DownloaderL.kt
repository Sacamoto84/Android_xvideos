package com.client.xvideos.l.featured.downloader

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.client.common.AppPath
import com.kdownloader.KDownloader
import timber.log.Timber
import java.io.File


class DownloaderL (
    private val kDownloader: KDownloader,
) {

    var fileCountRaw by mutableIntStateOf(0)

    fun saveAlbums(listUrl: List<String>, albumName: String){

        val dir = File(AppPath.downloaded_albums_l, albumName)
        dir.mkdirs()

        fileCountRaw = listUrl.size

        // Список имён файлов, которые уже есть
        val existingFiles = dir.listFiles()?.map { it.name }?.toSet() ?: emptySet()

        // Фильтруем список URL — оставляем только те, которых нет на диске
        val urlsToDownload = listUrl.filter { url ->
            val fileName = url.substringAfterLast('/').substringBefore('?')
            fileName !in existingFiles
        }

        urlsToDownload.forEach {

            val fileName = it.substringAfterLast('/').substringBefore('?')
            val request = kDownloader
                .newRequestBuilder(it, dir.absolutePath, fileName)
                .tag(fileName)
                .build()

            // Using all of these lambdas is not mandatory. for example - you can only use onStart or onProgress also
            val downloadId = kDownloader.enqueue(request,
                onStart = {
                    Timber.d(">>> Download Started $fileName")
                },
                onProgress = {
                },
                onCompleted = {
                    Timber.d(">>> Download onCompleted $fileName")
                },
                onError = {
                    Timber.e(">>> Download onError $fileName")
                },
                onPause = {
                }
            )
        }

    }




}