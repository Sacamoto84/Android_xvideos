package com.client.xvideos.screens_red.use_case.download

import com.client.xvideos.feature.Downloader
import com.client.xvideos.feature.redgifs.types.GifsInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(DelicateCoroutinesApi::class)
fun downloadItem(item : GifsInfo) {
    GlobalScope.launch {
        Timber.i("!!! downloadItem() id:${item.id} userName:${item.userName} url:${item.urls.hd}")
        Downloader.downloadRedName(item.id, item.userName, item.urls.hd.toString())
        Timber.i("!!! downloadItem() ... завершено")
    }
}

