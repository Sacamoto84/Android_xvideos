package com.client.xvideos.redgifs.ui.ui.lazyrow123

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.navigator.LocalNavigator
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.common.UsersRed
import com.client.xvideos.redgifs.common.video.player_row_mini.RedUrlVideoImageAndLongClick
import com.client.xvideos.redgifs.model.GifsInfo
import com.client.xvideos.redgifs.model.URL1
import com.client.xvideos.redgifs.ui.explorer.ScreenRedExplorer
import com.client.xvideos.redgifs.ui.fullscreen.ScreenRedFullScreen
import com.client.xvideos.redgifs.ui.top_this_week.ProfileInfo1
import com.redgifs.common.block.ui.DialogBlock
import com.redgifs.common.expand_menu_video.ExpandMenuVideo
import com.redgifs.common.expand_menu_video.ExpandMenuVideoTags
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyRow123(
    host: LazyRow123Host,
    modifier: Modifier = Modifier,
    onClickOpenProfile: (String) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(0.dp),
    contentBeforeList: @Composable (() -> Unit) = {},
    //Для меню
    isRunLike: Boolean = false,
    onAppendLoaded: (LazyPagingItems<GifsInfo>) -> Unit = {},
) {
    val listGifs = host.pager.collectAsLazyPagingItems() as LazyPagingItems<GifsInfo>

    LazyRow123Content(
        host = host,
        listGifs = listGifs,
        modifier = modifier,
        onClickOpenProfile = onClickOpenProfile,
        contentPadding = contentPadding,
        contentBeforeList = contentBeforeList,
        isRunLike = isRunLike,
        onAppendLoaded = onAppendLoaded
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyRow123Content(
    host: LazyRow123Host,
    listGifs: LazyPagingItems<GifsInfo>,
    modifier: Modifier = Modifier,
    onClickOpenProfile: (String) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(0.dp),
    contentBeforeList: @Composable (() -> Unit) = {},
    isRunLike: Boolean = false,
    onAppendLoaded: (LazyPagingItems<GifsInfo>) -> Unit = {},
) {
    SideEffect { Timber.d("!!! LazyRow123::SideEffect columns: ${host.columns} : $listGifs") }

    val isConnected by host.isConnected.collectAsStateWithLifecycle()

    val state = host.state

    var blockItem by remember { mutableStateOf<GifsInfo?>(null) }

    val navigator = LocalNavigator.current


    /**
     * Отображения индикатора первой загрузки
     */
    val isInitialLoading = listGifs.loadState.refresh is LoadState.Loading && listGifs.itemCount == 0

    val block = host.hostDI.block

    val downloadList = host.hostDI.downloadRed.downloadList.collectAsState().value

    val loadState = listGifs.loadState

    var wasAppendLoading by remember { mutableStateOf(false) }

    var wasDataLoaded by remember { mutableStateOf(false) }

    var isIndicatorLoading by remember { mutableStateOf(true) }
    var isIndicatorError by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }

    // 1. REFRESH - начальная загрузка / обновление всего списка
    LaunchedEffect(loadState.refresh) {

        if (loadState.refresh is LoadState.NotLoading) {
            isIndicatorLoading = false
            isIndicatorError = false
            wasDataLoaded = true
            onAppendLoaded(listGifs)
        }

        // Loading is in progress.
        if (loadState.refresh is LoadState.Loading) {
            isIndicatorLoading = true
            isIndicatorError = false
        }

        // Loading is in progress.
        if (loadState.refresh is LoadState.Error) {

            val error = (loadState.refresh as LoadState.Error).error
            errorMessage = error.message ?: "Неизвестная ошибка"
            Timber.i(errorMessage)
            isIndicatorLoading = false
            isIndicatorError = true
        }

    }

//    if (!wasDataLoaded) {
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            CircularProgressIndicator()
//        }; return
//    }

    LaunchedEffect(loadState.append) {
        if (loadState.append is LoadState.Loading && !wasAppendLoading) {
            wasAppendLoading = true
            onAppendLoaded(listGifs)
        }
        if (loadState.append !is LoadState.Loading) {
            wasAppendLoading = false
        }
    }

    if (block.blockVisibleDialog) {
        DialogBlock(
            visible = block.blockVisibleDialog,
            onDismiss = { block.blockVisibleDialog = false },
            onBlockConfirmed = {
                if ((blockItem != null)) {
                    block.blockItem(blockItem!!)
                    listGifs.refresh()
                    blockItem = null
                }
            }
        )
    }

    Box(modifier.fillMaxSize()) {

        if (host.columns in 1..4) {

            if (listGifs.itemCount > 0) {
                LazyVerticalGrid(
                    state = state,
                    columns = GridCells.Fixed(host.columns),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = contentPadding,
                )
                {
                    item( key = "before", span = { GridItemSpan(maxLineSpan) }) { contentBeforeList() }

                    items(
                        count = listGifs.itemCount, key = { index -> listGifs[index]?.id ?: index}
                    ) { index ->
                        var isVideo by remember { mutableStateOf(false) }
                        val item = listGifs[index]
                        if (item != null) {
                            Box(
                                modifier = Modifier.padding(vertical = 1.dp).padding(horizontal = 1.dp).fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                RedUrlVideoImageAndLongClick(
                                    item, index,
                                    onLongClick = {
                                        blockItem = item
                                        navigator?.push(ScreenRedFullScreen(item))
                                    },
                                    onVideo = { isVideo = it },
                                    isVisibleView = false,
                                    isVisibleDuration = false,
                                    play = false,
                                    isNetConnected = isConnected,
                                    onFullScreen = {
                                        blockItem = item
                                        navigator?.push(ScreenRedFullScreen(item))
                                    },
                                    downloadRed = host.hostDI.downloadRed,
                                )

                                Column(modifier = Modifier.align(Alignment.TopEnd)) {

                                    ExpandMenuVideo(
                                        item = item,
                                        modifier = Modifier,
                                        onClick = { blockItem = item },
                                        onRunLike = {
                                            if (isRunLike) {
                                                listGifs.refresh()
                                            }
                                        },
                                        onRefresh = { listGifs.refresh() },
                                        host.isCollection,
                                        {block},
                                        {host.hostDI.redApi},
                                        {host.hostDI.savedRed},
                                        downloadRed = {host.hostDI.downloadRed}
                                    )

                                    if (item.tags.isNotEmpty()) {
                                        ExpandMenuVideoTags(
                                            item = item,
                                            modifier = Modifier,
                                            onClick = { it1 ->
                                                host.hostDI.search.searchText.value =
                                                    TextFieldValue(
                                                        text = it1,
                                                        selection = TextRange(it1.length)
                                                    )
                                                host.hostDI.search.searchTextDone.value = it1
                                                ScreenRedExplorer.screenType = 0
                                                navigator?.popAll()
                                            }
                                        )
                                    }
                                }

                                AnimatedVisibility(
                                    !isVideo,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomStart),
                                    enter = slideInVertically(
                                        initialOffsetY = { fullHeight -> fullHeight },
                                        animationSpec = tween(durationMillis = 200)
                                    ),
                                    exit = slideOutVertically(
                                        targetOffsetY = { fullHeight -> fullHeight },
                                        animationSpec = tween(durationMillis = 200)
                                    )
                                ) {
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        if (host.visibleProfileInfo) {
                                            ProfileInfo1(
                                                modifier = Modifier
                                                    .padding(start = 2.dp, bottom = 2.dp)
                                                    .align(Alignment.BottomStart),
                                                onClick = { onClickOpenProfile(item.userName) },
                                                videoItem = item,
                                                listUsers = UsersRed.listAllUsers,
                                                visibleUserName = host.columns <= 2,
                                                sizeIcon = 36.dp,
                                                cornerRadius = 8.dp,
                                                verticalAlignment = Alignment.Top
                                            )
                                        }
                                        LazyRow123Icons(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .offset(2.dp, 2.dp),
                                            host.hostDI.savedRed,
                                            item,
                                            downloadList
                                        )
                                    }
                                }

                            }
                        }
                    }
                }
            }

        } else  {
            val pageCount = listGifs.itemCount
            val statePager = rememberPagerState { pageCount }

            VerticalPager(
                state = statePager,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 2
            )
            { index ->
                val item = listGifs[index]
                var isVideo by remember { mutableStateOf(false) }

                if (item == null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .padding(horizontal = 2.dp)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {

                        RedUrlVideoImageAndLongClick(
                            item,
                            index,
                            onLongClick = {
                                blockItem = item
                                navigator?.push(ScreenRedFullScreen(item))
                            },
                            onVideo = { isVideo = it },
                            isVisibleView = false,
                            isVisibleDuration = false,
                            play = index == statePager.currentPage,
                            isNetConnected = isConnected,
                            onFullScreen = {
                                blockItem = item
                                navigator?.push(ScreenRedFullScreen(item))
                            },
                            downloadRed = host.hostDI.downloadRed,
                        )

                        Column(modifier = Modifier.align(Alignment.TopEnd)) {
                            ExpandMenuVideo( item = item, modifier = Modifier, onClick = { blockItem = item }, onRunLike = { if (isRunLike) { listGifs.refresh() } },
                                onRefresh = { listGifs.refresh() }, host.isCollection, {block}, {host.hostDI.redApi}, {host.hostDI.savedRed}, downloadRed = {host.hostDI.downloadRed} )

                            if (item.tags.isNotEmpty()) {
                                ExpandMenuVideoTags( item = item, modifier = Modifier,
                                    onClick = { it1 ->
                                        host.hostDI.search.searchText.value = TextFieldValue( text = it1, selection = TextRange(it1.length) )
                                        host.hostDI.search.searchTextDone.value = it1
                                        ScreenRedExplorer.screenType = 0
                                        navigator?.popAll()
                                    }
                                )
                            }
                        }

                        AnimatedVisibility( !isVideo, modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth(),
                            enter = slideInVertically( initialOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(durationMillis = 200)
                            ), exit = slideOutVertically( targetOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(durationMillis = 200) ) ) {
                            if (host.visibleProfileInfo) {
                                ProfileInfo1(
                                    modifier = Modifier
                                        .padding(
                                            start = 2.dp,
                                            bottom = 2.dp
                                        )
                                        .align(Alignment.BottomStart),
                                    onClick = { onClickOpenProfile(item.userName) },
                                    videoItem = item,
                                    listUsers = UsersRed.listAllUsers,
                                    visibleUserName = true,
                                    sizeIcon = 32.dp,
                                    cornerRadius = 8.dp
                                )
                            }
                            LazyRow123Icons( modifier = Modifier.align(Alignment.BottomCenter), host.hostDI.savedRed, item, downloadList )
                        }
                    }
                }
            }

        }

        //Индикаторы

        if (isIndicatorLoading) {
            Box( modifier = modifier.align(Alignment.Center).offset(0.dp, 40.dp)
                , contentAlignment = Alignment.Center ) { CircularProgressIndicator() }
        }

        if (isIndicatorError) {
            Box( modifier = modifier.align(Alignment.Center).offset(0.dp, 40.dp)
                , contentAlignment = Alignment.Center ) {
                //CircularProgressIndicator(color = ThemeRed.colorRed)
                Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(ThemeRed.colorRed)){ }
                Text(errorMessage, color = Color.White)
            }
        }

//            if (listGifs.loadState.append is LoadState.Loading && listGifs.itemCount > 0) { Box( modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(16.dp) ) { CircularProgressIndicator(color = ThemeRed.colorYellow) } }

    }
}

@Preview(showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun PreviewLazyRow123Content() {
    val sampleGifs = listOf(
        GifsInfo(
            id = "1",
            userName = "User 1",
            tags = listOf("tag1", "tag2"),
            urls = URL1(thumbnail = "https://media.redgifs.com/FamousIdleWasp-poster.jpg")
        ),
        GifsInfo(
            id = "2",
            userName = "User 2",
            tags = listOf("tag3"),
            urls = URL1(thumbnail = "https://media.redgifs.com/FamousIdleWasp-poster.jpg")
        ),
        GifsInfo(
            id = "3",
            userName = "User 3",
            tags = listOf("tag4"),
            urls = URL1(thumbnail = "https://media.redgifs.com/FamousIdleWasp-poster.jpg")
        )
    )
    val pagingData = PagingData.from(sampleGifs)
    val listGifs = flowOf(pagingData).collectAsLazyPagingItems()

    // LazyRow123Host and its dependencies are hard to mock without the full context.
    // This is a placeholder for the preview. In a real scenario, you would provide
    // a mock or fake implementation of LazyRow123Host.

    // Note: Due to the complexity of the HostDI and other classes, 
    // a functional preview would require significant mocking of the business logic layers.
}
