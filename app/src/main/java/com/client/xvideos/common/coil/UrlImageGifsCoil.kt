package com.client.xvideos.common.coil


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.asDrawable
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.ImageRequest
import coil3.size.Precision
import coil3.size.Scale
import com.client.xvideos.common.AppPath
import com.client.xvideos.l.theme.ThemeL
import com.composeunstyled.Text
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.math.roundToInt

enum class LoadingIndicator()



private fun createUnsafeImageLoader(context: Context): ImageLoader {
    // ── Створюємо trust-all сертифікат (дуже небезпечно — тільки для розробки/тестів!)
    @SuppressLint("CustomX509TrustManager")
    val trustAllCerts = arrayOf<TrustManager>(
        object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit

            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        }
    )

    val sslContext = SSLContext.getInstance("TLS").apply {
        init(null, trustAllCerts, SecureRandom())
    }

    val unsafeOkHttpClient = OkHttpClient.Builder()
        .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
        .hostnameVerifier { _, _ -> true }
        .build()

    return ImageLoader
        .Builder(context)
        .components {
            // ← саме тут замінюємо старий .okHttpClient()
            add(OkHttpNetworkFetcherFactory(callFactory = { unsafeOkHttpClient }))
            // або коротше, якщо не потрібні додаткові параметри:
            // add(OkHttpNetworkFetcherFactory(unsafeOkHttpClient))
        }
        // .logger(DebugLogger())           // ← раджу увімкнути під час дебагу
        .build()
}






@Suppress("UiComposable")
@OptIn(FlowPreview::class)
@Composable
fun UrlImageGifsCoil(

    url: String,
    urlGif : String?= null, //url для gif файла

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

    isVisibleLoadingIndicator: Boolean = true, //Показ индикатора прогресса после индикатора загрузки
    sizeLoadingIndicator: Dp = 32.dp, //Показ индикатора прогресса после индикатора загрузки


    isVisibleProgressText: Boolean = true,   //Показ текста скачанных данных

    isFullScreen: Boolean = false //Режим полного экрана с поддержкой поворота
) {

//    if (!isVisible) {
//        Box(modifier = Modifier.fillMaxSize().background(Color.Transparent))
//        return
//    }

    //if (isAnimated) return

//    SideEffect {
//        Timber.i("!!! UrlImageGifsCoil url:{$url}")
//    }

    val context = LocalContext.current

    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    var isPlaying by remember { mutableStateOf(autoPlay) }

    val rawProgress by remember(url) {
        derivedStateOf {
            CoilProgressManager.progressMap[url] ?: CoilProgressItem( url, 0L, 0L, false )
        }
    }

//    // Debounce: обновляем UI не чаще 100–200 мс
    val progress by produceState(rawProgress) {
        while (!rawProgress.done) {
            value = rawProgress
            delay(200)
        }
        value = rawProgress
    }
//
    val bytes = progress.bytes
    val total = progress.total

    val dataSource = remember(url, urlGif) {

        if (url.contains("https://"))
            url.toUri()
        else {
            val fileName = url.substringAfterLast('/').substringBefore('?')
            val file = when (albumName) {
                "likes", "crypto" -> File(url)
                else -> File(AppPath.l_downloaded_albums, "$albumName/$fileName")
            }
            file
        }

    }

    // Один глобальный ImageLoader на всё приложение
    val imageLoader = remember {CoilImageLoaderFactory.getImageLoader(context) }

    val imageRequest = remember(dataSource, rotate) {
        ImageRequest.Builder(context)
            .data(dataSource)
            .scale(Scale.FIT)
            .apply {
                if (!isFullScreen) {
                    //size(512, 64)
                    precision(Precision.INEXACT)
                }
            }
            .build()
    }

    var state by remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }

    //// Управление воспроизведением анимации
    LaunchedEffect(state, isPlaying, isAnimated) {

        if (isAnimated && state is AsyncImagePainter.State.Success) {

            val drawable =
                (state as AsyncImagePainter.State.Success).result.image.asDrawable(context.resources)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && drawable is AnimatedImageDrawable) {
                if (isPlaying) {
                    drawable.start()
                    Timber.d("!!! AnimatedImageDrawable started")
                } else {
                    drawable.stop()
                    Timber.d("!!! AnimatedImageDrawable stopped")
                }
            } else if (drawable is android.graphics.drawable.AnimatedVectorDrawable) {
                if (isPlaying) {
                    drawable.start()
                } else {
                    drawable.stop()
                }
            } else if (drawable is android.graphics.drawable.Animatable) {
                if (isPlaying) {
                    drawable.start()
                } else {
                    drawable.stop()
                }
            }
        }
    }

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




            AsyncImage(
                onState = { st ->
                    state = st
                },
                model = imageRequest,
                imageLoader = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) createUnsafeImageLoader(context) else imageLoader,
                contentDescription = null,
                contentScale = contentScale,

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













        when (state) {
            is AsyncImagePainter.State.Empty -> {

            }

            is AsyncImagePainter.State.Loading -> {

                if (isVisibleLoadingIndicator) {

                    Box(
                        modifier = Modifier.matchParentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isAnimated) {
                            // Показываем прогресс в процентах если известен общий размер
                            if (total > 0 && bytes > 0) {
                                val progress = (bytes.toFloat() / total.toFloat())
                                CircularProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.size(sizeLoadingIndicator)
                                )

                            } else {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(sizeLoadingIndicator),
                                    color = Color.Gray
                                )
                            }
                        } else {
                            CircularProgressIndicator(
                                modifier = Modifier.size(sizeLoadingIndicator),
                                color = Color.Gray
                            )
                        }
                    }


                }

            }

            is AsyncImagePainter.State.Success -> {

            }

            is AsyncImagePainter.State.Error -> {
                Box(
                    modifier = Modifier.matchParentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ошибка загрузки", color = Color.Gray)
                }
            }
        }


        //        // Кнопка управления анимацией
        if (isAnimated) {
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .align(Alignment.BottomStart)
                    .size(sizeButton)
                    .clip(CircleShape)
                    //.background(Color.Gray.copy(alpha = 0.5f), CircleShape)

                    .combinedClickable(
                        onClick = { isPlaying = !isPlaying },
                        onLongClick = {


                        }
                    )

//                    .then(
//                        if (!url.contains("https://")) {
//                            Modifier
//
//                                .combinedClickable(
//                                    onClick = {isPlaying = !isPlaying},
//                                    onLongClick = {
//
//
//                                    }
//                                )
//
//
//
//                        } else Modifier
//                    )
                ,
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
                        tint = Color.Black,
                        modifier = Modifier.size(sizeButtonIcon)
                    )

                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(sizeButtonIcon)
                            .offset((-0.5).dp, (-0.5).dp)
                    )

                }
            }
        }

//        // Прогресс загрузки
        if (isVisibleProgressText) {
            Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                if (bytes > 1000) {
                    ProgressText(
                        bytesRead = bytes,
                        totalBytes = total
                    )
                }
            }
        }

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
