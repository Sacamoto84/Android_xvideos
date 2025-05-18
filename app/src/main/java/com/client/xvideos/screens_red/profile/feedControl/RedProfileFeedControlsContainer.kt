package com.client.xvideos.screens_red.profile.feedControl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Контейнер выбора All Gifs Images
 * Sort by Latest
 *         Trending
 *         Top
 * Выбор отображения количества столбцов 1 2
 */
@Composable
fun RedProfileFeedControlsContainer() {
    //--- Feed Gif Types Control ---
    // Выбор типа отображения All Gifs Images


    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        //--- Feed Ordering Control ---
        RedProfileSortBy()

        //--- Feed View Mode Control ---




    }

}