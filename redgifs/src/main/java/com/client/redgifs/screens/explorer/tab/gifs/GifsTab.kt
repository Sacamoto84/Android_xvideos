package com.client.redgifs.screens.explorer.tab.gifs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.redgifs.ThemeRed
import com.client.redgifs.common.block.BlockRed
import com.client.redgifs.common.search.SearchRed
import com.client.redgifs.common.ui.lazyrow123.LazyRow123
import com.client.redgifs.network.types.Order
import com.client.redgifs.screens.LocalRootScreenModel
import com.client.redgifs.screens.profile.ScreenRedProfile
import com.client.redgifs.screens.profile.rememberVisibleRangePercentIgnoringFirstNForGrid
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

object GifsTab : Screen {

    private fun readResolve(): Any = GifsTab

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val vm: ScreenRedExplorerGifsSM = getScreenModel()

        val navigator = LocalNavigator.currentOrThrow

        val root = LocalRootScreenModel.current

        val block = vm.block

        val state = rememberPullToRefreshState()
        var isRefreshing by remember { mutableStateOf(false) }

        val host = vm.lazyHost.pager.collectAsLazyPagingItems()

        val scope = rememberCoroutineScope()

        val haptic = LocalHapticFeedback.current

        val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForGrid(
            gridState = vm.lazyHost.state, itemsToIgnore = 0, numberOfColumns = column.intValue
        )

        val searchR = SearchRed.searchText.collectAsStateWithLifecycle().value

        Scaffold(bottomBar = {
            Row(modifier = background(ThemeRed.colorTabLevel0).height(50.dp), verticalAlignment = Alignment.Bottom) {
                //
                if (searchR == "") {
                    SortByOrder(
                        containerColor = ThemeRed.colorCommonBackground,
                        list = listOf(
                            Order.TOP_WEEK,
                            Order.TOP_MONTH,
                            Order.TOP_ALLTIME,
                            Order.TRENDING,
                            Order.LATEST
                        ),
                        selected = vm.lazyHost.sortType.collectAsStateWithLifecycle().value,
                        onSelect = { vm.lazyHost.changeSortType(it) }

                    )
                } else {
                    SortByOrder(
                        containerColor = ThemeRed.colorCommonBackground,
                        list = listOf(Order.TOP, Order.TRENDING, Order.LATEST),
                        selected = vm.lazyHost.sortType.collectAsStateWithLifecycle().value,
                        onSelect = { vm.lazyHost.changeSortType(it) })
                }

                SearchRed.CustomBasicTextField(
                    value = SearchRed.searchText.collectAsStateWithLifecycle().value,
                    onValueChange = { SearchRed.searchText.value = it }, onDone = { SearchRed.searchTextDone.value = it },
                    modifier = Modifier.padding(start = 4.dp).weight(1f)
                )

                Box(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(48.dp)
                        .border(1.dp, Color(0x80757575), RoundedCornerShape(8.dp))
                        .background(ThemeRed.colorCommonBackground)
                        .clickable(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                vm.lazyHost.gotoUp()
                            }), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.ArrowUpward,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier
                    )
                }


            }
        }, containerColor = ThemeRed.colorCommonBackground) {

            Box(
                modifier = Modifier
                    .padding(bottom = it.calculateBottomPadding())
                    .fillMaxSize()
            ) {

                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        scope.launch {
                            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                            isRefreshing = true
                            delay(500)
                            isRefreshing = false

                        }

                        host.refresh()

                    },
                    modifier = Modifier,
                    state = state,
                    indicator = {
                        Indicator(
                            modifier = Modifier.align(Alignment.TopCenter),
                            isRefreshing = isRefreshing,
                            containerColor = Color.White,
                            color = Color.Black,
                            state = state
                        )
                    },
                ) {


                    LazyRow123(
                        host = vm.lazyHost,
                        modifier = Modifier.fillMaxSize(),
                        onClickOpenProfile = { name ->
                            vm.lazyHost.currentIndexGoto = vm.lazyHost.currentIndex
                            navigator.push(ScreenRedProfile(name))
                        },
                        contentPadding = PaddingValues(top = 0.dp),
                        contentBeforeList = { },
                    )

                    //---- Скролл ----
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(Alignment.CenterEnd)
                            .width(2.dp)
                    ) {
                        VerticalScrollbar(scrollPercent)
                    }


                }


            }


        }


    }

}


class ScreenRedExplorerGifsSM @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    val block: BlockRed
) : ScreenModel {

    val isConnected = connectivityObserver.isConnected.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000L),
        false
    )
    val lazyHost = LazyRow123Host(
        connectivityObserver = connectivityObserver,
        scope = screenModelScope,
        extraString = "",
        typePager = TypePager.TOP,
        block = block
    )


}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedExplorerGifs {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedExplorerGifsSM::class)
    abstract fun bindScreenRedExplorerGifsSreenModel(hiltListScreenModel: ScreenRedExplorerGifsSM): ScreenModel
}