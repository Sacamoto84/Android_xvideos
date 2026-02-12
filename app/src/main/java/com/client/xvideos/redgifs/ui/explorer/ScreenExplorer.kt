package com.client.xvideos.redgifs.ui.explorer

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import com.client.xvideos.l.ui.screens.TabRow
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.ui.explorer.tab.FavoritesTab
import com.client.xvideos.redgifs.ui.explorer.tab.gifs.R_ScreenGifsTab
import com.client.xvideos.redgifs.ui.explorer.tab.niches.R_ScreenNichesTab
import com.client.xvideos.redgifs.ui.explorer.tab.saved.R_ScreenSavedTab
import com.client.xvideos.redgifs.ui.explorer.tab.search.SearchTab
import com.client.xvideos.redgifs.ui.explorer.tab.setting.R_ScreenSettingTab

private val l = listOf(
    Icons.Outlined.Movie,
    Icons.Outlined.Group,
    Icons.Outlined.BookmarkBorder,
    Icons.Outlined.Search,
    Icons.Outlined.Settings
)

class ScreenRedExplorer : Screen {

    override val key: ScreenKey = uniqueScreenKey

    companion object {
        var screenType by mutableIntStateOf(0)
    }

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
                                //R_ScreenGifsTab.columnSelect.addColumn(g0, g1, g2, g3, g4)
                            }
                        }
                    }
                    screenType = it
                },
                //overlay0 = { TabBarPoints(R_ScreenGifsTab.columnSelect.column, screenType == 0) },
            )


        }, containerColor = ThemeRed.colorCommonBackground2) { paddingValues ->
            Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
                when (screenType) {
                    0 -> R_ScreenGifsTab.Content()
                    1 -> R_ScreenNichesTab.Content()
                    2 -> R_ScreenSavedTab.Content()
                    3 -> SearchTab.Content()
                    4 -> R_ScreenSettingTab.Content()
                    else -> FavoritesTab.Content()
                }
            }
        }

    }
}
