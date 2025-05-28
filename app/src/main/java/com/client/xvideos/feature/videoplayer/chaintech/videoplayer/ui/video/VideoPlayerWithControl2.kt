package com.client.xvideos.feature.videoplayer.chaintech.videoplayer.ui.video

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.client.xvideos.BuildConfig
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host.MediaPlayerHost
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.PlayerOption
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.VideoPlayerConfig
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.util.CMPPlayer2
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import timber.log.Timber
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun VideoPlayerWithControl2(
    modifier: Modifier,
    playerHost: MediaPlayerHost,
    playerConfig: VideoPlayerConfig,
    onClick: () -> Unit = {},

    menuContent: @Composable () -> Unit = {},
    menuContentWidth: Dp = 192.dp,
    menuDefaultOpen: Boolean,
    menuOpenChanged: (Boolean) -> Unit,

    autoRotate: Boolean
) {

   if (BuildConfig.DEBUG) { SideEffect { Timber.i("@@@ VideoPlayerWithControl2() menuDefaultOpen:$menuDefaultOpen") } }

    var isScreenLocked by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(playerConfig.showControls) } // State for showing/hiding controls

    var volumeDragAmount by remember { mutableFloatStateOf(0f) }
    var activeOption by remember { mutableStateOf(PlayerOption.NONE) }
    var exoPlayer by remember { mutableStateOf<androidx.media3.exoplayer.ExoPlayer?>(null) }


    val volumeDragModifier = Modifier.pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragStart = {
                volumeDragAmount = 0f
            },
            onDragEnd = {
                val dx = if (volumeDragAmount.absoluteValue > 400) 1f else 1 / 30f

//                //if (dx == 1f){
//                    exoPlayer?.seekForward()
//                //}

                playerHost.isSliding = true
                playerHost.seekTo(
                    (playerHost.currentTime + (if (volumeDragAmount > 0) dx else -dx)).coerceIn(
                        0f,
                        playerHost.totalTime.toFloat()
                    )
                )
                playerHost.isSliding = false
            },
            onDragCancel = { },
            onHorizontalDrag = { _, dragAmount ->
                volumeDragAmount += dragAmount

            }
        )
    }


    val zoomState = rememberZoomState(maxScale = 3f)
    LaunchedEffect(playerHost.videoFitMode) {
        zoomState.reset()
    }

    // Container for the video player and control components
    Box(
        modifier = modifier.clipToBounds()
    ) {

        Box(
            modifier = modifier
                .zoomable(
                    zoomState = zoomState,
                    zoomEnabled = (!isScreenLocked && playerConfig.isZoomEnabled),
                    enableOneFingerZoom = false,
                    onTap = {
                        onClick.invoke()
                        if (playerConfig.showControls) {
                            showControls = !showControls // Toggle show/hide controls on tap
                            activeOption = PlayerOption.NONE
                        }
                    }
                )

        ) {

            // Video player component
            CMPPlayer2(
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
                error = { playerHost.triggerError(it) },
                headers = playerHost.headers,
                drmConfig = playerHost.drmConfig,
                selectedQuality = playerHost.selectedQuality,
                onExoPlayer = {
                    exoPlayer = it
                },
                autoRotate = autoRotate
            )

//            playerConfig.watermarkConfig?.let {
//                MovingWatermark(
//                    config = it,
//                    modifier = Modifier.matchParentSize()
//                )
//            }

//            Box(
//                modifier = Modifier
//                    .matchParentSize() // Covers the WebView entirely
//                    .background(Color.Blue) // Invisible but blocks touch
//                    .pointerInput(Unit) {} // Consumes all touch events, blocking WebView
//            )


            //val sizePx = with(LocalDensity.current) { (width - squareSize).toPx() }
            //val anchors = mapOf(0f to "A", sizePx / 2 to "B", sizePx to "C")

            if (!isScreenLocked && playerConfig.isGestureVolumeControlEnabled) {

                //Нижняя сенсорная часть
                Box(
                    modifier = Modifier
                        .fillMaxHeight(1 / 3f)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .then(volumeDragModifier)
                        .alpha(0.5f)
                        .background(Color.Magenta)
                )


                val squareSize = menuContentWidth
                //var squareSize by remember { mutableStateOf(0.dp) }
                val density = LocalDensity.current

                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                )
                {
                    val width = maxWidth
                    val maxWidthPx = with(density) { (width).toPx() }
                    val startPx = with(density) { (width - squareSize).toPx() }

                    val anchors = DraggableAnchors {
                        SwipeState.Center at startPx
                        SwipeState.Right at maxWidthPx
                    }

                    //Timber.i("@@@ menuDefaultOpen $menuDefaultOpen")

                    val swipeState = remember {
                        AnchoredDraggableState(
                            initialValue = if (menuDefaultOpen) SwipeState.Center else SwipeState.Right,
                            anchors = anchors
                        )
                    }

                    LaunchedEffect(swipeState.currentValue) {
                        Timber.i("@@@ LaunchedEffect(swipeState)")
                        menuOpenChanged(swipeState.currentValue == SwipeState.Center)
                    }


                    Box(
                        modifier = Modifier
                            //.fillMaxHeight(1/3f)
                            .padding(top = 100.dp)
                            .height(64.dp)
                            .fillMaxWidth()
                            //.alpha(0.5f)
                            //.background(color = Color.Magenta)
                            .anchoredDraggable(
                                swipeState, Orientation.Horizontal,
                                flingBehavior =
                                    AnchoredDraggableDefaults.flingBehavior(
                                        swipeState,
                                        positionalThreshold = { distance -> distance * 0.125f },
                                        animationSpec = tween(100)
                                    )
                            ), contentAlignment = Alignment.CenterStart
                    ) {

                        if (swipeState.currentValue == SwipeState.Right && !swipeState.isAnimationRunning) {
                            Box(
                                Modifier
                                    .width(2.dp)
                                    .height(48.dp)
                                    .align(Alignment.CenterEnd)
                                    .alpha(0.6f)
                                    .background(
                                        color = Color(0xFFFFFFFF),
                                        shape = RoundedCornerShape(1.dp)
                                    )
                            )
                        }

                        Box(
                            modifier = Modifier
                                //.offset { IntOffset(swappableState.offset.value.roundToInt(), 0) }
                                .offset {
                                    IntOffset(
                                        x = swipeState.requireOffset().roundToInt(),
                                        y = 0
                                    )
                                }
                                .width(squareSize)
                                .height(64.dp)
                                .background(
                                    Color(0xA1969696),
                                    shape = RoundedCornerShape(
                                        topStart = 12.dp,
                                        bottomStart = 12.dp
                                    )
                                ),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            menuContent.invoke()
                        }

                    }
                }


            }

        }


//        FullControlComposable2(
//            playerHost = playerHost,
//            playerConfig = playerConfig,
//            showControls = showControls,
//            isScreenLocked = isScreenLocked,
//            showVolumeControl = false,
//            activeOption = activeOption,
//            activeOptionCallBack = { activeOption = it },
//            onBackwardToggle = {
//                playerHost.isSliding = true
//                playerHost.seekToTime = maxOf(0f, playerHost.currentTime - playerConfig.fastForwardBackwardIntervalSeconds.toFloat()).toFloat()
//                playerHost.isSliding = false
//            },
//            onForwardToggle = {
//                playerHost.isSliding = true
//                playerHost.seekToTime = minOf( playerHost.totalTime.toFloat(), playerHost.currentTime + playerConfig.fastForwardBackwardIntervalSeconds.toFloat()
//                ).toFloat()
//                playerHost.isSliding = false
//            },
//            onChangeSliderTime = {
//                playerHost.seekToTime = it?.toFloat()
//            },
//            onLockScreenToggle = { isScreenLocked = it },
//            userInteractionCallback = {
//                //handleControlInteraction()
//            }
//        )

        if (playerHost.isBuffering) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (playerConfig.loaderView != null) {
                    playerConfig.loaderView?.invoke()
                } else {

                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(playerConfig.pauseResumeIconSize),
                        color = playerConfig.loadingIndicatorColor
                    )

                }
            }

        }

    }

}

enum class SwipeState { Left, Center, Right }
