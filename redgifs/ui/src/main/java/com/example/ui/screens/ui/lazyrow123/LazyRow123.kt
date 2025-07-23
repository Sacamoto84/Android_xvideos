package com.example.ui.screens.ui.lazyrow123

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.redgifs.model.GifsInfo
import com.redgifs.common.ThemeRed
import com.redgifs.common.block.ui.DialogBlock
import com.redgifs.common.expand_menu_video.ExpandMenuVideo
import com.redgifs.common.UsersRed
import com.composeunstyled.Text
import com.example.ui.screens.explorer.ScreenRedExplorer
import com.example.ui.screens.fullscreen.ScreenRedFullScreen
import com.example.ui.screens.top_this_week.ProfileInfo1
import com.redgifs.common.expand_menu_video.ExpandMenuVideoTags
import com.redgifs.common.video.player_row_mini.RedUrlVideoImageAndLongClick
import timber.log.Timber

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

    val listGifs : LazyPagingItems<GifsInfo> = host.pager.collectAsLazyPagingItems() as LazyPagingItems<GifsInfo>

    SideEffect { Timber.d("!!! LazyRow123::SideEffect columns: ${host.columns} : $listGifs") }

    val isConnected by host.isConnected.collectAsStateWithLifecycle()
    val state = host.state//rememberLazyGridState()
    var blockItem by remember { mutableStateOf<GifsInfo?>(null) }

    val navigator = LocalNavigator.currentOrThrow

    // Отображаем индикатор загрузки поверх контента, если это первая загрузка
    // a) ПЕРВОНАЧАЛЬНАЯ загрузка (+ pull‑to‑refresh)
    val isInitialLoading = listGifs.loadState.refresh is LoadState.Loading && listGifs.itemCount == 0          // важно!
    val isErrorInitial = listGifs.loadState.refresh is LoadState.Error

    val block = host.hostDI.block

    val downloadList = host.hostDI.downloadRed.downloadList.collectAsState().value

    if (listGifs.itemCount == 0) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text( "Отсутствуют данные", color = Color.White, fontFamily = ThemeRed.fontFamilyDMsanss, fontSize = 20.sp ) }; return }

    val loadState = listGifs.loadState
    var wasRefreshLoading by remember { mutableStateOf(false) }
    var wasAppendLoading by remember { mutableStateOf(false) }
    var wasDataLoaded by remember { mutableStateOf(false) }

    // Первая загрузка
    LaunchedEffect(loadState.refresh) {
        if (loadState.refresh is LoadState.NotLoading && !wasDataLoaded && listGifs.itemCount > 0) {
            wasDataLoaded = true
            onAppendLoaded(listGifs)
        }

        // Сброс флага, если снова началась загрузка (например, swipe-to-refresh)
        if (loadState.refresh is LoadState.Loading) {
            wasDataLoaded = false
        }
    }

    // Догрузка следующих страниц
    LaunchedEffect(loadState.append) {
        if (loadState.append is LoadState.Loading && !wasAppendLoading) {
            wasAppendLoading = true
            onAppendLoaded(listGifs) // false — догрузка
        }
        if (loadState.append !is LoadState.Loading) {
            wasAppendLoading = false
        }
    }

    //Диалог для блокировки
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
                    modifier = Modifier.fillMaxSize()//.then(modifier)
                    ,
                    contentPadding = contentPadding,
                )
                {
                    item(
                        key = "before",
                        span = { GridItemSpan(maxLineSpan) }) { contentBeforeList() }

                    items(
                        count = listGifs.itemCount,
                        key = { index -> listGifs[index]?.id ?: index}
                    ) { index ->

                        //Timber.i("777 index:$index itemCount:${listGifs.itemCount}")

                        //if (index >= listGifs.itemCount) return@items

                        var isVideo by remember { mutableStateOf(false) }

                        val item = listGifs[index]

                        //Timber.i("777 index:$index item id:${item?.id}")

                        if (item != null) {

                            Box(
                                modifier = Modifier
                                    .padding(vertical = 2.dp).padding(horizontal = 2.dp).fillMaxSize().clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {

                                RedUrlVideoImageAndLongClick(
                                    item, index,
                                    onLongClick = {
                                        blockItem = item
                                        navigator.push(ScreenRedFullScreen(item))
                                    },
                                    onVideo = { isVideo = it },
                                    isVisibleView = false,
                                    isVisibleDuration = false,
                                    play = false,//centrallyLocatedOrMostVisibleItemIndex == index && host.columns == 1,
                                    isNetConnected = isConnected,
                                    onFullScreen = {
                                        blockItem = item
                                        navigator.push(ScreenRedFullScreen(item))
                                    },
                                    downloadRed = host.hostDI.downloadRed,
                                )


                                Column(modifier = Modifier.align(Alignment.TopEnd)) {

                                    //Меню на 3 точки
                                    ExpandMenuVideo(
                                        item = item,
                                        modifier = Modifier,
                                        onClick = {
                                            blockItem = item //Для блока и идентификации и тема
                                        },
                                        onRunLike = {
                                            if (isRunLike) {
                                                listGifs.refresh()
                                            }
                                        },
                                        onRefresh = {
                                            listGifs.refresh()
                                        },
                                        host.isCollection,
                                        block,
                                        host.hostDI.redApi,
                                        host.hostDI.savedRed,
                                        downloadRed = host.hostDI.downloadRed
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
                                                navigator.popAll()
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
                                        initialOffsetY = { fullHeight -> fullHeight }, // снизу вверх
                                        animationSpec = tween(durationMillis = 200)
                                    ),
                                    exit = slideOutVertically(
                                        targetOffsetY = { fullHeight -> fullHeight }, // сверху вниз
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
                                            modifier = Modifier.align(Alignment.BottomEnd)
                                                .offset(2.dp, 2.dp), host.hostDI, item, downloadList
                                        )
                                    }
                                }
                            }

                        }

                    }
                }

            }
        } else {

            val pageCount = listGifs.itemCount
            val statePager = rememberPagerState { pageCount }

            VerticalPager(
                state = statePager,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 2
            ) { index ->
                val item = listGifs[index]

                var isVideo by remember { mutableStateOf(false) }

                if (item == null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else {

                    Box(
                        modifier = Modifier
                            .padding(vertical = 2.dp).padding(horizontal = 2.dp).fillMaxSize().clip(RoundedCornerShape(12.dp)).border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {

                        RedUrlVideoImageAndLongClick( item, index,
                            onLongClick = {
                                blockItem = item
                                navigator.push(ScreenRedFullScreen(item))
                            }, onVideo = { isVideo = it }, isVisibleView = false,isVisibleDuration = false, play = index == statePager.currentPage, isNetConnected = isConnected,
                            onFullScreen = {
                                blockItem = item
                                navigator.push(ScreenRedFullScreen(item))
                            }, downloadRed = host.hostDI.downloadRed,
                        )

                        Column(modifier = Modifier.align(Alignment.TopEnd)) {

                            //Меню на 3 точки
                            ExpandMenuVideo( item = item, modifier = Modifier, onClick = { blockItem = item }, onRunLike = { if (isRunLike) { listGifs.refresh() } },
                                onRefresh = { listGifs.refresh() }, host.isCollection, block, host.hostDI.redApi, host.hostDI.savedRed, downloadRed = host.hostDI.downloadRed )

                            if (item.tags.isNotEmpty()) {
                                ExpandMenuVideoTags( item = item, modifier = Modifier,
                                    onClick = { it1 ->
                                        host.hostDI.search.searchText.value = TextFieldValue( text = it1, selection = TextRange(it1.length) )
                                        host.hostDI.search.searchTextDone.value = it1
                                        ScreenRedExplorer.screenType = 0
                                        navigator.popAll()
                                    }
                                )
                            }
                        }

                        AnimatedVisibility( !isVideo, modifier = Modifier.fillMaxWidth(),
                            enter = slideInVertically( initialOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(durationMillis = 200)
                            ), exit = slideOutVertically( targetOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(durationMillis = 200) ) ) {
                            if (host.visibleProfileInfo) { ProfileInfo1( modifier = Modifier.padding(start = 2.dp, bottom = 2.dp).align(Alignment.BottomStart), onClick = { onClickOpenProfile(item.userName) }, videoItem = item, listUsers = UsersRed.listAllUsers, visibleUserName = true, sizeIcon = 32.dp, cornerRadius = 8.dp ) }
                            LazyRow123Icons( modifier = Modifier.align(Alignment.BottomCenter), host.hostDI, item, downloadList )
                        }
                    }
                }
            }

            if (isInitialLoading) { Box( modifier = modifier.align(Alignment.Center).offset(0.dp, 40.dp), contentAlignment = Alignment.Center ) { CircularProgressIndicator(color = ThemeRed.colorYellow) } }
            if (listGifs.loadState.append is LoadState.Loading && listGifs.itemCount > 0) { Box( modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp) ) { CircularProgressIndicator(color = ThemeRed.colorYellow) } }

        }

    }
}


