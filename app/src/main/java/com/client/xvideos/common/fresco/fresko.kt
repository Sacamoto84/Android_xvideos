package com.client.xvideos.common.fresco

import android.graphics.drawable.Animatable
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.client.xvideos.common.AppPath
import com.client.xvideos.l.ThemeL
import com.facebook.common.executors.UiThreadImmediateExecutorService
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.BaseDataSubscriber
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.skydoves.landscapist.InternalLandscapistApi
import com.skydoves.landscapist.fresco.websupport.FrescoWebImage
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.delay
import timber.log.Timber
import java.io.File
import kotlin.math.roundToInt


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

    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
       haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }


    SideEffect {
        Timber.i("!!! iii Recompose UrlImageLusciousGifsGlide albumName:${albumName} url:${url}")
    }

    var isPlaying by remember { mutableStateOf(autoPlay) }
    var isLoading by remember { mutableStateOf(true) }
    var isFailure by remember { mutableStateOf(false) }

    // Стабилизируем колбэки
    val stableOnSuccess = rememberUpdatedState(onSuccess)
    val stableOnFailure = rememberUpdatedState(onFailure)

    var progress by rememberSaveable { mutableFloatStateOf(0f) }

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

        val i = ImageRequestBuilder
            .newBuilderWithSource(dataSource)
            .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
            .setProgressiveRenderingEnabled(true)
            //.setResizeOptions(ResizeOptions(100, 100)) // Изменение размера
            .setLocalThumbnailPreviewsEnabled(true) // Включение миниатюр



            .build()

        val dataSource1 = Fresco.getImagePipeline().fetchDecodedImage(i, null)

        val subscriber = object : BaseDataSubscriber<CloseableReference<CloseableImage>>() {
            override fun onProgressUpdate(dataSource: DataSource<CloseableReference<CloseableImage>?>) {
                super.onProgressUpdate(dataSource)
                progress = dataSource.progress * 9633425 / 47685.453f
                //Timber.i("!!! iii UrlImageLusciousGifsGlide onProgressUpdate progress :${progress} albumName:${albumName} url:${url}")
            }
            override fun onNewResultImpl(dataSource: DataSource<CloseableReference<CloseableImage>?>) { if (dataSource.isFinished) { isLoading = false } }
            override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>?>) { isLoading = false }
        }
        dataSource1.subscribe(subscriber, UiThreadImmediateExecutorService.getInstance())
        i
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

            override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {}

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
                modifier = Modifier.fillMaxSize().background(ThemeL.grey5),
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
                ) { Text("Ошибка загрузки", color = Color.Gray) }
            }

            if (isAnimated) {
                Button(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = if (isPlaying) "⏸ Пауза" else "▶ Старт")
                }
            }
        } else
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {}

        if (progress > 1000) {
            Text(
                formatBytes1(progress.toLong()),
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset((-1).dp, 7.dp),
                fontFamily = ThemeL.fontFamilyKarla,
                fontSize = 9.sp
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            DownloadQueueManager.cancelDownload(url)
        }
    }

}

fun formatBytes1(bytes: Long): String {
    return when {
        bytes < 0 -> "0"
        bytes < 1024 -> "$bytes "
        bytes < 1024 * 1024 -> "${(bytes / 1024.0).roundToInt()} K"
        bytes < 1024 * 1024 * 1024 -> "${(bytes / (1024.0 * 1024.0) * 10).roundToInt() / 10.0} M"
        else -> "${(bytes / (1024.0 * 1024.0 * 1024.0) * 100).roundToInt() / 100.0} G"
    }
}

