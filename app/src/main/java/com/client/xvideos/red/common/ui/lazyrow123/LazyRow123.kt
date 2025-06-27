package com.client.xvideos.red.common.ui.lazyrow123

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
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ViewCozy
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.ViewComfyAlt
import androidx.compose.material.icons.outlined.ViewCozy
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.red.ThemeRed
import com.client.xvideos.red.common.block.BlockRed
import com.client.xvideos.red.common.block.ui.DialogBlock
import com.client.xvideos.red.common.downloader.DownloadRed
import com.client.xvideos.red.common.video.player_row_mini.RedUrlVideoImageAndLongClick
import com.client.xvideos.red.common.expand_menu_video.ExpandMenuVideo
import com.client.xvideos.red.screens.top_this_week.ProfileInfo1
import com.client.xvideos.red.common.expand_menu_video.ExpandMenuVideoModel
import com.client.xvideos.red.common.saved.SavedRed
import com.client.xvideos.red.common.saved.collection.ui.DialogCollection
import com.client.xvideos.red.common.snackBar.SnackBarEvent
import com.client.xvideos.red.common.users.UsersRed
import com.client.xvideos.red.screens.fullscreen.ScreenRedFullScreen
import com.composeunstyled.Text
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@Composable
fun LazyRow123(
    host: LazyRow123Host,
    modifier: Modifier = Modifier,
    onClickOpenProfile: (String) -> Unit = {},
    gotoPosition: Int,
    option: List<ExpandMenuVideoModel> = emptyList(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    contentBeforeList: @Composable (() -> Unit) = {},

    //Для меню
    onRunLike: () -> Unit = {},

    ) {

    SideEffect { Timber.d("!!! LazyRow123::SideEffect columns: ${host.columns} gotoPosition: $gotoPosition") }

    val listGifs = host.pager.collectAsLazyPagingItems() as LazyPagingItems<GifsInfo>

    val isConnected by host.isConnected.collectAsState()
    val state = host.state//rememberLazyGridState()
    var blockItem by remember { mutableStateOf<GifsInfo?>(null) }

    val navigator = LocalNavigator.currentOrThrow

//    BackHandler {
//        if (fullScreen)
//            fullScreen = false
//        else {
//            navigator.pop()
//        }
//    }

    if (listGifs.itemCount == 0) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            Text("Отсутствуют данные", color = Color.White, fontFamily = ThemeRed.fontFamilyDMsanss, fontSize = 20.sp)
        }
        return
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
    if (BlockRed.blockVisibleDialog) {
        DialogBlock(
            visible = BlockRed.blockVisibleDialog,
            onDismiss = { BlockRed.blockVisibleDialog = false },
            onBlockConfirmed = {
                if ((blockItem != null)) {
                    BlockRed.blockItem(blockItem!!)
                    host.refresh()
                    blockItem = null
                }
            }
        )
    }





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
                        .padding(vertical = 8.dp)
                        .padding(horizontal = 4.dp)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color.DarkGray, RoundedCornerShape(16.dp)),
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
                        scope = host.scope,
                        onRunLike = onRunLike
                    )


                    if(host.visibleProfileInfo) {
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


                            if (SavedRed.collectionList.any { it.list.any { it2 -> it2.id == item.id } }) {
                                Icon(Icons.Outlined.ViewComfyAlt, contentDescription = null, tint = Color.White,
                                    modifier = Modifier.padding(bottom = 6.dp, end = 6.dp).size(18.dp))
                            }

                            //
                            if (SavedRed.creatorsList.any { it.username == item.userName }) {
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
                            if (SavedRed.likesList.any { it.id == item.id }) {
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


//    if (fullScreen) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(Color.DarkGray)
//        ) {
//
//            if (blockItem != null) {
//                RedUrlVideoImageAndLongClick(
//                    blockItem!!, 0, onLongClick = {},
//                    onVideo = { },
//                    isVisibleView = false,
//                    isVisibleDuration = false,
//                    play = true,
//                    isNetConnected = isConnected,
//                    onVideoUri = {},
//                    onFullScreen = { fullScreen = fullScreen.not() }
//                )
//            }
//
//        }
//    }

}


