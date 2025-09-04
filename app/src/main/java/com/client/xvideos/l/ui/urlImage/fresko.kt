package com.client.xvideos.l.ui.urlImage

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.client.xvideos.common.AppPath
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.InternalLandscapistApi
import com.skydoves.landscapist.fresco.FrescoImage
import java.io.File


// Альтернативный вариант с более детальным контролем анимации
@OptIn(InternalLandscapistApi::class)
@Composable
fun UrlImageLusciousGifsGlide(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    loadIndicator: Boolean = true,
    albumName: String,
    isAnimated: Boolean = false
) {
    var isPlaying by remember { mutableStateOf(true) }

    val fileName = url.substringAfterLast('/').substringBefore('?')
    val file = when (albumName) {
        "likes", "crypto" -> File(url)
        else -> File(AppPath.downloaded_albums_l, "$albumName/$fileName")
    }

    val dataSource: String = remember(url){if (file.exists()) file.path else url}

    val animatedFile = isAnimated ||
            fileName.endsWith(".gif", true) ||
            fileName.endsWith(".webp", true)


    val imageRequest = remember(dataSource) {
        ImageRequestBuilder.newBuilderWithSource(dataSource.toUri())
            .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
            .setProgressiveRenderingEnabled(true)
            //.setResizeOptions(ResizeOptions(100, 100)) // Изменение размера
            .setLocalThumbnailPreviewsEnabled(true) // Включение миниатюр
    }

    Column(modifier = modifier) {
        FrescoImage(

            imageUrl = dataSource,
            imageRequest = {
                imageRequest
            },

            imageOptions = ImageOptions(
                contentScale = contentScale,
                alignment = Alignment.Center
            ),

            modifier = Modifier.fillMaxSize().background(Color.Gray),


            loading = {
                if (loadIndicator) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                }
            },
//
            failure = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ошибка загрузки", color = Color.Red)
                }
            },

        )

        if (animatedFile) {
            Button(
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = if (isPlaying) "⏸ Пауза" else "▶ Старт")
            }
        }
    }
}



// Утилитарные функции для работы с Fresco
object FrescoUtils {
    fun clearCache() {
        Fresco.getImagePipeline().clearCaches()
    }

    fun clearMemoryCache() {
        Fresco.getImagePipeline().clearMemoryCaches()
    }

    fun pauseInBackground() {
        Fresco.getImagePipeline().pause()
    }

    fun resume() {
        Fresco.getImagePipeline().resume()
    }

    fun prefetchImage(uri: String, context: Context) {
        val imageRequest = ImageRequestBuilder
            .newBuilderWithSource(Uri.parse(uri))
            .build()

        Fresco.getImagePipeline().prefetchToBitmapCache(imageRequest, context)
    }
}

//@Composable
//fun UrlImageLusciousGifsGlide(
//    url: String,
//    modifier: Modifier = Modifier,
//    contentScale: ContentScale = ContentScale.FillWidth,
//    loadIndicator: Boolean = true,
//    onLoading: (Boolean) -> Unit = {},
//    onSuccess: (Boolean) -> Unit = {},
//    albumName: String,
//    isAnimated: Boolean = false
//) {
//    // Определяем имя файла и проверяем наличие
//    val fileName = url.substringAfterLast('/').substringBefore('?')
//    val file = when (albumName) {
//        "likes", "crypto" -> File(url)
//        else -> File(AppPath.downloaded_albums_l, "$albumName/$fileName")
//    }
//
//    val dataSource: Any = if (file.exists()) {
//        when (albumName) {
//            "likes" -> file
//            "crypto" -> file //EncryptedFileModel(file)
//            else -> file
//        }
//    } else {
//        url
//    }
//
//    val context = LocalContext.current
//    var isPlaying by remember { mutableStateOf(true) }
//    var isLoading by remember { mutableStateOf(true) }
//
//    val animatedFile = isAnimated || fileName.endsWith(".gif", true) || fileName.endsWith(".webp", true)
//
//    Column(modifier = modifier) {
//        Box(
//            modifier = Modifier
//                .weight(1f)
//                .fillMaxWidth()
//        ) {
//            AndroidView(
//                factory = { context ->
//                    SimpleDraweeView(context).apply {
//                        layoutParams = ViewGroup.LayoutParams(
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            ViewGroup.LayoutParams.MATCH_PARENT
//                        )
//
//                        // Настройка масштабирования
//                        hierarchy.actualImageScaleType = when (contentScale) {
//                            ContentScale.FillWidth, ContentScale.Crop -> ScalingUtils.ScaleType.CENTER_CROP
//                            ContentScale.Fit -> ScalingUtils.ScaleType.FIT_CENTER
//                            ContentScale.FillBounds -> ScalingUtils.ScaleType.FIT_XY
//                            else -> ScalingUtils.ScaleType.CENTER_CROP
//                        }
//
//                        // Настройка placeholder и failure
//                        hierarchy.setPlaceholderImage(ColorDrawable(android.graphics.Color.GRAY))
//                        hierarchy.setFailureImage(ColorDrawable(android.graphics.Color.RED))
//                    }
//                },
//                update = { draweeView ->
//                    val imageRequest = ImageRequestBuilder.newBuilderWithSource(
//                        when (dataSource) {
//                            is String -> Uri.parse(dataSource)
//                            is File -> Uri.fromFile(dataSource)
//                            //is EncryptedFileModel -> Uri.fromFile(dataSource) // Адаптируйте под свою модель
//                            else -> Uri.parse(dataSource.toString())
//                        }
//                    ).apply {
//                        // Настройки для анимированных изображений
//                        if (animatedFile) {
//                            setAutoRotateEnabled(true)
//                            setLocalThumbnailPreviewsEnabled(true)
//                        }
//
//                        // Кеширование
//                        setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
//                        setRequestPriority(Priority.HIGH)
//
//                    }.build()
//
//                    val draweeController = Fresco.newDraweeControllerBuilder()
//                        .setImageRequest(imageRequest)
//                        .setAutoPlayAnimations(isPlaying && animatedFile)
//                        .setControllerListener(object : BaseControllerListener<ImageInfo>() {
//                            override fun onSubmit(id: String?, callerContext: Any?) {
//                                isLoading = true
//                                onLoading(true)
//                            }
//
//                            override fun onFinalImageSet(
//                                id: String?,
//                                imageInfo: ImageInfo?,
//                                animatable: Animatable?
//                            ) {
//                                isLoading = false
//                                onLoading(false)
//                                onSuccess(true)
//
//                                // Контроль анимации
//                                animatable?.let { anim ->
//                                    if (isPlaying) {
//                                        anim.start()
//                                    } else {
//                                        anim.stop()
//                                    }
//                                }
//                            }
//
//                            override fun onFailure(id: String?, throwable: Throwable?) {
//                                isLoading = false
//                                onLoading(false)
//                            }
//                        })
//                        .setOldController(draweeView.controller)
//                        .build()
//
//                    draweeView.controller = draweeController
//                },
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.Gray)
//            )
//
//            // Индикатор загрузки
//            if (isLoading && loadIndicator) {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(32.dp),
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                }
//            }
//        }
//
//        // Кнопка Play/Pause для анимированных файлов
//        if (animatedFile) {
//            Button(
//                onClick = {
//                    isPlaying = !isPlaying
//                },
//                modifier = Modifier.fillMaxWidth().padding(8.dp),
//
//            ) {
//                Text(
//                    text = if (isPlaying) "⏸ Пауза" else "▶ Старт",
//                    color = Color.White
//                )
//            }
//        }
//    }
//}