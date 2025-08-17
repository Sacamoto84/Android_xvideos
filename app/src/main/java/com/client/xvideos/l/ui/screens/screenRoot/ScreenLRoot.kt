package com.client.xvideos.l.ui.screens.screenRoot

import android.annotation.SuppressLint
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        val scope = rememberCoroutineScope()

        LaunchedEffect(mediaCategories) {

        }

        Scaffold(
            bottomBar = {
                ScreenLRootBottomNavigator(
                    onClickOpenDrawer = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    }
                )
            },
            containerColor = ThemeL.greyBackground
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

                        if (mediaCategories != null) {
                            val a =
                                mediaCategories?.genres//?//.filter { it.onlyContent?.id == "2" || it.onlyContent == null }
                                    ?: emptyList()
                            LazyColumn {
                                items(a) {
                                    Text(
                                        it.title,
                                        fontSize = 16.sp,
                                        color = ThemeL.textColor,
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .clickable(
                                                onClick = {

                                                }
                                            )

                                    )
                                }
                            }
                        }

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

@Composable
fun ScreenLRootBottomNavigator(
    onClickOpenDrawer: () -> Unit = {},

    ) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(ThemeL.grey4),
    ) {

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Box(modifier = Modifier.height(46.dp).weight(1f).clickable(onClick = onClickOpenDrawer), contentAlignment = Alignment.Center) {
                Icon( Icons.Filled.Menu, contentDescription = null, tint = ThemeL.textColor )
            }
            VerticalDivider()
            Box( modifier = Modifier.height(46.dp).weight(1f), contentAlignment = Alignment.Center)
            { Text("Manga", color = ThemeL.textColor, fontSize = 16.sp, fontFamily = ThemeL.fontFamilyKarla) }
            VerticalDivider()
            Box( modifier = Modifier.height(46.dp).weight(1f), contentAlignment = Alignment.Center)
            { Text("Hentai", color = ThemeL.textColor, fontSize = 16.sp, fontFamily = ThemeL.fontFamilyKarla) }
            VerticalDivider()
            Box( modifier = Modifier.height(46.dp).weight(1f), contentAlignment = Alignment.Center)
            { Text("Porn", color = ThemeL.textColor, fontSize = 16.sp, fontFamily = ThemeL.fontFamilyKarla) }

        }

    }
}


//class ScreenLRootSM @Inject constructor(
//    val luscious: Luscious
//) : ScreenModel {
//
//}

//@Module
//@InstallIn(SingletonComponent::class)
//abstract class ScreenModuleLRootBlock {
//    @Binds
//    @IntoMap
//    @ScreenModelKey(ScreenLRootSM::class)
//    abstract fun bindScreenLRootScreenModel(hiltListScreenModel: ScreenLRootSM): ScreenModel
//}
