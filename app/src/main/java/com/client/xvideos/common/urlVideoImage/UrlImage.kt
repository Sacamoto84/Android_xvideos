package com.client.xvideos.common.urlVideoImage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.client.xvideos.common.coil.UrlImageGifsCoil

/**
 * url: строка с адресом изображения
 */
@Composable
fun UrlImage(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillWidth,
    loadIndicator: Boolean = true
) {
    //Timber.d("!!! UrlImage: $url")
    Box(modifier = modifier) {
        UrlImageGifsCoil(
            url = url,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            albumName = "net"
        )
    }
}
