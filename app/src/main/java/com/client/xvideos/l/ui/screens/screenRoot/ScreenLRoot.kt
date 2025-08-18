package com.client.xvideos.l.ui.screens.screenRoot

import android.annotation.SuppressLint
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.l.net.Luscious
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.net.graphQl.mediaCategories
import com.client.xvideos.l.ui.screens.screenAlbumTopHits.ScreenLAlbumTopHits
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ExperimentalZoomableApi
import javax.inject.Inject

class ScreenLRoot() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalZoomableApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val haptic = LocalHapticFeedback.current

        //val vm: ScreenLRootSM = getScreenModel()

        val drawerState = rememberDrawerState(DrawerValue.Closed)



        Scaffold( containerColor = ThemeL.greyBackground
        ) { paddingValues ->

            ModalNavigationDrawer(
                drawerState = drawerState,
                scrimColor = Color.Transparent,
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier
                            .width(280.dp)
                            .padding(bottom = paddingValues.calculateBottomPadding()), // 👈 фиксированная ширина
                        drawerContainerColor = ThemeL.grey6 // для примера
                    ) {
//
//                        when (selectIndexDrawer) {
//                            SelectIndex.Default -> DrawerContentDefault()
//                            SelectIndex.Manga -> DrawerContentManga()
//                            SelectIndex.Hentai -> DrawerContentHentai()
//                            SelectIndex.Porn -> DrawerContentPorn()
//                            else -> {}
//                        }


//                        Column {
//
//                            Text(
//                                "Drawer",
//                                fontSize = 22.sp,
//                                color = Color.LightGray,
//                                modifier = Modifier.padding(16.dp)
//                            )
//
//                            HorizontalDivider()
//
//                            NavigationDrawerItem(
//                                label = { Text("Item 1") },
//                                selected = false,
//                                onClick = { /* Handle click */ }
//                            )
//                            HorizontalDivider()
//                            NavigationDrawerItem(
//                                label = { Text("Item 2") },
//                                selected = false,
//                                onClick = { /* Handle click */ }
//                            )
//
//                        }
                    }
                },
            ) {
                Navigator(screen = ScreenLAlbumTopHits())
            }

        }

    }

}

enum class SelectIndex(val value: Int) {
    Unselect(-1),
    Default(0),
    Manga(1),
    Hentai(2),
    Porn(3)
}

