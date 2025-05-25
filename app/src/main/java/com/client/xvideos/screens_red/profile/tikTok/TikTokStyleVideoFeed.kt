package com.client.xvideos.screens_red.profile.tikTok

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.AppPath
import com.client.xvideos.feature.findVideoOnRedCacheDownload
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.screens_red.ThemeRed
import com.client.xvideos.screens_red.profile.ScreenRedProfileSM
import com.client.xvideos.screens_red.profile.atom.RedUrlVideoLiteChaintech
import timber.log.Timber

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TikTokStyleVideoFeed(
    vm: ScreenRedProfileSM,
    videoItems: List<GifsInfo>,
    modifier: Modifier = Modifier,
    onChangeTime: (Pair<Float, Int>) -> Unit,
    onPageUIElementsVisibilityChange: (Boolean) -> Unit, // Новый колбэк

    onClick: (GifsInfo) -> Unit = {},
    onLongClick: (GifsInfo) -> Unit = {},

    timeA: Float = 3f,
    timeB: Float = 6f,
    enableAB: Boolean = false,

    isMute: Boolean = true, //Глобальный мут


    /**
     * Вызывается при изменении текущей страницы в пейджере.
     */
    onChangePagerPage: (Int) -> Unit = {},

    menuContent: @Composable () -> Unit = {},
    menuContentWidth: Dp = 192.dp

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
        onPageUIElementsVisibilityChange(!pagerState.isScrollInProgress)
        onChangePagerPage(pagerState.currentPage)
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


        val videoUri: String = remember(videoItem.id, videoItem.userName) {
            Timber.tag("???").i("Перерачсет videoItem.id = ${videoItem.id}")
            //Определяем адрес откуда брать видео, из кеша или из сети
            if (findVideoOnRedCacheDownload(videoItem.id, videoItem.userName))
                "${AppPath.cache_download_red}/${videoItem.userName}/${videoItem.id}.mp4"
            else
                "https://api.redgifs.com/v2/gifs/${videoItem.id.lowercase()}/hd.m3u8"
        }

        RedUrlVideoLiteChaintech(
            url = videoUri,
            videoItem.urls.thumbnail,
            play = isCurrentPage and vm.play,
            onChangeTime = onChangeTime,
            isMute = isMute,
            onPlayerControlsReady = { controls ->
                // Этот колбэк вызовется для КАЖДОЙ страницы пейджера,
                // когда её плеер будет готов. Нам нужен контроллер только для ТЕКУЩЕЙ страницы.
                if (isCurrentPage) {
                    vm.currentPlayerControls = controls
                }
            },
            timeA = timeA,
            timeB = timeB,
            enableAB = enableAB,
            onClick = {
                vm.play = !vm.play
            },
            menuContent = menuContent,
            menuContentWidth = menuContentWidth
        )

    }
}
