package com.client.xvideos.screens_red.profile.atom

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import chaintech.videoplayer.host.MediaPlayerEvent
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.ScreenResize
import chaintech.videoplayer.model.VideoPlayerConfig
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.client.xvideos.screens_red.ThemeRed
import com.client.xvideos.screens_red.profile.PlayerControls
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class, DelicateCoroutinesApi::class, DelicateCoroutinesApi::class,
    DelicateCoroutinesApi::class, DelicateCoroutinesApi::class, DelicateCoroutinesApi::class
)
@Composable
fun RedUrlVideoLiteChaintech(
    url: String,
    thumnailUrl: String = "",
    play: Boolean,
    onChangeTime: (Pair<Int, Int>) -> Unit,
    onLongClick: () -> Unit = {},
    isMute: Boolean = false,
    onPlayerControlsReady: (PlayerControls) -> Unit = {},
    timeA: Int = 0,
    timeB: Int = 0,
    enableAB: Boolean = false,
    onPaused: (Boolean) -> Unit = {}
    ) {

    var isBuffering by remember { mutableStateOf(false) }
    var currentTime by remember { mutableIntStateOf(0) }
    var duration by remember { mutableIntStateOf(0) }

    val playerHost = remember {
        MediaPlayerHost(
            mediaUrl = url,
            isPaused = true
        )
    }

    LaunchedEffect(url) {

        //GlobalScope.launch(Dispatchers.Main) {
            delay(100)
            println("!!! Смена Url $url")
            playerHost.seekTo(0f)
        //}

    }

    LaunchedEffect(play) {
        if (play)
            playerHost.play()
        else
            playerHost.pause()
    }

    LaunchedEffect(playerHost, onPlayerControlsReady) {
        val controls = object : PlayerControls {

            override fun forward(seconds: Int) {
                val currentPosition = (currentTime + seconds).coerceAtMost(duration).toFloat()
                playerHost.seekTo(currentPosition)
            }

            override fun rewind(seconds: Int) {
                val currentPosition = (currentTime - seconds).coerceAtLeast(0).toFloat()
                playerHost.seekTo(currentPosition)
            }

            override fun seekTo(positionSeconds: Int) {
                playerHost.seekTo(
                    positionSeconds.coerceIn(0, duration).toFloat()
                ) // Уточните, принимает ли Long или Int, секунды или мс
            }

            override fun stop(positionSeconds: Int) {
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




    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        LaunchedEffect(isMute) {
            if (isMute)
                playerHost.mute()
            else
                playerHost.unmute()
        }

        onChangeTime(currentTime to duration)

        playerHost.onEvent = { event ->
            when (event) {
                is MediaPlayerEvent.MuteChange -> {
                    println("!!! Mute status changed: ${event.isMuted}")
                }

                is MediaPlayerEvent.PauseChange -> {
                    println("!!!Pause status changed: ${event.isPaused}")
                    onPaused(event.isPaused)
                }

                is MediaPlayerEvent.BufferChange -> {
                    println("!!!Buffering status: ${event.isBuffering}")
                    isBuffering = event.isBuffering
                }

                is MediaPlayerEvent.CurrentTimeChange -> {
                    println("!!!Current playback time: ${event.currentTime}s")
                    currentTime = event.currentTime.toInt()
                    if (enableAB && currentTime >= timeB) {
                        println("!!! playerHost.seekTo(${timeA}) ")

                        GlobalScope.launch {
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
                playerHost.seekTo(0f)
            }
        }

        playerHost.setVideoFitMode(ScreenResize.FIT)

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

//                    UrlImage1(
//                        url = thumnailUrl,
//                        contentScale = ContentScale.Fit,
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(Color.Transparent)
//                        //.zIndex(1f) // убедиться, что над видео
//                    )

                }
            )
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
