package com.client.xvideos.l.featured.saved

import androidx.compose.runtime.mutableStateListOf
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.kdownloader.KDownloader
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.redgifs.common.snackBar.SnackBarEvent
import timber.log.Timber
import java.io.File

class SavedL_Likes( val snackBarEvent: SnackBarEvent, val kDownloader: KDownloader) {

    val listUrl = mutableStateListOf<PicsDetails>()

    init {
        refresh()
    }

    fun add(item: PicsDetails) {
        println("!!! SavedL_Likes addLikes() item:${item.url_to_original}")

        downloadLikes(item, kDownloader, onComplete = {
            snackBarEvent.success("Like")
            refresh()
        }, onError = {
            snackBarEvent.error("Ошибка добавления лайка")
        })
    }

    fun remove(url: String) {
        println("!!! SavedL_Likes removeLikes() url:${url}")
        val fileName = url.substringAfterLast('/').substringBefore('?')
        val file = File(AppPath.likes_l, fileName)
        if (file.exists()) {
            if (!file.delete()) {
                snackBarEvent.error("Не удалось удалить файл: ${file.absolutePath}")
            }else
                snackBarEvent.info("Unlike")
        }else{
            snackBarEvent.error("Файл не найден: ${file.absolutePath}")
        }
        refresh()
    }

    fun refresh() {
        try {
            println("!!! SavedL_Likes refresh()")
            val files = File(AppPath.likes_l).list()?.mapNotNull{ fileNameToPicsDetails(File(it), AppPath.likes_l) }
           if (files != null) {
               listUrl.clear()
               listUrl.addAll(files)
               println("!!! SavedL_Likes refresh() files:${listUrl.size}")
           }
        } catch (e: Exception) {
            Timber.e("!!! eee SavedL_Likes refresh() Ошибка получения списка likes ${e.localizedMessage}")
            snackBarEvent.error("Ошибка получения списка likes")
        }

    }

}




private fun downloadLikes(
    item: PicsDetails,
    kDownloader: KDownloader,
    onComplete: () -> Unit,
    onError: () -> Unit
) {

    val fileName = item.width.toString()+"_"+item.height+"_"+item.is_animated+"_"+item.album+"_"+item.url_to_original?.substringAfterLast('/')?.substringBefore('?')

    val dir = File(AppPath.likes_l)
    dir.mkdirs()

    val request = kDownloader
        .newRequestBuilder(item.url_to_original!!, dir.absolutePath, fileName)
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