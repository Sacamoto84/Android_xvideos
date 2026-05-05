package com.client.xvideos.l.ui.element.lazyRowPictureDetails

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.client.xvideos.l.model.PicsDetails

@OptIn(ExperimentalFoundationApi::class)
class LazyRowPictureDetailsHost(
    val albumName: String,
    val idAlbum: String = ""
) {

    val dpCacheWindow = LazyLayoutCacheWindow(ahead = 150.dp, behind = 100.dp)

    @OptIn(ExperimentalFoundationApi::class)
    val state = LazyStaggeredGridState()

    val state1 = LazyListState(cacheWindow = dpCacheWindow)

    /**
     * Количество отображаемых столбцов
     */
    var columns by mutableIntStateOf(3)

    var selectedImage by mutableStateOf<PicsDetails?>(null)

    var filteredPic =  mutableStateListOf<PicsDetails>()

}

