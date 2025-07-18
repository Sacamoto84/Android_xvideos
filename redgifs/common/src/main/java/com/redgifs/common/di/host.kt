package com.redgifs.common.di

import com.client.common.connectivityObserver.ConnectivityObserver
import com.redgifs.common.block.BlockRed
import com.redgifs.common.downloader.DownloadRed
import com.redgifs.common.saved.SavedRed
import com.redgifs.common.search.SearchNichesRed
import com.redgifs.common.search.SearchRed
import com.redgifs.common.snackBar.SnackBarEvent
import com.redgifs.network.api.RedApi
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
    val snackBarEvent: SnackBarEvent
)
