package com.client.xvideos.screens.item.util

import android.annotation.SuppressLint
import android.util.SparseArray
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.media3.common.FlagSet
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.analytics.AnalyticsListener.EventTime
import com.client.xvideos.screens.item.atom.BottomControls
import io.sanghun.compose.video.RepeatMode
import io.sanghun.compose.video.VideoPlayer
import io.sanghun.compose.video.controller.VideoPlayerControllerConfig
import io.sanghun.compose.video.uri.VideoPlayerMediaItem
import timber.log.Timber
import java.util.concurrent.TimeUnit

@Composable
fun ZoomableVideoPlayer(videoUri: String) {

    var totalDuration by remember { mutableLongStateOf(0L) }
    var currentTime by remember { mutableLongStateOf(0L) }

    var bufferedPercentage by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var playbackState by remember { mutableIntStateOf(0) }

    var playerE: Player? = null

    Column {

        VideoPlayer(
            mediaItems = listOf(
                VideoPlayerMediaItem.NetworkMediaItem(
                    url = videoUri,
                    mediaMetadata = MediaMetadata.Builder().setTitle("Widevine HLS: Example")
                        .build(),
                    mimeType = MimeTypes.APPLICATION_M3U8,
                )
            ),
            handleLifecycle = true,
            autoPlay = true,
            usePlayerController = true,
            enablePip = false,
            handleAudioFocus = true,
            controllerConfig = VideoPlayerControllerConfig(
                showSpeedAndPitchOverlay = false,
                showSubtitleButton = false,
                showCurrentTimeAndTotalTime = true,
                showBufferingProgress = true,
                showForwardIncrementButton = true,
                showBackwardIncrementButton = true,
                showBackTrackButton = false,
                showNextTrackButton = false,
                showRepeatModeButton = false,
                controllerShowTimeMilliSeconds = 1_000,
                controllerAutoShow = true,
                showFullScreenButton = true,
            ),
            volume = 0.0f,  // volume 0.0f to 1.0f
            repeatMode = RepeatMode.NONE,       // or RepeatMode.ALL, RepeatMode.ONE
            onCurrentTimeChanged = { // long type, current player time (millisec)
                Timber.tag("CurrentTime").e(it.toString())
                //currentTime = it
            },
            playerInstance = { // ExoPlayer instance (Experimental)
                addAnalyticsListener(
                    object : AnalyticsListener {

                        @OptIn(UnstableApi::class)
                        override fun onEvents(player: Player, events: AnalyticsListener.Events) {
                            super.onEvents(player, events)
                            totalDuration = player.duration.coerceAtLeast(0L)
                            currentTime = player.currentPosition.coerceAtLeast(0L)
                            bufferedPercentage = player.bufferedPercentage
                            isPlaying = player.isPlaying
                            playbackState = player.playbackState
                            playerE = player
                        }

                    }
                )
            },
            modifier = Modifier
//                    .graphicsLayer(
//                        scaleX = scale,
//                        scaleY = scale,
//                        translationX = offset.x,
//                        translationY = offset.y
//                    )
                .fillMaxWidth()
        )

        BottomControls(
            totalDuration = { totalDuration },
            bufferedPercentage = { bufferedPercentage },
            currentTime = { currentTime },
            modifier = Modifier,
            onSeekChanged = { timeMs: Float ->
                //playerE?.pause()
                currentTime = timeMs.toLong()
                Timber.i("!!! onSeekChanged  ${timeMs.toLong()}")
            },
            onValueChangedFinished = {
                Timber.i("!!! onValueChangedFinished ${it.toLong()}")
                playerE?.seekTo(it.toLong())
                //playerE?.playWhenReady = true
            },

            )

    }


}

@SuppressLint("DefaultLocale")
fun Long.formatMinSec(): String {
    return if (this == 0L) {
        "..."
    } else {
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(this),
            TimeUnit.MILLISECONDS.toSeconds(this) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(this)
                    )
        )
    }
}