package com.client.xvideos.feature.videoplayer.chaintech.videoplayer.util

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host.DrmConfig
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host.MediaPlayerError
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.PlayerSpeed
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.ScreenResize
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.concurrent.TimeUnit

@OptIn(UnstableApi::class)
@Composable
fun CMPPlayer(
    modifier: Modifier,
    url: String,
    isPause: Boolean,
    totalTime: (Int) -> Unit,
    currentTime: (Float) -> Unit,
    isSliding: Boolean,
    seekToTime: Float?,
    speed: PlayerSpeed,
    size: ScreenResize,
    bufferCallback: (Boolean) -> Unit,
    didEndVideo: () -> Unit,
    loop: Boolean,
    volume: Float,
    isLiveStream: Boolean,
    error: (MediaPlayerError) -> Unit,
    headers: Map<String, String>?,
    drmConfig: DrmConfig?,
    selectedQuality: VideoQuality?,
    selectedAudioTrack: AudioTrack?,
    selectedSubTitle: SubtitleTrack?
) {
    val context = LocalContext.current
    val exoPlayer = rememberExoPlayerWithLifecycle(
        url,
        context,
        isPause,
        isLiveStream,
        headers,
        drmConfig,
        error,
        selectedQuality,
        selectedAudioTrack,
        selectedSubTitle
    )
    val playerView = rememberPlayerView(exoPlayer, context)

    var isBuffering by remember { mutableStateOf(false) }

    // Notify buffer state changes
    LaunchedEffect(isBuffering) {
        bufferCallback(isBuffering)
    }

    // Update current time every second
    LaunchedEffect(exoPlayer) {
        while (isActive) {
            currentTime(
                //TimeUnit.MILLISECONDS.toSeconds(exoPlayer.currentPosition).coerceAtLeast(0L).toFloat()
                (exoPlayer.currentPosition/1000f).coerceAtLeast(0f)
            )
            delay(16) // Delay for 1 second
            //println("!!! ${exoPlayer.currentPosition}")
            //println("!!! ${TimeUnit.MILLISECONDS.toSeconds(exoPlayer.currentPosition/1000).coerceAtLeast(0L).toFloat()}")
        }
    }

    // Keep screen on while the player view is active
    LaunchedEffect(playerView) {
        playerView.keepScreenOn = true
    }

    Box {
        AndroidView(
            factory = { playerView },
            modifier = modifier,
            update = {
                exoPlayer.playWhenReady = !isPause
                exoPlayer.volume = volume
                seekToTime?.let { exoPlayer.seekTo((it * 1000).toLong()) }
                exoPlayer.setPlaybackSpeed(speed.toFloat())
                playerView.resizeMode = when (size) {
                    ScreenResize.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                    ScreenResize.FILL -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            }
        )

        // Manage player listener and lifecycle
        DisposableEffect(key1 = exoPlayer) {
            val listener = createPlayerListener(
                isSliding,
                totalTime,
                currentTime,
                loadingState = { isBuffering = it },
                didEndVideo,
                loop,
                exoPlayer,
                error
            )

            exoPlayer.addListener(listener)

            onDispose {
                exoPlayer.removeListener(listener)
                exoPlayer.release()
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.release()
                playerView.keepScreenOn = false
                CacheManager.release()
            }
        }
    }
}

private fun PlayerSpeed.toFloat(): Float {
    return when (this) {
        PlayerSpeed.X0_5 -> 0.5f
        PlayerSpeed.X1 -> 1f
        PlayerSpeed.X1_5 -> 1.5f
        PlayerSpeed.X2 -> 2f
    }
}


internal fun createPlayerListener(
    isSliding: Boolean,
    totalTime: (Int) -> Unit,
    currentTime: (Float) -> Unit,
    loadingState: (Boolean) -> Unit,
    didEndVideo: () -> Unit,
    loop: Boolean,
    exoPlayer: ExoPlayer,
    error: (MediaPlayerError) -> Unit
): Player.Listener {

    return object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            if (!isSliding) {
                totalTime(
                    TimeUnit.MILLISECONDS.toSeconds(player.duration).coerceAtLeast(0L).toInt()
                )
                currentTime( TimeUnit.MILLISECONDS.toSeconds(player.currentPosition).coerceAtLeast(0L).toFloat() )
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    loadingState(true)
                }

                Player.STATE_READY -> {
                    loadingState(false)
                }

                Player.STATE_ENDED -> {
                    loadingState(false)
                    didEndVideo()
                    exoPlayer.seekTo(0)
                    if (loop) exoPlayer.play()
                }

                Player.STATE_IDLE -> {
                    loadingState(false)
                }
            }
        }
        override fun onPlayerError(error: PlaybackException) {
            error(MediaPlayerError.PlaybackError(error.message ?: "Unknown playback error"))
        }
    }
}