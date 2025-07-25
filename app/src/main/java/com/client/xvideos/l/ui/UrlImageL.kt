package com.client.xvideos.l.ui

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.composeunstyled.Text
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import timber.log.Timber


@Composable
fun UrlImageLusciousZoomedGifs(url: String, modifier: Modifier = Modifier,  contentScale : ContentScale = ContentScale.FillWidth, loadIndicator : Boolean = true, isGrayscale: Boolean = false) {

    val context = LocalContext.current
    val colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.9f) })
    val imageRequest = ImageRequest.Builder(context)
        .data(url)
        .crossfade(true)
        .memoryCacheKey(url)
        .diskCacheKey(url)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                add(ImageDecoderDecoder.Factory())
            } // Использует modern API
            add(GifDecoder.Factory()) // Fallback для старых API
        }
        .build()

        CoilImage(
            imageLoader = { imageLoader },
            imageRequest = { imageRequest },
            imageOptions = ImageOptions(
                contentScale = contentScale,
                alignment = Alignment.Center,
                colorFilter = if (isGrayscale) colorFilter else null
            ),
            modifier = Modifier.then(modifier),
            loading = {
                //Box(modifier = Modifier.fillMaxSize().background(Color.Magenta))
                if (loadIndicator) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp)//.align(Alignment.Center)
                            , color = Color.Gray
                        )
                    }
                }
            },
            failure = {
                Timber.e(">>>>>>>>>>>>" + it.reason)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ошибка загрузки", color = Color.Gray)
                }
            }
        )




}