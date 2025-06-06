package com.client.xvideos.screens_red.top_this_week.row1

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.UserInfo
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.client.xvideos.screens_red.ThemeRed
import com.client.xvideos.screens_red.top_this_week.model.SortTop
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import timber.log.Timber

@Composable
fun TikTokPow1(
    lazyPagingItems: LazyPagingItems<GifsInfo>,
    currentSortType: SortTop,
    listUsers: List<UserInfo>, modifier: Modifier = Modifier,
    shouldScrollToTopAfterSortChange: Boolean = false,
    onScrollToTopIntentConsumed: () -> Unit, // Лямбда для сброса флага в SM
    onClickOpenProfile: (String) -> Unit = {},
    onCurrentPosition : (Int) -> Unit = {}, //Вывести текущую позицию
    gotoPosition : Int = 0
) {

    val pagerState = rememberPagerState(pageCount = { lazyPagingItems.itemCount })

    LaunchedEffect(currentSortType, lazyPagingItems.itemCount) {
        if (shouldScrollToTopAfterSortChange) {
            snapshotFlow { lazyPagingItems.loadState.refresh }
                .distinctUntilChanged()
                .filter { it is LoadState.NotLoading && lazyPagingItems.itemCount > 0 }
                .collect {
                    if (pagerState.pageCount > 0) {
                        Timber.d("!!! Scrolling VerticalPager to page 0. Current page: ${pagerState.currentPage}, Page count: ${pagerState.pageCount}, SortType: $currentSortType")
                        pagerState.scrollToPage(0)
                        onScrollToTopIntentConsumed()
                    } else {
                        Timber.d("!!! Not scrolling VerticalPager. Page count is 0 or less. SortType: $currentSortType")
                        onScrollToTopIntentConsumed()
                    }
                }
        }
        else {
            Timber.d("!!! TikTokPow1: No scroll intent or conditions not met. shouldScroll=$shouldScrollToTopAfterSortChange")
        }
    }

    LaunchedEffect(gotoPosition) {
        if (gotoPosition >= 0 && gotoPosition < lazyPagingItems.itemCount+1) {
            pagerState.scrollToPage(gotoPosition)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        onCurrentPosition(pagerState.currentPage)
    }

    VerticalPager(
        beyondViewportPageCount = 2,
        state = pagerState,
        modifier = Modifier.then(modifier),
        //key = { index -> listGifs[index].id } // Ключ для стабильности элементов
    ) { pageIndex ->
        val videoItem = lazyPagingItems[pageIndex]
        val isCurrentPage = pagerState.currentPage == pageIndex
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (videoItem != null) {
                UrlImage(videoItem.urls.poster!!, modifier = Modifier.aspectRatio(1080f / 1920), contentScale = ContentScale.Crop)
                ProfileInfo1(modifier= Modifier.align(Alignment.BottomStart),onClick = { onClickOpenProfile(videoItem.userName)}, videoItem = videoItem, listUsers = listUsers)
            }
        }
    }


    Box(
        modifier = Modifier.fillMaxSize().offset((-4).dp, (-4).dp), contentAlignment = Alignment.BottomEnd
    ) {
        Text(
            "${pagerState.currentPage} / ${lazyPagingItems.itemCount} ",
            color = Color.White,
            fontFamily = ThemeRed.fontFamilyPopinsRegular,
            fontSize = 14.sp
        )
    }

}


@Composable
fun ProfileInfo1(modifier: Modifier = Modifier, onClick: ()->Unit, videoItem: GifsInfo, listUsers: List<UserInfo>){
    Row(
        modifier = Modifier.then(modifier).background(Color(0x0CFFFFFF)).clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically) {

        val a = listUsers.firstOrNull { it1 -> it1.username == videoItem.userName }

        if ((a != null) && (a.profileImageUrl != null)) {
            Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).size(48.dp), contentAlignment = Alignment.Center) {
                UrlImage(a.profileImageUrl, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
        } else {
            Box(modifier = Modifier.clip(CircleShape).size(48.dp).background(Color.DarkGray), contentAlignment = Alignment.Center)
            { Icon(Icons.Default.AssignmentInd, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.White) }
        }
        ////////////
        Text(videoItem.userName, color = Color.White, fontFamily = ThemeRed.fontFamilyPopinsRegular, fontSize = 22.sp, modifier = Modifier.padding(start = 8.dp))
    }
}
