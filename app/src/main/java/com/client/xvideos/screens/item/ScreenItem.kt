package com.client.xvideos.screens.item

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.screens.item.util.ZoomableVideoPlayer

class ScreenItem(val url: String) : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val vm = getScreenModel<ScreenItemScreenModel, ScreenItemScreenModel.Factory> { factory ->
            factory.create(url)
        }

        Column {

            ZoomableVideoPlayer(vm.passedString)

//            AsyncImage(
//                model = vm.a.value?.thumbUrl169,
//                contentDescription = null,
//            )
            Divider()
//            AsyncImage(
//                model = vm.a.value?.thumbUrl,
//                contentDescription = null,
//            )
            Divider()
//            AsyncImage(
//                model = vm.a.value?.thumbSlide,
//                contentDescription = null,
//            )
            Divider()
//            AsyncImage(
//                model = vm.a.value?.thumbSlideBig,
//                contentDescription = null,
//            )


        }


    }


}