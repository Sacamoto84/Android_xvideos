package com.redgifs.common.video.player_row_mini.atom

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.client.common.videoplayer.host.MediaPlayerEvent
import com.client.common.videoplayer.host.MediaPlayerHost
import com.client.common.videoplayer.model.ScreenResize
import com.redgifs.common.video.CanvasTimeDurationLine1
import com.redgifs.common.video.player_with_menu.atom.StaticPlayer


/**
 * Превьюшка для режима в два столбика
 */
@Composable
fun Red_Video_Lite_Row2(
    url: String,
    play: Boolean = true,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    poster : (Boolean)->Unit
) {

    var time by remember { mutableFloatStateOf(0f) }
    var duration by remember { mutableIntStateOf(0) }

    //if (BuildConfig.DEBUG) { SideEffect { Timber.i("@@@ Red_Video_Lite_2Rrow() play = $play, url = $url time = $time, duration = $duration") } }

    val playerHost = remember { MediaPlayerHost(mediaUrl = url, isPaused = false, isMuted = true) }

    LaunchedEffect(play) { if (play) playerHost.play() else playerHost.pause() }
    LaunchedEffect(Unit) { playerHost.videoFitMode = ScreenResize.FIT; playerHost.mute() }

    LaunchedEffect(Unit) {
        playerHost.onEvent = { event ->
            when (event) {
                is MediaPlayerEvent.CurrentTimeChange -> { time = event.currentTime }
                is MediaPlayerEvent.TotalTimeChange -> { duration = event.totalTime }
                else -> {}
            }
        }
    }

    LaunchedEffect(playerHost.poster) { poster(playerHost.poster) }

    Box(modifier = Modifier.fillMaxWidth()) {

        StaticPlayer(playerHost, false)

        Box(modifier = Modifier.padding(bottom = 48.dp).fillMaxSize().combinedClickable(onClick = onClick, onLongClick = onLongClick))

        if (!playerHost.poster) {
            Box(
                Modifier.fillMaxWidth().align(Alignment.BottomEnd),
                contentAlignment = Alignment.BottomCenter
            ) {
                CanvasTimeDurationLine1(
                    time,
                    duration,
                    timeA = 0f,
                    timeB = 0f,
                    timeABEnable = false,
                    visibleAB = false,
                    play = play,
                    onSeek = { playerHost.seekTo(it) },
                    onSeekFinished = {},
                    modifier = Modifier.padding(start = 2.dp, end = 2.dp).fillMaxWidth().offset(y = 5.dp),
                    isVisibleTime = true,
                    isVisibleStep = false
                )
            }
        }

    }

}
