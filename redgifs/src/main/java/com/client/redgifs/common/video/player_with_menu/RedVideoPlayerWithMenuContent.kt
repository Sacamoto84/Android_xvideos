package com.client.redgifs.common.video.player_with_menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.redgifs.BuildConfig
import com.client.redgifs.screens.profile.PlayerControls
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun RedVideoPlayerWithMenuContent(
    url: String,
    play: Boolean,
    onChangeTime: (Pair<Float, Int>) -> Unit,
    isMute: Boolean = false,
    onPlayerControlsReady: (PlayerControls) -> Unit = {},
    timeA: Float = 0f,
    timeB: Float = 0f,
    enableAB: Boolean,
    onClick: () -> Unit = {},
    menuContent: @Composable () -> Unit = {},
    menuContentWidth: Dp = 192.dp,
    menuDefaultOpen: Boolean = false,
    menuOpenChanged: (Boolean) -> Unit = {},
    autoRotate: Boolean,
    isCurrentPage : Boolean
) {

    if (BuildConfig.DEBUG) { SideEffect {
        Timber.i("@@@ RedUrlVideoLiteChaintech() play:$play isMute:$isMute") } }

    val scope = rememberCoroutineScope()

    var currentTime by remember { mutableFloatStateOf(0f) }
    var duration by remember { mutableIntStateOf(0) }

    LaunchedEffect(isCurrentPage) { snapshotFlow { currentTime to duration }.distinctUntilChanged().collectLatest {if (isCurrentPage) onChangeTime(it)}}

    val playerHost = remember { MediaPlayerHost(mediaUrl = url, isPaused = true) }

    LaunchedEffect(url) {
        delay(100)
        println("!!! Смена Url $url")
        playerHost.seekTo(0f)
    }

    LaunchedEffect(enableAB) { if (enableAB) { playerHost.seekTo(timeA.toFloat()) } }
    LaunchedEffect(play) { if (play) playerHost.play() else playerHost.pause() }
    LaunchedEffect(isMute) { if (isMute) playerHost.mute() else playerHost.unmute() }

    LaunchedEffect(playerHost, enableAB, timeA, timeB) {
        playerHost.setVideoFitMode(ScreenResize.FIT)
        playerHost.onEvent = { event ->
            when (event) {
                is MediaPlayerEvent.CurrentTimeChange -> {
                    //println("!!!Current playback time: ${event.currentTime}s")
                    currentTime = event.currentTime
                    Timber.i("!!! Current playback time: ${event.currentTime}s enableAB:$enableAB timeA:$timeA timeB:$timeB")
                    if (enableAB && currentTime >= timeB) {
                        println("!!! playerHost.seekTo(${timeA}) ")
                        scope.launch { playerHost.seekTo(timeA.toFloat()) }
                    }
                }
                is MediaPlayerEvent.TotalTimeChange -> { println("!!!Video duration updated: ${event.totalTime}s"); duration = event.totalTime.toInt() }
                else -> {}
            }
        }
    }

    LaunchedEffect(playerHost, onPlayerControlsReady) {

        val controls = object : PlayerControls {
            override fun forward(seconds: Float) { val currentPosition = (currentTime + seconds).coerceAtMost(duration.toFloat()).toFloat(); playerHost.seekTo(currentPosition) }
            override fun rewind(seconds: Float) { val currentPosition = (currentTime - seconds).coerceAtLeast(0f).toFloat(); playerHost.seekTo(currentPosition) }
            override fun seekTo(positionSeconds: Float) { playerHost.seekTo( positionSeconds.coerceIn(0f, duration.toFloat()).toFloat()) }
            override fun stop()  { playerHost.pause(); playerHost.seekTo(0f) }
            override fun pause() { playerHost.pause() }
            override fun play()  { playerHost.play() }
        }
        onPlayerControlsReady(controls)

    }

    VideoPlayerWithMenuContent(
        onClick = onClick,
        modifier = Modifier.fillMaxSize(),
        playerHost = playerHost,

        menuContent = menuContent,
        menuContentWidth = menuContentWidth,
        menuDefaultOpen = menuDefaultOpen,
        menuOpenChanged = menuOpenChanged,
        autoRotate = autoRotate
    )

}


@Composable
fun CurrentTimeText(
    currentTime: Float,
    duration: Int
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            "${currentTime} / ${duration}",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(8.dp),
            fontFamily = ThemeRed.fontFamilyPopinsRegular,
            fontSize = 10.sp
        )
    }
}


// Вспомогательная функция для форматирования времени из секунд в MM:SS
private fun formatDuration(totalSeconds: Int): String {
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
