package com.client.xvideos.common.coil

import android.graphics.drawable.AnimatedImageDrawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.asDrawable
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Precision
import coil3.size.Scale
import com.client.xvideos.common.AppPath
import com.client.xvideos.l.theme.ThemeL
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.File
import kotlin.math.roundToInt

@Suppress("UiComposable")
@OptIn(FlowPreview::class)
@Composable
fun UrlImageGifsCoil(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    loadIndicator: Boolean = true,
    albumName: String,
    isAnimated: Boolean = false,
    onSuccess: () -> Unit = {},
    onFailure: () -> Unit = {},
    autoPlay: Boolean = false,
    sizeButton: Dp = 32.dp,
    sizeButtonIcon: Dp = 20.dp,
    rotate: Boolean = false,                 //Поворот изображения
    isVisible: Boolean = true,

    //new
    isVisibleProgressIndictor: Boolean = true, //Показ индикатора прогресса после индикатора загрузки


    isFullScreen: Boolean = false //Режим полного экрана с поддержкой поворота
) {

    if (isAnimated) return

//    SideEffect {
//        Timber.i("!!! UrlImageGifsCoil url:{$url}")
//    }

    val context = LocalContext.current

    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    var isPlaying by remember { mutableStateOf(autoPlay) }

//    val rawProgress by remember(url) {
//        derivedStateOf {
//            CoilProgressManager.progressMap[url] ?: CoilProgressItem(
//                url,
//                0L,
//                0L,
//                false
//            )
//        }
//    }
//
//    // Debounce: обновляем UI не чаще 100–200 мс
//    val progress by produceState(rawProgress) {
//        while (!rawProgress.done) {
//            value = rawProgress
//            delay(150)
//        }
//    }
//
//    val bytes = progress.bytes
//    val total = progress.total
//    val done = progress.done


//    LaunchedEffect(Unit) {
//        snapshotFlow { bytesRead }
//            .debounce(100)
//            .collect { newValue ->
//                displayProgress = newValue
//            }
//    }

    val dataSource = remember(url) {

        if (url.contains("https://"))
            url.toUri()
        else {
            val fileName = url.substringAfterLast('/').substringBefore('?')
            val file = when (albumName) {
                "likes", "crypto" -> File(url)
                else -> File(AppPath.downloaded_albums_l, "$albumName/$fileName")
            }
            file
        }

    }

    // Один глобальный ImageLoader на всё приложение
    val imageLoader = remember { CoilImageLoaderFactory.getImageLoader(context) }

    val imageRequest = remember(dataSource, rotate) {
        ImageRequest.Builder(context)
            .data(dataSource)
            //.crossfade(true)
            .scale(Scale.FIT)

            .apply {
                if (!isFullScreen) {
                    //size(512, 64)
                    precision(Precision.INEXACT)
                }
            }



//            .listener(
//                onStart = {
//                    bytesRead = 0L
//                    contentLength = 0L
//                },
//                onSuccess = { _, result ->
//                },
//                onError = { _, result ->
//                    //Timber.e("!!! eee UrlImageGifsCoil throwable:${result.throwable}")
//                }
//            )
            .build()
    }

//    // Painter для контроля анимации
//    val painter = rememberAsyncImagePainter(
//        model = imageRequest,
//        imageLoader = imageLoader
//    )

    //val state = painter.state.collectAsState().value


//// Управление воспроизведением анимации
//    LaunchedEffect(state, isPlaying, isAnimated) {
//
//        if (isAnimated && state is AsyncImagePainter.State.Success) {
//
//            val drawable = state.result.image.asDrawable(context.resources)
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && drawable is AnimatedImageDrawable) {
//                if (isPlaying) {
//                    drawable.start()
//                    Timber.d("!!! AnimatedImageDrawable started")
//                } else {
//                    drawable.stop()
//                    Timber.d("!!! AnimatedImageDrawable stopped")
//                }
//            } else if (drawable is android.graphics.drawable.AnimatedVectorDrawable) {
//                if (isPlaying) {
//                    drawable.start()
//                } else {
//                    drawable.stop()
//                }
//            } else if (drawable is android.graphics.drawable.Animatable) {
//                if (isPlaying) {
//                    drawable.start()
//                } else {
//                    drawable.stop()
//                }
//            }
//
//        }
//    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
            .then(
                if (isFullScreen) {
                    Modifier.onSizeChanged { containerSize = it }
                } else
                    Modifier
            ),
        contentAlignment = Alignment.Center
    )
    {

        SubcomposeAsyncImage(
            model = imageRequest,
            imageLoader = imageLoader,
            contentDescription = null,
            contentScale = contentScale,

            onLoading = {

            },

//            placeholder = forwardingPainter(
//                painter = painterResource(R.drawable.placeholder),
//                colorFilter = ColorFilter(Color.Red),
//                alpha = 0.5f,
//            ),
            modifier = Modifier

                .background(ThemeL.grey5)
                .then(
                    if (isFullScreen && rotate) {
                        Modifier.graphicsLayer(

                            rotationZ = 90f,
                            scaleX = if (containerSize != IntSize.Zero) {
                                if (containerSize.height > containerSize.width)
                                    containerSize.height.toFloat() / containerSize.width
                                else
                                    containerSize.width.toFloat() / containerSize.height
                            } else 1f,

                            scaleY = if (containerSize != IntSize.Zero) {
                                if (containerSize.height > containerSize.width)
                                    containerSize.height.toFloat() / containerSize.width
                                else
                                    containerSize.width.toFloat() / containerSize.height
                            } else 1f
                        )

                    } else
                        Modifier
                )
                .fillMaxSize()
                .then(
                    if (isAnimated) {
                        Modifier.clickable { isPlaying = !isPlaying }
                    } else Modifier
                )
        )

//        Image(
//            painter = painter,
//            contentDescription = null,
//            contentScale = contentScale,
//            modifier = Modifier
//                .background(ThemeL.grey5)
//                .then(
//                    if (rotate) {
//                        Modifier.graphicsLayer(
//                            rotationZ = 90f,
//                            scaleX = if (containerSize != IntSize.Zero) {
//                                if (containerSize.height > containerSize.width)
//                                    containerSize.height.toFloat() / containerSize.width
//                                else
//                                    containerSize.width.toFloat() / containerSize.height
//                            } else 1f,
//
//                            scaleY = if (containerSize != IntSize.Zero) {
//                                if (containerSize.height > containerSize.width)
//                                    containerSize.height.toFloat() / containerSize.width
//                                else
//                                    containerSize.width.toFloat() / containerSize.height
//                            } else 1f
//                        )
//                    } else
//                        Modifier
//                )
//                .fillMaxSize()
//                .then(
//                    if (isAnimated) {
//                        Modifier.clickable { isPlaying = !isPlaying }
//                    } else Modifier
//                )
//        )

//        // Кнопка управления анимацией
//        if (isAnimated) {
//            Box(
//                modifier = Modifier
//                    .padding(2.dp)
//                    .align(Alignment.BottomStart)
//                    .size(sizeButton)
//                    .clip(CircleShape)
//                    //.background(Color.Gray.copy(alpha = 0.5f), CircleShape)
//                    .then(
//                        if (!url.contains("https://")) {
//                            Modifier.clickable { isPlaying = !isPlaying }
//                        } else Modifier
//                    ),
//                contentAlignment = Alignment.Center
//            ) {
//                if (url.contains("https://")) {
//                    Icon(
//                        Icons.Default.Animation,
//                        contentDescription = null,
//                        tint = Color.White,
//                        modifier = Modifier.size(sizeButtonIcon)
//                    )
//                } else {
//                    Icon(
//                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
//                        contentDescription = null,
//                        tint = Color.White,
//                        modifier = Modifier.size(sizeButtonIcon)
//                    )
//                }
//            }
//        }

//        when (state) {
//            is AsyncImagePainter.State.Empty -> {
//
//            }
//
//            is AsyncImagePainter.State.Loading -> {
//                if (loadIndicator) {
//                    Box(
//                        modifier = Modifier.matchParentSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        // Показываем прогресс в процентах если известен общий размер
//                        if (contentLength > 0 && bytesRead > 0) {
//                            val progress = (bytesRead.toFloat() / contentLength.toFloat())
//                            CircularProgressIndicator(
//                                progress = { progress },
//                                modifier = Modifier.size(32.dp)
//                            )
//                        } else {
//                            CircularProgressIndicator(
//                                modifier = Modifier.size(32.dp),
//                                color = Color.Gray
//                            )
//                        }
//                    }
//                }
//            }
//
//            is AsyncImagePainter.State.Success -> {
//
//            }
//
//            is AsyncImagePainter.State.Error -> {
//                Box(
//                    modifier = Modifier.matchParentSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text("Ошибка загрузки", color = Color.Gray)
//                }
//            }
//        }


//        // Прогресс загрузки
//        Box(modifier = Modifier.align(Alignment.BottomEnd)) {
//            if (displayProgress > 1000) {
//                ProgressText(
//                    bytesRead = displayProgress,
//                    totalBytes = contentLength
//                )
//            }
//        }

    }


}


@Composable
private fun ProgressText(
    bytesRead: Long,
    totalBytes: Long,
    visibleByte: Boolean = true
) {
    if (bytesRead > 1000) {
        val text = if (totalBytes > 0) {
            if (visibleByte) "${formatBytes1(bytesRead)} / ${formatBytes1(totalBytes)}" else formatBytes1(
                totalBytes
            )
        } else {
            formatBytes1(bytesRead)
        }

        Text(
            text,
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
