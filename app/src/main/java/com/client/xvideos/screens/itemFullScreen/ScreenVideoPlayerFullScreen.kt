package com.client.xvideos.screens.itemFullScreen

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
import cafe.adriel.voyager.hilt.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.screens.videoplayer.ScreenVideoPlayerSM

class ScreenVideoPlayerFullScreen(val url: String) : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(UnstableApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

//        val a = getScreenModel<ScreenModel_Item, ScreenModel_Item.Factory> { factory ->
//            factory.create(url)
//        }

        //val vm = navigator.rememberNavigatorScreenModel { a }
        //val vm = rememberScreenModel { a }

        val vm = navigator.getNavigatorScreenModel<ScreenVideoPlayerSM, ScreenVideoPlayerSM.Factory> { factory ->
            factory.create(url)
        }

        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {




        }

    }


}