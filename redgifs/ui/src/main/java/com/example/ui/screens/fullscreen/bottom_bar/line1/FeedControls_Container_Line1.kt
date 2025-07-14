package com.example.ui.screens.fullscreen.bottom_bar.line1

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.profile.ScreenRedProfileSM
import com.example.ui.screens.ui.atom.Selector
import com.example.ui.screens.ui.sortByOrder.SortByOrder
import com.redgifs.common.ThemeRed
import timber.log.Timber

/**
 * Контейнер выбора All Gifs Images
 * Sort by Latest
 *         Trending
 *         Top
 * Выбор отображения количества столбцов 1 2
 */
@Composable
fun FeedControls_Container_Line1(vm: ScreenRedProfileSM) {

    Timber.d("!!! RedProfileFeedControlsContainer")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //--- Feed Ordering Control ---
        SortByOrder(list =vm.orderList, selected =  vm.order, onSelect = {
            vm.order = it
            vm.clear()
            //vm.loadNextPage()
        })

        //--- Feed Gif Types Control ---
        // Выбор типа отображения All Gifs Images
        Box(
            Modifier
                .padding(horizontal = 4.dp)
                .weight(1f)
                .border(1.dp, ThemeRed.colorBorderGray, RoundedCornerShape(8.dp))
                .background(ThemeRed.colorCommonBackground2)
        ) {
            GifTypes_Control(vm)
        }
        Selector(vm.selector.collectAsStateWithLifecycle().value) {
            vm.setSelector(it)
            vm.likedHost.columns = it
        }

        //--- Feed View Mode Control ---

    }

}