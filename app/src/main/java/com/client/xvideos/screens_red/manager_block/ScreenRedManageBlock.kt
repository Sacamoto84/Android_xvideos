package com.client.xvideos.screens_red.manager_block

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.client.xvideos.screens_red.manager_block.bottomr_bar.BottomrBar

class ScreenRedManageBlock() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val vm: ScreenRedManageBlockSM = getScreenModel()

        val blockList = vm.blockList.collectAsState().value

        Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {BottomrBar()}) {

            val state = rememberLazyListState()

            LazyColumn(state = state, modifier = Modifier.padding(bottom = it.calculateBottomPadding()).fillMaxSize()) {

                items(blockList) {
                    Box(modifier = Modifier.padding(8.dp).fillMaxWidth().height(128.dp).background(Color.Green)){

                        UrlImage(it.urls.thumbnail)


                    }
                }






            }




        }

    }
}