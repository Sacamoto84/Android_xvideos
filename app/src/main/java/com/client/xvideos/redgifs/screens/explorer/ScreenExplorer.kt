package com.client.xvideos.redgifs.screens.explorer

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import com.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.screens.explorer.tab.FavoritesTab
import com.client.xvideos.redgifs.screens.explorer.tab.gifs.GifsTab
import com.client.xvideos.redgifs.screens.explorer.tab.niches.NichesTab
import com.client.xvideos.redgifs.screens.explorer.tab.saved.SavedTab
import com.client.xvideos.redgifs.screens.explorer.tab.search.SearchTab
import com.client.xvideos.redgifs.screens.explorer.tab.setting.SettingTab
import com.client.xvideos.redgifs.screens.explorer.top.TabRow

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

        //val vm: ScreenRedExplorerSM = getScreenModel()
        var screenType by rememberSaveable{ mutableIntStateOf(0)}

        val l = listOf(
            Icons.Outlined.Movie,
            Icons.Outlined.Group,
            Icons.Outlined.BookmarkBorder,
            Icons.Outlined.Search,
            Icons.Outlined.Settings
        )
        Scaffold(bottomBar = {
            TabRow(
                containerColor = ThemeRed.colorTabLevel0,
                titlesIcon = l,
                onChangeState = { screenType = it })
        }, containerColor = ThemeRed.colorCommonBackground2) { paddingValues ->
            Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
                when (screenType) {
                    0 -> GifsTab.Content()
                    1 -> NichesTab.Content()
                    2 -> SavedTab.Content()
                    3 -> SearchTab.Content()
                    4 -> SettingTab.Content()
                    else -> FavoritesTab.Content()
                }
            }
        }

    }
}

