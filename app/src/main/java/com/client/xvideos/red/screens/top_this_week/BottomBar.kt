package com.client.xvideos.red.screens.top_this_week

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.red.ThemeRed
import com.client.xvideos.red.common.downloader.ui.DownloadIndicator
import com.client.xvideos.red.common.ui.sortByOrder.SortByOrder

@Composable
fun BottomBar(
    vm : ScreenRedTopThisWeekSM,
    onClickLazy: () -> Unit,
    onClickTiktok: () -> Unit,
    onClickLazyOne: () -> Unit,
    onClickLazy2: () -> Unit,
    onClickLazy3: () -> Unit,
) {

    Column {

        //Индикатор загрузки
        DownloadIndicator()

        Row(
            modifier = Modifier.padding(start = 8.dp, top = 2.dp).fillMaxWidth()
                .horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically
        ) {

            SortByOrder(
                listOf(Order.WEEK, Order.MONTH, Order.TRENDING, Order.LATEST),
                vm.lazyHost.sortType.collectAsStateWithLifecycle().value
            ) {
                vm.lazyHost.changeSortType(it)
            }

            Spacer(modifier = Modifier.width(4.dp))

            val options = listOf("T", "1", "2", "3")
            val actions = listOf(
                { onClickTiktok.invoke() },
                { onClickLazyOne.invoke() },
                { onClickLazy2.invoke() },
                { onClickLazy3.invoke() }
            )

            SingleChoiceSegmentedButton(
                options = options,
                onOptionSelected = { index -> actions[index]() }
            )



//Column {
//    Row(
//        modifier = Modifier.height(24.dp)//.background(Brush.linearGradient(listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))))
//        , verticalAlignment = Alignment.CenterVertically
//    ) {
//
//
//
//
//
//
//        Image(
//            painter = painterResource(id = R.drawable.verificed),
//            contentDescription = null,
//            modifier = Modifier.padding().size(24.dp)
//        )
//
//        var checked by remember { mutableStateOf(false) }
//        Checkbox(
//            checked = checked,
//            onCheckedChange = { checked = it },
//            shape = RoundedCornerShape(4.dp),
//            backgroundColor = Color.White,
//            borderWidth = 1.dp,
//            borderColor = Color.Black.copy(0.33f),
//            modifier = Modifier.size(22.dp),
//            contentDescription = "Add olives"
//        ) {
//            Icon(Icons.Filled.Check, contentDescription = null)
//        }
//    }
//
//
//    Row(
//        modifier = Modifier.height(24.dp)//.background(Brush.linearGradient(listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))))
//        , verticalAlignment = Alignment.CenterVertically
//
//    ) {
//
//        Icon(
//            Icons.Filled.StarOutline,
//            contentDescription = null,
//            tint = Color.Yellow,
//            modifier = Modifier.size(24.dp)
//        )
//
//        var checked by remember { mutableStateOf(false) }
//        Checkbox(
//            checked = checked,
//            onCheckedChange = { checked = it },
//            shape = RoundedCornerShape(4.dp),
//            backgroundColor = Color.White,
//            borderWidth = 1.dp,
//            borderColor = Color.Black.copy(0.33f),
//            modifier = Modifier.size(22.dp),
//            contentDescription = "Add olives"
//        ) {
//            Icon(Icons.Filled.Check, contentDescription = null)
//        }
//    }
//}
            IconButton(onClick = {}, modifier = Modifier.padding(horizontal = 4.dp).size(48.dp).border(1.dp, ThemeRed.colorBorderGray, RoundedCornerShape(8.dp))) {
                Icon(Icons.Filled.StarOutline, contentDescription = null, tint = Color.White)
            }

            IconButton(onClick = {}, modifier = Modifier.padding(horizontal = 0.dp).size(48.dp).border(1.dp, ThemeRed.colorBorderGray, RoundedCornerShape(8.dp))) {
                Icon(Icons.Filled.Save, contentDescription = null, tint = Color.White)
            }



//            Button(onClick = onClickTiktok) {
//                Text("Tiktok")
//            }
//
//            Button(onClick = onClickLazyOne) {
//                Text("1")
//            }
//
//            Button(onClick = onClickLazy2) {
//                Text("2")
//            }
//
//            Button(onClick = onClickLazy3) {
//                Text("3")
//            }

        }
    }
}

@Composable
fun SingleChoiceSegmentedButton(
    modifier: Modifier = Modifier,
    options: List<String>,
    onOptionSelected: (Int) -> Unit // ← внешний обработчик по индексу
) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, label ->
            val shape = when (index) {
                0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                options.lastIndex -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                else -> RoundedCornerShape(0.dp)
            }

            SegmentedButton(
                modifier = Modifier.height(48.dp).width(40.dp),
                shape = shape,
                onClick = {
                    selectedIndex = index
                    onOptionSelected(index) // ← вызываем внешнее действие
                },
                selected = index == selectedIndex,
                label = { Text(label) },
                icon = {},
                colors = SegmentedButtonDefaults.colors(
                    activeBorderColor = ThemeRed.colorBottomBarDivider,
                    inactiveBorderColor = ThemeRed.colorBottomBarDivider
                )
            )
        }
    }
}