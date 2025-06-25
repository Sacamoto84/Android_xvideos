package com.client.xvideos.red.screens.explorer.tab.saved

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Dataset
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.red.ThemeRed
import com.client.xvideos.red.common.ui.lazyrow123.LazyRow123
import com.client.xvideos.red.screens.explorer.tab.FavoritesTab
import com.client.xvideos.red.screens.explorer.tab.gifs.GifsTab
import com.client.xvideos.red.screens.explorer.tab.niches.NichesTab
import com.client.xvideos.red.screens.explorer.tab.saved.tab.SavedLikesTab
import com.client.xvideos.red.screens.explorer.top.TabRow
import com.client.xvideos.red.screens.profile.ScreenRedProfile
import com.client.xvideos.red.screens.top_this_week.ScreenRedTopThisWeek


object SavedTab : Screen {

    private fun readResolve(): Any = SavedTab

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        var screenType by rememberSaveable {  mutableIntStateOf(0) }

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
            bottomBar = { TabRow(titlesIcon = l, onChangeState = { screenType = it }) },
            modifier = Modifier.fillMaxSize(),
            containerColor = ThemeRed.colorCommonBackground2
        ) { paddingValues ->

            Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
                when (screenType) {
                    0 -> SavedLikesTab.Content()
                    1 -> ScreenRedTopThisWeek.Content()
                    3 -> NichesTab.Content()
                    else -> FavoritesTab.Content()
                }
            }
        }
    }
}
