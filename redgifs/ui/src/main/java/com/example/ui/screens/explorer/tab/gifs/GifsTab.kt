package com.example.ui.screens.explorer.tab.gifs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TimeInput
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.client.common.connectivityObserver.ConnectivityObserver
import com.client.common.sharedPref.SettingElementInt
import com.client.common.sharedPref.Settings
import com.example.ui.screens.explorer.ScreenRedExplorer.Companion.screenType
import com.example.ui.screens.profile.ScreenRedProfile
import com.example.ui.screens.profile.atom.VerticalScrollbar
import com.example.ui.screens.profile.rememberVisibleRangePercentIgnoringFirstNForGrid
import com.example.ui.screens.ui.atom.ButtonUp
import com.example.ui.screens.ui.lazyrow123.LazyRow123
import com.example.ui.screens.ui.lazyrow123.LazyRow123Host
import com.example.ui.screens.ui.lazyrow123.TypePager
import com.example.ui.screens.ui.sortByOrder.SortByOrder
import com.redgifs.common.ThemeRed
import com.redgifs.common.di.HostDI
import com.redgifs.model.Order
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject



class ColumnSelect(private val pref: SettingElementInt){

    var column by mutableIntStateOf( pref.field.value )

    fun addColumn(g0: Boolean, g1: Boolean, g2: Boolean, g3: Boolean, g4: Boolean) {
        val flags = listOf(g0, g1, g2, g3, g4)
        val enabledIndices =
            flags.mapIndexedNotNull { index, enabled -> if (enabled) index else null }
        if (enabledIndices.isEmpty()) return // ничего не включено
        val currentIndex = column
        val currentPos = enabledIndices.indexOf(currentIndex).takeIf { it != -1 } ?: 2
        val nextPos = (currentPos + 1) % enabledIndices.size
        column = enabledIndices[nextPos]
        pref.setValue(column)
    }

}







object GifsTab : Screen {

    private fun readResolve(): Any = GifsTab

    override val key: ScreenKey = uniqueScreenKey

    @Transient
    val columnSelect  = ColumnSelect(Settings.current_count_niches)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val vm: ScreenRedExplorerGifsSM = getScreenModel()

        val navigator = LocalNavigator.currentOrThrow

        val state = rememberPullToRefreshState()
        var isRefreshing by remember { mutableStateOf(false) }

        val host = vm.lazyHost.pager.collectAsLazyPagingItems()

        val scope = rememberCoroutineScope()

        val haptic = LocalHapticFeedback.current

        val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForGrid(
            gridState = vm.lazyHost.state, itemsToIgnore = 0, numberOfColumns = columnSelect.column
        )

        val search = vm.hostDI.search

        val searchR = search.searchText.collectAsStateWithLifecycle().value

        LaunchedEffect(columnSelect.column) {
            vm.lazyHost.columns = columnSelect.column
        }

        val isFocused = vm.hostDI.search.focused.collectAsStateWithLifecycle().value

        Scaffold(bottomBar = {

            Column(Modifier.background(ThemeRed.colorTabLevel1)) {
                HorizontalDivider(color = ThemeRed.colorBorderGray)
                Row(
                    modifier = Modifier
                        .padding(top = 1.dp, start = 1.dp)
                        .background(ThemeRed.colorTabLevel1), verticalAlignment = Alignment.Bottom
                ) {

                    AnimatedVisibility(
                        visible = !isFocused,
                        enter = expandHorizontally(animationSpec = tween(durationMillis = 250)) + fadeIn(
                            animationSpec = tween(durationMillis = 250)
                        ),
                        exit = shrinkHorizontally(animationSpec = tween(durationMillis = 250)) + fadeOut(
                            animationSpec = tween(durationMillis = 250)
                        ),
                    ) {

                        //
                        if (searchR.text == "") {
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

                    }

                    search.CustomBasicTextField(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .weight(1f)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    AnimatedVisibility(
                        visible = !isFocused,

                        enter = expandHorizontally(
                            animationSpec = tween(durationMillis = 250),
                            expandFrom = Alignment.Start
                        ) + fadeIn(
                            animationSpec = tween(durationMillis = 250)
                        ),
                        exit = shrinkHorizontally(
                            animationSpec = tween(durationMillis = 250),
                            shrinkTowards = Alignment.Start
                        ) + fadeOut(
                            animationSpec = tween(durationMillis = 250)
                        ),

                        ) {
                        ButtonUp {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            vm.lazyHost.gotoUp()
                        }
                    }


                }
                Spacer(modifier = Modifier.height(1.dp))
                HorizontalDivider(color = ThemeRed.colorCommonBackground)
                HorizontalDivider(color = ThemeRed.colorBorderGray)
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
    val hostDI: HostDI
) : ScreenModel {

    val lazyHost = LazyRow123Host(
        connectivityObserver = connectivityObserver,
        scope = screenModelScope,
        extraString = "",
        typePager = TypePager.TOP,
        hostDI = hostDI
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