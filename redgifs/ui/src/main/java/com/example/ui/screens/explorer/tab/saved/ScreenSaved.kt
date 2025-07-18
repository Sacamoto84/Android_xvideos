package com.example.ui.screens.explorer.tab.saved

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import com.example.ui.screens.explorer.tab.FavoritesTab
import com.example.ui.screens.explorer.tab.saved.tab.SavedCollectionTab
import com.example.ui.screens.explorer.tab.saved.tab.SavedCreatorsTab
import com.example.ui.screens.explorer.tab.saved.tab.SavedDownloadTab
import com.example.ui.screens.explorer.tab.saved.tab.SavedLikesTab
import com.example.ui.screens.explorer.tab.saved.tab.SavedNichesTab
import com.example.ui.screens.explorer.top.TabRow
import com.redgifs.common.ThemeRed

object SavedTab : Screen {

    private fun readResolve(): Any = SavedTab

    override val key: ScreenKey = uniqueScreenKey

    var screenType by mutableIntStateOf(0)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

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
                                    0 -> SavedLikesTab.addColumn()
                                    4 -> SavedCollectionTab.addColumn()
                                }
                            }
                            screenType = it
                        },
                        overlay0 = {
                            Box(
                                modifier = Modifier.size(24.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row {
                                    repeat(SavedLikesTab.column.intValue) {
                                        Box(
                                            modifier = Modifier
                                                .padding(end = 2.dp)
                                                .clip(CircleShape)
                                                .size(4.dp)
                                                .background(if (screenType == 0) Color.White else Color.Gray)
                                        )
                                    }
                                }
                            }
                        },
                        overlay4 = {
                            Box(
                                modifier = Modifier.size(24.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row {
                                    repeat(SavedCollectionTab.column.intValue) {
                                        Box(
                                            modifier = Modifier
                                                .padding(end = 2.dp)
                                                .clip(CircleShape)
                                                .size(4.dp)
                                                .background(if (screenType == 4) Color.White else Color.Gray)
                                        )
                                    }
                                }
                            }
                        },


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
