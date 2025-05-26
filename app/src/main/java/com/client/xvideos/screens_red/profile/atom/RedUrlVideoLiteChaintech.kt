package com.client.xvideos.screens_red.profile.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.client.xvideos.BuildConfig
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host.MediaPlayerEvent
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host.MediaPlayerHost
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.ScreenResize
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.VideoPlayerConfig
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.ui.video.VideoPlayerComposable
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.ui.video.VideoPlayerWithControl2
import com.client.xvideos.screens_red.ThemeRed
import com.client.xvideos.screens_red.profile.PlayerControls
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun RedUrlVideoLiteChaintech(
    url: String,
    thumnailUrl: String = "",
    play: Boolean,
    onChangeTime: (Pair<Float, Int>) -> Unit,
    onLongClick: () -> Unit = {},
    isMute: Boolean = false,
    onPlayerControlsReady: (PlayerControls) -> Unit = {},
    timeA: Float = 0f,
    timeB: Float = 0f,
    enableAB: Boolean = false,
    onClick: () -> Unit = {},
    menuContent: @Composable () -> Unit = {},
    menuContentWidth: Dp = 192.dp,
    menuDefaultOpen: Boolean = false,
    menuOpenChanged: (Boolean) -> Unit = {},
    rotate : Float
) {

    if (BuildConfig.DEBUG) {
        SideEffect {
            Timber.i("@@@ RedUrlVideoLiteChaintech() play:$play isMute:$isMute timeA:$timeA menuDefaultOpen:$menuDefaultOpen")
        }
    }

    val scope = rememberCoroutineScope()

    var currentTime by remember { mutableFloatStateOf(0f) }
    var duration by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        snapshotFlow { currentTime to duration }
            .collectLatest { onChangeTime(it) }
    }

    val playerHost = remember { MediaPlayerHost(mediaUrl = url, isPaused = true) }

    LaunchedEffect(url) {
        delay(100)
        println("!!! Смена Url $url")
        playerHost.seekTo(0f)
    }

    LaunchedEffect(play) {
        if (play)
            playerHost.play()
        else
            playerHost.pause()
    }

    LaunchedEffect(playerHost, onPlayerControlsReady) {
        val controls = object : PlayerControls {

            override fun forward(seconds: Float) {
                val currentPosition =
                    (currentTime + seconds).coerceAtMost(duration.toFloat()).toFloat()
                playerHost.seekTo(currentPosition)
            }

            override fun rewind(seconds: Float) {
                val currentPosition = (currentTime - seconds).coerceAtLeast(0f).toFloat()
                playerHost.seekTo(currentPosition)
            }

            override fun seekTo(positionSeconds: Float) {
                playerHost.seekTo(
                    positionSeconds.coerceIn(0f, duration.toFloat()).toFloat()
                )
            }

            override fun stop() {
                playerHost.pause()
                playerHost.seekTo(0f)
            }

            override fun pause() {
                playerHost.pause()
            }

            override fun play() {
                playerHost.play()
            }
        }
        onPlayerControlsReady(controls)
    }

    Box(modifier = Modifier.fillMaxWidth()) {

        LaunchedEffect(isMute) {
            if (isMute)
                playerHost.mute()
            else
                playerHost.unmute()
        }

//        LaunchedEffect(currentTime, duration) {
//            Timber.d("@@@ RedUrlVideoLiteChaintech onChangeTime($currentTime, $duration)")
//
//            //onChangeTime(currentTime to duration)
//        }


        playerHost.onEvent = { event ->
            when (event) {
                is MediaPlayerEvent.MuteChange -> {
                    println("!!! Mute status changed: ${event.isMuted}")
                }

                is MediaPlayerEvent.PauseChange -> {
                    println("!!!Pause status changed: ${event.isPaused}")
                }

                is MediaPlayerEvent.BufferChange -> {
                    println("???Buffering status: ${event.isBuffering}")
                    //isBuffering = event.isBuffering
                }

                is MediaPlayerEvent.CurrentTimeChange -> {
                    //println("!!!Current playback time: ${event.currentTime}s")
                    currentTime = event.currentTime
                    if (enableAB && currentTime >= timeB) {
                        println("!!! playerHost.seekTo(${timeA}) ")
                        scope.launch {
                            playerHost.seekTo(timeA.toFloat())
                        }
                    }
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

        LaunchedEffect(enableAB) {
            if (enableAB) {
                playerHost.seekTo(timeA.toFloat())
            } else {
                //playerHost.seekTo(0f)
            }
        }

        playerHost.setVideoFitMode(ScreenResize.FIT)


        VideoPlayerWithControl2(
            onClick = onClick,
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
                isGestureVolumeControlEnabled = true,
                showAudioTracksOptions = false,
                showControls = true,

                loaderView = {

//                    UrlImage1(
//                        url = thumnailUrl,
//                        contentScale = ContentScale.Fit,
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(Color.Transparent)
//                        //.zIndex(1f) // убедиться, что над видео
//                    )

                },
            ),
            menuContent = menuContent,
            menuContentWidth = menuContentWidth,
            menuDefaultOpen = menuDefaultOpen,
            menuOpenChanged = menuOpenChanged,
            rotate = rotate
        )

    Text(
        "${currentTime} / ${duration}",
        color = Color.White,
        modifier = Modifier.align(Alignment.BottomStart),
        fontFamily = ThemeRed.fontFamilyPopinsRegular,
        fontSize = 10.sp
    )

    }
}


@Composable
fun UrlImage1(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillWidth,
) {

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
