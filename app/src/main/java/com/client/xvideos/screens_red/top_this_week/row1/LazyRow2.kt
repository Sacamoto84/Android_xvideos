package com.client.xvideos.screens_red.top_this_week.row1

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.UserInfo
import com.client.xvideos.screens_red.profile.atom.RedUrlVideoImageAndLongClick
import kotlinx.coroutines.flow.collectLatest
import kotlin.collections.filter


@Composable
fun LazyRow2(
    listGifs: List<GifsInfo>,
    listUsers: List<UserInfo>,
    modifier: Modifier = Modifier,
    onClickOpenProfile: (String) -> Unit = {},
    onCurrentPosition : (Int) -> Unit = {}, //Вывести текущую позицию
    gotoPosition: Int
) {

    val state = rememberLazyGridState()

    // Отслеживаем текущую позицию (верхний видимый элемент)
    val firstVisibleItemIndex by remember {
        derivedStateOf { state.firstVisibleItemIndex }
    }

    // Эффект при изменении позиции
    LaunchedEffect(state) { // Ключ - сам state
        snapshotFlow { state.firstVisibleItemIndex } // Превращаем свойство в Flow
            .collectLatest { visibleIndex -> // Собираем только последние значения
                onCurrentPosition(visibleIndex)
            }
    }

    LaunchedEffect(gotoPosition) {
        if (gotoPosition >= 0 && gotoPosition < listGifs.size) {
            state.scrollToItem(gotoPosition)
        }
    }

    LazyVerticalGrid(
        state = state,
        columns = GridCells.Fixed(2),
        modifier = Modifier.then(modifier),
        contentPadding = PaddingValues(8.dp)
    ) {

        itemsIndexed(listGifs, key = { index, item -> item.id }) { index, item ->
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