package com.client.xvideos.common.fresco

import android.content.Context
import android.graphics.drawable.Animatable
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.client.xvideos.common.AppPath
import com.client.xvideos.l.ThemeL
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.skydoves.landscapist.InternalLandscapistApi
import com.skydoves.landscapist.fresco.websupport.FrescoWebImage
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.delay
import timber.log.Timber
import java.io.File


// Альтернативный вариант с более детальным контролем анимации
@OptIn(InternalLandscapistApi::class, InternalAPI::class)
@Composable
fun UrlImageLusciousGifsGlide(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    loadIndicator: Boolean = true,
    albumName: String,
    isAnimated: Boolean = false,
    onSuccess: () -> Unit = {},
    onFailure: () -> Unit = {},
    autoPlay: Boolean = false
) {

    SideEffect {
        Timber.i("!!! iii Recompose UrlImageLusciousGifsGlide albumName:${albumName} url:${url}")
    }

    var isPlaying by remember { mutableStateOf(autoPlay) }
    var isLoading by remember { mutableStateOf(true) }
    var isFailure by remember { mutableStateOf(false) }

    // Стабилизируем колбэки
    val stableOnSuccess = rememberUpdatedState(onSuccess)
    val stableOnFailure = rememberUpdatedState(onFailure)


    val dataSource = remember(url) {
        val fileName = url.substringAfterLast('/').substringBefore('?')

        val file = when (albumName) {
            "likes", "crypto" -> File(url)
            else -> File(AppPath.downloaded_albums_l, "$albumName/$fileName")
        }

        if (file.exists())
            if (albumName == "crypto") Uri.parse("https://likesCrypto/$fileName") else file.toUri()
        else url.toUri()

    }

    val imageRequest = remember(dataSource) {
        ImageRequestBuilder
            .newBuilderWithSource(dataSource)
            //.setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
            .setProgressiveRenderingEnabled(true)
            //.setResizeOptions(ResizeOptions(100, 100)) // Изменение размера
            //.setLocalThumbnailPreviewsEnabled(true) // Включение миниатюр
            .build()
    }


    var animation: Animatable? by remember { mutableStateOf(null) }
    LaunchedEffect(animation, isPlaying) {
        if (isPlaying) {
            animation?.start()
        } else {
            animation?.stop()
        }
    }

    val controllerListener = remember(url) {
        object : BaseControllerListener<ImageInfo>() {

            override fun onSubmit(id: String?, callerContext: Any?) {
                isLoading = true; isFailure = false
            }

            override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) { }

            override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, anim: Animatable?) {
                isLoading = false; isFailure = false
                stableOnSuccess.value()

                // Управляем анимацией напрямую через Animatable
                anim?.let { animatable ->
                    animation = animatable
                }
            }

            override fun onFailure(id: String?, throwable: Throwable?) {
                isLoading = false
                isFailure = true
                Timber.e("!!! eee UrlImageLusciousGifsGlide id:{$id} throwable:${throwable}")
                stableOnFailure.value()
            }
        }
    }

    // Добавляем состояние для контроля инициализации
    var isControllerReady by remember { mutableStateOf(false) }

    LaunchedEffect(dataSource) {
        // Небольшая задержка перед созданием контроллера
        delay(100)
        isControllerReady = true
    }

    Box(modifier = modifier) {

        if (isControllerReady) {
            FrescoWebImage(
                controllerBuilder = {
                    Fresco.newDraweeControllerBuilder()
                        //.setUri(dataSource)
                        .setImageRequest(imageRequest)
                        .setAutoPlayAnimations(true)
                        .setControllerListener(controllerListener)
                        .setOldController(null) // Явно сбрасываем старый контроллер
                },
                modifier = Modifier.fillMaxSize().background(ThemeL.grey6),
            )

            if (isLoading) {
                if (loadIndicator) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(modifier = Modifier.size(32.dp)) }
                }
            }

            if (isFailure) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text("Ошибка загрузки", color = Color.Red) }
            }

            if (isAnimated) {
                Button( onClick = { isPlaying = !isPlaying }, modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(text = if (isPlaying) "⏸ Пауза" else "▶ Старт")
                }
            }
        } else
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red),
                contentAlignment = Alignment.Center
            ) {
                //CircularProgressIndicator(modifier = Modifier.size(32.dp))
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