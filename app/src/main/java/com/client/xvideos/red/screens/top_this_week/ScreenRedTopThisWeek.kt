package com.client.xvideos.red.screens.top_this_week

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.red.ThemeRed
import com.client.xvideos.red.common.users.UsersRed
import com.client.xvideos.red.screens.profile.ScreenRedProfile
import com.client.xvideos.red.screens.top_this_week.model.VisibleType
import com.client.xvideos.red.common.ui.lazyrow123.LazyRow123
import com.client.xvideos.red.screens.top_this_week.row1.TikTokPow1
import com.client.xvideos.red.screens.top_this_week.state.ErrorState
import com.client.xvideos.red.screens.top_this_week.state.FullScreenLoading
import com.client.xvideos.red.screens.top_this_week.state.LoadingNextPageIndicator
import timber.log.Timber

class ScreenRedTopThisWeek : Screen {
    override val key: ScreenKey = "ScreenRedTopThisWeek"

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: ScreenRedTopThisWeekSM = getScreenModel()

        val items = vm.lazyHost.pager.collectAsLazyPagingItems()

        val shouldScrollToTop by vm.lazyHost.scrollToTopAfterSortChange.collectAsState() // Подписываемся на флаг
        val visibleType by vm.visibleType.collectAsState()

        val isConnected by vm.isConnected.collectAsStateWithLifecycle()

        Timber.i("!!! key:${key} --- currentIndex:${vm.lazyHost.currentIndex} currentIndexGoto:${vm.lazyHost.currentIndexGoto}")


//        LaunchedEffect(Unit) {
//            Timber.d("!!! LaunchedEffect(Unit) currentIndexGoto:$currentIndexGoto currentIndex:$currentIndex")
//            currentIndexGoto = currentIndex
//        }

        LaunchedEffect(visibleType) {
            //vm.currentIndexGoto =  vm.currentIndex

            vm.lazyHost.columns = with(visibleType) {
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
                        currentSortType = vm.lazyHost.sortType.collectAsState().value,
                        listUsers = UsersRed.listAllUsers,
                        shouldScrollToTopAfterSortChange = shouldScrollToTop,
                        onScrollToTopIntentConsumed = { vm.lazyHost.consumedScrollToTopIntent() },
                        modifier = Modifier.fillMaxSize(),
                        onClickOpenProfile = { navigator.push(ScreenRedProfile(it)) },
                        onCurrentPosition = { index ->
                            vm.lazyHost.currentIndex = index
                        },
                        gotoPosition = vm.lazyHost.currentIndexGoto
                    )
                }

                if ((visibleType == VisibleType.ONE) || (visibleType == VisibleType.TWO) || (visibleType == VisibleType.THREE)) {

                    LazyRow123(
                        vm.lazyHost,
                        modifier = Modifier.fillMaxSize(),
                        onClickOpenProfile = { vm.lazyHost.currentIndexGoto =  vm.lazyHost.currentIndex; navigator.push(ScreenRedProfile(it)) },
                        gotoPosition = vm.lazyHost.currentIndexGoto,
                        option = vm.expandMenuVideoList,
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

                Text( vm.lazyHost.currentIndex.toString(), modifier = Modifier.align(Alignment.CenterEnd), color = Color.White)

            }

        }
    }
}

