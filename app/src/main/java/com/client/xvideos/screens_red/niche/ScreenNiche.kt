package com.client.xvideos.screens_red.niche

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.client.xvideos.screens_red.common.lazyrow123.LazyRow123
import com.client.xvideos.screens_red.common.sortByOrder.SortByOrder
import com.client.xvideos.screens_red.common.users.UsersRed
import com.client.xvideos.screens_red.niche.atom.NichePreview
import com.client.xvideos.screens_red.niche.atom.NicheProfile
import com.client.xvideos.screens_red.niche.atom.NicheTopCreator
import com.client.xvideos.screens_red.profile.ScreenRedProfile
import com.client.xvideos.screens_red.profile.ScreenRedProfileSM
import com.client.xvideos.screens_red.profile.atom.RedProfileCreaterInfo
import com.client.xvideos.screens_red.top_this_week.model.SortTop
import kotlin.math.roundToInt


class ScreenRedNiche(val nicheName: String = "pumped-pussy") : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedBoxWithConstraintsScope")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val vm = getScreenModel<ScreenNicheSM, ScreenNicheSM.Factory> { factory ->
            factory.create(nicheName)
        }

        val gridState = rememberLazyGridState()

        val sort = vm.sortType.collectAsStateWithLifecycle().value

        val items = vm.pager.collectAsLazyPagingItems()
        val isConnected by vm.isConnected.collectAsStateWithLifecycle()


        val toolbarHeight = 50.dp
        val minToolbarHeight = 0.dp // высота лишь третьей строки
        val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.toPx() }
        val minToolbarHeightPx = with(LocalDensity.current) { minToolbarHeight.toPx() }

        val offsetY = remember { mutableFloatStateOf(0f) }

        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val delta = available.y
                    val newOffset = (offsetY.value + delta).coerceIn(
                        -(toolbarHeightPx - minToolbarHeightPx),
                        0f
                    )
                    offsetY.value = newOffset
                    return Offset.Zero
                }
            }
        }

        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color(0xFF0F0F0F)) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(nestedScrollConnection)
            ) {

                LazyRow123(
                    columns = 2,
                    listGifs = items,
                    listUsers = UsersRed.listAllUsers,
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    onClickOpenProfile = {
                        vm.currentIndexGoto = vm.currentIndex;
                        navigator.push(ScreenRedProfile(it))
                    },
                    onCurrentPosition = { index ->
                        vm.currentIndex = index
                    },
                    gotoPosition = vm.currentIndexGoto,
                    option = vm.expandMenuVideoList,
                    onRefresh = {
                        val temp = vm.sortType.value
                        vm.changeSortType(Order.FORCE_TEMP)
                        vm.changeSortType(temp)
                    },
                    isConnected = isConnected,
                    contentPadding = PaddingValues(top = toolbarHeight),
                    contentBeforeList = {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F0F0F))) {

                            NicheProfile(vm.niche)
                            Text("Related Niches", color = Color.White, modifier = Modifier.padding(start = 16.dp, top = 16.dp))
                            LazyRow {
                                items(vm.related.niches.size) {
                                    NichePreview(vm.related.niches[it])
                                }
                            }

                            Text("✨ Top Creators in ${vm.niche.name}", color = Color.White, modifier = Modifier.padding(start = 16.dp, top = 16.dp))
                            LazyRow {
                                items(vm.topCreator.creators) {
                                    NicheTopCreator(it, onClick = { navigator.push(ScreenRedProfile(it.username)) })
                                }
                            }

                        }
                    }
                )

            }

        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(toolbarHeight)
                .offset { IntOffset(x = 0, y = offsetY.floatValue.roundToInt()) }
                .background(MaterialTheme.colorScheme.primaryContainer)) {

            SortByOrder(
                listOf(
                    Order.TRENDING, Order.TOP,
                    Order.LATEST,
                ), sort, onSelect = { vm.changeSortType(it) })

        }

    }

}