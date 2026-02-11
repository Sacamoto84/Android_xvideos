package com.client.xvideos.l.ui.screens.explorer

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import com.client.xvideos.LocalRootScreenModel
import com.client.xvideos.R
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.depth
import com.client.xvideos.l.ui.screens.explorer.tab.albumTopHits.L_ScreenAlbumTopHits
import com.client.xvideos.l.ui.screens.explorer.tab.config.L_ScreenConfigTab
import com.client.xvideos.l.ui.screens.explorer.tab.saved.L_SavedTab
import com.client.xvideos.l.ui.screens.screenAlbumList.L_ScreenAlbumList
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.ui.explorer.tab.gifs.R_ScreenGifsTab
import com.client.xvideos.redgifs.ui.explorer.top.TabRow
import com.client.xvideos.redgifs.ui.ui.atom.TabBarPoints
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

class L_ScreenExplorer : Screen {

    override val key: ScreenKey = uniqueScreenKey

    companion object {
        var screenType by mutableIntStateOf(0)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

//        val rootVm = LocalRootScreenModel.current

//        DisposableEffect(Unit) {
//            rootVm.showOverlay({
//                Image(painterResource(R.drawable.logo), contentDescription = null, modifier = Modifier.size(32.dp))
//            }
//            )
//            onDispose {
//                rootVm.hideOverlay()
//            }
//        }

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) { depth = 0 }

        // ПЕРЕНЕСЕНО СЮДА: Теперь эти списки создаются внутри Composable
        val l = remember {
            persistentListOf(
                Icons.Outlined.FormatListBulleted,
                Icons.Outlined.Topic,
                Icons.Outlined.BookmarkBorder,
                Icons.Outlined.Settings
            )
        }

        val tags = remember {
            persistentListOf(
                "",
                "",
                "bBookMark",
                ""
            )
        }

        val columnR_ScreenGifsTab = Settings.l_gifsTab_column_current_count.field.collectAsStateWithLifecycle().value


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
                overlay0 = { TabBarPoints(columnR_ScreenGifsTab, screenType == 0) },
                tags = tags
            )


        }, containerColor = ThemeRed.colorCommonBackground2) { paddingValues ->
            Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {


                //Navigator(
                when (screenType) {
                    0 -> L_ScreenAlbumList.Content()
                    1 -> L_ScreenAlbumTopHits.Content()
                    2 -> L_SavedTab.Content()
                    3 -> L_ScreenConfigTab().Content()
                    else -> L_SavedTab
                }
                //)

            }
        }

    }
}
