package com.client.xvideos.common.videoplayer

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.client.xvideos.common.diagnostics.AppDiagnostics
import com.client.xvideos.common.videoplayer.host.DrmConfig
import com.client.xvideos.common.videoplayer.host.MediaPlayerError
import com.client.xvideos.common.videoplayer.util.CacheManager
import com.client.xvideos.common.videoplayer.util.VideoQuality
import com.client.xvideos.common.videoplayer.util.applyQualitySelection
import com.client.xvideos.common.videoplayer.util.createHlsMediaSource
import com.client.xvideos.common.videoplayer.util.createHlsMediaSourceWithDrm
import com.client.xvideos.common.videoplayer.util.createProgressiveMediaSource
import com.client.xvideos.common.videoplayer.util.createProgressiveMediaSourceWithoutDiskCache
import com.client.xvideos.common.videoplayer.util.getExoPlayerLifecycleObserver
import com.client.xvideos.common.videoplayer.util.shouldUseDiskCacheForMedia

@OptIn(UnstableApi::class)
@Composable
fun rememberExoPlayerWithLifecycle(
    url: String,
    context: Context,
    isPause: Boolean,
    isLiveStream: Boolean,
    isLooping: Boolean,
    headers: Map<String, String>?,
    drmConfig: DrmConfig?,
    error: (MediaPlayerError) -> Unit,
    selectedQuality: VideoQuality?,
    minBufferMs: Int = 2500,
    maxBufferMs: Int = 30000,
    bufferForPlaybackMs: Int = 500,
    bufferForPlaybackAfterRebufferM: Int = 1000,
    useDiskCache: Boolean = true,
): ExoPlayer {
    val lifecycleOwner = LocalLifecycleOwner.current
    val trackSelector = remember { DefaultTrackSelector(context) }
    val useDiskCacheForMedia = shouldUseDiskCacheForMedia(url, isLiveStream, drmConfig, useDiskCache)
    val cache = remember(context, url, useDiskCacheForMedia) {
        if (useDiskCacheForMedia) CacheManager.getCache(context) else null
    }

    val loadControl = DefaultLoadControl.Builder().setBufferDurationsMs(minBufferMs, maxBufferMs, bufferForPlaybackMs, bufferForPlaybackAfterRebufferM).build()

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            //.setTrackSelector(trackSelector)
            .setSeekForwardIncrementMs(1000L) // Устанавливаем приращение для перемотки вперед на 1000 мс (1 секунда)
            .setSeekBackIncrementMs(1000L)    // Опционально: Устанавливаем приращение для перемотки назад на 1000 мс
            .build().apply {
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                setHandleAudioBecomingNoisy(true)
            }
    }

    LaunchedEffect(isLooping) {
        exoPlayer.repeatMode = if (isLooping) {
            Player.REPEAT_MODE_ONE
        } else {
            Player.REPEAT_MODE_OFF
        }
    }

    LaunchedEffect(selectedQuality) {
        applyQualitySelection(trackSelector, selectedQuality)
    }

    DisposableEffect(url, useDiskCacheForMedia) {
        onDispose {
            if (useDiskCacheForMedia) {
                CacheManager.release()
            }
        }
    }

    LaunchedEffect(url, useDiskCacheForMedia) {
        try {
            val mediaItem = MediaItem.fromUri(url.toUri())

            val mediaSource = when {
                drmConfig != null -> createHlsMediaSourceWithDrm(mediaItem, headers, drmConfig)
                isLiveStream || url.endsWith(".m3u8", ignoreCase = true) -> createHlsMediaSource(
                    mediaItem,
                    headers
                )
                useDiskCacheForMedia && cache != null -> createProgressiveMediaSource(mediaItem, cache, context, headers)
                else -> createProgressiveMediaSourceWithoutDiskCache(mediaItem, context, headers)
            }

            exoPlayer.apply {
                stop()
                clearMediaItems()
                setMediaSource(mediaSource)
                prepare()
                seekTo(0, 0)
            }

        } catch (e: Exception) {
            AppDiagnostics.recordPlayerError(
                source = "Media3 prepare",
                url = url,
                message = e.message ?: "Failed to load media"
            )
            error(MediaPlayerError.PlaybackError(e.message ?: "Failed to load media"))
        }
    }

    var appInBackground by remember { mutableStateOf(false) }

    DisposableEffect(key1 = lifecycleOwner, appInBackground) {
        val lifecycleObserver = getExoPlayerLifecycleObserver(exoPlayer, isPause, appInBackground) {
            appInBackground = it
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose { lifecycleOwner.lifecycle.removeObserver(lifecycleObserver) }
    }
    return exoPlayer
}
