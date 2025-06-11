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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.client.xvideos.screens_red.common.lazyrow123.LazyRow123
import com.client.xvideos.screens_red.common.users.UsersRed
import com.client.xvideos.screens_red.niche.atom.NichePreview
import com.client.xvideos.screens_red.niche.atom.NicheProfile
import com.client.xvideos.screens_red.niche.atom.NicheTopCreator
import com.client.xvideos.screens_red.niche.model.SortByNiches
import com.client.xvideos.screens_red.profile.ScreenRedProfile
import com.client.xvideos.screens_red.profile.ScreenRedProfileSM
import com.client.xvideos.screens_red.profile.atom.RedProfileCreaterInfo
import com.client.xvideos.screens_red.top_this_week.model.SortTop


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

        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color(0xFF0F0F0F)) {

            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(4.dp) // Отступы по краям сетки
            ) {

                item(key = "profile", span = { GridItemSpan(maxLineSpan) }) {

                    UrlImage(
                        "https://www.redgifs.com/static/DEFAULT_NICHE_BACKGROUND-BmUEhMGK.png",
                        modifier = Modifier.height(110.dp),
                        contentScale = ContentScale.FillHeight
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF0F0F0F), Color.Transparent),
                                    startY = Float.POSITIVE_INFINITY, // снизу
                                    endY = 0f // вверх
                                )
                            )
                    )

                    NicheProfile(vm.niche)
                }

                item(key = "profile1", span = { GridItemSpan(maxLineSpan) }) {
                    LazyRow {
                        items(vm.related.niches.size) {
                            NichePreview(vm.related.niches[it])
                        }
                    }
                }

                item(key = "topCreator", span = { GridItemSpan(maxLineSpan) }) {
                    LazyRow {
                        items(vm.topCreator.creators) {
                            NicheTopCreator(
                                it,
                                onClick = { navigator.push(ScreenRedProfile(it.username)) })
                        }
                    }
                }

                item(key = "info", span = { GridItemSpan(maxLineSpan) }) {
                    SortNichesByTop(
                        listOf(
                            SortByNiches.TRENDING,
                            SortByNiches.TOP,
                            SortByNiches.LATEST,
                        ), sort, onSelect = { vm.changeSortType(it) })
                }

                item(key = "grid", span = { GridItemSpan(maxLineSpan) }) {
                    LazyRow123(
                        columns = 2,
                        listGifs = items,
                        listUsers = UsersRed.listAllUsers,
                        modifier = Modifier.fillMaxWidth().height(1000.dp),
                        onClickOpenProfile = {
                            vm.currentIndexGoto =  vm.currentIndex;
                            navigator.push(ScreenRedProfile(it)) },
                        onCurrentPosition = { index ->
                            vm.currentIndex = index
                        },
                        gotoPosition = vm.currentIndexGoto,
                        option = vm.expandMenuVideoList,
                        onRefresh = {
                            val temp = vm.sortType.value
                            vm.changeSortType(SortByNiches.FORCE_TEMP)
                            vm.changeSortType(temp)
                        },
                        isConnected = isConnected
                    )
                }


            }


        }


    }

}