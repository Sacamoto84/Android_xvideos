package com.client.xvideos.screens_red.top_this_week

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.screens_red.common.downloaderIndicator.DownloadIndicator
import com.client.xvideos.screens_red.profile.bottom_bar.line1.SortBy
import com.client.xvideos.screens_red.top_this_week.model.SortTop

@Composable
fun BottomBar(
    vm : ScreenRedTopThisWeekSM,
    onClickWeek: () -> Unit,
    onClickMonth: () -> Unit,
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
                .horizontalScroll(rememberScrollState())
        ) {

            SortTopBy(
                listOf(SortTop.WEEK, SortTop.MONTH, SortTop.TRENDING, SortTop.LATEST),
                vm.sortType.collectAsStateWithLifecycle().value
            ) {
                vm.changeSortType(it)
            }


            Button(onClick = onClickLazy) {
                Text("Lazy")
            }

            Button(onClick = onClickTiktok) {
                Text("Tiktok")
            }

            Button(onClick = onClickLazyOne) {
                Text("1")
            }

            Button(onClick = onClickLazy2) {
                Text("2")
            }

            Button(onClick = onClickLazy3) {
                Text("3")
            }
        }
    }
}