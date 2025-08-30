package com.client.xvideos.l.featured.downloader

import com.client.common.AppPath
import com.client.common.kdownloader.KDownloader
import timber.log.Timber
import java.io.File

fun downloadLikes(url : String, kDownloader : KDownloader, onComplete: () -> Unit, onError: () -> Unit){

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