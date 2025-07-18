package com.client.common.videoplayer.ui.video

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.client.common.BuildConfig
import com.client.common.videoplayer.host.MediaPlayerHost
import com.client.common.videoplayer.model.PlayerOption
import com.client.common.videoplayer.model.VideoPlayerConfig
import com.client.common.videoplayer.ui.video.controls.FullControlComposable
import com.client.common.videoplayer.util.CMPPlayer
import com.client.common.videoplayer.util.getSeekTime
import com.client.common.videoplayer.util.saveCurrentPosition
import kotlinx.coroutines.delay
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

@Composable
fun VideoPlayerWithControl(
    modifier: Modifier,
    playerHost: MediaPlayerHost,
    playerConfig: VideoPlayerConfig
) {

    if (BuildConfig.DEBUG) {
        SideEffect {
            Timber.i("@@@ VideoPlayerWithControl()")
        }
    }

    var isScreenLocked by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(playerConfig.showControls) } // State for showing/hiding controls
    var showVolumeControl by remember { mutableStateOf(false) }
    var volumeDragAmount by remember { mutableStateOf(0f) }
    var activeOption by remember { mutableStateOf(PlayerOption.NONE) }

    val timeSource = remember { TimeSource.Monotonic }
    var lastInteractionMark by remember { mutableStateOf(timeSource.markNow()) }
    val handleControlInteraction = {
        if (playerConfig.showControls) {
            lastInteractionMark = timeSource.markNow()  // Reset the interaction timer
            showControls = true
        }
    }

    // Auto-hide controls if enabled
    if (playerConfig.showControls && playerConfig.isAutoHideControlEnabled) {
        LaunchedEffect(showControls, lastInteractionMark) {
            if (showControls) {
                val timeoutDuration = playerConfig.controlHideIntervalSeconds.seconds
                delay(timeoutDuration.inWholeMilliseconds) // Delay hiding controls
                if (!playerHost.isSliding && lastInteractionMark.elapsedNow() >= timeoutDuration) {
                    showControls = false // Hide controls if seek bar is not being slid
                }
            }
        }
    }

    val volumeDragModifier = Modifier.pointerInput(Unit) {
        detectVerticalDragGestures(
            onDragStart = {
                showVolumeControl = true
                volumeDragAmount = 0f
            },
            onVerticalDrag = { _, dragAmount ->
                volumeDragAmount += dragAmount
                val volumeChange = volumeDragAmount / 4000f // Adjust sensitivity
                playerHost.setVolume((playerHost.volumeLevel - volumeChange).coerceIn(0f, 1f))
                if (playerHost.volumeLevel > 0) playerHost.unmute() else playerHost.mute()
            },
            onDragEnd = {
                showVolumeControl = false // Hide immediately when finger is lifted
            }
        )
    }

    //Get Last saved time from preference for resume video
    LaunchedEffect(playerHost.totalTime) {
        getSeekTime(playerHost, playerConfig)?.let {
            playerHost.isSliding = true
            playerHost.seekToTime = it
            playerHost.isSliding = false
        }
    }

    //Save video last position into preference
    var previousUrl by remember { mutableStateOf(playerHost.url) }
    DisposableEffect(playerHost.url) {
        onDispose {
            if(playerConfig.enableResumePlayback) {
                saveCurrentPosition(playerHost, previousUrl)
                previousUrl = playerHost.url
            }
        }
    }

    val zoomState = rememberZoomState(maxScale = 3f)
    LaunchedEffect(playerHost.videoFitMode) {
        zoomState.reset()
    }

    // Container for the video player and control components
    Box(
        modifier = modifier
            .clipToBounds()
    ) {
        Box(
            modifier = modifier
                .zoomable(
                    zoomState = zoomState,
                    zoomEnabled = (!isScreenLocked && playerConfig.isZoomEnabled),
                    enableOneFingerZoom = false,
                    onTap = {
                        if(playerConfig.showControls) {
                            showControls = !showControls // Toggle show/hide controls on tap
                            activeOption = PlayerOption.NONE
                        }
                    }
                )
        ){
            // Video player component
            CMPPlayer(
                modifier = Modifier
                    .fillMaxSize(),
                url = playerHost.url,
                isPause = playerHost.isPaused,
                totalTime = { playerHost.updateTotalTime(it) }, // Update total time of the video
                currentTime = {
                    if (playerHost.isSliding.not()) {
                        playerHost.updateCurrentTime(it)  // Update current playback time
                        playerHost.seekToTime = null // Reset slider time if not sliding
                    }
                },
                isSliding = playerHost.isSliding, // Pass seek bar sliding state
                seekToTime = playerHost.seekToTime, // Pass seek bar slider time
                speed = playerHost.speed, // Pass selected playback speed
                size = playerHost.videoFitMode,
                bufferCallback = { playerHost.setBufferingStatus(it) },
                didEndVideo = {
                    playerHost.triggerMediaEnd()
                    if (!playerHost.isLooping) {
                        playerHost.togglePlayPause()
                    }
                },
                loop = playerHost.isLooping,
                volume = playerHost.volumeLevel,
                isLiveStream = playerConfig.isLiveStream,
                error = { playerHost.triggerError(it)},
                headers = playerHost.headers,
                drmConfig = playerHost.drmConfig,
                selectedQuality = playerHost.selectedQuality,
                selectedAudioTrack = playerHost.selectedAudioTrack,
                selectedSubTitle = playerHost.selectedsubTitle
            )

            Box(
                modifier = Modifier
                    .matchParentSize() // Covers the WebView entirely
                    .background(Color.Transparent) // Invisible but blocks touch
                    .pointerInput(Unit) {} // Consumes all touch events, blocking WebView
            )

            if (!isScreenLocked && playerConfig.isGestureVolumeControlEnabled) {
                // Detect right-side drag gestures
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.3f) // Occupy 30% of the right side dynamically
                        .align(Alignment.CenterEnd)
                        .then(volumeDragModifier) // Apply drag gesture detection only on the right side
                )
            }
        }


        FullControlComposable(
            playerHost = playerHost,
            playerConfig = playerConfig,
            showControls = showControls,
            isScreenLocked = isScreenLocked,
            showVolumeControl = showVolumeControl,
            activeOption = activeOption,
            activeOptionCallBack = { activeOption = it },
            onBackwardToggle = {
                playerHost.isSliding = true
                playerHost.seekToTime = maxOf(0f, playerHost.currentTime - playerConfig.fastForwardBackwardIntervalSeconds.toFloat()).toFloat()
                playerHost.isSliding = false
            },
            onForwardToggle = {
                playerHost.isSliding = true
                playerHost.seekToTime = minOf( playerHost.totalTime.toFloat(), playerHost.currentTime + playerConfig.fastForwardBackwardIntervalSeconds.toFloat()
                ).toFloat()
                playerHost.isSliding = false
            },
            onChangeSliderTime = {
                playerHost.seekToTime = it?.toFloat()
            },
            onLockScreenToggle = { isScreenLocked = it },
            userInteractionCallback = { handleControlInteraction() }
        )

        if (playerHost.isBuffering) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (playerConfig.loaderView != null) {
                    playerConfig.loaderView?.invoke()
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                            .size(playerConfig.pauseResumeIconSize),
                        color = playerConfig.loadingIndicatorColor
                    )
                }
            }
        }
    }
}