package com.client.xvideos.screens_red.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.screens_red.ThemeRed
import com.client.xvideos.screens_red.common.block.BlockRed
import com.client.xvideos.screens_red.profile.atom.CanvasTimeDurationLine
import com.client.xvideos.screens_red.profile.atom.RedProfileCreaterInfo
import com.client.xvideos.screens_red.common.video.player_row_mini.RedUrlVideoImageAndLongClick
import com.client.xvideos.screens_red.profile.atom.VerticalScrollbar
import com.client.xvideos.screens_red.profile.bottom_bar.BottomBar
import com.client.xvideos.screens_red.profile.tags.TagsBlock
import com.client.xvideos.screens_red.profile.tikTok.MenuContent
import com.client.xvideos.screens_red.profile.tikTok.TikTokStyleVideoFeed

import com.composeunstyled.rememberDisclosureState
import timber.log.Timber

class ScreenRedProfile(val profileName: String) : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedBoxWithConstraintsScope")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        //val vm: ScreenRedProfileSM = getScreenModel()

        val vm = getScreenModel<ScreenRedProfileSM, ScreenRedProfileSM.Factory> { factory ->
            factory.create(profileName)
        }

        val gridState = rememberLazyGridState()
        val list = vm.list.collectAsState()

        val isLoading = vm.isLoading.collectAsState().value

        val stateDisclosure = rememberDisclosureState()
        var prevIndex by remember { mutableIntStateOf(0) }

        val selector = vm.selector.collectAsStateWithLifecycle().value

        var trackVisible by remember { mutableStateOf(false) }
        var visibleItems by remember { mutableIntStateOf(0) }

        //Расчет процентов для скролл
        val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForGrid(
            gridState = gridState, itemsToIgnore = 3, numberOfColumns = 2
        )


        //🟨🟨🟨🟨🟨🟨🟨🟨🎨🎨🎨🟨🟨🟨🟨🟨🟨🟨🟨
        //╭┈┈ Диалог блокировки ┈┈╮
        //│ Отмена    Блокировать │
        //╰┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈╯
        DialogBlock(
            visible = BlockRed.blockVisibleDialog,
            onDismiss = { BlockRed.blockVisibleDialog = false }) {
            val a = vm.currentTikTokGifInfo; if (a != null) {
            BlockRed.blockItem(a)
            BlockRed.refreshListAndBlock(vm._list)
        }
        }
        //🟨🟨🟨🟨🟨🟨🟨🟨⬆️⬆️⬆️⬆️⬆️❗


//        // триггерим подгрузку, когда остаётся ≤6 элементов до конца
//        LaunchedEffect(gridState) {
//            withContext(Dispatchers.IO) {
//                snapshotFlow { gridState.layoutInfo }
//                    .distinctUntilChanged()
//                    .collect { info ->
//                        val last = info.visibleItemsInfo.lastOrNull()?.index ?: 0
//                        visibleItems = last
//                        //Timber.d("!!! prevIndex = $prevIndex")
//                        //Timber.d("!!! last = $last")
//                        //Timber.d("!!! info.totalItemsCount = ${info.totalItemsCount}")
//
//                        // Триггер только если движемся ВНИЗ
//                        if (last > prevIndex) {
//                            val total = info.totalItemsCount
//                            //if (total - last <= 6)
//                            //vm.loadNextPage()
//                        }
//                        prevIndex = last
//                    }
//            }
//        }

        Scaffold(
            bottomBar = {
                Column {

                    Box(
                        Modifier
                            .padding(bottom = 4.dp)
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(0))
                            .height(16.dp)
                            .fillMaxWidth()
                            //.alpha(al.value)
                            .background(Color.Black), contentAlignment = Alignment.BottomCenter
                    ) {

                        CanvasTimeDurationLine(
                            currentTime = vm.currentPlayerTime, duration = vm.currentPlayerDuration,
                            timeA = vm.timeA, timeB = vm.timeB,
                            timeABEnable = vm.enableAB, play = vm.play,
                            onSeek = {
                                if (vm.currentPlayerControls != null) {
                                    vm.currentPlayerControls!!.seekTo(it)
                                }
                            },
                            onSeekFinished = { }
                        )

                        BasicText(
                            vm.currentTikTokPage.toString() + "/" + vm.list.collectAsState().value.lastIndex,
                            style = TextStyle(
                                color = Color.White,
                                fontFamily = ThemeRed.fontFamilyPopinsRegular,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 0.dp, y = (-0).dp)
                        )

                    }

                    //   }
                    BottomBar(vm)
                }
            },
            containerColor = Color.Black
        ) {
            Box(Modifier.padding(bottom = it.calculateBottomPadding())) {

                //Тикток при одном селекторе
                if (selector == 1) {

                    TikTokStyleVideoFeed(
                        vm,
                        list.value,
                        onChangeTime = { it1 ->
                            vm.currentPlayerTime = it1.first
                            vm.currentPlayerDuration = it1.second
                        },
                        onPageUIElementsVisibilityChange = { it1 -> trackVisible = it1 },
                        isMute = vm.mute,
                        onLongClick = { },

                        //Текущий выбранный элемент в пейджере
                        onChangePagerPage = { it1 -> vm.currentTikTokPage = it1 },
                        modifier = Modifier,
                        timeA = vm.timeA, timeB = vm.timeB, enableAB = vm.enableAB,

                        menuContent = { MenuContent(vm) },
                        menuContentWidth = 300.dp,

                        menuDefaultOpen = vm.menuCenter,
                        menuOpenChanged = { it1 ->
                            vm.menuCenter = it1
                            Timber.i("@@@ menuOpenChanged vm.menuCenter = it $it1")
                        },
                        initialPage = vm.tictikStartIndex
                    )

                } else {

                    Box(modifier = Modifier.fillMaxSize()) {

                        LaunchedEffect(vm.currentTikTokPage) {
                            gridState.scrollToItem(vm.currentTikTokPage)
                        }

                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            contentPadding = PaddingValues(4.dp) // Отступы по краям сетки
                        ) {

                            item(key = "info", span = { GridItemSpan(maxLineSpan) }) {
                                if (vm.creator != null){
                                    RedProfileCreaterInfo(vm.creator!!)
                                }
                            }

                            item(key = "tags", span = { GridItemSpan(maxLineSpan) }) {
                                if (vm.creator != null){
                                    TagsBlock(vm.tags.collectAsStateWithLifecycle().value.toList())
                                }
                            }



                            //Тайлы картинок и видео
                            itemsIndexed(
                                list.value,
                                key = { index, item -> item.id }) { index, item ->
                                Box(modifier = Modifier
                                    .fillMaxSize()
                                    .aspectRatio(1080f / 1920)) {
                                    RedUrlVideoImageAndLongClick(item, index, onLongClick = {
                                        vm.openFullScreen(index)
                                    }, onDoubleClick = {}, onFullScreen = {
                                        vm.openFullScreen(index)
                                    }, isNetConnected = true //!!!
                                    )
                                }
                            }
                        }

                        //Индикатор загрузки
                        if (isLoading) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(56.dp),
                                    strokeWidth = 8.dp
                                )
                            }
                        }

                        Text("      " + visibleItems.toString(), color = Color.White)

                        //---- Скролл ----
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .align(Alignment.CenterEnd)
                                .width(2.dp)
                        ) { VerticalScrollbar(scrollPercent) }

                    }


                }


            }
        }


//        // триггерим подгрузку, когда остаётся ≤6 элементов до конца
//        LaunchedEffect(gridState) {
//            withContext(Dispatchers.IO) {
//                snapshotFlow { gridState.layoutInfo }
//                    .distinctUntilChanged()
//                    .collect { info ->
//                        val last = info.visibleItemsInfo.lastOrNull()?.index ?: 0
//                        visibleItems = last - 3
//                        //Timber.d("!!! prevIndex = $prevIndex")
//                        //Timber.d("!!! last = $last")
//                        //Timber.d("!!! info.totalItemsCount = ${info.totalItemsCount}")
//
//                        // Триггер только если движемся ВНИЗ
//                        if (last > prevIndex) {
//                            val total = info.totalItemsCount
//                            //if (total - last <= 6)
//                            //vm.loadNextPage()
//                        }
//                        prevIndex = last
//                    }
//            }
//        }


//        Scaffold(
//            //bottomBar = { RedBottomBar() },
//            containerColor = ThemeRed.colorCommonBackground
//        ) { padding ->
//
//            Box(modifier = Modifier.fillMaxSize()) {
//
//                LazyVerticalGrid(
//                    state = gridState,
//                    columns = if (vm.selector.collectAsStateWithLifecycle().value == 2) GridCells.Fixed(
//                        2
//                    ) else GridCells.Fixed(1),
//                    modifier = Modifier.fillMaxSize(),
//                    horizontalArrangement = Arrangement.spacedBy(4.dp),
//                    verticalArrangement = Arrangement.spacedBy(4.dp),
//                    contentPadding = PaddingValues(4.dp) // Отступы по краям сетки
//                ) {
//
////                        item {
////                            Box(modifier = Modifier.aspectRatio(1f)) {
////                                RedUrlVideoLite("https://api.redgifs.com/v2/gifs/easytightibisbill/hd.m3u8")
////                            }
////
////                        }
//
//
//                    //Описание и теги
//                    item(
//                        key = "info",
//                        span = { GridItemSpan(maxLineSpan) } // Заставляет этот item занять все столбцы
//                    ) {
//                        vm.creator?.let { profileData ->
//                            RedProfileCreaterInfo(profileData)
//                        }
//                    }
//
//                    //Теги
//                    item(
//                        key = "tags",
//                        span = { GridItemSpan(maxLineSpan) } // Заставляет этот item занять все столбцы
//                    ) {
//                        vm.creator?.let { profileData ->
//                            Disclosure(state = stateDisclosure) {
//                                DisclosureHeading(
//                                    modifier = Modifier
//                                        .padding(horizontal = 2.dp)
//                                        .padding(top = 4.dp, bottom = 4.dp)
//                                        .fillMaxWidth()
//                                        .height(48.dp)
//                                        .clip(RoundedCornerShape(8.dp))
//                                        .background(if (stateDisclosure.expanded) ThemeRed.colorBorderGray else Color.Transparent)
//                                        .border(
//                                            1.dp,
//                                            ThemeRed.colorBorderGray,
//                                            RoundedCornerShape(8.dp)
//                                        ),
//                                    //shape = RoundedCornerShape(8.dp),
//                                    contentPadding = PaddingValues(
//                                        //vertical = 12.dp,
//                                        horizontal = 16.dp
//                                    ),
//                                    onClick = {
//                                        stateDisclosure.expanded =
//                                            stateDisclosure.expanded.not()
//                                    }) {
//
//                                    val degrees by animateFloatAsState(
//                                        if (stateDisclosure.expanded) -180f else 0f,
//                                        tween()
//                                    )
//
//                                    Row(
//                                        Modifier.fillMaxWidth(),
//                                        horizontalArrangement = Arrangement.SpaceBetween,
//                                        verticalAlignment = Alignment.CenterVertically
//                                    ) {
//                                        Text(
//                                            "Tags",
//                                            color = Color.White,
//                                            fontFamily = ThemeRed.fontFamilyPopinsRegular,
//                                            fontSize = 18.sp
//                                        )
//                                        Icon(
//                                            painter = painterResource(R.drawable.arrow_down),
//                                            contentDescription = null, tint = Color.White,
//                                            modifier = Modifier
//                                                .size(12.dp)
//                                                .rotate(degrees)
//                                        )
//                                    }
//                                }
//                                DisclosurePanel {
//                                    Box(
//                                        Modifier
//                                            .padding(top = 2.dp)
//                                            .padding(horizontal = 2.dp)
//                                            .fillMaxWidth()
//                                            .clip(RoundedCornerShape(8.dp))
//                                            .background(ThemeRed.colorBorderGray)
//                                            .padding(4.dp)
//                                    ) {
//                                        TagsBlock(vm.tags.collectAsStateWithLifecycle().value.toList())
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    //Управление списком
//                    item(key = "keyboard", span = { GridItemSpan(maxLineSpan) }) {
//                        Box(
//                            Modifier
//                                .padding(horizontal = 2.dp)
//                                .padding(bottom = 2.dp)
//                                .fillMaxWidth()
//                        ) { RedProfileFeedControlsContainer(vm) }
//                    }
//
//
//                    //Тайлы картинок и видео
//                    itemsIndexed(list.value, key = { index, item -> item.id }) { index, item ->
//
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .aspectRatio(1080f / 1920)
//                        ) {
//                            //      RedUrlVideoLite("https://api.redgifs.com/v2/gifs/easytightibisbill/hd.m3u8")
//                            if ((visibleItems - 1) == index || (visibleItems - 2) == index || (visibleItems + 1) == index || (visibleItems) == index) {
//
//                                RedUrlVideoLite(
//                                    "https://api.redgifs.com/v2/gifs/${item.id.lowercase()}/hd.m3u8",
//                                    item.urls.thumbnail,
//                                    play = (visibleItems - 1) == index
//                                )
//                                //                                RedUrlVideoImageAndLongClick(
////                                    item,
////                                    index,
////                                    onLongClick = {},
////                                    onDoubleClick = {}
////                                )
//                            } else {
//                                RedProfileTile(item, index)
//                            }
//                        }
//
//                    }
//
//                }
//
//                //Индикатор загрузки
//                if (isLoading) {
//                    Box(
//                        Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator(
//                            modifier = Modifier.size(56.dp),
//                            strokeWidth = 8.dp
//                        )
//                    }
//                }
//
//                Text("      " + visibleItems.toString(), color = Color.White)
//
//
//                //Скролл
//                Box(
//                    modifier = Modifier
//                        .fillMaxHeight()
//                        .align(Alignment.CenterEnd)
//                        .width(2.dp)
//                ) { VerticalScrollbar(scrollPercent) }
//
//            }
//        }


    }
}

