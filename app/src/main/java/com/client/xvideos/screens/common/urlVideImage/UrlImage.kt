package com.client.xvideos.screens.common.urlVideImage

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage

/**
 * url: строка с адресом изображения
 */
@Composable
fun UrlImage(url : String, modifier : Modifier = Modifier) {

    val context = LocalContext.current

    val imageRequest = ImageRequest.Builder(context)
        .data(url)
        .memoryCacheKey(url)
        .diskCacheKey(url)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    CoilImage(
        imageRequest = { imageRequest },
        imageOptions = ImageOptions(
            contentScale = ContentScale.FillWidth,
            alignment = Alignment.Center
        ),
        modifier = Modifier.then(modifier)
    )
}