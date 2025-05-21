package com.client.xvideos.feature.videoplayer.chaintech.videoplayer.util

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host.MediaPlayerError
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.PlayerSpeed
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.util.createPlayerListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.concurrent.TimeUnit

//@Composable
//fun CMPAudioPlayer(
//    modifier: Modifier,
//    url: String,
//    isPause: Boolean,
//    totalTime: (Int) -> Unit,
//    currentTime: (Float) -> Unit,
//    isSliding: Boolean,
//    seekToTime: Float?,
//    loop: Boolean,
//    loadingState: (Boolean) -> Unit,
//    speed: PlayerSpeed,
//    volume: Float,
//    didEndAudio: () -> Unit,
//    error: (MediaPlayerError) -> Unit
//) {
//    val context = LocalContext.current
//    val exoPlayer = rememberExoPlayer(url, context, error)
//
//    // Update repeat mode based on isRepeat state
//    LaunchedEffect(loop) {
//        exoPlayer.repeatMode = if (loop) {
//            Player.REPEAT_MODE_ONE
//        } else {
//            Player.REPEAT_MODE_OFF
//        }
//    }
//
//    // Update current time every second
//    LaunchedEffect(exoPlayer) {
//        while (isActive) {
//            currentTime( TimeUnit.MILLISECONDS.toSeconds(exoPlayer.currentPosition).coerceAtLeast(0L).toFloat() )
//            delay(200)
//        }
//    }
//
//    // Manage player listener and lifecycle
//    DisposableEffect(key1 = exoPlayer) {
//
//        val listener = createPlayerListener(
//            totalTime,
//            currentTime,
//            loadingState,
//            didEndAudio,
//            isSliding,
//            exoPlayer,
//            error
//        )
//
//        exoPlayer.addListener(listener)
//
//        onDispose {
//            exoPlayer.removeListener(listener)
//            exoPlayer.release()
//        }
//    }
//
//    // Control playback based on isPause state
//    LaunchedEffect(isPause) {
//        exoPlayer.playWhenReady = !isPause
//    }
//
//    LaunchedEffect(volume) {
//        exoPlayer.volume = volume
//    }
//
//    // Seek to slider time if provided
//    seekToTime?.let { time ->
//        LaunchedEffect(time) {
//            exoPlayer.seekTo((time * 1000).toLong())
//        }
//    }
//
//    LaunchedEffect(speed) {
//        exoPlayer.setPlaybackSpeed(speed.toFloat())
//    }
//}


private fun PlayerSpeed.toFloat(): Float {
    return when (this) {
        PlayerSpeed.X0_5 -> 0.5f
        PlayerSpeed.X1 -> 1f
        PlayerSpeed.X1_5 -> 1.5f
        PlayerSpeed.X2 -> 2f
    }
}




@OptIn(UnstableApi::class)
@Composable
private fun rememberExoPlayer(
    url: String,
    context: Context,
    error: (MediaPlayerError) -> Unit
): ExoPlayer {
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            setHandleAudioBecomingNoisy(true)
        }
    }

    // Prepare media source when URL changes
    LaunchedEffect(url) {
        try {
            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            val dataSourceFactory = DefaultDataSource.Factory(context)
            val mediaSource =
                ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()
            exoPlayer.seekTo(0)
        } catch (e: Exception) {
            error(MediaPlayerError.PlaybackError(e.message ?: "Failed to load media"))
        }
    }

    return exoPlayer
}

private fun createPlayerListener(
    totalTime: (Int) -> Unit,
    currentTime: (Int) -> Unit,
    loadingState: (Boolean) -> Unit,
    didEndAudio: () -> Unit,
    isSliding: Boolean,
    exoPlayer: ExoPlayer,
    error: (MediaPlayerError) -> Unit
): Player.Listener {

    return object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            if (!isSliding) {
                totalTime(
                    TimeUnit.MILLISECONDS.toSeconds(player.duration).coerceAtLeast(0L).toInt()
                )
                currentTime(
                    TimeUnit.MILLISECONDS.toSeconds(player.currentPosition).coerceAtLeast(0L)
                        .toInt()
                )
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> loadingState(true)
                Player.STATE_READY -> loadingState(false)
                Player.STATE_ENDED -> {
                    exoPlayer.seekTo(0)
                    didEndAudio()
                }

                Player.STATE_IDLE -> { /* No-op */
                }
            }
        }
        override fun onPlayerError(error: PlaybackException) {
            error(MediaPlayerError.PlaybackError(error.message ?: "Unknown playback error"))
        }
    }
}
