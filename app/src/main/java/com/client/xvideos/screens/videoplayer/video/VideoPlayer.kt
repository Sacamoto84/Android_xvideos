/*
 * Copyright 2023 Dora Lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.client.xvideos.screens.videoplayer.video

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.PorterDuff
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MOVIE
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.util.RepeatModeUtil.REPEAT_TOGGLE_MODE_ALL
import androidx.media3.common.util.RepeatModeUtil.REPEAT_TOGGLE_MODE_NONE
import androidx.media3.common.util.RepeatModeUtil.REPEAT_TOGGLE_MODE_ONE
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerView
import androidx.media3.ui.R
import com.client.xvideos.screens.videoplayer.ScreenVideoPlayerSM
import com.client.xvideos.screens.videoplayer.video.cache.VideoPlayerCacheManager
import com.client.xvideos.screens.videoplayer.video.controller.VideoPlayerControllerConfig
import com.client.xvideos.screens.videoplayer.video.controller.applyToExoPlayerView
import com.client.xvideos.screens.videoplayer.video.uri.VideoPlayerMediaItem
import com.client.xvideos.screens.videoplayer.video.uri.toUri
import com.client.xvideos.screens.videoplayer.video.util.findActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

/**
 * [VideoPlayer] is UI component that can play video in Jetpack Compose. It works based on ExoPlayer.
 * You can play local (e.g. asset files, resource files) files and all video files in the network environment.
 * For all video formats supported by the [VideoPlayer] component, see the ExoPlayer link below.
 *
 * If you rotate the screen, the default action is to reset the player state.
 * To prevent this happening, put the following options in the `android:configChanges` option of your app's AndroidManifest.xml to keep the settings.
 * ```
 * keyboard|keyboardHidden|orientation|screenSize|screenLayout|smallestScreenSize|uiMode
 * ```
 *
 * This component is linked with Compose [androidx.compose.runtime.DisposableEffect].
 * This means that it move out of the Composable Scope, the ExoPlayer instance will automatically be destroyed as well.
 *
 * @see <a href="https://exoplayer.dev/supported-formats.html">Exoplayer Support Formats</a>
 *
 * @param modifier Modifier to apply to this layout node.
 * @param mediaItems [VideoPlayerMediaItem] to be played by the video player. The reason for receiving media items as an array is to configure multi-track. If it's a single track, provide a single list (e.g. listOf(mediaItem)).
 * @param handleLifecycle Sets whether to automatically play/stop the player according to the activity lifecycle. Default is true.
 * @param autoPlay Autoplay when media item prepared. Default is true.
 * @param usePlayerController Using player controller. Default is true.
 * @param controllerConfig Player controller config. You can customize the Video Player Controller UI.
 * @param seekBeforeMilliSeconds The seek back increment, in milliseconds. Default is 10sec (10000ms). Read-only props (Changes in values do not take effect.)
 * @param seekAfterMilliSeconds The seek forward increment, in milliseconds. Default is 10sec (10000ms). Read-only props (Changes in values do not take effect.)
 * @param repeatMode Sets the content repeat mode.
 * @param volume Sets thie player volume. It's possible from 0.0 to 1.0.
 * @param onCurrentTimeChanged A callback that returned once every second for player current time when the player is playing.
 * @param fullScreenSecurePolicy Windows security settings to apply when full screen. Default is off. (For example, avoid screenshots that are not DRM-applied.)
 * @param onFullScreenEnter A callback that occurs when the player is full screen. (The [VideoPlayerControllerConfig.showFullScreenButton] must be true to trigger a callback.)
 * @param onFullScreenExit A callback that occurs when the full screen is turned off. (The [VideoPlayerControllerConfig.showFullScreenButton] must be true to trigger a callback.)
 * @param enablePip Enable PIP (Picture-in-Picture).
 * @param enablePipWhenBackPressed With [enablePip] is `true`, set whether to enable PIP mode even when you press Back. Default is false.
 * @param handleAudioFocus Set whether to handle the video playback control automatically when it is playing in PIP mode and media is played in another app. Default is true.
 * @param playerBuilder Return exoplayer builder. This instance allows you to customise the ExoPlayer while in the Building process. Used to add various components like other RenderFactories.
 * @param playerInstance Return exoplayer instance. This instance allows you to add [androidx.media3.exoplayer.analytics.AnalyticsListener] to receive various events from the player.
 */
@SuppressLint("SourceLockedOrientationActivity", "UnsafeOptInUsageError")
@Composable
fun VideoPlayer(
    vm : ScreenVideoPlayerSM,
    modifier: Modifier = Modifier,
    mediaItems: List<VideoPlayerMediaItem>,
    handleLifecycle: Boolean = true,
    autoPlay: Boolean = true,
    usePlayerController: Boolean = true,
    controllerConfig: VideoPlayerControllerConfig = VideoPlayerControllerConfig.Default,
    seekBeforeMilliSeconds: Long = 10000L,
    seekAfterMilliSeconds: Long = 10000L,
    repeatMode: RepeatMode = RepeatMode.NONE,
    resizeMode: ResizeMode = ResizeMode.FIT,
    @FloatRange(from = 0.0, to = 1.0) volume: Float = 1f,
    onCurrentTimeChanged: (Long) -> Unit = {},
    fullScreenSecurePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit,
    onFullScreenEnter: () -> Unit = {},
    onFullScreenExit: () -> Unit = {},
    defaultFullScreeen: Boolean = false,
    handleAudioFocus: Boolean = true,
    playerBuilder: ExoPlayer.Builder.() -> ExoPlayer.Builder = { this },
    playerInstance: ExoPlayer.() -> Unit = {},
    trackSelector: DefaultTrackSelector,
) {
    val context = LocalContext.current
    var currentTime by remember { mutableLongStateOf(0L) }

    var mediaSession = remember<MediaSession?> { null }

    val player = remember {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()

        ExoPlayer.Builder(context)
            .setSeekBackIncrementMs(seekBeforeMilliSeconds)
            .setSeekForwardIncrementMs(seekAfterMilliSeconds)
            .setTrackSelector(trackSelector)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AUDIO_CONTENT_TYPE_MOVIE)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                handleAudioFocus,
            )
            .apply {
                val cache = VideoPlayerCacheManager.getCache()
                if (cache != null) {
                    val cacheDataSourceFactory = CacheDataSource.Factory()
                        .setCache(cache)
                        .setUpstreamDataSourceFactory(
                            DefaultDataSource.Factory(
                                context,
                                httpDataSourceFactory
                            )
                        )
                    setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
                }
            }
            .playerBuilder()
            .build()
            .also(playerInstance)
    }

    val defaultPlayerView = remember {
        PlayerView(context).apply {

            val basic_progressbar= this.findViewById<ProgressBar>(R.id.exo_buffering)
            basic_progressbar?.indeterminateDrawable?.setColorFilter(Color.parseColor("#FFA500"), PorterDuff.Mode.SRC_IN)


            val exoPrev = this.findViewById<ImageButton>(R.id.exo_play_pause)
            exoPrev.setColorFilter(
                Color.parseColor("#FFA500"), // Задаем цвет из ресурсов
                PorterDuff.Mode.SRC_IN // Режим наложения
            )
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)

            if (currentTime != player.currentPosition) {
                onCurrentTimeChanged(currentTime)
            }

            currentTime = player.currentPosition
        }
    }

    LaunchedEffect(usePlayerController) {
        defaultPlayerView.useController = usePlayerController
    }

    LaunchedEffect(player) {
        defaultPlayerView.player = player
    }

    LaunchedEffect(mediaItems, player) {
        mediaSession?.release()
        mediaSession = MediaSession.Builder(context, ForwardingPlayer(player))
            .setId(
                "VideoPlayerMediaSession_${
                    UUID.randomUUID().toString().lowercase().split("-").first()
                }"
            )
            .build()
        val exoPlayerMediaItems = withContext(Dispatchers.IO) {
            mediaItems.map {
                val uri = it.toUri(context)

                MediaItem.Builder().apply {
                    setUri(uri)
                    setMediaMetadata(it.mediaMetadata)
                    setMimeType(it.mimeType)
                    setDrmConfiguration(
                        if (it is VideoPlayerMediaItem.NetworkMediaItem) {
                            it.drmConfiguration
                        } else {
                            null
                        },
                    )
                }.build()
            }
        }

        player.setMediaItems(exoPlayerMediaItems)
        player.prepare()

        if (autoPlay) {
            player.play()
        }
    }

    var isFullScreenModeEntered by remember(defaultFullScreeen) { mutableStateOf(defaultFullScreeen) }

    LaunchedEffect(controllerConfig) {
        controllerConfig.applyToExoPlayerView(defaultPlayerView) {
            isFullScreenModeEntered = it

            if (it) {
                onFullScreenEnter()
            }
        }
    }

    LaunchedEffect(controllerConfig, repeatMode) {

        defaultPlayerView.setRepeatToggleModes(
            if (controllerConfig.showRepeatModeButton) {
                REPEAT_TOGGLE_MODE_ALL or REPEAT_TOGGLE_MODE_ONE
            } else {
                REPEAT_TOGGLE_MODE_NONE
            },
        )
        player.repeatMode = repeatMode.toExoPlayerRepeatMode()

    }

    LaunchedEffect(volume) {
        player.volume = volume
    }

    VideoPlayerSurface(
        vm = vm,
        modifier = modifier,
        defaultPlayerView = defaultPlayerView,
        player = player,
        usePlayerController = usePlayerController,
        handleLifecycle = handleLifecycle,
        surfaceResizeMode = resizeMode
    )

    if (isFullScreenModeEntered) {
        var fullScreenPlayerView by remember { mutableStateOf<PlayerView?>(null) }

        VideoPlayerFullScreenDialog(
            vm = vm,
            player = player,
            currentPlayerView = defaultPlayerView,
            controllerConfig = controllerConfig,
            repeatMode = repeatMode,
            resizeMode = resizeMode,
            onDismissRequest = {
                Timber.e("!!! onDismissRequest Нажата кнопка выхода из полноэкранного режимати из фулскрин")
                fullScreenPlayerView?.let {
                    PlayerView.switchTargetView(player, it, defaultPlayerView)

                    defaultPlayerView.findViewById<ImageButton>(R.id.exo_fullscreen)
                        .performClick()

                    val currentActivity = context.findActivity()
                    currentActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    //currentActivity.setFullScreen(false)
                    onFullScreenExit()
                }

                isFullScreenModeEntered = false

            },
            fullScreenPlayerView = {
                fullScreenPlayerView = this
            },
        )
    }

}

