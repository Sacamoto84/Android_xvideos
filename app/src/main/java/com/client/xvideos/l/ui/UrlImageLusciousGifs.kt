package com.client.xvideos.l.ui

import android.net.Uri
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.client.common.AppPath
import com.composeunstyled.Text
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.imagepipeline.image.ImageInfo
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.fresco.websupport.FrescoWebImage
import com.skydoves.landscapist.glide.GlideImage
import io.ktor.client.plugins.cache.storage.FileStorage
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import java.io.File


@Composable
fun UrlImageLusciousGifsGlide(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillWidth,
    loadIndicator: Boolean = true,
    isGrayscale: Boolean = false,
    onLoading: (Boolean) -> Unit = {},
    onSuccess: (Boolean) -> Unit = {},
    albumName: String
) {
    val context = LocalContext.current

    val colorFilter = ColorFilter.colorMatrix(
        ColorMatrix().apply { setToSaturation(0.9f) }
    )

    // Определяем имя файла и проверяем наличие
    val fileName = url.substringAfterLast('/').substringBefore('?')

    val file  = if (albumName != "likes") {
        File(AppPath.downloaded_albums_l, "$albumName/$fileName")
    }
    else{
        File(url)
    }

    val dataSource: Any = if (file.exists()) file else url

    GlideImage(
        imageModel = { dataSource }, // локальный файл или URL
        modifier = modifier,
        imageOptions = ImageOptions(
            contentScale = contentScale,
            alignment = Alignment.Center,
            colorFilter = if (isGrayscale) colorFilter else null
        ),
        loading = {
            onLoading(true)
            if (loadIndicator) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color.Gray
                    )
                }
            }
        },
        failure = {
            onLoading(false)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Ошибка загрузки", color = Color.Gray)
            }
        },
//        success = {
//            onLoading(false)
//            onSuccess(true)
//        }
    )
}

@Composable
fun UrlImageLusciousGifs(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillWidth,
    loadIndicator: Boolean = true,
    isGrayscale: Boolean = false,
    onLoading: (Boolean) -> Unit = {},
    onSuccess: (Boolean) -> Unit = {},
    albumName : String
) {

    val context = LocalContext.current

    val colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.9f) })

    // Определяем имя файла и проверяем наличие
    val fileName = url.substringAfterLast('/').substringBefore('?')
    //val file = File(AppPath.downloaded_albums_l, "$albumName/$fileName")

    val file  = if (albumName != "likes") {
        File(AppPath.downloaded_albums_l, "$albumName/$fileName")
    }
    else{
        File(AppPath.likes_l, url)
    }

    val dataSource: Any = if (file.exists()) file else url  // либо локальный файл, либо сеть

    val imageRequest = remember {
        ImageRequest.Builder(context)
            .data(dataSource)
            .crossfade(true)
            .apply {
                memoryCacheKey("${url}_preview")
                diskCacheKey("${url}_preview")
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .dispatcher(Dispatchers.Default)
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
    albumName: String
) {
    // Определяем имя файла и проверяем наличие
    val fileName = url.substringAfterLast('/').substringBefore('?')
    val file = if (albumName != "likes") File(AppPath.downloaded_albums_l, "$albumName/$fileName") else File(url)
    val dataSource: Uri = if (file.exists()) {
        Uri.fromFile(file) // локальный файл
    } else {Uri.parse(url) }// сетевой url


    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    val controllerBuilder = remember(dataSource) {
        {
            Fresco.newDraweeControllerBuilder()
                .setUri(dataSource)
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
    }

    Box {
        FrescoWebImage(
            controllerBuilder = controllerBuilder,
            modifier = Modifier.then(modifier)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = Color.Gray
                )
            }
        }

        if (hasError) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Ошибка загрузки", color = Color.Gray)
            }
        }
    }
}
