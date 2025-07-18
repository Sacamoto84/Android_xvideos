package com.redgifs.common.video.player_row_mini.atom

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.common.videoplayer.host.MediaPlayerEvent
import com.client.common.videoplayer.host.MediaPlayerHost
import com.client.common.videoplayer.model.ScreenResize
import com.client.common.videoplayer.model.VideoPlayerConfig
import com.client.common.videoplayer.ui.video.VideoPlayerWithControl
import com.redgifs.common.BuildConfig
import com.redgifs.common.video.CanvasTimeDurationLine
import com.redgifs.common.video.CanvasTimeDurationLine1
import timber.log.Timber


/**
 * Превьюшка для режима в два столбика
 */
@Composable
fun Red_Video_Lite_Row2(
    url: String,
    play: Boolean = true,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    overlayBottomEnd: @Composable () -> Unit = {},
) {

    var time by remember { mutableFloatStateOf(0f) }
    var duration by remember { mutableIntStateOf(0) }

    if (BuildConfig.DEBUG) {
        SideEffect {
            Timber.i("@@@ Red_Video_Lite_2Rrow() play = $play, url = $url time = $time, duration = $duration")
        }
    }

    val playerHost = remember { MediaPlayerHost(mediaUrl = url, isPaused = false, isMuted = true) }

    LaunchedEffect(play) { if (play) playerHost.play() else playerHost.pause() }
    LaunchedEffect(Unit) { playerHost.videoFitMode = ScreenResize.FIT; playerHost.mute() }

    LaunchedEffect(Unit) {

        playerHost.onEvent = { event ->
            when (event) {

                is MediaPlayerEvent.CurrentTimeChange -> {
                    //println("!!!Current playback time: ${event.currentTime}s")
                    time = event.currentTime
                }

                is MediaPlayerEvent.TotalTimeChange -> {
                    //println("!!!Video duration updated: ${event.totalTime}s")
                    duration = event.totalTime
                }

                else -> {}
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {

        StaticVideoPlayer(playerHost)


        Box(modifier = Modifier.padding(bottom = 48.dp).fillMaxSize().combinedClickable(onClick = onClick, onLongClick = onLongClick))


        Row (Modifier.fillMaxWidth().align(Alignment.BottomEnd), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Box(modifier = Modifier.padding(start = 16.dp).fillMaxWidth().weight(1f)) {

                TimeDuration(time, duration, modifier = Modifier.align(Alignment.TopStart).offset((-2).dp, (-6).dp) )

                CanvasTimeDurationLine1(
                    time, duration, timeA = 0f, timeB = 0f,
                    timeABEnable = false, visibleAB = false,
                    play = play, onSeek = { playerHost.seekTo(it) },
                    onSeekFinished = {}, modifier = Modifier.padding(start = 0.dp, end = 0.dp),
                    isVisibleTime = false, isVisibleStep = false
                )
            }
            Box(modifier = Modifier.size(48.dp)) {
                overlayBottomEnd.invoke()
            }
        }

    }

}


@Composable
private fun TimeDuration(time: Float, duration: Int, modifier: Modifier = Modifier) {
    Text(
        "%02d/%02d".format(time.toInt(), duration), modifier = Modifier.then(modifier), color = Color.White, fontFamily = FontFamily.Monospace,
        fontSize = 10.sp
    )
}

@Composable
private fun StaticVideoPlayer(playerHost: MediaPlayerHost) {
    VideoPlayerWithControl(
        modifier = Modifier.fillMaxSize(),
        playerHost = playerHost,
        playerConfig = VideoPlayerConfig(
            showControls = false,
            isZoomEnabled = false,
            reelVerticalScrolling = false,
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
            loaderView = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        )
    )
}


//@Composable
//private fun CanvasTimeDurationLine(currentTime: Float, duration: Int) {
//
//    Canvas(
//        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
//    ) {
//        val canvasWidth = size.width
//        val canvasHeight = size.height // Будет равно progressHeight
//
//        // 1. Рисуем фон (полная длительность)
//        drawLine(
//            color = Color(0xFF909090), // Цвет фона прогресс-бара
//            start = Offset(x = 0f, y = canvasHeight / 2),
//            end = Offset(x = canvasWidth, y = canvasHeight / 2),
//            strokeWidth = canvasHeight,
//            cap = StrokeCap.Square // Закругленные концы
//        )
//
//        // 2. Рассчитываем и рисуем текущий прогресс
//        if (duration > 0) { // Убедимся, что длительность известна и не равна нулю
//
//            val progressRatio = currentTime / duration.toFloat()
//
//            val progressWidth = canvasWidth * progressRatio.coerceIn(0f, 1f)
//
//            drawLine(
//                color = Color(0xFFE74658), // Цвет активного прогресса
//                start = Offset(x = 0f, y = canvasHeight / 2),
//                end = Offset(x = progressWidth, y = canvasHeight / 2),
//                strokeWidth = 1.5.dp.toPx(),
//                cap = StrokeCap.Square // Закругленные концы
//            )
//
//            //Индикатор
//            drawCircle(
//                color = Color(0xFFE74658),
//                center = Offset(progressWidth, canvasHeight / 2),
//                radius = 2.dp.toPx()
//            )
//
//        }
//
//
//    }
//
//}