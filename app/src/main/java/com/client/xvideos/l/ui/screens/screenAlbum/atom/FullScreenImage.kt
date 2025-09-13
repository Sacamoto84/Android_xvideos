package com.client.xvideos.l.ui.screens.screenAlbum.atom

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.common.fresco.UrlImageLusciousGifsGlide
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.model.PicsDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ZoomState
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

class FullScreenImage(
    val item: PicsDetails,
    val albumName: String,
    val filteredPic: List<PicsDetails>,
    val autoPlay: Boolean = false,
    val isAnimated: Boolean = false,
    val expandMenu: @Composable (PicsDetails) -> Unit = {},
    val onClose: (Int) -> Unit
) : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val density = LocalDensity.current
        val configuration = LocalConfiguration.current
        val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
        val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

        var isClosing by remember { mutableStateOf(false) }

        var dataItem by remember(Unit) { mutableStateOf(item) }
        var corruptCancel by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(isClosing) {
            if (isClosing) {
                onClose(
                    if (corruptCancel) filteredPic.indexOf(dataItem)
                        .coerceIn(0, filteredPic.size - 1) else -1
                )
                navigator.pop()
            }
        }

        BackHandler {
            isClosing = true
        }

        val zoomState = rememberZoomState()
        val pagerState = rememberPagerState(
            filteredPic.indexOf(item).coerceIn(0, filteredPic.lastIndex),
            pageCount = { filteredPic.size }
        )

        // Состояние для LazyRow
        val lazyRowState = rememberLazyListState()

        // Текущий индекс из pagerState
        val currentIndex = pagerState.currentPage

        val initialIndex by remember {
            mutableIntStateOf(
                filteredPic.indexOf(item).coerceIn(0, filteredPic.lastIndex)
            )
        }

        LaunchedEffect(currentIndex) {
            if (currentIndex != initialIndex) {
                corruptCancel = true
            }
        }


        // Автоматическая прокрутка LazyRow к текущему элементу
        LaunchedEffect(currentIndex) {
            // Обновляем dataItem при изменении страницы в pager
            dataItem = filteredPic[currentIndex]

            // Сбрасываем зум при смене страницы
            //zoomState.reset()
            if (zoomState.scale > 1.0f) {
                zoomState.changeScale(1.0f, Offset.Zero)
                delay(200)
            }

            // Прокручиваем LazyRow к текущему элементу
            lazyRowState.animateScrollToItem((currentIndex - 1).coerceIn(0, filteredPic.size - 1))

        }

        // Также сбрасываем зум при изменении dataItem через кнопки или миниатюры
        LaunchedEffect(dataItem) {
            val newIndex = filteredPic.indexOf(dataItem)
            if (newIndex != currentIndex) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(newIndex)
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart
        ) {
            // Полупрозрачный фон
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .checkerboardBackground(
                        squareSize = 12.dp,
                        lightColor = Color(0xFF252525),//Color.White,
                        darkColor = Color(0xFF181818)//Color.LightGray
                    )
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(),
                pageSpacing = 8.dp,
                key = { page -> filteredPic[page].url_to_original!! }
            ) { page ->
                val pageItem = filteredPic[page]

                UrlImageLusciousGifsGlide(
                    url = pageItem.url_to_original!!,
                    modifier = Modifier
                        .aspectRatio(pageItem.width.toFloat() / pageItem.height)
                        .zoomable(
                            zoomState = zoomState,
                            enableOneFingerZoom = false,
                            onDoubleTap = { position ->
                                // Двойной тап для зума/раззума
                                coroutineScope.launch {
                                    if (zoomState.scale > 1.0f) {
                                        // Если уже увеличено - сбрасываем
                                        //zoomState.reset()
                                        zoomState.changeScale(1.0f, Offset.Zero)
                                    } else {
                                        // Увеличиваем в 2-3 раза по центру тапа
                                        zoomState.changeScale(2.5f, position)
                                    }
                                }
                            }
                        ),
                    onSuccess = { },
                    albumName = albumName,
                    autoPlay = autoPlay,
                    isAnimated = pageItem.is_animated
                )
            }

            Box(modifier = Modifier.align(Alignment.TopStart)) {
                Text(
                    currentIndex.toString(),
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp), fontFamily = ThemeL.fontFamilyKarla
                )
            }

            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                expandMenu(item)
            }

            Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                LazyRow(
                    state = lazyRowState,
                    modifier = Modifier.height(72.dp)
                ) {
                    itemsIndexed(filteredPic) { index, it1 ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 1.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .aspectRatio(it1.width.toFloat() / it1.height)
                                .clickable(onClick = {
                                    dataItem = it1
                                    corruptCancel = true
                                })
                                .border(
                                    2.dp,
                                    if (index == currentIndex) Color.Yellow else Color.Transparent,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(2.dp)
                        ) {
                            UrlImageLusciousGifsGlide(
                                url = it1.url_to_original!!,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .fillMaxSize(),
                                contentScale = ContentScale.FillBounds,
                                onSuccess = { },
                                albumName = albumName,
                                autoPlay = false,
                                isAnimated = it1.is_animated
                            )
                        }
                    }
                }

            }
        }
    }
}

// Дополнительная функция для создания кастомного Modifier для блокировки pager при зуме
fun Modifier.blockPagerWhenZoomed(zoomState: ZoomState): Modifier = this.then(
    if (zoomState.scale > 1.1f) {
        Modifier.pointerInput(Unit) {
            detectTapGestures { /* Блокируем все жесты */ }
        }
    } else {
        Modifier
    }
)

// Вариант 2: Создание кастомного Modifier
fun Modifier.checkerboardBackground(
    squareSize: Dp = 8.dp,
    lightColor: Color = Color.White,
    darkColor: Color = Color.LightGray
): Modifier = this.then(
    Modifier.drawBehind {
        val squareSizePx = squareSize.toPx()
        val squaresHorizontal = (size.width / squareSizePx).toInt() + 1
        val squaresVertical = (size.height / squareSizePx).toInt() + 1

        for (i in 0..squaresHorizontal) {
            for (j in 0..squaresVertical) {
                val isLightSquare = (i + j) % 2 == 0
                val color = if (isLightSquare) lightColor else darkColor

                drawRect(
                    color = color,
                    topLeft = Offset(
                        x = i * squareSizePx,
                        y = j * squareSizePx
                    ),
                    size = Size(squareSizePx, squareSizePx)
                )
            }
        }
    }
)


