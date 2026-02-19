package com.client.xvideos.redgifs.ui.explorer.tab.niches

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.common.connectivityObserver.ConnectivityObserver
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.common.di.HostDI
import com.client.xvideos.redgifs.common.saved.SavedRed
import com.client.xvideos.redgifs.common.search.SearchNichesRed
import com.client.xvideos.redgifs.model.Niche
import com.client.xvideos.redgifs.model.Order
import com.client.xvideos.redgifs.ui.niche.R_ScreenNiche
import com.client.xvideos.redgifs.ui.profile.atom.VerticalScrollbar
import com.client.xvideos.redgifs.ui.profile.rememberVisibleRangePercentIgnoringFirstNForLazyColumn
import com.client.xvideos.redgifs.ui.ui.atom.ButtonUp
import com.client.xvideos.redgifs.ui.ui.lazyrow123.LazyRow123Host
import com.client.xvideos.redgifs.ui.ui.lazyrow123.model.TypePager
import com.client.xvideos.redgifs.ui.ui.sortByOrder.SortByOrder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

object R_ScreenNichesTab : Screen {

    private fun readResolve(): Any = R_ScreenNichesTab

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val vm: ScreenRedExplorerNichesSM = getScreenModel()
        val navigator = LocalNavigator.currentOrThrow

        val listNiche = vm.nichesPager.collectAsLazyPagingItems()
        val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForLazyColumn(gridState = vm.lazyHost.stateColumn)
        
        val sortType by vm.lazyHost.sortType.collectAsStateWithLifecycle()
        val searchText by vm.search.searchText.collectAsStateWithLifecycle()
        val isSearchFocused by vm.search.focused.collectAsStateWithLifecycle()

        NichesTabContent(
            listNiche = listNiche,
            state = vm.lazyHost.stateColumn,
            scrollPercent = scrollPercent,
            sortType = sortType,
            onSortTypeChange = { vm.lazyHost.changeSortType(it) },
            isSearchFocused = isSearchFocused,
            onUpClick = { vm.lazyHost.gotoUpColumn() },
            onNicheClick = { id -> navigator.push(R_ScreenNiche(id)) },
            savedRed = vm.hostDI.savedRed,
            searchWidget = { modifier ->
                vm.search.CustomBasicTextField(
                    value = searchText,
                    onValueChange = { vm.search.searchText.value = it },
                    onDone = { vm.search.searchTextDone.value = it },
                    modifier = modifier
                )
            }
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NichesTabContent(
    listNiche: LazyPagingItems<Niche>,
    state: LazyListState,
    scrollPercent: Pair<Float, Float>,
    sortType: Order,
    onSortTypeChange: (Order) -> Unit,
    isSearchFocused: Boolean,
    onUpClick: () -> Unit,
    onNicheClick: (String) -> Unit,
    savedRed: SavedRed?,
    searchWidget: @Composable (Modifier) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Scaffold(
        bottomBar = {
            Column(Modifier.background(ThemeRed.colorTabLevel1)) {
                HorizontalDivider(color = ThemeRed.colorBorderGray)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp, horizontal = 4.dp)
                        .background(ThemeRed.colorTabLevel1),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedVisibility(visible = !isSearchFocused) {
                        SortByOrder(
                            list = listOf(
                                Order.NICHES_SUBSCRIBERS_D,
                                Order.NICHES_SUBSCRIBERS_A,
                                Order.NICHES_POST_D,
                                Order.NICHES_POST_A,
                                Order.NICHES_NAME_A_Z,
                                Order.NICHES_NAME_Z_A
                            ),
                            selected = sortType,
                            onSelect = onSortTypeChange,
                            containerColor = ThemeRed.colorTabLevel0
                        )
                    }

                    searchWidget( Modifier.padding(horizontal = 4.dp).weight(1f) )

                    AnimatedVisibility(visible = !isSearchFocused) {
                        ButtonUp {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onUpClick()
                        }
                    }
                }
                HorizontalDivider(color = ThemeRed.colorBorderGray)
            }
        },
        containerColor = ThemeRed.colorCommonBackground2
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()).fillMaxSize()
        ) {
            LazyColumn(
                state = state,
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    count = listNiche.itemCount,
                    key = listNiche.itemKey { it.id },
                    contentType = listNiche.itemContentType { "niche" }
                ) { index ->

                    val item = listNiche[index]

                    if (item != null) {
                        Box(modifier = Modifier.padding(vertical = 4.dp)) {

                            if (savedRed != null) {
                                NichePreview2(
                                    niches = { item },
                                    onClick = { onNicheClick(item.id) },
                                    savedRed = { savedRed }
                                )
                            } else {
                                // Placeholder for Preview
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .fillMaxWidth().height(78.dp)
                                        .background(Color(0xFF323232), RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = item.name,
                                        color = Color.White, modifier = Modifier.padding(start = 16.dp),
                                        fontFamily = ThemeRed.fontFamilyDMsanss
                                    )
                                }
                            }

                            Text(
                                text = (index + 1).toString(),
                                color = Color.Gray,
                                fontFamily = ThemeRed.fontFamilyDMsanss,
                                modifier = Modifier
                                    .padding(top = 8.dp, end = 16.dp)
                                    .align(Alignment.TopEnd),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
                
                if (listNiche.loadState.refresh is LoadState.Loading || listNiche.loadState.append is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = ThemeRed.colorYellow)
                        }
                    }
                }
            }

            // Scrollbar
            Box(
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd).width(2.dp)
            ) {
                VerticalScrollbar(scrollPercent)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun R_ScreenNichesTabPreview() {
    val items = remember {
        listOf(
            Niche("1", "Amateurs", 1200, 5000, "", null),
            Niche("2", "Anal", 1500, 8000, "", null),
            Niche("3", "Babe", 800, 3000, "", null),
            Niche("4", "Blowjob", 2500, 15000, "", null),
            Niche("5", "Creampie", 1800, 9000, "", null)
        )
    }
    val pagingData = PagingData.from(items)
    val listNiche = flowOf(pagingData).collectAsLazyPagingItems()
    
    NichesTabContent(
        listNiche = listNiche,
        state = rememberLazyListState(),
        scrollPercent = 0f to 0.3f,
        sortType = Order.NICHES_SUBSCRIBERS_D,
        onSortTypeChange = {},
        isSearchFocused = false,
        onUpClick = {},
        onNicheClick = {},
        savedRed = null,
        searchWidget = { modifier ->
            Box(
                modifier
                    .height(44.dp)
                    .background(ThemeRed.colorTabLevel0, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    "Search niches...",
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 12.dp),
                    fontSize = 14.sp
                )
            }
        }
    )
}

class ScreenRedExplorerNichesSM @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    val hostDI: HostDI,
    val search: SearchNichesRed
) : ScreenModel {

    val lazyHost = LazyRow123Host(
        connectivityObserver = connectivityObserver,
        scope = screenModelScope,
        extraString = "",
        typePager = TypePager.EXPLORER_NICHES,
        startOrder = Order.NICHES_SUBSCRIBERS_D,
        startColumns = 1,
        hostDI = hostDI
    )

    @Suppress("UNCHECKED_CAST")
    val nichesPager: Flow<PagingData<Niche>> = lazyHost.pager
        .map { it as PagingData<Niche> }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedExplorerNiches {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedExplorerNichesSM::class)
    abstract fun bindScreenRedExplorerNichesSreenModel(hiltListScreenModel: ScreenRedExplorerNichesSM): ScreenModel
}
