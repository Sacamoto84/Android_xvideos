package com.client.xvideos.screens.item.util

import android.app.Activity
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
import android.os.Build
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.SHOW_BUFFERING_WHEN_PLAYING

@OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun Player(passedString: String, autostart: Boolean = false, repeat: Boolean = false) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    var player: Player? by remember {
        mutableStateOf(null)
    }

    val enterFullscreen = { activity.requestedOrientation = SCREEN_ORIENTATION_USER_LANDSCAPE }
    val exitFullscreen = {
        // Will reset to SCREEN_ORIENTATION_USER later
        activity.requestedOrientation = SCREEN_ORIENTATION_USER
    }

    val playerView = remember {
        PlayerView(context).apply {
            this.player = player
        }
    }

    DisposableEffect(key1 = player) {
        playerView.player = player
        onDispose {
            playerView.player = null
            //player = null
        }
    }

    activity.requestedOrientation = SCREEN_ORIENTATION_USER

    playerView.controllerAutoShow = true
    playerView.keepScreenOn = true
    playerView.setShowBuffering(SHOW_BUFFERING_WHEN_PLAYING)

    playerView.setFullscreenButtonClickListener { isFullScreen ->
        with(context) {
            if (isFullScreen) {
                if (activity.requestedOrientation == SCREEN_ORIENTATION_USER) {
                    enterFullscreen()
                }
            } else {
                exitFullscreen()
            }
        }
    }


    ComposableLifecycle { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {
                if (Build.VERSION.SDK_INT > 23) {
                    player = initPlayer(context, passedString)
                    playerView.onResume()
                }
            }

            Lifecycle.Event.ON_RESUME -> {
            }

            Lifecycle.Event.ON_PAUSE -> {
            }

            Lifecycle.Event.ON_STOP -> {
                playerView.apply {
                    player?.release()
                    onPause()
                    player = null
                }
            }

            else -> {}
        }
    }

    AndroidView(modifier = Modifier.fillMaxWidth().background(Color.Black),
        factory = { playerView }
    )
}