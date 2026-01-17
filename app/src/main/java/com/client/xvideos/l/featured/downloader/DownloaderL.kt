package com.client.xvideos.l.featured.downloader

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloaderL @Inject constructor() {

    private val downloadDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()
    val scope = CoroutineScope(SupervisorJob() + downloadDispatcher)

    val listDownloaderAlbum = mutableSetOf<DownloaderAlbum>()

    fun clear() {
        downloadDispatcher.close()
    }

}