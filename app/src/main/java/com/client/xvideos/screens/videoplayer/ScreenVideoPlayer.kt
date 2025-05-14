package com.client.xvideos.screens.videoplayer

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.screens.videoplayer.atom.ComposeTags
import com.client.xvideos.screens.videoplayer.atom.ZoomableVideoPlayer

class ScreenVideoPlayer(val url: String) : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(UnstableApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val vm = getScreenModel<ScreenVideoPlayerSM, ScreenVideoPlayerSM.Factory> { factory ->
            factory.create(url)
        }

        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            //Отобразить теги
            if (!vm.isFullScreen) {
                ComposeTags(vm.tags, onClick = { vm.openTag(it, navigator) })
            }
            //Отображение плеера и его кнопок
            ZoomableVideoPlayer(vm, videoUri = vm.passedString, Modifier.weight(1f))
        }

    }


}