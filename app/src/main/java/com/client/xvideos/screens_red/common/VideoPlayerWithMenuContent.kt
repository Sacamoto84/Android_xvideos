package com.client.xvideos.screens_red.common

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
fun VideoPlayerWithMenuContent(
    modifier: Modifier,
    playerHost: MediaPlayerHost,
    onClick: () -> Unit = {},

    menuContent: @Composable () -> Unit = {},
    menuContentWidth: Dp = 192.dp,
    menuDefaultOpen: Boolean,
    menuOpenChanged: (Boolean) -> Unit,

    autoRotate: Boolean
) {

    if (BuildConfig.DEBUG) { SideEffect { Timber.i("@@@ VideoPlayerWithMenuContent() menuDefaultOpen:$menuDefaultOpen") } }

    val zoomState = rememberZoomState(maxScale = 3f)
    LaunchedEffect(playerHost.videoFitMode) { zoomState.reset() }

    var volumeDragAmount by remember { mutableFloatStateOf(0f) }
    val volumeDragModifier = Modifier.pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragStart = { volumeDragAmount = 0f },
            onDragEnd = {
                val dx = if (volumeDragAmount.absoluteValue > 400) 1f else 1 / 30f
                playerHost.isSliding = true
                playerHost.seekTo((playerHost.currentTime + (if (volumeDragAmount > 0) dx else -dx)).coerceIn(0f, playerHost.totalTime.toFloat()))
                playerHost.isSliding = false
            },
            onDragCancel = { },
            onHorizontalDrag = { _, dragAmount -> volumeDragAmount += dragAmount }
        )
    }


    Box( modifier = modifier.clipToBounds() ) {

        Box(modifier = modifier.zoomable(zoomState = zoomState, zoomEnabled = true, enableOneFingerZoom = false, onTap = { onClick.invoke() })) {
            StaticPlayer( playerHost, autoRotate )
        }

        //--- Меню контент ---
        MenuContent(menuContent = menuContent, menuContentWidth=menuContentWidth, menuDefaultOpen=menuDefaultOpen, menuOpenChanged=menuOpenChanged)

        //Нижняя сенсорная часть
        Box(modifier = Modifier.fillMaxHeight(1 / 3f).fillMaxWidth().align(Alignment.BottomCenter).then(volumeDragModifier).alpha(0.5f).background(Color.Transparent))

        if (playerHost.isBuffering) {
            Box( modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center).size(40.dp), color = Color.LightGray)
            }
        }

    }

}




@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun MenuContent(
    menuContent: @Composable () -> Unit = {},
    menuContentWidth: Dp = 192.dp,
    menuDefaultOpen: Boolean,
    menuOpenChanged: (Boolean) -> Unit){

    val squareSize = menuContentWidth
    val density = LocalDensity.current

    //--- Меню контент ---
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

        val swipeState = remember { AnchoredDraggableState(initialValue = if (menuDefaultOpen) SwipeState.Center else SwipeState.Right, anchors = anchors)}
        LaunchedEffect(swipeState.currentValue) {
            Timber.i("@@@ LaunchedEffect(swipeState)")
            menuOpenChanged(swipeState.currentValue == SwipeState.Center)
        }

        Box(
            modifier = Modifier
                .padding(top = 100.dp).height(64.dp).fillMaxWidth()
                .anchoredDraggable(
                    swipeState, Orientation.Horizontal,
                    flingBehavior =
                        AnchoredDraggableDefaults.flingBehavior(swipeState, positionalThreshold = { distance -> distance * 0.125f }, animationSpec = tween(100))
                ), contentAlignment = Alignment.CenterStart
        ) {

            if (swipeState.currentValue == SwipeState.Right && !swipeState.isAnimationRunning) {
                Box(Modifier.width(2.dp).height(48.dp).align(Alignment.CenterEnd).alpha(0.6f).background(color = Color(0xFFFFFFFF),shape = RoundedCornerShape(1.dp)))
            }

            Box(
                modifier = Modifier
                    .offset {IntOffset(x = swipeState.requireOffset().roundToInt(), y = 0)}
                    .width(squareSize).height(64.dp).background(Color(0xA1969696), shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                contentAlignment = Alignment.CenterStart
            ) {
                menuContent.invoke()
            }

        }

    }
    //!!!--- Меню контент ---!!!

}

@Composable
private fun StaticPlayer(
playerHost: MediaPlayerHost,
autoRotate: Boolean
){
    // Video player component
    CMPPlayer2(
        modifier = Modifier.fillMaxSize(),
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
        isLiveStream = false,
        error = { playerHost.triggerError(it) },
        headers = playerHost.headers,
        drmConfig = playerHost.drmConfig,
        selectedQuality = playerHost.selectedQuality,
        autoRotate = autoRotate
    )

}

enum class SwipeState { Left, Center, Right }
