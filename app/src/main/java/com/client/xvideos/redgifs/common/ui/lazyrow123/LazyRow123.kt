package com.client.xvideos.redgifs.common.ui.lazyrow123

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.common.R
import com.redgifs.model.GifsInfo
import com.redgifs.common.ThemeRed
import com.redgifs.common.block.ui.DialogBlock
import com.redgifs.common.downloader.DownloadRed
import com.client.xvideos.redgifs.common.expand_menu_video.ExpandMenuVideo
import com.redgifs.common.UsersRed
import com.client.xvideos.redgifs.common.video.player_row_mini.RedUrlVideoImageAndLongClick
import com.client.xvideos.redgifs.screens.fullscreen.ScreenRedFullScreen
import com.client.xvideos.redgifs.screens.top_this_week.ProfileInfo1
import com.composeunstyled.Text
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
    onAppendLoaded : (LazyPagingItems<GifsInfo>) -> Unit = {},
    filterTags : List<String> = emptyList()
    ) {

    val listGifs = host.pager.collectAsLazyPagingItems() as LazyPagingItems<GifsInfo>

    SideEffect { Timber.d("!!! LazyRow123::SideEffect columns: ${host.columns} : $listGifs") }

    val isConnected by host.isConnected.collectAsState()
    val state = host.state//rememberLazyGridState()
    var blockItem by remember { mutableStateOf<GifsInfo?>(null) }

    val navigator = LocalNavigator.currentOrThrow

    // Отображаем индикатор загрузки поверх контента, если это первая загрузка
    // a) ПЕРВОНАЧАЛЬНАЯ загрузка (+ pull‑to‑refresh)
    val isInitialLoading = listGifs.loadState.refresh is LoadState.Loading
            && listGifs.itemCount == 0          // важно!
    val isErrorInitial = listGifs.loadState.refresh is LoadState.Error

    val block = host.block

//    BackHandler {
//        if (fullScreen)
//            fullScreen = false
//        else {
//            navigator.pop()
//        }
//    }

    if (listGifs.itemCount == 0) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Отсутствуют данные",
                color = Color.White,
                fontFamily = ThemeRed.fontFamilyDMsanss,
                fontSize = 20.sp
            )
        }
        //return
    }

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

//    val centrallyLocatedOrMostVisibleItemIndex by remember {
//        derivedStateOf {
//            val layoutInfo = state.layoutInfo
//            val visibleItemsInfo = layoutInfo.visibleItemsInfo
//            if (visibleItemsInfo.isEmpty()) {
//                return@derivedStateOf state.firstVisibleItemIndex // Или 0, или -1 как индикатор отсутствия
//            }
//
//            val viewportHeight = layoutInfo.viewportSize.height
//
//            val viewportCenterY = layoutInfo.viewportStartOffset + viewportHeight / 2
//
//            var bestCandidateIndex = -1
//
//            bestCandidateIndex = visibleItemsInfo.maxByOrNull { itemInfo ->
//                val itemTop = itemInfo.offset.y
//                val itemBottom = itemInfo.offset.y + itemInfo.size.height
//                val visibleTop = max(itemTop, layoutInfo.viewportStartOffset)
//                val visibleBottom = min(itemBottom, layoutInfo.viewportEndOffset)
//                val visibleHeight = max(0f, (visibleBottom - visibleTop).toFloat())
//                visibleHeight // Можно использовать просто видимую высоту, если ширина у всех элементов одинаковая в LazyVerticalGrid
//                // visibleHeight * itemInfo.size.width // Если ширина может отличаться (маловероятно в Fixed)
//            }?.index ?: state.firstVisibleItemIndex
//
//
//            // --- ВАРИАНТ 2: Ближайший к центру viewport ---
////            bestCandidateIndex = visibleItemsInfo.minByOrNull { itemInfo ->
////                val itemCenterY = itemInfo.offset.y + itemInfo.size.height / 2f
////                abs(itemCenterY - viewportCenterY)
////            }?.index ?: state.firstVisibleItemIndex
//
//
//            bestCandidateIndex
//        }
//    }

//    LaunchedEffect(gotoPosition) {
//        if (gotoPosition >= 0 && gotoPosition < listGifs.itemCount) {state.scrollToItem(gotoPosition)}
//    }



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

        LazyVerticalGrid(
            state = state,
            columns = GridCells.Fixed(host.columns.coerceIn(1..3)),
            modifier = Modifier.then(modifier),
            contentPadding = contentPadding,
        ) {
            item(key = "before", span = { GridItemSpan(maxLineSpan) }) { contentBeforeList() }

            items(
                count = listGifs.itemCount,
            ) { index ->

                var isVideo by remember { mutableStateOf(false) }
                val item = listGifs[index]

                var videoUri by remember { mutableStateOf("") }

                if (item != null) {

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
                            item, index, onLongClick = {},
                            onVideo = { isVideo = it },
                            isVisibleView = false,
                            isVisibleDuration = false,
                            play = false,//centrallyLocatedOrMostVisibleItemIndex == index && host.columns == 1,
                            isNetConnected = isConnected,
                            onVideoUri = { videoUri = it },
                            onFullScreen = {
                                blockItem = item
                                //fullScreen = fullScreen.not()
                                navigator.push(ScreenRedFullScreen(item))
                            }
                        )

                        //Меню на 3 точки
                        ExpandMenuVideo(
                            item = item,
                            modifier = Modifier.align(Alignment.TopEnd),
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
                            host.redApi,
                            host.savedRed
                        )


                        if (host.visibleProfileInfo) {
                            ProfileInfo1(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomStart)
                                    .offset((4).dp, (-4).dp),
                                onClick = { onClickOpenProfile(item.userName) },
                                videoItem = item,
                                listUsers = UsersRed.listAllUsers,
                                visibleUserName = host.columns <= 2 && !isVideo,
                                visibleIcon = !isVideo
                            )
                        }

                        AnimatedVisibility(
                            !isVideo, modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.BottomCenter),
                            enter = slideInVertically(
                                initialOffsetY = { fullHeight -> fullHeight }, // снизу вверх
                                animationSpec = tween(durationMillis = 200)
                            ),
                            exit = slideOutVertically(
                                targetOffsetY = { fullHeight -> fullHeight }, // сверху вниз
                                animationSpec = tween(durationMillis = 200)
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.End
                            ) {


                                if (host.savedRed.collections.collectionList.any { it.list.any { it2 -> it2.id == item.id } }) {
                                    Icon(
                                        painter = painterResource(R.drawable.collection_multi_input_svgrepo_com),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(bottom = 6.dp, end = 6.dp)
                                            .size(18.dp)
                                    )
                                }

                                //
                                if (host.savedRed.creators.list.any { it.username == item.userName }) {
                                    Icon(
                                        Icons.Filled.Person,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(bottom = 6.dp, end = 6.dp)
                                            .size(18.dp)
                                    )
                                }

                                //✅ Лайк
                                if (host.savedRed.likes.list.any { it.id == item.id }) {
                                    Icon(
                                        Icons.Filled.FavoriteBorder,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(bottom = 6.dp, end = 6.dp)
                                            .size(18.dp)
                                    )
                                }

                                //✅ Иконка того что видео скачано
                                if (DownloadRed.downloadList.contains(item.id)) {
                                    Icon(
                                        Icons.Default.Save,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(bottom = 6.dp, end = 6.dp)
                                            .size(18.dp)
                                    )
                                }

                            }

                        }

                    }
                }
            }
        }


        if (isInitialLoading) {
            Box(
                modifier = modifier
                    .align(Alignment.Center)
                    .offset(0.dp, 40.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ThemeRed.colorYellow)
            }
        }

        if (listGifs.loadState.append is LoadState.Loading && listGifs.itemCount > 0) {
            Box(modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)) {
                CircularProgressIndicator(color = ThemeRed.colorYellow) // Может быть меньше размером
            }
        }

    }

}


