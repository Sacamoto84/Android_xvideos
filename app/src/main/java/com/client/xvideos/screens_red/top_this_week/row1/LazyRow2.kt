package com.client.xvideos.screens_red.top_this_week.row1

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.lifecycle.distinctUntilChanged
import androidx.paging.compose.LazyPagingItems
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.UserInfo
import com.client.xvideos.screens_red.profile.atom.RedUrlVideoImageAndLongClick
import com.client.xvideos.screens_red.top_this_week.model.VisibleType
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun LazyRow2(
    columns: Int,
    listGifs: LazyPagingItems<GifsInfo>,
    listUsers: List<UserInfo>,
    modifier: Modifier = Modifier,
    onClickOpenProfile: (String) -> Unit = {},
    onCurrentPosition: (Int) -> Unit = {}, //Вывести текущую позицию
    gotoPosition: Int
) {



    SideEffect {
        Timber.d("!!! LazyRow2::SideEffect columns: $columns gotoPosition: $gotoPosition")
    }

    if (listGifs.itemCount == 0) return

    val state = rememberLazyGridState()


    val centrallyLocatedOrMostVisibleItemIndex by remember {
        derivedStateOf {
            val layoutInfo = state.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) {
                return@derivedStateOf state.firstVisibleItemIndex // Или 0, или -1 как индикатор отсутствия
            }

            val viewportHeight = layoutInfo.viewportSize.height
            val viewportCenterY = layoutInfo.viewportStartOffset + viewportHeight / 2

            var bestCandidateIndex = -1
            val maxVisibleArea = -1f
            val minDistanceToCenter = Float.MAX_VALUE

            // Критерий 1: Наибольшая видимая площадь (приоритетнее, если нужно)
            // или Критерий 2: Ближайший к центру
            // Можно выбрать один или комбинировать

//            val candidate = visibleItemsInfo.minByOrNull { itemInfo ->
//                // Рассчитываем видимую высоту элемента
//                val itemTop = itemInfo.offset.y
//                val itemBottom = itemInfo.offset.y + itemInfo.size.height
//
//                val visibleTop =
//                    max(itemTop, layoutInfo.viewportStartOffset)
//                val visibleBottom =
//                    min(itemBottom, layoutInfo.viewportEndOffset)
//                val visibleHeight =
//                    max(0f, (visibleBottom - visibleTop).toFloat())
//                val visibleArea = visibleHeight * itemInfo.size.width // Площадь = видимая высота * ширина элемента
//
//                // Для отладки можно логировать
//                // Timber.d("Item ${itemInfo.index}: top=$itemTop, bottom=$itemBottom, visTop=$visibleTop, visBottom=$visibleBottom, visHeight=$visibleHeight, area=$visibleArea")
//
//                // --- ВАРИАНТ 1: Приоритет на максимальную видимую площадь ---
//                // Мы хотим максимизировать visibleArea, поэтому minByOrNull будет с отрицательным значением
//                // -visibleArea
//
//                // --- ВАРИАНТ 2: Приоритет на близость к центру ---
//                val itemCenterY = itemInfo.offset.y + itemInfo.size.height / 2
//                val distanceToCenter = abs(itemCenterY - viewportCenterY)
//                distanceToCenter.toFloat() // minByOrNull будет искать минимальное это значение
//
//                // --- ВАРИАНТ 3: Комбинированный или более сложный (пока остановимся на одном из двух) ---
//            }

            // Выбор варианта (раскомментируйте нужный):

            // --- ВАРИАНТ 1: Наибольшая видимая площадь ---

            bestCandidateIndex = visibleItemsInfo.maxByOrNull { itemInfo ->
                val itemTop = itemInfo.offset.y
                val itemBottom = itemInfo.offset.y + itemInfo.size.height
                val visibleTop = max(itemTop, layoutInfo.viewportStartOffset)
                val visibleBottom = min(itemBottom, layoutInfo.viewportEndOffset)
                val visibleHeight = max(0f, (visibleBottom - visibleTop).toFloat())
                visibleHeight // Можно использовать просто видимую высоту, если ширина у всех элементов одинаковая в LazyVerticalGrid
                // visibleHeight * itemInfo.size.width // Если ширина может отличаться (маловероятно в Fixed)
            }?.index ?: state.firstVisibleItemIndex


            // --- ВАРИАНТ 2: Ближайший к центру viewport ---
//            bestCandidateIndex = visibleItemsInfo.minByOrNull { itemInfo ->
//                val itemCenterY = itemInfo.offset.y + itemInfo.size.height / 2f
//                abs(itemCenterY - viewportCenterY)
//            }?.index ?: state.firstVisibleItemIndex


            bestCandidateIndex
        }
    }


    LaunchedEffect(Unit) { // Ключ - сам state
        snapshotFlow { centrallyLocatedOrMostVisibleItemIndex } // Превращаем свойство в Flow
            .collectLatest { visibleIndex ->
                if (visibleIndex != -1) {
                    onCurrentPosition(visibleIndex)
                }
            }
    }

    LaunchedEffect(gotoPosition) {
        if (gotoPosition >= 0 && gotoPosition < listGifs.itemCount) {
            state.scrollToItem(gotoPosition)
        }
    }

    LazyVerticalGrid(
        state = state,
        columns = GridCells.Fixed(columns.coerceIn(1..3)),
        modifier = Modifier.then(modifier),
        contentPadding = PaddingValues(8.dp)
    ) {

        items(
            count = listGifs.itemCount,
            //key = { index -> listGifs[index]!!.id }
        ) { index ->

            val item = listGifs[index]

            if (item != null) {

                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color.DarkGray, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {

                    RedUrlVideoImageAndLongClick(item, index, onLongClick = {
                        //vm.openFullScreen(index)
                    }, onDoubleClick = {}, onFullScreen = {
                        //vm.openFullScreen(index)
                    }
                    )

                    ProfileInfo1(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset((4).dp, (-4).dp),
                        onClick = { onClickOpenProfile(item.userName) },
                        videoItem = item,
                        listUsers = listUsers
                    )

                }
            }
        }

    }

}