package com.client.xvideos.red.screens.explorer

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import com.client.xvideos.red.ThemeRed
import com.client.xvideos.red.screens.explorer.tab.FavoritesTab
import com.client.xvideos.red.screens.explorer.tab.gifs.GifsTab
import com.client.xvideos.red.screens.explorer.tab.niches.NichesTab
import com.client.xvideos.red.screens.explorer.tab.saved.SavedTab
import com.client.xvideos.red.screens.explorer.top.TabRow

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    BottomNavigationItem(
        selected = tabNavigator.current.key == tab.key,
        onClick = { tabNavigator.current = tab },
        icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) }
    )
}

class ScreenRedExplorer() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val vm: ScreenRedExplorerSM = getScreenModel()

        Scaffold(bottomBar ={ TabRow(onChangeState = {vm.screenType = it}) }, containerColor = ThemeRed.colorCommonBackground2){paddingValues ->
            Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
                when (vm.screenType) {
                    0 -> GifsTab.Content()
                    3 -> NichesTab.Content()
                    4 -> SavedTab.Content()
                    else -> FavoritesTab.Content()
                }
            }
        }

    }
}

