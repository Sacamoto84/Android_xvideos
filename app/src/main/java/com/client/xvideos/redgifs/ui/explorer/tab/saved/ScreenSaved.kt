package com.client.xvideos.redgifs.ui.explorer.tab.saved

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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import com.client.xvideos.l.ui.screens.TabRow
import com.client.xvideos.redgifs.ui.explorer.tab.FavoritesTab
import com.client.xvideos.redgifs.ui.explorer.tab.saved.tab.R_SavedCollectionTab
import com.client.xvideos.redgifs.ui.explorer.tab.saved.tab.R_SavedCreatorsTab
import com.client.xvideos.redgifs.ui.explorer.tab.saved.tab.R_SavedDownloadTab
import com.client.xvideos.redgifs.ui.explorer.tab.saved.tab.R_SavedLikesTab
import com.client.xvideos.redgifs.ui.explorer.tab.saved.tab.SavedNichesTab
import com.client.xvideos.redgifs.common.ThemeRed
import kotlinx.collections.immutable.persistentListOf

object R_ScreenSavedTab : Screen {

    private fun readResolve(): Any = R_ScreenSavedTab

    override val key: ScreenKey = uniqueScreenKey

    var screenType by mutableIntStateOf(0)

    val l = persistentListOf(
        Icons.Outlined.FavoriteBorder,
        Icons.Outlined.Person,
        Icons.Outlined.Group,
        Icons.Outlined.Save,
        //Icons.Outlined.Dataset,
        //Icons.Outlined.Folder,
        Icons.Outlined.Apps,
    )

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

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
                                    //0 -> R_SavedLikesTab.columnSelect.addColumn()
                                    //4 -> R_SavedCollectionTab.columnSelect.addColumn()
                                }
                            }
                            screenType = it
                        },
                        overlay0 = {
//                            TabBarPoints(
//                                R_SavedLikesTab.columnSelect.column,
//                                screenType == 0
//                            )
                        },
                        overlay4 = {
//                            TabBarPoints(
//                                R_SavedCollectionTab.columnSelect.column,
//                                screenType == 4
//                            )
                        },
                    )
                }
            },

            modifier = Modifier.fillMaxSize(),
            containerColor = ThemeRed.colorCommonBackground2
        ) { paddingValues ->

            Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
                when (screenType) {
                    0 -> R_SavedLikesTab.Content()
                    1 -> R_SavedCreatorsTab.Content()
                    3 -> R_SavedDownloadTab.Content()
                    2 -> SavedNichesTab.Content()
                    4 -> R_SavedCollectionTab.Content()
                    else -> FavoritesTab.Content()
                }
            }
        }
    }
}
