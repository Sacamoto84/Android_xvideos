package com.client.xvideos.screens_red.profile.molecule

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.client.xvideos.feature.redgifs.types.MediaInfo
import com.client.xvideos.screens_red.ThemeRed
import com.client.xvideos.screens_red.profile.atom.RedUrlVideoLiteChaintech

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TikTokStyleVideoFeed(videoItems: List<MediaInfo>, modifier: Modifier = Modifier, onChangeTime: (Pair<Int, Int>) -> Unit) {

    if (videoItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(ThemeRed.colorCommonBackground), contentAlignment = Alignment.Center) {
            Text("Нет видео для отображения", color = Color.LightGray , fontFamily = ThemeRed.fontFamilyPopinsRegular, fontSize = 20.sp)
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { videoItems.size })

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        key = { index -> videoItems[index].id } // Ключ для стабильности элементов
    ) { pageIndex ->
        val videoItem = videoItems[pageIndex] // pageIndex - это индекс текущей отображаемой страницы

        // Управляем воспроизведением в зависимости от того, видима ли страница
        // и соответствует ли она текущей активной странице в пейджере.
        val isCurrentPage = pagerState.currentPage == pageIndex

        RedUrlVideoLiteChaintech(
            "https://api.redgifs.com/v2/gifs/${videoItem.id.lowercase()}/hd.m3u8",
            videoItem.urls.thumbnail,
            play = isCurrentPage,
            onChangeTime = onChangeTime
        )

    }
}
