package com.redgifs.common.downloader

import androidx.compose.runtime.mutableStateSetOf
import com.client.common.AppPath
import com.redgifs.model.GifsInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRed @Inject constructor(
    val downloader: Downloader
) {

    var downloadList = mutableStateSetOf<String>()


    init {
        refreshDownloadList()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun downloadItem(item : GifsInfo) {
        GlobalScope.launch {
            Timber.i("!!! downloadItem() id:${item.id} userName:${item.userName} url:${item.urls.hd}")
            downloader.downloadRedName(item.id, item.userName, item.urls.hd.toString())
            Timber.i("!!! downloadItem() ... завершено")
        }
    }



    @OptIn(DelicateCoroutinesApi::class)
    fun refreshDownloadList() {
        GlobalScope.launch(Dispatchers.IO) {
            val rootDir = File(AppPath.cache_download_red)
            if (!rootDir.exists() || !rootDir.isDirectory) {
                downloadList.clear()
                return@launch
            }
            val files = rootDir.walkTopDown().filter { it.isFile && it.name.endsWith(".mp4") }.map { it.name.dropLast(4) }.toSet()
            withContext(Dispatchers.Main) {
                downloadList.clear()
                downloadList.addAll(files)
            }
        }
    }


}