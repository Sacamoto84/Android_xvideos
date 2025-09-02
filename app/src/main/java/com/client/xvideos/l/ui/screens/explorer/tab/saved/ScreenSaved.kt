package com.client.xvideos.l.ui.screens.explorer.tab.saved

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EnhancedEncryption
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Folder
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
import com.client.xvideos.common.sharedPref.Settings
import com.client.xvideos.l.ui.screens.explorer.tab.saved.albums.ScreenLSavedAlbumsTab
import com.client.xvideos.l.ui.screens.explorer.tab.saved.likes.ScreenLSavedLikesTab
import com.client.xvideos.redgifs.ui.explorer.top.TabRow
import com.client.xvideos.redgifs.ui.ui.atom.TabBarPoints
import com.client.xvideos.redgifs.common.ThemeRed

object SavedLTab : Screen {

    private fun readResolve(): Any = SavedLTab

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
            //Icons.Outlined.FavoriteBorder,
            Icons.Outlined.Save,
            Icons.Outlined.Folder,
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
                                    0 -> ScreenLSavedLikesTab.columnSelect.addColumn(g0, g1, g2, g3, g4)
                                    4 -> ScreenLSavedLikesTab.columnSelect.addColumn(g0, g1, g2, g3, g4)
                                }
                            }
                            screenType = it
                        },
                        overlay0 = { TabBarPoints(ScreenLSavedLikesTab.columnSelect.column, screenType == 0) },
                        overlay4 = { TabBarPoints(ScreenLSavedLikesTab.columnSelect.column, screenType == 4) },
                    )
                }
            },

            modifier = Modifier.fillMaxSize(),
            containerColor = ThemeRed.colorCommonBackground2
        ) { paddingValues ->

            Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
                when (screenType) {
                    0 -> ScreenLSavedLikesTab.Content()
                    1 -> ScreenLSavedAlbumsTab.Content()
                    3 -> ScreenLSavedAlbumsTab.Content()
                    2 -> ScreenLSavedAlbumsTab.Content()
                    4 -> ScreenLSavedAlbumsTab.Content()
                    else -> ScreenLSavedAlbumsTab.Content()
                }
            }
        }
    }
}
