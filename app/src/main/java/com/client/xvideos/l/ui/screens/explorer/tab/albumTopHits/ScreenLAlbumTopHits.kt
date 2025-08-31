package com.client.xvideos.l.ui.screens.explorer.tab.albumTopHits

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.net.AlbumTopHitsImpl
import com.client.xvideos.l.net.Luscious
import com.client.xvideos.l.ui.element.AlbumListItem
import com.client.xvideos.l.ui.screens.screenAlbum.ScreenLAlbum
import com.client.xvideos.l.ui.screens.explorer.tab.albumTopHits.atom.DrawerContentDefault
import com.client.xvideos.l.ui.screens.explorer.tab.albumTopHits.atom.DrawerContentHentai
import com.client.xvideos.l.ui.screens.explorer.tab.albumTopHits.atom.DrawerContentManga
import com.client.xvideos.l.ui.screens.explorer.tab.albumTopHits.atom.DrawerContentPorn
import com.client.xvideos.l.ui.screens.explorer.tab.albumTopHits.atom.ScreenLRootBottomNavigator
import com.client.xvideos.l.ui.screens.SelectIndex
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ExperimentalZoomableApi

object ScreenLAlbumTopHits : Screen {

    override val key: ScreenKey = uniqueScreenKey

    private fun readResolve(): Any = ScreenLAlbumTopHits

    @OptIn(ExperimentalZoomableApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val vm = getScreenModel<ScreenLAlbumTopHitsSM, ScreenLAlbumTopHitsSM.Factory> { factory ->
            factory.create(0)
        }

        val items = vm.albumTopHits.collectAsState().value?.items

        val haptic = LocalHapticFeedback.current

        //val vm: ScreenLRootSM = getScreenModel()

        var dialogExpanded by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        var selectIndexDrawer by remember { mutableStateOf(SelectIndex.Unselect) }



        Scaffold(
            bottomBar = {
                ScreenLRootBottomNavigator(
                    selectIndexDrawer,
                    onSelected = {
                        selectIndexDrawer = it
                        dialogExpanded = true
                    }
                )
            },


            containerColor = ThemeL.greyBackground,
        ) {


            if (dialogExpanded) {
                Dialog(
                    onDismissRequest = { dialogExpanded = false }
                ) {

                    Box(
                        modifier = Modifier
                            .padding(bottom = 96.dp, top = 16.dp)
                            .fillMaxWidth(0.9f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, ThemeL.grey0, RoundedCornerShape(16.dp))
                            .background(ThemeL.grey6)
                            .padding(horizontal = 24.dp)
                            .padding(vertical = 16.dp)
                    ) {
                        when (selectIndexDrawer) {
                            SelectIndex.Default -> DrawerContentDefault()
                            SelectIndex.Manga -> DrawerContentManga()
                            SelectIndex.Hentai -> DrawerContentHentai()
                            SelectIndex.Porn -> DrawerContentPorn()
                            else -> {}
                        }
                    }

                }
            }




            LazyColumn(state = rememberLazyListState()) {

                items(items?.size ?: 0) { index ->
                    val item = items?.get(index)
                    if (item == null) return@items

                    Text(
                        item.title,
                        color = ThemeL.textColor,
                        fontSize = 24.sp,
                        fontFamily = ThemeL.fontFamilyKarla,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp, top = 16.dp)
                    )

                    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

                    FlowRow(
                        maxItemsInEachRow = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp), horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val itemWidth = (screenWidth - 8.dp) / 3  // учитываем padding
                        item.items.dropLast(1).forEach { item ->
                            Box(
                                modifier = Modifier
                                    .width(itemWidth)
                                    .padding(vertical = 2.dp)
                            ) {
                                AlbumListItem(
                                    modifier = Modifier.fillMaxWidth(),
                                    title = item.title,
                                    coverUrl = item.cover.url,
                                    numberOfAnimatedPictures = item.numberOfAnimatedPictures,
                                    numberOfPictures = item.numberOfPictures,
                                    onClick = { navigator.push(ScreenLAlbum(item.id.toLong())) }
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .padding(horizontal = 4.dp)
                            .fillMaxWidth()
                            .height(32.dp)
                            .border(1.dp, ThemeL.grey3, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "See All >",
                            color = ThemeL.textColor,
                            modifier = Modifier,
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontFamily = ThemeL.fontFamilyKarla,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

class ScreenLAlbumTopHitsSM @AssistedInject constructor(
    @Assisted val idAlbum: Long,
    val luscious: Luscious
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(idAlbum: Long): ScreenLAlbumTopHitsSM
    }

    var albumTopHits = MutableStateFlow<AlbumTopHitsImpl?>(null)

    init {
        screenModelScope.launch {
            albumTopHits.value = luscious.getAlbumTopHits()
        }
    }

}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLAlbumTopHits {

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(ScreenLAlbumTopHitsSM.Factory::class)
    abstract fun bindHiltProfilesScreenModelFactory(
        hiltDetailsScreenModelFactory: ScreenLAlbumTopHitsSM.Factory
    ): ScreenModelFactory

}