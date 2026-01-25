package com.client.xvideos.l.ui.screens.explorer.tab.saved

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.l.ui.screens.TabRow
import com.client.xvideos.l.ui.screens.explorer.tab.saved.albums.L_ScreenSavedAlbumsTab
import com.client.xvideos.l.ui.screens.explorer.tab.saved.crypto.L_ScreenSavedCryptoTab
import com.client.xvideos.l.ui.screens.explorer.tab.saved.crypto.L_ScreenSavedCryptoTab_AddColumn
import com.client.xvideos.l.ui.screens.explorer.tab.saved.likes.L_ScreenSavedLikesTab
import com.client.xvideos.l.ui.screens.explorer.tab.saved.likes.L_ScreenSavedLikesTab_AddColumn
import com.client.xvideos.redgifs.ui.ui.atom.TabBarPoints
import com.client.xvideos.redgifs.common.ThemeRed
import kotlinx.collections.immutable.persistentListOf

object L_SavedTab : Screen {

    private fun readResolve(): Any = L_SavedTab

    override val key: ScreenKey = uniqueScreenKey

    val l = persistentListOf(
        //Icons.Outlined.FavoriteBorder,
        Icons.Outlined.Save,
        Icons.Outlined.Folder,
        Icons.Outlined.Apps,
        Icons.Outlined.Lock,
        //Icons.Outlined.LockOpen,
    )

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        var screenType by rememberSaveable{mutableIntStateOf(0)}

        val columnLikes = Settings.l_likesTab_column_current_count.field.collectAsStateWithLifecycle().value
        val columnCrypto = Settings.l_likesTab_column_current_count.field.collectAsStateWithLifecycle().value

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
                                    0 -> L_ScreenSavedLikesTab_AddColumn()
                                    4 -> L_ScreenSavedCryptoTab_AddColumn()
                                }
                            }
                            screenType = it
                        },
                        overlay0 = { TabBarPoints(columnLikes, screenType == 0) },
                        overlay4 = { TabBarPoints(columnCrypto, screenType == 0)}
                    )
                }
            },

            modifier = Modifier.fillMaxSize(),
            containerColor = ThemeRed.colorCommonBackground2
        ) { paddingValues ->

            Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
                when (screenType) {
                    0 -> L_ScreenSavedLikesTab.Content()
                    1 -> L_ScreenSavedAlbumsTab.Content()
                    2 -> {}
                    3 -> L_ScreenSavedCryptoTab.Content()
                    else -> {}
                }
            }
        }
    }
}
