package com.client.xvideos.l.ui.screens.screenAlbumList

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.l.model.AlbumListFilter
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.ui.element.AlbumListItem
import com.client.xvideos.l.ui.screens.screenAlbum.ScreenLAlbum
import com.client.xvideos.l.ui.screens.screenAlbumList.atom.AlbumListPageSelector
import com.client.xvideos.l.ui.screens.screenAlbumList.bottomBar.AlbumListBottomBar
import com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.AlbumListFilter
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ExperimentalZoomableApi

object ScreenLAlbumList {

    // Сохраняем первый экземпляр навсегда
    private val firstInstance: Screen by lazy {
        ScreenLAlbumListImpl(filter = null, isFirst = true)
    }

    private var instanceCounter = 0

    // Получить первый экземпляр (всегда живой)
    fun getFirst(): Screen = firstInstance

    // Создать новый экземпляр
    fun create(filter: AlbumListFilter? = null): Screen {
        instanceCounter++
        return ScreenLAlbumListImpl(filter = filter, isFirst = false, instanceId = instanceCounter)
    }

    // Создать экземпляр или вернуть первый
    fun getInstance(filter: AlbumListFilter? = null, useFirst: Boolean = false): Screen {
        return if (useFirst && filter == null) {
            firstInstance
        } else {
            create(filter)
        }
    }

    class ScreenLAlbumListImpl(
        val filter: AlbumListFilter?,
        private val isFirst: Boolean = false,
        private val instanceId: Int = 0
    ) : Screen {

        override val key: ScreenKey = if (isFirst) {
            "ScreenLAlbumList_FIRST" // Уникальный ключ для первого экземпляра
        } else {
            "ScreenLAlbumList_$instanceId" // Уникальные ключи для остальных
        }

        @OptIn(ExperimentalZoomableApi::class)
        @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
        @Composable
        override fun Content() {

            val navigator = LocalNavigator.currentOrThrow
            val vm = getScreenModel<ScreenLAlbumListSM, ScreenLAlbumListSM.Factory> { factory ->
                factory.create(filter)
            }

            //val items = vm.albumList.collectAsStateWithLifecycle().value?.items

            val bigList = vm.bigList

            val info = vm.info.collectAsStateWithLifecycle().value

            val currentFilter = vm.filter.collectAsStateWithLifecycle().value

            val isRefreshing = vm.isRefreshing.collectAsStateWithLifecycle().value

            val filterGCount = vm.filterGenreStateCount.collectAsStateWithLifecycle().value
            val filterTagsCount = vm.filterTaggedStateCount.collectAsStateWithLifecycle().value

            val haptic = LocalHapticFeedback.current
            val scope = rememberCoroutineScope()

            // Pull to refresh state
            val pullToRefreshState = rememberPullToRefreshState()

            var totalPages by remember { mutableIntStateOf(1) }

            val statePager = rememberPagerState(initialPage = 0, pageCount = { totalPages })

            LaunchedEffect(info) {
                totalPages = info?.totalPages ?: 1
            }

            LaunchedEffect(statePager.currentPage) {

                val currentPage = statePager.currentPage

                val pagesToLoad = setOf(
                    maxOf(0, currentPage - 1), // предыдущая
                    currentPage,                // текущая
                    minOf(statePager.pageCount - 1, currentPage + 1) // следующая
                )

                pagesToLoad.forEach { page ->
                    vm.loadAlbumList(page)
                }


            }

            val drawerState = rememberDrawerState(DrawerValue.Closed)

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    // Filter overlay
                    if (currentFilter != null) {
                        Box(modifier = Modifier) {
                            AlbumListFilter(
                                filter = currentFilter,
                                filterGCount = filterGCount,
                                filterTagsCount = filterTagsCount,
                                onClose = {

                                }
                            ) { newFilter ->

                                vm.screenModelScope.launch {
                                    statePager.scrollToPage(0)
                                    vm.filterUpdate(newFilter)
                                    vm.loadInitialData()
                                }

                            }
                        }
                    }
                },
                scrimColor = Color.Transparent,
            )
            {
                Scaffold(
                    bottomBar = {
                        AlbumListBottomBar(
                            onClickVisibleFilter = { scope.launch { drawerState.open() } },
                            onClickPrev = { vm.loadPrevList() },
                            onClickNext = { vm.loadNextList() }
                        )
                    },
                    containerColor = ThemeL.greyBackground
                )
                { padding ->

                    HorizontalPager(
                        statePager,
                        Modifier.padding(bottom = padding.calculateBottomPadding()).fillMaxSize(), beyondViewportPageCount = 1
                    ) { page ->

                        val items = bigList[page]?.items

                        // Wrap LazyVerticalGrid with PullToRefreshBox
                        PullToRefreshBox(
                            isRefreshing = isRefreshing,
                            onRefresh = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                //vm.refreshData()
                            },
                            indicator = {
                                Indicator(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .size(48.dp),
                                    isRefreshing = isRefreshing,
                                    state = pullToRefreshState,
                                    containerColor = ThemeL.grey3,
                                    maxDistance = (96 + 54).dp
                                )
                            },
                            state = pullToRefreshState,
                            modifier = Modifier.fillMaxSize()//.padding(bottom = padding.calculateBottomPadding())
                        )
                        {
                            LazyVerticalGrid(
                                state = rememberLazyGridState(),
                                modifier = Modifier.fillMaxSize(),
                                columns = GridCells.Fixed(2)
                            ) {
                                item(key = "dummy", span = { GridItemSpan(maxLineSpan) }) {
                                    Spacer( Modifier.height(48.dp) )
                                }

                                item(key = "page_selector", span = { GridItemSpan(maxLineSpan) })
                                {
                                    if (info != null) {
                                        Box(
                                            Modifier.padding(vertical = 4.dp, horizontal = 4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AlbumListPageSelector(page, info.totalPages) {
                                                scope.launch {
                                                    statePager.scrollToPage(it)
                                                }
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                vm.loadAlbumList(it)
                                            }
                                        }
                                    }
                                }

                                items(items?.size ?: 0, key = { items?.get(it)?.id!! }) { index ->
                                    val item = items?.get(index)
                                    if (item != null) {
                                        Box(
                                            Modifier.padding(vertical = 4.dp, horizontal = 4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AlbumListItem(
                                                title = item.title,
                                                coverUrl = item.cover.url,
                                                numberOfAnimatedPictures = item.numberOfAnimatedPictures,
                                                numberOfPictures = item.numberOfPictures,
                                            ) {
                                                navigator.push(ScreenLAlbum(item.id.toLong()))
                                            }
                                        }
                                    }
                                }

                                item(
                                    key = "page_selector2",
                                    span = { GridItemSpan(maxLineSpan) }
                                ) {
                                    if (items?.isNotEmpty() == true && info != null) {
                                        AlbumListPageSelector(page, info.totalPages) {
                                            scope.launch {
                                                vm.state.scrollToItem(0)
                                                statePager.scrollToPage(it)
                                            }
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            vm.loadAlbumList(it)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


