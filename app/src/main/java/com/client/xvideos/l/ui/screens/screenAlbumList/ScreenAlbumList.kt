package com.client.xvideos.l.ui.screens.screenAlbumList

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
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
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.ui.element.AlbumListItem
import com.client.xvideos.l.ui.screens.screenAlbum.ScreenLAlbum
import com.client.xvideos.l.ui.screens.screenAlbumList.bottomBar.AlbumListBottomBar
import com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.AlbumListFilter
import kotlinx.coroutines.launch
import my.nanihadesuka.compose.LazyVerticalGridScrollbar
import my.nanihadesuka.compose.ScrollbarSettings
import net.engawapg.lib.zoomable.ExperimentalZoomableApi
import timber.log.Timber


class DefaultPagerState1(
    currentPage: Int,
    currentPageOffsetFraction: Float,
    updatedPageCount: () -> Int,
) : PagerState(currentPage, currentPageOffsetFraction) {

    var pageCountState = mutableStateOf(updatedPageCount)
    override val pageCount: Int
        get() = pageCountState.value.invoke()

    companion object {
        /** To keep current page and current page offset saved */
        val Saver: Saver<DefaultPagerState1, *> =
            listSaver(
                save = {
                    listOf(
                        it.currentPage,
                        (it.currentPageOffsetFraction).coerceIn(-0.5f, 0.5f),
                        it.pageCount,
                    )
                },
                restore = {
                    DefaultPagerState1(
                        currentPage = it[0] as Int,
                        currentPageOffsetFraction = it[1] as Float,
                        updatedPageCount = { it[2] as Int },
                    )
                },
            )
    }
}












object ScreenLAlbumList : Screen {

    private fun readResolve(): Any = ScreenLAlbumList



    @OptIn(ExperimentalZoomableApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val vm = getScreenModel<ScreenLAlbumListSM, ScreenLAlbumListSM.Factory> { factory -> factory.create(null) }
        val bigList = vm.bigList
        val info = vm.info.collectAsStateWithLifecycle().value
        val currentFilter = vm.filter.collectAsStateWithLifecycle().value
        val isRequest = vm.isRequest.collectAsStateWithLifecycle().value
        val filterGCount = vm.filterGenreStateCount.collectAsStateWithLifecycle().value
        val filterTagsCount = vm.filterTaggedStateCount.collectAsStateWithLifecycle().value
        val haptic = LocalHapticFeedback.current
        val scope = rememberCoroutineScope()

        var totalPages by remember { mutableIntStateOf(1) }

        LaunchedEffect(info) { totalPages = info?.totalPages ?: 1 }

       // val statePager = rememberPagerState(initialPage = 9999, pageCount = { totalPages })

        LaunchedEffect(vm.statePager.currentPage) {
            vm.statePager.pageCountState.value = { totalPages }
            vm.savedPagerPage = vm.statePager.currentPage
            val currentPage = vm.statePager.currentPage
            val pagesToLoad = setOf( maxOf(0, currentPage - 1), currentPage, minOf(vm.statePager.pageCount - 1, currentPage + 1), minOf(vm.statePager.pageCount - 1, currentPage + 2) )
            pagesToLoad.forEach { page -> vm.loadAlbumList(page) }
        }

        ModalNavigationDrawer(
            drawerState = vm.drawerState,
            drawerContent = {
                // Filter overlay
                if (currentFilter != null) {
                    Box(modifier = Modifier) {
                        AlbumListFilter(
                            filter = currentFilter,
                            filterGCount = filterGCount,
                            filterTagsCount = filterTagsCount,
                            onClose = { })
                        { newFilter ->
                            vm.screenModelScope.launch {
                                vm.stateGrid.clear()
                                vm.statePager.scrollToPage(0)
                                vm.filterUpdate(newFilter)
                                vm.loadInitialData()
                            }
                        }
                    }
                }
            },
            scrimColor = Color.Transparent,
            modifier = Modifier.padding(top = 4.dp).fillMaxSize()
        )
        {
            Scaffold(
                topBar = {

                    if (isRequest) {
//                            val animColor by infiniteTransition.animateColor(
//                                initialValue = Color.Transparent,
//                                targetValue = Color(0xff0c94ff), // оранжевый
//                                animationSpec = infiniteRepeatable(
//                                    animation = tween(100),
//                                    repeatMode = RepeatMode.Reverse
//                                ),
//                                label = "colorAnim"
//                            )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(Color(0xff0c94ff)), contentAlignment = Alignment.Center
                        ) {}
                    }

                },
                bottomBar = {
                    AlbumListBottomBar(
                        onClickVisibleFilter = { scope.launch { vm.drawerState.open() } },
                        currentPage = vm.statePager.currentPage, totalPages = info?.totalPages ?: 1,
                        onChange = {
                            scope.launch { vm.statePager.scrollToPage(it) }
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            vm.loadAlbumList(it)
                        }
                    )
                },
                containerColor = ThemeL.greyBackground
            )
            { padding ->

                HorizontalPager(
                    vm.statePager,
                    Modifier.padding(bottom = padding.calculateBottomPadding()).fillMaxSize(),
                    beyondViewportPageCount = 1,
                    // Добавляем ключ для страниц пейджера
                    key = { page -> "${key}_page_$page" }
                )
                { page ->

                    val items = bigList[page]?.albumListImplInfoAndList?.items

                    val stateGrid  = if (vm.stateGrid.containsKey(page)) {
                        vm.stateGrid[page]!!
                    } else
                    {
                        vm.stateGrid.put(page, LazyGridState())
                        vm.stateGrid[page]!!
                    }

                    //val stateGrid =  vm.stateGrid.get(page)         //rememberLazyGridState()

                    LazyVerticalGridScrollbar(
                        state = stateGrid,
                        settings = ScrollbarSettings.Default.copy(
                            thumbUnselectedColor = Color(0xFFA3A3A3),
                            thumbSelectedColor = Color(0xFFB3B3B3),
                            thumbThickness = 3.dp,
                            scrollbarPadding = 0.dp,
                            alwaysShowScrollbar = true
                        )
                    ) {

                        LazyVerticalGrid(
                            state = stateGrid, modifier = Modifier.fillMaxSize(),
                            columns = GridCells.Fixed(2)
                        )
                        {

                            item(key = "dummy", span = { GridItemSpan(maxLineSpan) }) {
                                Spacer(Modifier.height(32.dp).background(ThemeL.red))
                            }

//                                item(key = "page_selector", span = { GridItemSpan(maxLineSpan) })
//                                {
//                                    if (info != null) {
//                                        Box(
//                                            Modifier.padding(vertical = 4.dp, horizontal = 4.dp),
//                                            contentAlignment = Alignment.Center
//                                        ) {
//                                            AlbumListPageSelector(page, info.totalPages) {
//                                                scope.launch {
//                                                    statePager.scrollToPage(it)
//                                                }
//                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
//                                                vm.loadAlbumList(it)
//                                            }
//                                        }
//                                    }
//                                }

                            items(
                                items?.size ?: 0,
                                key = { items?.get(it)?.id!! }) { index ->
                                val item = items?.get(index)
                                if (item != null) {
                                    Box(
                                        Modifier.padding(
                                            vertical = 4.dp,
                                            horizontal = 4.dp
                                        ),
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
                        }
                    }
                }

            }
        }
    }
}



