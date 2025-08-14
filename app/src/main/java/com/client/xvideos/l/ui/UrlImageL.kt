package com.client.xvideos.l.ui

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.common.RotationOptions
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.fresco.websupport.FrescoWebImage
import timber.log.Timber

@Composable
fun UrlImageLusciousGifs(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillWidth,
    loadIndicator: Boolean = true,
    isGrayscale: Boolean = false,
    onLoading: (Boolean) -> Unit = {},
    onSuccess: (Boolean) -> Unit = {},
) {

    val context = LocalContext.current

    val colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.9f) })

    val imageRequest = remember {
        ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .apply {
                memoryCacheKey("${url}_preview")
                diskCacheKey("${url}_preview")
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()
    }

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } // Использует modern API
                add(GifDecoder.Factory()) // Fallback для старых API
            }
            .build()
    }

    CoilImage(
        imageLoader = { imageLoader },
        imageRequest = { imageRequest },
        imageOptions = ImageOptions(
            contentScale = contentScale,
            alignment = Alignment.Center,
            colorFilter = if (isGrayscale) colorFilter else null
        ),
        modifier = Modifier.then(modifier),

        success = { state, painter ->
            onSuccess(true)
            Image(
                painter = painter,
                modifier = Modifier.fillMaxSize(),//.size(128.dp), // draw a resized image.
                contentDescription = "Image"
            )
        },

        loading = {
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

@Composable
fun UrlImageLusciousGifsFull(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillWidth,
    onSuccess: (Boolean) -> Unit = {},
) {

    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    val controllerBuilder = {
        Fresco.newDraweeControllerBuilder()
            .setUri(url)
            .setControllerListener(object : BaseControllerListener<ImageInfo>() {
                override fun onSubmit(id: String?, callerContext: Any?) {
                    isLoading = true
                    hasError = false
                }

                override fun onFinalImageSet(
                    id: String?,
                    imageInfo: ImageInfo?,
                    animatable: android.graphics.drawable.Animatable?
                ) {
                    isLoading = false
                    hasError = false
                    onSuccess(true)
                }

                override fun onFailure(id: String?, throwable: Throwable?) {
                    isLoading = false
                    hasError = true
                }
            })
            .setAutoPlayAnimations(true)
    }

    Box {
        FrescoWebImage(
            controllerBuilder = controllerBuilder,
            modifier = Modifier.then(modifier)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp)//.align(Alignment.Center)
                    , color = Color.Gray
                )
            }
        }

        if (hasError) {
            Text("Ошибка загрузки", Modifier.align(Alignment.Center))
        }
    }

//    GlideImage(
//        imageModel = { url },
//        requestOptions = {
//            RequestOptions()
//                .override(256, 256)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .centerCrop()
//        },
//        imageOptions = ImageOptions(
//            contentScale = ContentScale.Crop,
//            alignment = Alignment.Center
//        )
//    )

}


//@Composable
//fun UrlImageLusciousGifsFull(
//    url: String,
//    modifier: Modifier = Modifier,
//    contentScale: ContentScale = ContentScale.FillWidth,
//    loadIndicator: Boolean = true,
//    onSuccess: (Boolean) -> Unit = {},
//) {
//
//    val context = LocalContext.current
//
//    val imageRequest = remember {
//        ImageRequest.Builder(context)
//            .data(url)
//            .crossfade(true)
//            .size(Size.ORIGINAL)
//            .apply {
//                memoryCacheKey("${url}_full")
//                diskCacheKey("${url}_full")
//            }
//            .diskCachePolicy(CachePolicy.ENABLED)
//            .memoryCachePolicy(CachePolicy.ENABLED)
//            .build()
//    }
//
//    val imageLoader = remember {
//        ImageLoader.Builder(context)
//            .components {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { add(ImageDecoderDecoder.Factory()) }
//                add(GifDecoder.Factory()) // Fallback для старых API
//            }
//            .build()
//    }
//
//    CoilImage(
//        imageLoader = { imageLoader },
//        imageRequest = { imageRequest },
//        imageOptions = ImageOptions(
//            contentScale = contentScale,
//            alignment = Alignment.Center,
//        ),
//        modifier = Modifier.then(modifier),
//
//        success = { state, painter ->
//            onSuccess(true)
//            Image(
//                painter = painter,
//                modifier = Modifier, // draw a resized image.
//                contentDescription = "Image"
//            )
//        },
//
//        loading = {
//            //Box(modifier = Modifier.fillMaxSize().background(Color.Magenta))
//            if (loadIndicator) {
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(32.dp)//.align(Alignment.Center)
//                        , color = Color.Gray
//                    )
//                }
//            }
//        },
//
//        failure = {
//            Timber.e(">>>>>>>>>>>>" + it.reason)
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                Text("Ошибка загрузки", color = Color.Gray)
//            }
//        }
//
//
//    )
//
//}