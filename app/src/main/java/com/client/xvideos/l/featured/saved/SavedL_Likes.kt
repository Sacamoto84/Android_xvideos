package com.client.xvideos.l.featured.saved

import androidx.compose.runtime.mutableStateListOf
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.kdownloader.KDownloader
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.model.lDownloadUrl
import com.client.xvideos.l.model.lSavedFileName
import timber.log.Timber
import java.io.File

class SavedL_Likes(val kDownloader: KDownloader) {

    val listUrl = mutableStateListOf<PicsDetails>()

    init {
        refresh()
    }

    fun add(item: PicsDetails) {
        println("!!! SavedL_Likes addLikes() item:${item.url_to_original}")

        downloadLikes(item, kDownloader, onComplete = {
            SnackBar.success("Like")
            refresh()
        }, onError = {
            SnackBar.error("Ошибка добавления лайка")
        })
    }

    fun remove(url: String) {
        println("!!! SavedL_Likes removeLikes() url:${url}")
        val fileName = url.substringAfterLast('/').substringBefore('?')
        val file = File(AppPath.l_likes, fileName)
        if (file.exists()) {
            if (!file.delete()) {
                SnackBar.error("Не удалось удалить файл: ${file.absolutePath}")
            }else
                SnackBar.info("Unlike")
        }else{
            SnackBar.error("Файл не найден: ${file.absolutePath}")
        }
        refresh()
    }

    fun refresh() {
        try {
            println("!!! SavedL_Likes refresh()")
            val files = File(AppPath.l_likes).list()?.mapNotNull{ fileNameToPicsDetails(File(it), AppPath.l_likes) }
           if (files != null) {
               listUrl.clear()
               listUrl.addAll(files)
               println("!!! SavedL_Likes refresh() files:${listUrl.size}")
           }
        } catch (e: Exception) {
            Timber.e("!!! eee SavedL_Likes refresh() Ошибка получения списка likes ${e.localizedMessage}")
            SnackBar.error("Ошибка получения списка likes")
        }

    }

}




private fun downloadLikes(
    item: PicsDetails,
    kDownloader: KDownloader,
    onComplete: () -> Unit,
    onError: () -> Unit
) {

    val downloadUrl = item.lDownloadUrl()
    val fileName = item.lSavedFileName()
    if (downloadUrl == null || fileName == null) {
        Timber.e(">>> Download Likes missing media url")
        onError()
        return
    }

    val dir = File(AppPath.l_likes)
    dir.mkdirs()

    val request = kDownloader
        .newRequestBuilder(downloadUrl, dir.absolutePath, fileName)
        .tag("likes")
        .build()

    // Using all of these lambdas is not mandatory. for example - you can only use onStart or onProgress also
    kDownloader.enqueue(
        request,
        onCompleted = {
            Timber.d(">>> Download Likes onCompleted $fileName")
            onComplete()
        },
        onError = {
            Timber.e(">>> Download Likes onError $fileName")
            onError()
        }

    )

}
