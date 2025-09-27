package com.client.xvideos.redgifs.common.di

import com.client.xvideos.common.connectivityObserver.ConnectivityObserver
import com.client.xvideos.redgifs.common.block.BlockRed
import com.redgifs.common.downloader.DownloadRed
import com.client.xvideos.redgifs.common.saved.SavedRed
import com.client.xvideos.redgifs.common.search.SearchNichesRed
import com.redgifs.common.search.SearchRed
import com.client.xvideos.redgifs.network.api.RedApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HostDI @Inject constructor (
    val connectivityObserver: ConnectivityObserver,
    val block: BlockRed,
    val redApi : RedApi,
    val savedRed: SavedRed,
    val downloadRed: DownloadRed,
    val search : SearchRed,
    val searchNiches : SearchNichesRed,
)
