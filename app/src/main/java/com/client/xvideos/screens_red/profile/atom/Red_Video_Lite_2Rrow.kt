package com.client.xvideos.screens_red.profile.atom

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host.MediaPlayerEvent
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host.MediaPlayerHost
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.ScreenResize
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.VideoPlayerConfig
import com.client.xvideos.screens_red.ThemeRed


/**
 * Превьюшка для режима в два столбика
 */
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun Red_Video_Lite_2Rrow(
    url: String,
    thumnailUrl: String = "",
    play: Boolean = true,
    onChangeTime: (Pair<Float, Int>) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    overlay: @Composable () -> Unit = {},
) {

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        val playerHost = remember {
            MediaPlayerHost(
                mediaUrl = url,
                isPaused = true,
                isMuted = true
            )
        }

        LaunchedEffect(play) {
            if (play) playerHost.play() else playerHost.pause()
        }

        var time by remember { mutableFloatStateOf(0f) }
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
                    time = event.currentTime
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

        playerHost.setVideoFitMode(ScreenResize.FIT)

        playerHost.mute()

        VideoPlayerComposable(
            modifier = Modifier.fillMaxSize(),
            playerHost = playerHost,
            playerConfig = VideoPlayerConfig(
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
                showControls = false,
                loaderView = {

                    UrlImage1(
                        url = thumnailUrl,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                        //.zIndex(1f) // убедиться, что над видео
                    )

                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }

                }
            )
        )

        Text(
            "%02d:%02d".format(time, duration),
            modifier = Modifier
                .align(Alignment.TopEnd),
            color = Color.White,
            fontFamily = ThemeRed.fontFamilyPopinsRegular,
            fontSize = 8.sp
        )


        Box(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
        )

        overlay

        CanvasTimeDurationLine(
            time, duration,
            timeA = 1f,
            timeB = 1f,
            timeABEnable = false
        )

    }
}
