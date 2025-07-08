package com.client.common.feature.videoplayer.util

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.ScaleAndRotateTransformation
import androidx.media3.ui.AspectRatioFrameLayout
import com.client.common.feature.videoplayer.host.DrmConfig
import com.client.common.feature.videoplayer.host.MediaPlayerError
import com.client.common.feature.videoplayer.model.PlayerSpeed
import com.client.common.feature.videoplayer.model.ScreenResize
import com.client.common.feature.videoplayer.rememberExoPlayerWithLifecycle
import com.client.common.feature.videoplayer.rememberPlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import timber.log.Timber

@OptIn(UnstableApi::class)
@Composable
fun CMPPlayer2(
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
    //isForward : Boolean = false,
    //isBack: Boolean = false,
    autoRotate: Boolean // можно менять как нужно
) {
    val context = LocalContext.current

    var exoPlayer = rememberExoPlayerWithLifecycle(
        url,
        context,
        isPause,
        isLiveStream,
        headers,
        drmConfig,
        error,
        selectedQuality,
        minBufferMs = 50000,
        maxBufferMs = 150000,
        bufferForPlaybackMs = 50,
        bufferForPlaybackAfterRebufferM = 100,
    )

    var currentRotate by remember { mutableFloatStateOf(0f) }

    //LaunchedEffect(exoPlayer) { onExoPlayer(exoPlayer) }

    val playerView = rememberPlayerView(exoPlayer, context)

    var isBuffering by remember { mutableStateOf(false) }

    // Notify buffer state changes
    LaunchedEffect(isBuffering) {
        bufferCallback(isBuffering)
    }

    LaunchedEffect(exoPlayer) {
        flow {
            while (isActive) {
                emit((exoPlayer.currentPosition / 1000f).coerceAtLeast(0f))
                delay(50)
            }
        }.collectLatest {
            currentTime(it)
        }

    }

    LaunchedEffect(exoPlayer) {
        while (isActive) {

            if (exoPlayer.videoFormat != null) {
                currentRotate =
                    if (exoPlayer.videoFormat!!.height >= exoPlayer.videoFormat!!.width) 0f else -90f
                Timber.i("@@@! H:${exoPlayer.videoFormat?.height}  W:${exoPlayer.videoFormat?.width} $exoPlayer")
                break
            } else {
                delay(100)
            }
        }
    }

//    LaunchedEffect(currentRotate) {
//        Timber.i("@@@! 777 LaunchedEffect currentRotate:$currentRotate autoRotate:$autoRotate")
//        if (autoRotate) {
//            val rotateEffect =
//                ScaleAndRotateTransformation.Builder().setRotationDegrees(currentRotate).build()
//            exoPlayer.setVideoEffects(listOf(rotateEffect))
//        }
//    }

    LaunchedEffect(autoRotate) {
        Timber.i("@@@! 888 LaunchedEffect currentRotate:$currentRotate autoRotate:$autoRotate")
        //if (autoRotate) {

        val rotateEffect =
            ScaleAndRotateTransformation.Builder().setRotationDegrees(if (autoRotate) -90f else 0f)
                .build()
        exoPlayer.setVideoEffects(listOf(rotateEffect))
        //}
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
                currentTime = {},
                loadingState = { isBuffering = it },
                didEndVideo,
                loop,
                exoPlayer,
                error,
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
