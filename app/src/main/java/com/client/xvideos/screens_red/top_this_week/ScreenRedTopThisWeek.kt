package com.client.xvideos.screens_red.top_this_week

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.screens_red.ThemeRed
import com.client.xvideos.screens_red.listAllUsers
import com.client.xvideos.screens_red.profile.ScreenRedProfile
import com.client.xvideos.screens_red.top_this_week.model.SortTop
import com.client.xvideos.screens_red.top_this_week.model.VisibleType
import com.client.xvideos.screens_red.top_this_week.row1.LazyRow1
import com.client.xvideos.screens_red.top_this_week.row1.LazyRow2
import com.client.xvideos.screens_red.top_this_week.row1.TikTokPow1
import com.client.xvideos.screens_red.top_this_week.state.ErrorState
import com.client.xvideos.screens_red.top_this_week.state.FullScreenLoading
import com.client.xvideos.screens_red.top_this_week.state.LoadingNextPageIndicator
import timber.log.Timber

class ScreenRedTopThisWeek : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: ScreenRedTopThisWeekSM = getScreenModel()
        val items = vm.pager.collectAsLazyPagingItems()
        val shouldScrollToTop by vm.scrollToTopAfterSortChange.collectAsState() // Подписываемся на флаг
        val visibleType by vm.visibleType.collectAsState()


        Timber.i("!!! --- currentIndex:${vm.currentIndex} currentIndexGoto:${vm.currentIndexGoto}")



//        LaunchedEffect(Unit) {
//            Timber.d("!!! LaunchedEffect(Unit) currentIndexGoto:$currentIndexGoto currentIndex:$currentIndex")
//            currentIndexGoto = currentIndex
//        }

        LaunchedEffect(visibleType) {
            //vm.currentIndexGoto =  vm.currentIndex

            vm.columns = with(visibleType) {
                when (this) {
                    VisibleType.ONE -> 1
                    VisibleType.TWO -> 2
                    VisibleType.THREE -> 3
                    else -> 2
                }
            }

        }


        Scaffold(
            bottomBar = {
                BottomBar(
                    vm = vm,
                    onClickWeek = { vm.changeSortType(SortTop.WEEK) },
                    onClickMonth = { vm.changeSortType(SortTop.MONTH) },
                    onClickLazy = { vm.changeVisibleType(VisibleType.LAZY) },
                    onClickTiktok = { vm.changeVisibleType(VisibleType.PAGER) },
                    onClickLazyOne = { vm.changeVisibleType(VisibleType.ONE) },
                    onClickLazy2 = { vm.changeVisibleType(VisibleType.TWO) },
                    onClickLazy3 = { vm.changeVisibleType(VisibleType.THREE) }
                )
            },

            containerColor = ThemeRed.colorCommonBackground,
            modifier = Modifier.fillMaxSize()
        ) { padding ->

            Timber.d("!!! items.itemCount = ${items.itemCount}")

            // Отображаем индикатор загрузки поверх контента, если это первая загрузка
            val isLoadingInitial =
                items.loadState.refresh is LoadState.Loading// && items.itemCount == 0
            val isErrorInitial = items.loadState.refresh is LoadState.Error


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = padding.calculateBottomPadding())
            ) {


                if (visibleType == VisibleType.PAGER) {
                    TikTokPow1(
                        lazyPagingItems = items,
                        currentSortType = vm.sortType.collectAsState().value,
                        listUsers = listAllUsers,
                        shouldScrollToTopAfterSortChange = shouldScrollToTop,
                        onScrollToTopIntentConsumed = { vm.consumedScrollToTopIntent() },
                        modifier = Modifier.fillMaxSize(),
                        onClickOpenProfile = { navigator.push(ScreenRedProfile(it)) },
                        onCurrentPosition = { index ->
                            vm.currentIndex = index
                        },
                        gotoPosition = vm.currentIndexGoto
                    )
                }

//                if (visibleType == VisibleType.LAZY) {
//
//                    LazyRow1(
//                        listGifs = items.itemSnapshotList.items,
//                        listUsers = listAllUsers,
//                        modifier = Modifier.fillMaxSize(),
//                        onClickOpenProfile = { navigator.push(ScreenRedProfile(it)) },
//                        onCurrentPosition = { index ->
//                            currentIndex = index
//                        },
//                        gotoPosition = currentIndexGoto
//                    )
//
//                }

                if ((visibleType == VisibleType.ONE) || (visibleType == VisibleType.TWO) || (visibleType == VisibleType.THREE)) {

                    LazyRow2(
                        columns = vm.columns,
                        listGifs = items,
                        listUsers = listAllUsers,
                        modifier = Modifier.fillMaxSize(),
                        onClickOpenProfile = {
                            vm.currentIndexGoto =  vm.currentIndex
                            navigator.push(ScreenRedProfile(it))
                                             },
                        onCurrentPosition = { index ->
                            vm.currentIndex = index
                        },
                        gotoPosition = vm.currentIndexGoto
                    )

                }

                if (isLoadingInitial) {
                    FullScreenLoading(modifier = Modifier.align(Alignment.Center))
                }

                if (isErrorInitial) {
                    val errorState = items.loadState.refresh as LoadState.Error
                    ErrorState(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        message = "Ошибка загрузки: ${errorState.error.localizedMessage}",
                        onRetry = { items.retry() }
                    )
                }

                if (items.loadState.append is LoadState.Loading && items.itemCount > 0) {
                    LoadingNextPageIndicator(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    )
                }

                if (items.loadState.append is LoadState.Error && items.itemCount > 0) {
                    val errorState = items.loadState.append as LoadState.Error
                    // Можно показать маленькое сообщение об ошибке внизу
                    Text(
                        "Ошибка загрузки ленты: ${errorState.error.localizedMessage}",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .background(Color.Red),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Text( vm.currentIndex.toString(), modifier = Modifier.align(Alignment.CenterEnd), color = Color.White)

            }

        }
    }
}

