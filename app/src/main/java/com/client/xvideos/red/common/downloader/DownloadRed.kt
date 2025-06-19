package com.client.xvideos.red.common.downloader

import androidx.compose.runtime.mutableStateSetOf
import com.client.xvideos.AppPath
import com.client.xvideos.feature.Downloader
import com.client.xvideos.feature.redgifs.types.GifsInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

object DownloadRed {

    var downloadList = mutableStateSetOf<String>()

    init {
        refreshDownloadList()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun downloadItem(item : GifsInfo) {
        GlobalScope.launch {
            Timber.i("!!! downloadItem() id:${item.id} userName:${item.userName} url:${item.urls.hd}")
            Downloader.downloadRedName(item.id, item.userName, item.urls.hd.toString())
            Timber.i("!!! downloadItem() ... завершено")
        }
    }

    fun findVideoInDownload(id: String, name: String): Boolean {
        //val mainPath = AppPath.cache_download_red + "/" + name + "/" + id + ".mp4"
        val mainPath = "${AppPath.cache_download_red}/$name/$id.mp4"
        val file = File(mainPath)
        return file.exists()
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