package com.client.xvideos.screens.dashboards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage


class ScreenDashBoards : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        //val vm = getScreenModel<ScreenDashBoardsScreenModel>()
        val vm: ScreenDashBoardsScreenModel = getScreenModel()
        // ...
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                items(vm.l) {
                    Divider()
                    Text(text = it.id.toString())
                    Text(text = it.title)
                    AsyncImage(
                        model = it.previewImage,
                        contentDescription = null,
                        modifier = Modifier.clickable { vm.openItem("https://www.xv-ru.com"+it.link, navigator) }
                    )
                }

            }
        }



    }
}