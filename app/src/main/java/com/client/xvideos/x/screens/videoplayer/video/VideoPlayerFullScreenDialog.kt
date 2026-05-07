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
import android.view.Window
import android.widget.ImageButton
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.R
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.x.screens.videoplayer.video.controller.VideoPlayerControllerConfig
import com.client.xvideos.x.screens.videoplayer.video.controller.applyToExoPlayerView
import com.client.xvideos.screens.videoplayer.video.util.findActivity
import com.client.xvideos.x.screens.videoplayer.video.VideoPlayerSurface
import com.client.xvideos.x.screens.videoplayerFullScreen.ScreenX_VideoPlayerFullScreenSM

/**
 * ExoPlayer does not support full screen views by default.
 * So create a full screen modal that wraps the Compose Dialog.
 *
 * Delegate all functions of the video controller that were used just before
 * the full screen to the video controller managed by that component.
 * Conversely, if the full screen dismissed, it will restore all the functions it delegated
 * for synchronization with the video controller on the full screen and the video controller on the previous screen.
 *
 * @param player Exoplayer instance.
 * @param currentPlayerView [PlayerView] instance currently in use for playback.
 * @param fullScreenPlayerView Callback to return all features to existing video player controller.
 * @param controllerConfig Player controller config. You can customize the Video Player Controller UI.
 * @param repeatMode Sets the content repeat mode.
 * @param enablePip Enable PIP.
 * @param onDismissRequest Callback that occurs when modals are closed.
 * @param securePolicy Policy on setting [android.view.WindowManager.LayoutParams.FLAG_SECURE] on a full screen dialog window.
 */
@SuppressLint("UnsafeOptInUsageError")
@Composable
internal fun VideoPlayerFullScreenDialog(
    vm: ScreenX_VideoPlayerFullScreenSM,
    player: ExoPlayer,
    currentPlayerView: PlayerView,
    fullScreenPlayerView: PlayerView.() -> Unit,
    controllerConfig: VideoPlayerControllerConfig,
    repeatMode: RepeatMode,
    resizeMode: ResizeMode,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current

    val navigator = LocalNavigator.currentOrThrow

    val internalFullScreenPlayerView = remember { PlayerView(context).also(fullScreenPlayerView) }

    BackHandler(enabled = true) { onDismissRequest() }

    LaunchedEffect(Unit) {
        PlayerView.switchTargetView(player, currentPlayerView, internalFullScreenPlayerView)
        val currentActivity = context.findActivity()
        currentActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    LaunchedEffect(controllerConfig) {
        controllerConfig.applyToExoPlayerView(internalFullScreenPlayerView) {
            if (!it) {
                onDismissRequest()
            }
        }

        internalFullScreenPlayerView.findViewById<ImageButton>(R.id.exo_fullscreen)
            .performClick()
    }

    VideoPlayerSurface(
        defaultPlayerView = internalFullScreenPlayerView,
        player = player,
        usePlayerController = true,
        autoDispose = false,
        surfaceResizeMode = resizeMode,
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        handleLifecycle = true,
        qualityChange = { vm.quality = it },
        quality = vm.quality,
        listFormat = vm.listFormat, // Убирает статус и навигационные панели
        speed = vm.speed,
        changePlaybackSpeed = { vm.changePlaybackSpeed(it) },
        switchTrack = { vm.switchTrack(it) }
    )
}


@Composable
internal fun getDialogWindow(): Window? =
    (LocalView.current.parent as? DialogWindowProvider)?.window

@Composable
internal fun getActivityWindow(): Window? = LocalView.current.context.findActivity().window
