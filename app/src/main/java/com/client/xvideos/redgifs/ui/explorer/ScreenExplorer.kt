package com.client.xvideos.redgifs.ui.explorer

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.l.ui.screens.TabRow
import com.client.xvideos.redgifs.ui.explorer.tab.FavoritesTab
import com.client.xvideos.redgifs.ui.explorer.tab.gifs.GifsTab
import com.client.xvideos.redgifs.ui.explorer.tab.niches.NichesTab
import com.client.xvideos.redgifs.ui.explorer.tab.saved.SavedTab
import com.client.xvideos.redgifs.ui.explorer.tab.search.SearchTab
import com.client.xvideos.redgifs.ui.explorer.tab.setting.SettingTab
import com.client.xvideos.redgifs.ui.explorer.top.TabRow
import com.client.xvideos.redgifs.ui.ui.atom.TabBarPoints
import com.client.xvideos.redgifs.common.ThemeRed
import kotlinx.collections.immutable.persistentListOf

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

    companion object {
        var screenType by mutableIntStateOf(0)
    }

    val l = persistentListOf(
        Icons.Outlined.Movie,
        Icons.Outlined.Group,
        Icons.Outlined.BookmarkBorder,
        Icons.Outlined.Search,
        Icons.Outlined.Settings
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        Scaffold(bottomBar = {

            TabRow(
                containerColor = ThemeRed.colorTabLevel0,
                titlesIcon = l,
                value = screenType,
                onChangeState = {
                    if (it == screenType) {
                        when (it) {
                            0 -> {
                                GifsTab.columnSelect.addColumn(g0, g1, g2, g3, g4)
                            }
                        }
                    }
                    screenType = it
                },
                overlay0 = { TabBarPoints(GifsTab.columnSelect.column, screenType == 0) },
            )


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
