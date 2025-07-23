package com.example.ui.screens.explorer.tab.saved

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.HorizontalDivider
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
import com.client.common.sharedPref.Settings
import com.example.ui.screens.explorer.tab.FavoritesTab
import com.example.ui.screens.explorer.tab.saved.tab.SavedCollectionTab
import com.example.ui.screens.explorer.tab.saved.tab.SavedCreatorsTab
import com.example.ui.screens.explorer.tab.saved.tab.SavedDownloadTab
import com.example.ui.screens.explorer.tab.saved.tab.SavedLikesTab
import com.example.ui.screens.explorer.tab.saved.tab.SavedNichesTab
import com.example.ui.screens.explorer.top.TabRow
import com.example.ui.screens.ui.atom.TabBarPoints
import com.redgifs.common.ThemeRed

object SavedTab : Screen {

    private fun readResolve(): Any = SavedTab

    override val key: ScreenKey = uniqueScreenKey

    var screenType by mutableIntStateOf(0)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val g0 = Settings.gallery_count[0].field.collectAsStateWithLifecycle().value
        val g1 = Settings.gallery_count[1].field.collectAsStateWithLifecycle().value
        val g2 = Settings.gallery_count[2].field.collectAsStateWithLifecycle().value
        val g3 = Settings.gallery_count[3].field.collectAsStateWithLifecycle().value
        val g4 = Settings.gallery_count[4].field.collectAsStateWithLifecycle().value

        val l = listOf(
            Icons.Outlined.FavoriteBorder,
            Icons.Outlined.Person,
            Icons.Outlined.Group,
            Icons.Outlined.Save,
            //Icons.Outlined.Dataset,
            //Icons.Outlined.Folder,
            Icons.Outlined.Apps,
        )

        Scaffold(
            bottomBar = {
                Column {
                    HorizontalDivider()
                    TabRow(
                        value = screenType,
                        containerColor = ThemeRed.colorTabLevel1,
                        //containerColor = ThemeRed.colorBottomBarBackground,
                        titlesIcon = l,
                        onChangeState = {
                            if (it == screenType) {
                                when (it) {
                                    0 -> SavedLikesTab.columnSelect.addColumn(g0, g1, g2, g3, g4)
                                    4 -> SavedCollectionTab.columnSelect.addColumn(g0, g1, g2, g3, g4)
                                }
                            }
                            screenType = it
                        },
                        overlay0 = { TabBarPoints(SavedLikesTab.columnSelect.column.intValue, screenType == 0) },
                        overlay4 = { TabBarPoints(SavedCollectionTab.columnSelect.column.intValue, screenType == 4) },
                    )
                }
            },

            modifier = Modifier.fillMaxSize(),
            containerColor = ThemeRed.colorCommonBackground2
        ) { paddingValues ->

            Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
                when (screenType) {
                    0 -> SavedLikesTab.Content()
                    1 -> SavedCreatorsTab.Content()
                    3 -> SavedDownloadTab.Content()
                    2 -> SavedNichesTab.Content()
                    4 -> SavedCollectionTab.Content()
                    else -> FavoritesTab.Content()
                }
            }
        }
    }
}
