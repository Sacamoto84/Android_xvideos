package com.client.xvideos.screens_red.profile.molecule

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.feature.redgifs.types.MediaInfo
import com.client.xvideos.screens_red.ThemeRed
import com.client.xvideos.screens_red.profile.atom.RedUrlVideoLiteChaintech

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TikTokStyleVideoFeed(
    videoItems: List<MediaInfo>,
    modifier: Modifier = Modifier,
    onChangeTime: (Pair<Int, Int>) -> Unit,
    onPageUIElementsVisibilityChange: (Boolean) -> Unit, // Новый колбэк
) {

    val pagerState = rememberPagerState(pageCount = { videoItems.size })

    if (videoItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ThemeRed.colorCommonBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Нет видео для отображения",
                color = Color.LightGray,
                fontFamily = ThemeRed.fontFamilyPopinsRegular,
                fontSize = 20.sp
            )
        }
        // Сообщаем, что UI не должен быть виден, если список пуст
        // (или решаем это на уровне вызывающего компонента)
        LaunchedEffect(Unit) {
            onPageUIElementsVisibilityChange(false)
        }
        return
    }

    LaunchedEffect(pagerState.isScrollInProgress, pagerState.currentPage) {
        // Мы хотим, чтобы UI элементы были видны, когда прокрутка НЕ идет
        // и мы находимся на какой-то конкретной странице (не между страницами).
        val shouldShowGlobalUI = !pagerState.isScrollInProgress
        onPageUIElementsVisibilityChange(shouldShowGlobalUI)
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        key = { index -> videoItems[index].id } // Ключ для стабильности элементов
    ) { pageIndex ->
        val videoItem = videoItems[pageIndex] // pageIndex - это индекс текущей отображаемой страницы

        // Управляем воспроизведением в зависимости от того, видима ли страница
        // и соответствует ли она текущей активной странице в пейджере.
        val isCurrentPage = pagerState.currentPage == pageIndex

        // Логика для UI элементов ВНУТРИ СТРАНИЦЫ (как в предыдущем примере)
        val showInternalPageUIElementsTarget = !pagerState.isScrollInProgress && isCurrentPage
        val internalUiAlpha by animateFloatAsState(
            targetValue = if (showInternalPageUIElementsTarget) 1f else 0f,
            animationSpec = tween(durationMillis = 200),
            label = "internalUiAlpha"
        )


        RedUrlVideoLiteChaintech(
            "https://api.redgifs.com/v2/gifs/${videoItem.id.lowercase()}/hd.m3u8",
            videoItem.urls.thumbnail,
            play = isCurrentPage,
            onChangeTime = onChangeTime
        )

        // Пример ВНУТРЕННЕГО UI элемента страницы
        if (isCurrentPage) { // Показываем только если это активная страница
            Text(
                text = "Page ${videoItem.id}",
                color = Color.White,
                modifier = Modifier
                    //.align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .alpha(internalUiAlpha)
            )
        }



    }
}
