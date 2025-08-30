package com.client.xvideos.l.featured.saved

import androidx.compose.runtime.mutableStateListOf
import com.client.common.AppPath
import com.client.common.kdownloader.KDownloader
import com.redgifs.common.snackBar.SnackBarEvent
import timber.log.Timber
import java.io.File

class SavedL_Likes( val snackBarEvent: SnackBarEvent, val kDownloader: KDownloader) {

    val listUrl = mutableStateListOf<String>()

    init {
        refresh()
    }

    fun add(url: String) {
        println("!!! SavedL_Likes addLikes() url:${url}")

        downloadLikes(url, kDownloader, onComplete = {
            snackBarEvent.success("Like")
            refresh()
        }, onError = {
            snackBarEvent.error("Ошибка добавления лайка")
        })
    }

    fun remove(url: String) {
        println("!!! removeLikes() url:${url}")
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
            val files = File(AppPath.likes_l).list()
            if (files != null) {
                listUrl.clear()
                listUrl.addAll(files)
            }
        } catch (e: Exception) {
            snackBarEvent.error("Ошибка получения списка likes")
        }

    }

}

private fun downloadLikes(
    url: String,
    kDownloader: KDownloader,
    onComplete: () -> Unit,
    onError: () -> Unit
) {

    val fileName = url.substringAfterLast('/').substringBefore('?')

    val dir = File(AppPath.likes_l)
    dir.mkdirs()

    val request = kDownloader
        .newRequestBuilder(url, dir.absolutePath, fileName)
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