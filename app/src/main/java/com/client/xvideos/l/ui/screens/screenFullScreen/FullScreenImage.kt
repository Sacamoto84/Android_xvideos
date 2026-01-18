package com.client.xvideos.l.ui.screens.screenFullScreen

import android.os.Build
import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.common.coil.UrlImageGifsCoil
import com.client.xvideos.common.noRippleClickable
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.ui.element.expandMenu.ExpandMenuType
import com.client.xvideos.l.ui.element.expandMenu.ExpandMenuViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import net.engawapg.lib.zoomable.ZoomState
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import kotlin.math.absoluteValue

var fullScreenImageFilteredPicArray: List<PicsDetails> = emptyList()

fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}

// ACTUAL OFFSET
fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction

// OFFSET ONLY FROM THE LEFT
fun PagerState.startOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtLeast(0f)
}

// OFFSET ONLY FROM THE RIGHT
fun PagerState.endOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtMost(0f)
}

@Parcelize
class FullScreenImage(
    val item: PicsDetails,
    val albumName: String,
    //val filteredPicArray: List<PicsDetails>,
    val autoPlay: Boolean = false,
    val isAnimated: Boolean = false,
    val expandMenu: ExpandMenuType,
    @IgnoredOnParcel val onClose: (Int) -> Unit = {},

    ) : Screen, Parcelable {

    @IgnoredOnParcel
    override val key: ScreenKey = uniqueScreenKey

    @RequiresApi(Build.VERSION_CODES.S)
    @OptIn(
        ExperimentalFoundationApi::class,
        ExperimentalMaterialApi::class,
        DelicateCoroutinesApi::class
    )
    @Composable
    override fun Content() {

//        run {
//            Timber.d("!!! >>>> filteredPic type: ${filteredPic::class.java.simpleName}")
//            //filteredPic.toList()
//        }

        /**
         * Показ полностью фуллскрин
         */
        var isFullScreen by remember { mutableStateOf(false) }

        //val filteredPic = filteredPicArray.toList() 🔴 📚 🗂️ 💾 𝑹𝒖𝒍𝒆𝒔 ⚡️⭐⭐⭐⭐⭐
        val filteredPic = fullScreenImageFilteredPicArray.toList()

        val expandMenuViewModel: ExpandMenuViewModel = hiltViewModel()

        val navigator = LocalNavigator.currentOrThrow

        var isClosing by remember { mutableStateOf(false) }

        var dataItem by remember(Unit) { mutableStateOf(item) }
        var corruptCancel by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()

        var rotate by remember { mutableStateOf(false) }

        //val zoomState = rememberZoomState()

        val pagerState = rememberPagerState(
            filteredPic.indexOf(item).coerceIn(0, filteredPic.lastIndex),
            pageCount = { filteredPic.size }
        )

        // Состояние для LazyRow
        val lazyRowState = rememberLazyListState(
            cacheWindow = LazyLayoutCacheWindow(
                ahead = 200.dp,
                behind = 200.dp
            )
        )

        LaunchedEffect(isClosing) {
            if (isClosing) {
                onClose(
                    if (corruptCancel) filteredPic.indexOf(dataItem)
                        .coerceIn(0, filteredPic.size - 1) else -1
                )
                navigator.pop()
            }
        }

        BackHandler { isClosing = true }

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

//            if (zoomState.scale > 1.0f) {
//                zoomState.changeScale(1.0f, Offset.Zero)
//                delay(200)
//            }

            // Прокручиваем LazyRow к текущему элементу
            lazyRowState.animateScrollToItem((currentIndex - 2).coerceIn(0, filteredPic.size - 1))

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
            modifier = Modifier
                .fillMaxSize()
                //Шахматная доска
                .checkerboardBackground(
                    squareSize = 12.dp,
                    lightColor = Color(0xFF252525),
                    darkColor = Color(0xFF181818)
                )
                .noRippleClickable( onClick = { isFullScreen = isFullScreen.not() } )

        ) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 0.dp,
                beyondViewportPageCount = 1,
                reverseLayout = false,
                key = { page -> filteredPic[page].url_to_original!! }
            ) { page ->
                val pageItem = filteredPic[page]
                val zoomState = rememberZoomState()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(
                            if (rotate) (pageItem.height.toFloat() / pageItem.width)
                            else (pageItem.width.toFloat() / pageItem.height),
                            matchHeightConstraintsFirst = false
                        )
                        .zIndex( if (pagerState.offsetForPage(page) <= 0) 0f else 100f )

                ) {
                    // Картинка с масштабированием и позиционированием
                    Box( modifier = Modifier.fillMaxSize() )
                    {
                        UrlImageGifsCoil(
                            rotate = rotate, contentScale = ContentScale.Fit, url = pageItem.url_to_original!!, modifier = Modifier.fillMaxSize()
                                .zoomable(
                                    zoomState = zoomState,
                                    enableOneFingerZoom = false,
                                    onDoubleTap = { position ->
                                        coroutineScope.launch {
                                            if (zoomState.scale > 1.0f) {
                                                zoomState.changeScale(1.0f, Offset.Zero)
                                            } else {
                                                zoomState.changeScale(2.5f, position)
                                            }
                                        }
                                    },
                                    onTap = {
                                        isFullScreen = isFullScreen.not()
                                    }

                                ),
                            onSuccess = { },
                            albumName = albumName,
                            autoPlay = autoPlay,
                            isAnimated = pageItem.is_animated,
                            isVisible = currentIndex == page,
                            isFullScreen = true
                        )

                    }
                }
            }

            Box(modifier = Modifier.align(Alignment.TopStart)) { Text( currentIndex.toString(), color = Color.Gray, modifier = Modifier.padding(start = 8.dp), fontFamily = ThemeL.fontFamilyKarla )}

            AnimatedVisibility(visible = !isFullScreen, enter = fadeIn(), exit = fadeOut())
            {
                //Верхние кнопки
                Row(modifier = Modifier.align(Alignment.TopStart).offset(y = 8.dp))
                {
                    IconButton(onClick = { rotate = rotate.not() }) { Icon(Icons.Default.ScreenRotation, contentDescription = null, tint = Color.White) }
                    IconButton(onClick = { }) { Icon( Icons.Default.Info, contentDescription = null, tint = Color.White ) }
                }
                Box( modifier = Modifier.align(Alignment.TopEnd).offset(y = 8.dp) ) { expandMenuViewModel.ExpandMenu( expandMenu, filteredPic[pagerState.currentPage], albumName ) }
            }

            val coroutineScope = rememberCoroutineScope()

            AnimatedVisibility(
                visible = !isFullScreen,
                enter = fadeIn() ,
                exit  = fadeOut(),
            )
            {
                SwipeableBottomPanel { swipeableState, hiddenOffset ->

                    Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                        LazyRow(
                            state = lazyRowState,
                            modifier = Modifier
                                .height(72.dp)
//                                .pointerInput(Unit) {
//                                    detectVerticalDragGestures(
//                                        onVerticalDrag = { change, dragAmount ->
//                                            swipeableState.performDrag(dragAmount)
//                                            change.consume()
//                                        },
//                                        onDragEnd = {
//                                            val targetState =
//                                                if (swipeableState.offset.value < hiddenOffset / 2) 0 else 1
//                                            coroutineScope.launch {
//                                                swipeableState.animateTo(targetState)
//                                            }
//                                        }
//                                    )
//                                }


                        ) {
                            itemsIndexed(
                                filteredPic,
                                key = { _, item -> item.url_to_original!! }) { index, it1 ->
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 1.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .aspectRatio(it1.width.toFloat() / it1.height)
                                        .clickable(onClick = {
                                            dataItem = it1
                                            corruptCancel = true
                                        })
                                        .border(2.dp, if (index == currentIndex) Color.Yellow else Color.Transparent, RoundedCornerShape(4.dp)).padding(2.dp)
                                ) {
                                    UrlImageGifsCoil(
                                        url = it1.url_to_original!!,
                                        modifier = Modifier.clip(RoundedCornerShape(4.dp)).fillMaxSize(),
                                        contentScale = ContentScale.FillBounds,
                                        onSuccess = { }, albumName = albumName, autoPlay = false, isAnimated = it1.is_animated, sizeButton = 20.dp, sizeButtonIcon = 12.dp
                                    )
                                }
                            }
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


