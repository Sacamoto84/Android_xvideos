package com.client.xvideos.red.screens.explorer.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.client.xvideos.red.screens.explorer.ScreenRedExplorerSM

object  FavoritesTab : Screen {

    private fun readResolve(): Any = FavoritesTab

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val vm: ScreenRedExplorerSM = getScreenModel()
        Box(modifier = Modifier.size(64.dp).background(Color.Red).clickable{vm.count++}){
            Text(vm.count.toString())
        }
    }
}

