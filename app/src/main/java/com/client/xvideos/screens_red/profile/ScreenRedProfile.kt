package com.client.xvideos.screens_red.profile

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.screens_red.profile.atom.CanvasTimeDurationLine
import com.client.xvideos.screens_red.profile.atom.RedUrlVideoImageAndLongClick
import com.client.xvideos.screens_red.profile.atom.VerticalScrollbar
import com.client.xvideos.screens_red.profile.bottom_bar.Red_Profile_Bottom_Bar
import com.client.xvideos.screens_red.profile.molecule.TikTokStyleVideoFeed
import com.composables.core.DragIndication
import com.composables.core.ModalBottomSheet
import com.composables.core.Scrim
import com.composables.core.Sheet
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.SheetDetent.Companion.Hidden
import com.composables.core.rememberModalBottomSheetState

import com.composeunstyled.rememberDisclosureState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext

class ScreenRedProfile() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedBoxWithConstraintsScope")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val vm: ScreenRedProfileSM = getScreenModel()

        val gridState = rememberLazyGridState()
        val list = vm.list.collectAsState()
        val isLoading = vm.isLoading.collectAsState().value
        val stateDisclosure = rememberDisclosureState()
        var prevIndex by remember { mutableIntStateOf(0) }

        val selector = vm.selector.collectAsStateWithLifecycle().value

        //RedUrlVideoLite("https://api.redgifs.com/v2/gifs/easytightibisbill/hd.m3u8")

        val density = LocalDensity.current

        //TikTokWithCollapsingToolbar(list.value)

        //🟨🟨🟨🟨🟨🟨🟨🟨⬆️⬆️⬆️⬆️⬆️❗

        //Расчет процентов для скролл
        val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForGrid(
            gridState = gridState,
            itemsToIgnore = 3,
            numberOfColumns = 2
        )

        var trackVisible by remember { mutableStateOf(false) }

        var visibleItems by remember { mutableIntStateOf(0) }

        // триггерим подгрузку, когда остаётся ≤6 элементов до конца
        LaunchedEffect(gridState) {
            withContext(Dispatchers.IO) {
                snapshotFlow { gridState.layoutInfo }
                    .distinctUntilChanged()
                    .collect { info ->
                        val last = info.visibleItemsInfo.lastOrNull()?.index ?: 0
                        visibleItems = last
                        //Timber.d("!!! prevIndex = $prevIndex")
                        //Timber.d("!!! last = $last")
                        //Timber.d("!!! info.totalItemsCount = ${info.totalItemsCount}")

                        // Триггер только если движемся ВНИЗ
                        if (last > prevIndex) {
                            val total = info.totalItemsCount
                            //if (total - last <= 6)
                            //vm.loadNextPage()
                        }
                        prevIndex = last
                    }
            }
        }

        val sheetState = rememberModalBottomSheetState(
            initialDetent = Hidden,
            animationSpec = tween(
                200
            )
        )

        ModalBottomSheet(state = sheetState) {
            Scrim()
            Sheet(
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(Color.White)
                    .widthIn(max = 640.dp)
                    .fillMaxWidth()
                    .imePadding(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DragIndication(
                        modifier = Modifier
                            .padding(top = 22.dp)
                            .background(Color.Black.copy(0.4f), RoundedCornerShape(100))
                            .width(32.dp)
                            .height(4.dp)
                    )

                    Text("Here is some content")
                    Text("Here is some content")
                    Text("Here is some content")
                    Text("Here is some content")


                }
            }
        }

        Scaffold(
            bottomBar = {
                Column {


//                    AnimatedVisibility(
//                        visible = trackVisible && selector == 1,
//                        enter = slideInVertically { fullHeight ->
//                            // Начальная позиция: смещаем вниз на полную высоту элемента,
//                            // чтобы он "выехал" снизу.
//                            fullHeight
//                        } + expandVertically(
//                            // Расширяем снизу вверх
//                            expandFrom = Alignment.Bottom
//                        ),
//                        // + fadeIn(       initialAlpha = 0.3f            )
//                        exit = slideOutVertically { fullHeight ->
//                            // Конечная позиция: смещаем вниз на полную высоту элемента,
//                            // чтобы он "уехал" вниз.
//                            fullHeight
//                        } + shrinkVertically(
//                            // Сжимаем снизу вверх (к низу)
//                            shrinkTowards = Alignment.Bottom
//                        ), //+ fadeOut()
//                    ) {
                        //Линия продолжительности видео

                       val al = animateFloatAsState(
                           if (trackVisible && selector == 1) 1f else 0f,
                           tween(400)
                       )

                        Box(
                            Modifier
                                .clip(RoundedCornerShape(0))
                                .height(8.dp)
                                .fillMaxWidth()
                                .alpha(al.value)
                                .background(Color.Black), contentAlignment = Alignment.BottomCenter
                        ) {


                            CanvasTimeDurationLine(
                                vm.currentPlayerTime,
                                vm.currentPlayerDuration,
                                vm.timeA,
                                vm.timeB,
                                vm.enableAB, vm.play
                            )

                        }
                 //   }
                    Red_Profile_Bottom_Bar(vm)
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
                        onChangeTime = {
                            vm.currentPlayerTime = it.first
                            vm.currentPlayerDuration = it.second
                        },
                        onPageUIElementsVisibilityChange = {
                            trackVisible = it
                        },
                        isMute = vm.mute,
                        onLongClick = {

                        },

                        //Текущий выбранный элемент в пейджере
                        onChangePagerPage = {
                            vm.currentTikTokPage = it
                        },
                        modifier = Modifier,

                        timeA = vm.timeA,
                        timeB = vm.timeB,
                        enableAB = vm.enableAB
                    )

                } else {

                    Box(modifier = Modifier.fillMaxSize()) {

                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            contentPadding = PaddingValues(4.dp) // Отступы по краям сетки
                        ) {

                            //Тайлы картинок и видео
                            itemsIndexed(
                                list.value,
                                key = { index, item -> item.id }) { index, item ->

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .aspectRatio(1080f / 1920)
                                ) {

                                    RedUrlVideoImageAndLongClick(
                                        item,
                                        index,
                                        onLongClick = {

                                            sheetState.currentDetent = FullyExpanded


                                        },
                                        onDoubleClick = {})
                                }

                            }

                        }

                        //Индикатор загрузки
                        if (isLoading) {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(56.dp),
                                    strokeWidth = 8.dp
                                )
                            }
                        }

                        Text("      " + visibleItems.toString(), color = Color.White)


                        //Скролл
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

