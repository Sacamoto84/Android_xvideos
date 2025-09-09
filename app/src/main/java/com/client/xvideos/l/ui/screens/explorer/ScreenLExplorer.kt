package com.client.xvideos.l.ui.screens.explorer

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Topic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.client.xvideos.common.sharedPref.Settings
import com.client.xvideos.l.ui.screens.depth
import com.client.xvideos.l.ui.screens.explorer.tab.albumTopHits.ScreenLAlbumTopHits
import com.client.xvideos.l.ui.screens.explorer.tab.config.ScreenLConfigTab
import com.client.xvideos.l.ui.screens.explorer.tab.saved.SavedLTab
import com.client.xvideos.l.ui.screens.screenAlbumList.ScreenLAlbumList
import com.client.xvideos.redgifs.ui.explorer.tab.gifs.GifsTab
import com.client.xvideos.redgifs.ui.explorer.top.TabRow
import com.client.xvideos.redgifs.ui.ui.atom.TabBarPoints
import com.client.xvideos.redgifs.common.ThemeRed

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    BottomNavigationItem(
        selected = tabNavigator.current.key == tab.key,
        onClick = { tabNavigator.current = tab },
        icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) }
    )
}

class ScreenLExplorer() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    companion object {
        var screenType by mutableIntStateOf(0)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        LaunchedEffect(Unit) {
            depth = 0
        }

        val g0 = Settings.gallery_count[0].field.collectAsStateWithLifecycle().value
        val g1 = Settings.gallery_count[1].field.collectAsStateWithLifecycle().value
        val g2 = Settings.gallery_count[2].field.collectAsStateWithLifecycle().value
        val g3 = Settings.gallery_count[3].field.collectAsStateWithLifecycle().value
        val g4 = Settings.gallery_count[4].field.collectAsStateWithLifecycle().value

        val l = listOf(
            Icons.Outlined.FormatListBulleted,
            Icons.Outlined.Topic,
            Icons.Outlined.BookmarkBorder,
            Icons.Outlined.Settings
        )
        Scaffold(bottomBar = {

            TabRow(
                containerColor = ThemeRed.colorTabLevel0,
                titlesIcon = l,
                value = screenType,
                onChangeState = {
                    if (it == screenType) {
                        when (it) {
                           // 0 -> {
                           //     SavedLTab.columnSelect.addColumn(g0, g1, g2, g3, g4)
                           // }
                            1 -> {

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
                    0 -> ScreenLAlbumList(null).Content()
                    1 -> ScreenLAlbumTopHits.Content()
                    2 -> SavedLTab.Content()
                    3 -> ScreenLConfigTab.Content()
                    else -> SavedLTab.Content()
                }
            }
        }

    }
}
