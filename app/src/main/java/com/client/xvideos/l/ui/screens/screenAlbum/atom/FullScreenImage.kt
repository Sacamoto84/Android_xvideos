package com.client.xvideos.l.ui.screens.screenAlbum.atom

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.client.xvideos.common.fresco.UrlImageLusciousGifsGlide
import com.client.xvideos.l.model.PicsDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ZoomState
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun FullScreenImage(
    item: PicsDetails,
    albumName: String,
    filteredPic: List<PicsDetails>,
    autoPlay: Boolean = false,
    isAnimated: Boolean = false,
    expandMenu: @Composable (PicsDetails) -> Unit = {},
    onClose: (Int) -> Unit
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    var isClosing by remember { mutableStateOf(false) }
    var success by remember { mutableStateOf(false) }
    val alphaAnim = remember { Animatable(0f) }
    var dataItem by remember(Unit) { mutableStateOf(item) }
    var corruptCancel by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(success) {
        if (success) {
            alphaAnim.animateTo(1f, tween(200))
        }
    }

    LaunchedEffect(isClosing) {
        if (isClosing) {
            onClose(
                if (corruptCancel) filteredPic.indexOf(dataItem)
                    .coerceIn(0, filteredPic.size - 1) else -1
            )
        }
    }

    BackHandler { isClosing = true }

    val zoomState = rememberZoomState()
    val pagerState = rememberPagerState(
        filteredPic.indexOf(item).coerceIn(0, filteredPic.lastIndex),
        pageCount = { filteredPic.size }
    )

    // Состояние для LazyRow
    val lazyRowState = rememberLazyListState()

    // Текущий индекс из pagerState
    val currentIndex = pagerState.currentPage

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
        lazyRowState.animateScrollToItem((currentIndex-1).coerceIn(0, filteredPic.size - 1))

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
            modifier = Modifier.matchParentSize()
                .checkerboardBackground(
                    squareSize = 12.dp,
                    lightColor = Color(0xFF252525),//Color.White,
                    darkColor = Color(0xFF181818)//Color.LightGray
                )
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.align(Alignment.Center).fillMaxSize(),
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
                    )
                ,
                onSuccess = { success = true },
                albumName = albumName,
                autoPlay = autoPlay,
                isAnimated = pageItem.is_animated
            )
        }

        Box(modifier = Modifier.align(Alignment.TopStart)) {
            Text(
                currentIndex.toString(),
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Box(modifier = Modifier.align(Alignment.TopEnd)) {
            expandMenu(item)
        }

        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            LazyRow(
                state = lazyRowState,
                modifier = Modifier.height(96.dp)
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

            Row(
                modifier = Modifier
                    .height(46.dp)
                    .padding(horizontal = 96.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    val index = (currentIndex - 1).coerceIn(0, filteredPic.size - 1)
                    dataItem = filteredPic[index]
                    corruptCancel = true
                }) {
                    Text("←")
                }

                Button(onClick = {
                    val index = (currentIndex + 1).coerceIn(0, filteredPic.size - 1)
                    dataItem = filteredPic[index]
                    corruptCancel = true
                }) {
                    Text("→")
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


