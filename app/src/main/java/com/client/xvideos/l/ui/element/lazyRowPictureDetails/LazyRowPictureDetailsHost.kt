package com.client.xvideos.l.ui.element.lazyRowPictureDetails

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import com.client.xvideos.l.model.PicsDetails

class LazyRowPictureDetailsHost(
    val albumName: String,
) {

    val state = LazyStaggeredGridState()

    var columns by mutableIntStateOf(2)

    var selectedImage by  mutableStateOf<PicsDetails?>(null)
    var selectedBounds by  mutableStateOf<Rect?>(null)

    val filteredPic =  mutableStateListOf<PicsDetails>()



}

