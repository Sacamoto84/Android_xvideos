package com.client.redgifs.common.video.player_with_menu.atom

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.client.redgifs.common.video.player_with_menu.model.SwipeState
import timber.log.Timber
import kotlin.math.roundToInt

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MenuContent(
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