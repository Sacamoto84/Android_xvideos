package com.client.xvideos.red.screens.explorer.tab.saved

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.stylusHoverIcon
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import com.client.xvideos.red.ThemeRed
import com.client.xvideos.red.screens.explorer.tab.FavoritesTab
import com.client.xvideos.red.screens.explorer.tab.niches.NichesTab
import com.client.xvideos.red.screens.explorer.tab.saved.tab.SavedCollectionTab
import com.client.xvideos.red.screens.explorer.tab.saved.tab.SavedCreatorsTab
import com.client.xvideos.red.screens.explorer.tab.saved.tab.SavedLikesTab
import com.client.xvideos.red.screens.explorer.tab.saved.tab.SavedNichesTab
import com.client.xvideos.red.screens.explorer.top.TabRow


object SavedTab : Screen {

    private fun readResolve(): Any = SavedTab

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        var screenType by rememberSaveable { mutableIntStateOf(0) }

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
                        //containerColor = ThemeRed.colorBottomBarBackground,
                        titlesIcon = l, onChangeState = {
                            if (it == screenType) {
                                when (it) {
                                    0 -> SavedLikesTab.addColumn()
                                }
                            }
                            screenType = it
                        },
                        overlay0 = {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                //.background(Color.Blue)
                                , contentAlignment = Alignment.CenterStart
                            ) {
                                Row {
                                    repeat(SavedLikesTab.column.intValue) {
                                        Box(
                                            modifier = Modifier
                                                .padding(end = 2.dp)
                                                .clip(CircleShape)
                                                .size(4.dp)
                                                .background(Color.White)
                                        )
                                    }
                                }
                            }
                        }
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
                    2 -> SavedNichesTab.Content()
                    4 -> SavedCollectionTab.Content()
                    else -> FavoritesTab.Content()
                }
            }
        }
    }
}
