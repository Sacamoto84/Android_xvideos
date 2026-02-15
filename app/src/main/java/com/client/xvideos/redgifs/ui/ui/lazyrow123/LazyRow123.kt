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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import cafe.adriel.voyager.navigator.LocalNavigator
import com.client.xvideos.redgifs.common.UsersRed
import com.client.xvideos.redgifs.common.video.player_row_mini.RedUrlVideoImageAndLongClick
import com.client.xvideos.redgifs.model.GifsInfo
import com.client.xvideos.redgifs.ui.explorer.ScreenRedExplorer
import com.client.xvideos.redgifs.ui.fullscreen.ScreenRedFullScreen
import com.client.xvideos.redgifs.ui.top_this_week.ProfileInfo1
import com.redgifs.common.block.ui.DialogBlock
import com.redgifs.common.expand_menu_video.ExpandMenuVideo
import com.redgifs.common.expand_menu_video.ExpandMenuVideoTags
import timber.log.Timber

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyRow123(
    host: LazyRow123Host,
    modifier: Modifier = Modifier,
    onClickOpenProfile: (String) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(0.dp),
    contentBeforeList: @Composable (() -> Unit) = {},
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
    SideEffect { Timber.d("!!! LazyRow123::SideEffect columns: ${host.columns}") }

    val isConnected by host.isConnected.collectAsStateWithLifecycle()
    val state = host.state
    var blockItem by remember { mutableStateOf<GifsInfo?>(null) }
    val navigator = LocalNavigator.current
    val block = host.hostDI.block
    val downloadList by host.hostDI.downloadRed.downloadList.collectAsState()
    val loadState = listGifs.loadState
    var wasAppendLoading by remember { mutableStateOf(false) }

    LaunchedEffect(loadState.refresh) {
        if (loadState.refresh is LoadState.NotLoading) {
            onAppendLoaded(listGifs)
        }
    }

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
                blockItem?.let {
                    block.blockItem(it)
                    listGifs.refresh()
                    blockItem = null
                }
            }
        )
    }

    Box(modifier.fillMaxSize()) {
        if (host.columns in 1..4) {
            LazyVerticalGrid(
                state = state,
                columns = GridCells.Fixed(host.columns),
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding,
            ) {
                item(key = "before", span = { GridItemSpan(maxLineSpan) }) { contentBeforeList() }

                items(
                    count = listGifs.itemCount,
                    key = listGifs.itemKey { it.id },
                    contentType = { "video_grid_item" }
                ) { index ->
                    listGifs[index]?.let { item ->
                        val isDownloaded = remember(item.id, downloadList) {
                            downloadList.any { it.id == item.id }
                        }
                        
                        LazyRow123GridItem(
                            item = item,
                            index = index,
                            host = host,
                            isConnected = isConnected,
                            isDownloaded = isDownloaded,
                            isRunLike = isRunLike,
                            onItemClick = {
                                blockItem = item
                                navigator?.push(ScreenRedFullScreen(item))
                            },
                            onRefresh = { listGifs.refresh() },
                            onClickOpenProfile = onClickOpenProfile,
                            onTagClick = { tag ->
                                host.hostDI.search.searchText.value = TextFieldValue(tag, TextRange(tag.length))
                                host.hostDI.search.searchTextDone.value = tag
                                ScreenRedExplorer.screenType = 0
                                navigator?.popAll()
                            }
                        )
                    }
                }
            }
        } else {
            val statePager = rememberPagerState { listGifs.itemCount }
            VerticalPager(state = statePager, modifier = Modifier.fillMaxSize(), beyondViewportPageCount = 1) { index ->
                listGifs[index]?.let { item ->
                     Box(
                        modifier = Modifier.padding(vertical = 2.dp).padding(horizontal = 2.dp).fillMaxSize().clip(RoundedCornerShape(12.dp)).border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        RedUrlVideoImageAndLongClick(
                            item = item,
                            index = index,
                            onLongClick = { navigator?.push(ScreenRedFullScreen(item)) },
                            isVisibleView = false,
                            isVisibleDuration = false,
                            play = true,
                            isNetConnected = isConnected,
                            onFullScreen = { navigator?.push(ScreenRedFullScreen(item)) },
                            downloadRed = { host.hostDI.downloadRed },
                        )
                    }
                } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }
        }
    }
}

@Composable
fun LazyRow123GridItem(
    item: GifsInfo,
    index: Int,
    host: LazyRow123Host,
    isConnected: Boolean,
    isDownloaded: Boolean,
    isRunLike: Boolean,
    onItemClick: () -> Unit,
    onRefresh: () -> Unit,
    onClickOpenProfile: (String) -> Unit,
    onTagClick: (String) -> Unit
) {
    var isVideo by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(1.dp)
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        RedUrlVideoImageAndLongClick(
            item = item,
            index = index,
            onLongClick = onItemClick,
            onVideo = { isVideo = it },
            isVisibleView = false,
            isVisibleDuration = false,
            play = false,
            isNetConnected = isConnected,
            onFullScreen = onItemClick,
            downloadRed = { host.hostDI.downloadRed },
        )

        Column(modifier = Modifier.align(Alignment.TopEnd)) {
            ExpandMenuVideo(
                item = item,
                onRunLike = { if (isRunLike) onRefresh() },
                onRefresh = onRefresh,
                isCollection = host.isCollection,
                block = { host.hostDI.block },
                redApi = { host.hostDI.redApi },
                savedRed = { host.hostDI.savedRed },
                downloadRed = { host.hostDI.downloadRed }
            )

            if (item.tags.isNotEmpty()) {
                ExpandMenuVideoTags(item = item, onClick = onTagClick)
            }
        }

        AnimatedVisibility(
            visible = !isVideo,
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart),
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(200)),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(200))
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (host.visibleProfileInfo) {
                    ProfileInfo1(
                        modifier = Modifier.padding(start = 2.dp, bottom = 2.dp).align(Alignment.BottomStart),
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
                    modifier = Modifier.align(Alignment.BottomEnd).offset(2.dp, 2.dp),
                    savedRed = { host.hostDI.savedRed },
                    item = item,
                    isDownloaded = isDownloaded
                )
            }
        }
    }
}
