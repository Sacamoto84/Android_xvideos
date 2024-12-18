package com.client.xvideos.screens.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.screens.item.atom.ComposeTags
import com.client.xvideos.screens.item.atom.ZoomableVideoPlayer

class ScreenItem(val url: String) : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val vm = getScreenModel<ScreenItemScreenModel, ScreenItemScreenModel.Factory> { factory ->
            factory.create(url)
        }

        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            //Отобразить теги
            ComposeTags(vm.tags, onClick = {vm.openTag(it, navigator)})

            ZoomableVideoPlayer(videoUri = vm.passedString, Modifier.weight(1f))

        }

    }


}