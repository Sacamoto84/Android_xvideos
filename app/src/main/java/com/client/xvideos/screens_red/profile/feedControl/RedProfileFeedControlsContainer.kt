package com.client.xvideos.screens_red.profile.feedControl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.client.xvideos.screens_red.profile.ScreenRedProfileSM
import timber.log.Timber

/**
 * Контейнер выбора All Gifs Images
 * Sort by Latest
 *         Trending
 *         Top
 * Выбор отображения количества столбцов 1 2
 */
@Composable
fun RedProfileFeedControlsContainer(vm: ScreenRedProfileSM) {

    Timber.d("!!! RedProfileFeedControlsContainer")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //--- Feed Ordering Control ---
        RedProfileSortBy(vm.orderList, vm.order) {
            vm.order = it
            vm.clear()
            vm.loadNextPage()
        }

        //--- Feed Gif Types Control ---
        // Выбор типа отображения All Gifs Images
        RedProfileFeedGifTypesControl(vm)

        //--- Feed View Mode Control ---


    }

}