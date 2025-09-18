package com.client.xvideos.common.fresco

import android.graphics.drawable.Animatable
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.client.xvideos.common.AppPath
import com.client.xvideos.l.theme.ThemeL
import com.facebook.common.executors.UiThreadImmediateExecutorService
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.BaseDataSubscriber
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.imagepipeline.common.RotationOptions
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.skydoves.landscapist.InternalLandscapistApi
import com.skydoves.landscapist.fresco.websupport.FrescoWebImage
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import timber.log.Timber
import java.io.File
import kotlin.math.roundToInt

@OptIn(InternalLandscapistApi::class, InternalAPI::class, FlowPreview::class)
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
    autoPlay: Boolean = false,
    sizeButton: Dp = 40.dp,
    sizeButtonIcon: Dp = 24.dp,
    rotate: Boolean = false
) {

    SideEffect {
        Timber.i("!!! UrlImageLusciousGifsGlide url:{$url}")
    }

    var isPlaying by remember { mutableStateOf(autoPlay) }
    var isLoading by remember { mutableStateOf(true) }
    var isFailure by remember { mutableStateOf(false) }

    val stableOnSuccess = rememberUpdatedState(onSuccess)
    val stableOnFailure = rememberUpdatedState(onFailure)

    var progress by remember { mutableFloatStateOf(0f) }

    var displayProgress by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        snapshotFlow { progress.toLong() }
            //.distinctUntilChanged()       // обновляем только при изменении числа
            .debounce(100)                // не чаще раза в 200мс
            .collect { newValue ->
                displayProgress = newValue
            }
    }


    val dataSource = remember(url)
    {
        val fileName = url.substringAfterLast('/').substringBefore('?')
        val file = when (albumName) {
            "likes", "crypto" -> File(url)
            else -> File(AppPath.downloaded_albums_l, "$albumName/$fileName")
        }
        if (file.exists()) if (albumName == "crypto") Uri.parse("https://likesCrypto/$fileName") else file.toUri()
        else url.toUri()
    }

    val imageRequest = remember(dataSource, rotate)
    {

        val builder = ImageRequestBuilder
            .newBuilderWithSource(dataSource)
            .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
            .setProgressiveRenderingEnabled(false)

        // Применяем поворот на уровне запроса
        if (rotate) {
            if (!isAnimated) builder.setRotationOptions(
                RotationOptions.forceRotation(
                    RotationOptions.ROTATE_90
                )
            )
        }

        val imageRequest = builder.build()

        // Подписываемся на прогресс
        val dataSource1 = Fresco.getImagePipeline().fetchDecodedImage(imageRequest, null)
        val subscriber = object : BaseDataSubscriber<CloseableReference<CloseableImage>>() {
            override fun onProgressUpdate(dataSource: DataSource<CloseableReference<CloseableImage>?>) {
                super.onProgressUpdate(dataSource)
                progress = dataSource.progress * 9633425 / 47685.453f
            }

            override fun onNewResultImpl(dataSource: DataSource<CloseableReference<CloseableImage>?>) {
                if (dataSource.isFinished) {
                    isLoading = false
                }
            }

            override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>?>) {
                isLoading = false
            }
        }
        dataSource1.subscribe(subscriber, UiThreadImmediateExecutorService.getInstance())
        imageRequest
    }

    var animation: Animatable? by remember { mutableStateOf(null) }
    LaunchedEffect(animation, isPlaying)
    {
        if (isPlaying) {
            animation?.start()
        } else {
            animation?.stop()
        }
    }

    val controllerListener = remember(url)
    {
        object : BaseControllerListener<ImageInfo>() {
            override fun onSubmit(id: String?, callerContext: Any?) {
                isLoading = true; isFailure = false
            }

            override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {}

            override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, anim: Animatable?) {
                isLoading = false; isFailure = false
                stableOnSuccess.value()
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

    var isControllerReady by remember { mutableStateOf(false) }

    LaunchedEffect(dataSource) {
        delay(100)
        isControllerReady = true
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().then(modifier),
        contentAlignment = Alignment.Center
    ) {

        val w = maxWidth
        val h = maxHeight

        if (isAnimated) Timber.i("!!! UrlImageLusciousGifsGlide w:{$w} h:{$h}")

        if (isControllerReady) {

            FrescoWebImage(
                controllerBuilder = {
                    Fresco.newDraweeControllerBuilder()
                        .setImageRequest(imageRequest)
                        .setAutoPlayAnimations(true)
                        .setControllerListener(controllerListener)
                        .setOldController(null)
                },
                contentScale = contentScale,
                modifier = Modifier
                    .background(ThemeL.grey5)
                    .then(
                        if (isAnimated) {
                            Modifier.graphicsLayer(
                                rotationZ = if (rotate) 90f else 0f,
                                scaleX = if (rotate) {
                                    if (h > w) h / w else w / h
                                } else {
                                    1f
                                },
                                scaleY = if (rotate) {
                                    if (h > w) h / w else w / h
                                } else {
                                    1f
                                },
                            )
                        } else Modifier
                    )
                    .fillMaxSize()
            )

            // Индикаторы загрузки и ошибки
            if (isLoading && loadIndicator) {
                Box(
                    modifier = Modifier.matchParentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            }

            if (isFailure) {
                Box(
                    modifier = Modifier.matchParentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ошибка загрузки", color = Color.Gray)
                }
            }

            // Кнопка управления анимацией
            if (isAnimated) {
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .align(Alignment.BottomStart)
                        .size(sizeButton)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.5f), CircleShape)
                        .then(
                            if (!url.contains("https://")) {
                                Modifier.clickable { isPlaying = !isPlaying }
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (url.contains("https://")) {
                        Icon(
                            Icons.Default.Animation,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(sizeButtonIcon)
                        )
                    } else {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(sizeButtonIcon)
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {}
        }


    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        if (displayProgress > 1000) {
            ProgressText(progress = displayProgress)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            DownloadQueueManager.cancelDownload(url)
        }
    }
}

@Composable
private fun ProgressText(progress: Long) {

    SideEffect {
        Timber.i("!!! UrlImageLusciousGifsGlide ProgressText progress:{$progress}")
    }

    if (progress > 1000) {
        Text(
            formatBytes1(progress),
            color = Color.White,
            modifier = Modifier.offset((-1).dp, 7.dp),
            fontFamily = ThemeL.fontFamilyKarla,
            fontSize = 9.sp
        )
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

