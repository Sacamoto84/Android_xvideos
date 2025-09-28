package com.client.xvideos.xvideos.screens.videoplayerFullScreen

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class ScreenX_VideoPlayerFullScreen(val url: String) : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(UnstableApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = getScreenModel<ScreenX_VideoPlayerFullScreenSM, ScreenX_VideoPlayerFullScreenSM.Factory> { factory ->  factory.create(url) }
        ZoomableVideoPlayerFullScreen(vm, videoUri = vm.passedString)
    }


}