package com.client.xvideos.screens_red.profile.atom

import androidx.annotation.OptIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.media3.common.util.UnstableApi
import chaintech.videoplayer.host.MediaPlayerEvent
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.ScreenResize
import chaintech.videoplayer.model.VideoPlayerConfig
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage

@OptIn(UnstableApi::class)
@Composable
fun RedUrlVideoLiteChaintech(url: String, thumnailUrl: String = "", play: Boolean, onChangeTime: (Pair<Int, Int>) -> Unit) {

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        val playerHost = remember {
            MediaPlayerHost(
                mediaUrl = url,
                isPaused = true
            )
        }

        LaunchedEffect(play) {

            if (play)
                playerHost.play()
            else
                playerHost.pause()


        }

        var time by remember { mutableIntStateOf(0) }
        var duration by remember { mutableIntStateOf(0) }

        onChangeTime(time to duration)

        playerHost.onEvent = { event ->
            when (event) {
                is MediaPlayerEvent.MuteChange -> {
                    println("!!! Mute status changed: ${event.isMuted}")
                }

                is MediaPlayerEvent.PauseChange -> {
                    println("!!!Pause status changed: ${event.isPaused}")
                }

                is MediaPlayerEvent.BufferChange -> {
                    println("!!!Buffering status: ${event.isBuffering}")
                }

                is MediaPlayerEvent.CurrentTimeChange -> {
                    println("!!!Current playback time: ${event.currentTime}s")
                    time = event.currentTime.toInt()
                }

                is MediaPlayerEvent.TotalTimeChange -> {
                    println("!!!Video duration updated: ${event.totalTime}s")
                    duration = event.totalTime.toInt()
                }

                is MediaPlayerEvent.FullScreenChange -> {
                    println("!!!FullScreen status changed: ${event.isFullScreen}")
                }

                MediaPlayerEvent.MediaEnd -> {
                    println("!!!Video playback ended")
                }
            }
        }

        playerHost.setVideoFitMode( ScreenResize.FIT)

        VideoPlayerComposable(
            modifier = Modifier.fillMaxSize(),
            playerHost = playerHost,
            playerConfig = VideoPlayerConfig(

                isPauseResumeEnabled = true,
                isSeekBarVisible = true,
                isMuteControlEnabled = true,
                isDurationVisible = true,

                seekBarThumbColor = Color.Red,
                seekBarActiveTrackColor = Color.Red,
                seekBarInactiveTrackColor = Color.White,
                durationTextColor = Color.White,
                seekBarBottomPadding = 10.dp,
                pauseResumeIconSize = 40.dp,

                isAutoHideControlEnabled = true,
                controlHideIntervalSeconds = 5,

                isFastForwardBackwardEnabled = true,

                isGestureVolumeControlEnabled = false,

                showAudioTracksOptions = false,
                showControls = false,

                loaderView = {

                    UrlImage1(
                        url = thumnailUrl,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize().background(Color.Transparent)
                            //.zIndex(1f) // убедиться, что над видео
                    )

                }
            )
        )

        Text("     ${time} : ${duration}", color = Color.White)

//        Canvas(
//            modifier = Modifier
//                .padding(horizontal = 1.5.dp)
//                //.padding(bottom = 16.dp)
//                .fillMaxWidth()
//                .align(Alignment.TopCenter)
//                .height(1.dp) // Задаем высоту Canvas
//            //.padding(top = 4.dp) // Небольшой отступ от текста времени
//        ) {
//            val canvasWidth = size.width
//            val canvasHeight = size.height // Будет равно progressHeight
//
//            // 1. Рисуем фон (полная длительность)
//            drawLine(
//                color = Color.LightGray, // Цвет фона прогресс-бара
//                start = Offset(x = 0f, y = canvasHeight / 2),
//                end = Offset(x = canvasWidth, y = canvasHeight / 2),
//                strokeWidth = canvasHeight,
//                cap = StrokeCap.Square // Закругленные концы
//            )
//
//            // 2. Рассчитываем и рисуем текущий прогресс
//            if (duration > 0) { // Убедимся, что длительность известна и не равна нулю
//                val progressRatio = time.toFloat() / duration.toFloat()
//                val progressWidth = canvasWidth * progressRatio.coerceIn(0f, 1f) // Ограничиваем от 0 до 1
//
//                drawLine(
//                    color = Color.Red, // Цвет активного прогресса
//                    start = Offset(x = 0f, y = canvasHeight / 2),
//                    end = Offset(x = progressWidth, y = canvasHeight / 2),
//                    strokeWidth = canvasHeight,
//                    cap = StrokeCap.Square // Закругленные концы
//                )
//            }
//        }
    }
}


@Composable
fun UrlImage1(url: String, modifier: Modifier = Modifier,  contentScale : ContentScale = ContentScale.FillWidth) {

    val context = LocalContext.current

    val imageRequest = ImageRequest.Builder(context)
        .data(url)
        .crossfade(true)
        .memoryCacheKey(url)
        .diskCacheKey(url)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    CoilImage(
        imageRequest = { imageRequest },
        imageOptions = ImageOptions(
            contentScale = contentScale,
            alignment = Alignment.Center
        ),
        modifier = Modifier.then(modifier),

        failure = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                com.composeunstyled.Text("Ошибка загрузки", color = Color.Gray)
            }
        }

    )
}

// Вспомогательная функция для форматирования времени из секунд в MM:SS
private fun formatDuration(totalSeconds: Int): String {
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
