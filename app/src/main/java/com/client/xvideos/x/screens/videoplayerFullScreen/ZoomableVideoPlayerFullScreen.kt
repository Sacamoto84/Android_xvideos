package com.client.xvideos.x.screens.videoplayerFullScreen

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.common.diagnostics.AppDiagnostics
import com.client.xvideos.common.eventBus.Event
import com.client.xvideos.common.eventBus.EventBus
import com.client.xvideos.screens.videoplayer.video.RepeatMode
import com.client.xvideos.screens.videoplayer.video.uri.VideoPlayerMediaItem
import com.client.xvideos.x.screens.videoplayer.FORMAT
import com.client.xvideos.x.screens.videoplayer.atom.formatMinSec
import com.client.xvideos.x.screens.videoplayer.video.controller.VideoPlayerControllerConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(UnstableApi::class)
@Composable
fun ZoomableVideoPlayerFullScreen(
    vm: ScreenX_VideoPlayerFullScreenSM,
    videoUri: String,
) {
    val context = LocalContext.current

    val navigator = LocalNavigator.currentOrThrow

    Timber.i("!!! ZoomableVideoPlayer url:$videoUri")
    //val activity = LocalContext.current as Activity
    //activity.requestedOrientation = SCREEN_ORIENTATION_PORTRAIT

    VideoPlayerFullScreen(
        vm = vm,
        onFullScreenExit = { position ->
            Timber.i("!!! onFullScreenExit position:${position.formatMinSec()} ")
            EventBus.postEvent(Event.X_FullScreenExitPosition(position))
            navigator.pop()
        },
        onFullScreenEnter = {
            Timber.i("!!! onFullScreenEnter")
            //vm.isFullScreen = true
        },

        defaultFullScreeen = true,

        trackSelector = DefaultTrackSelector(context),
        mediaItems = listOf(
            VideoPlayerMediaItem.NetworkMediaItem(
                url = videoUri,
                mediaMetadata = MediaMetadata.Builder().setTitle("Widevine HLS: Example").build(),
                mimeType = MimeTypes.APPLICATION_M3U8,
            )
        ),
        handleLifecycle = true,
        autoPlay = true,
        usePlayerController = false,
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

            controllerShowTimeMilliSeconds = 10_000,
            controllerAutoShow = true,
            showFullScreenButton = true,
        ),
        volume = 0.0f,  // volume 0.0f to 1.0f
        repeatMode = RepeatMode.NONE,       // or RepeatMode.ALL, RepeatMode.ONE
        onCurrentTimeChanged = { // long type, current player time (millisec)
            //Timber.tag("CurrentTime").e(it.toString())
            //currentTime = it
        },
        playerInstance = { // ExoPlayer instance (Experimental)

            vm.playerE = this

            addListener(

                object : Player.Listener {

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        Timber.i("!!! onPlaybackStateChanged ${Player.STATE_READY} positionFromFullscreen:${vm.positionForFullscreen}")
                        if (playbackState == Player.STATE_READY && vm.positionForFullscreen != -1L) {
                            Timber.i("!!! >>> positionFromFullscreen ${vm.positionForFullscreen.formatMinSec()}")
                            vm.playerE?.seekTo(vm.positionForFullscreen)
                            val temp = vm.positionForFullscreen
                            vm.positionForFullscreen = -1
                            vm.screenModelScope.launch {
                                delay(16)
                                vm.playerE?.seekTo(temp)
                            }
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        AppDiagnostics.recordPlayerError(
                            source = "X fullscreen player",
                            url = videoUri,
                            message = error.message ?: "Unknown playback error"
                        )
                    }

                    override fun onTracksChanged(tracks: Tracks) {
                        // Update UI using current tracks.
                        if (tracks.groups.size == 0) return

                        Timber.i("!!! onTracksChanged " + tracks.groups[0].toString())

                        vm.listFormat.clear()

                        val group = tracks.groups[0]
                        for (j in 0 until group.length) {
                            val format = group.getTrackFormat(j)

                            vm.listFormat.add(
                                FORMAT(
                                    id = j,
                                    width = format.width,
                                    height = format.height,
                                    bitrate = format.bitrate,
                                    isSelect = group.isTrackSelected(j)
                                )
                            )
                            Timber.d("!!! Group: 0, Format: $j, Resolution: ${format.width}x${format.height}, Bitrate: ${format.bitrate}")
                        }
                        vm.listFormat.sortBy { it.height }
                        if (!vm.once) {
                            vm.once = true
                            vm.quality = vm.listFormat.last().height
                            Timber.d("!!! vm.quality: ${vm.quality}")
                        }
                    }
                }
            )

            addAnalyticsListener(
                object : AnalyticsListener {

                    @OptIn(UnstableApi::class)
                    override fun onEvents(player: Player, events: AnalyticsListener.Events) {
                        super.onEvents(player, events)
                        vm.totalDuration = player.duration.coerceAtLeast(0L)
                        vm.currentTime = player.currentPosition.coerceAtLeast(0L)
                        vm.bufferedPercentage = player.bufferedPercentage
                        vm.isPlaying = player.isPlaying
                        vm.playbackState = player.playbackState
                    }
                }
            )
        },

        modifier = Modifier.fillMaxSize(),
    )

}
