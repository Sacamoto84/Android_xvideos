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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.UserInfo
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.client.xvideos.screens_red.ThemeRed
import kotlin.Int

@Composable
fun TikTokPow1(
    listGifs: List<GifsInfo>, listUsers: List<UserInfo>, modifier: Modifier = Modifier,
    initialPage: Int = 0,
    onClickOpenProfile: (String) -> Unit = {}
) {

    val pagerState = rememberPagerState(pageCount = { listGifs.size })

    //LaunchedEffect(initialPage) { pagerState.scrollToPage(initialPage) }

    VerticalPager(
        beyondViewportPageCount = 2,
        state = pagerState,
        modifier = Modifier.then(modifier),
        key = { index -> listGifs[index].id } // Ключ для стабильности элементов
    ) { pageIndex ->

        val videoItem = listGifs[pageIndex]
        val isCurrentPage = pagerState.currentPage == pageIndex

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            UrlImage(
                videoItem.urls.poster!!,
                modifier = Modifier.aspectRatio(1080f / 1920),
                contentScale = ContentScale.Crop
            )


            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(Color(0x0CFFFFFF))
                    .clickable(
                        onClick = {
                            onClickOpenProfile(videoItem.userName)
                        }
                    ), verticalAlignment = Alignment.CenterVertically) {

                val a = listUsers.firstOrNull { it1 -> it1.username == videoItem.userName }

                if ((a != null) && (a.profileImageUrl != null)) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        UrlImage(
                            a.profileImageUrl,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(48.dp)
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AssignmentInd,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.Green
                        )
                    }

                }

                ////////////
                Text(
                    videoItem.userName,
                    color = Color.White,
                    fontFamily = ThemeRed.fontFamilyPopinsRegular,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )

            }

        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset((-4).dp, (-4).dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Text(
            "${pagerState.currentPage} / ${listGifs.size} ",
            color = Color.White,
            fontFamily = ThemeRed.fontFamilyPopinsRegular,
            fontSize = 14.sp
        )
    }


}