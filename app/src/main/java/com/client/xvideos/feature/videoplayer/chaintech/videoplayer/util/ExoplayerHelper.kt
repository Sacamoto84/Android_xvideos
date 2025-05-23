package com.client.xvideos.feature.videoplayer.chaintech.videoplayer.util

import android.content.Context
import android.media.MediaDrm
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.FrameworkMediaDrm
import androidx.media3.exoplayer.drm.LocalMediaDrmCallback
import androidx.media3.exoplayer.drm.UnsupportedDrmException
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host.DrmConfig
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host.MediaPlayerError

@OptIn(UnstableApi::class)
@Composable
fun rememberPlayerView(exoPlayer: ExoPlayer, context: Context): PlayerView {
    val playerView = remember(context) {
        PlayerView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
        }
    }
    val currentPlayer by rememberUpdatedState(exoPlayer)

    LaunchedEffect(currentPlayer) {
        playerView.player = currentPlayer
    }

    DisposableEffect(playerView) {
        onDispose {
            playerView.player = null
        }
    }
    return playerView
}

@OptIn(UnstableApi::class)
@Composable
fun rememberExoPlayerWithLifecycle(
    url: String,
    context: Context,
    isPause: Boolean,
    isLiveStream: Boolean,
    headers: Map<String, String>?,
    drmConfig: DrmConfig?,
    error: (MediaPlayerError) -> Unit,
    selectedQuality: VideoQuality?,
    selectedAudioTrack: AudioTrack?,
    selectedSubtitleTrack: SubtitleTrack?,
    minBufferMs: Int = 2500,
    maxBufferMs: Int = 30000,
    bufferForPlaybackMs: Int = 500,
    bufferForPlaybackAfterRebufferM: Int = 1000,
    onFramerate: (Float) -> Unit = {},

    ): ExoPlayer {
    val lifecycleOwner = LocalLifecycleOwner.current
    val cache = remember(context) { CacheManager.getCache(context) }
    val trackSelector = remember { DefaultTrackSelector(context) }

    val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            minBufferMs,
            maxBufferMs,
            bufferForPlaybackMs,
            bufferForPlaybackAfterRebufferM
        )
        .build()

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .setTrackSelector(trackSelector)
            .setSeekForwardIncrementMs(1000L) // Устанавливаем приращение для перемотки вперед на 1000 мс (1 секунда)
            .setSeekBackIncrementMs(1000L)    // Опционально: Устанавливаем приращение для перемотки назад на 1000 мс
            .build().apply {
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                repeatMode = Player.REPEAT_MODE_OFF
                setHandleAudioBecomingNoisy(true)
            }
    }

    LaunchedEffect(selectedQuality) {
        applyQualitySelection(trackSelector, selectedQuality)
    }
    LaunchedEffect(selectedAudioTrack) {
        applyAudioTrackSelection(trackSelector, selectedAudioTrack)
    }

    LaunchedEffect(selectedSubtitleTrack) {
        applySubTitleTrackSelection(trackSelector, selectedSubtitleTrack)
    }

    LaunchedEffect(url) {
        try {
            val mediaItem = MediaItem.fromUri(url.toUri())

            val mediaSource = when {
                drmConfig != null -> createHlsMediaSourceWithDrm(mediaItem, headers, drmConfig)
                isLiveStream || url.endsWith(".m3u8", ignoreCase = true) -> createHlsMediaSource(
                    mediaItem,
                    headers
                )

                else -> createProgressiveMediaSource(mediaItem, cache, context, headers)
            }

            exoPlayer.apply {
                stop()
                clearMediaItems()
                setMediaSource(mediaSource)
                prepare()
                seekTo(0, 0)
                onFramerate(videoFormat?.frameRate ?: -1f)
            }
        } catch (e: Exception) {
            error(MediaPlayerError.PlaybackError(e.message ?: "Failed to load media"))
        }
    }

    var appInBackground by remember {
        mutableStateOf(false)
    }

    DisposableEffect(key1 = lifecycleOwner, appInBackground) {
        val lifecycleObserver =
            getExoPlayerLifecycleObserver(exoPlayer, isPause, appInBackground) {
                appInBackground = it
            }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
    return exoPlayer
}

@OptIn(UnstableApi::class)
private fun applyQualitySelection(
    trackSelector: DefaultTrackSelector,
    selectedQuality: VideoQuality?
) {
    trackSelector.setParameters(
        trackSelector.buildUponParameters().apply {
            selectedQuality?.let {
                setMaxVideoBitrate(it.bitrate.toInt())
                setMinVideoBitrate(it.bitrate.toInt())
            } ?: setMaxVideoBitrate(Int.MAX_VALUE)
        }
    )
}

@OptIn(UnstableApi::class)
private fun applyAudioTrackSelection(trackSelector: DefaultTrackSelector, audioTrack: AudioTrack?) {
    trackSelector.setParameters(
        trackSelector.buildUponParameters()
            .setPreferredAudioLanguage(audioTrack?.language)
    )
}

@OptIn(UnstableApi::class)
private fun applySubTitleTrackSelection(
    trackSelector: DefaultTrackSelector,
    subtitleTrack: SubtitleTrack?
) {
    trackSelector.setParameters(
        trackSelector.buildUponParameters().apply {
            if (subtitleTrack != null) {
                setPreferredTextLanguage(subtitleTrack.language)
                setRendererDisabled(C.TRACK_TYPE_TEXT, false)
                setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
            } else {
                setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                setRendererDisabled(C.TRACK_TYPE_TEXT, true)
                setPreferredTextLanguage(null)
                setSelectUndeterminedTextLanguage(false)
            }
        }
    )
}


@OptIn(UnstableApi::class)
private fun createHlsMediaSource(mediaItem: MediaItem, headers: Map<String, String>?): MediaSource {
    val headersMap = headers ?: emptyMap()
    val dataSourceFactory = DefaultHttpDataSource.Factory()
        .setAllowCrossProtocolRedirects(true)
        .setConnectTimeoutMs(15_000)
        .setReadTimeoutMs(15_000)
        .setDefaultRequestProperties(headersMap)

    return HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
}

@OptIn(UnstableApi::class)
private fun createProgressiveMediaSource(
    mediaItem: MediaItem,
    cache: Cache,
    context: Context,
    headers: Map<String, String>?
): MediaSource {
    val headersMap = headers ?: emptyMap()
    val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        .setAllowCrossProtocolRedirects(true)
        .setConnectTimeoutMs(15_000)
        .setReadTimeoutMs(15_000)
        .setDefaultRequestProperties(headersMap)

    val dataSourceFactory = CacheDataSource.Factory()
        .setCache(cache)
        .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context, httpDataSourceFactory))
        .setCacheWriteDataSinkFactory(null)
        .setFlags(
            CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR or
                    CacheDataSource.FLAG_BLOCK_ON_CACHE or
                    CacheDataSource.FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS
        )

    return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
}

@OptIn(UnstableApi::class)
private fun createHlsMediaSourceWithDrm(
    mediaItem: MediaItem,
    headers: Map<String, String>?,
    drmConfig: DrmConfig
): MediaSource {
    val headersMap = headers ?: emptyMap()
    val dataSourceFactory = DefaultHttpDataSource.Factory()
        .setAllowCrossProtocolRedirects(true)
        .setConnectTimeoutMs(15_000)
        .setReadTimeoutMs(15_000)
        .setDefaultRequestProperties(headersMap)

    val drmSessionManager = try {
        DefaultDrmSessionManager.Builder()
            .setUuidAndExoMediaDrmProvider(C.CLEARKEY_UUID) { FrameworkMediaDrm.newInstance(C.CLEARKEY_UUID) }
            .build(LocalMediaDrmCallback(VideoUtils.createDrmJson(drmConfig)))
    } catch (e: UnsupportedDrmException) {
        throw RuntimeException("Unsupported DRM scheme: ${e.message}", e)
    } catch (e: MediaDrm.MediaDrmStateException) {
        throw RuntimeException("DRM state issue: ${e.message}", e)
    } catch (e: Exception) {
        throw RuntimeException("Failed to create DRM session manager: ${e.message}", e)
    }

    return HlsMediaSource.Factory(dataSourceFactory)
        .setDrmSessionManagerProvider { drmSessionManager }
        .createMediaSource(mediaItem)
}