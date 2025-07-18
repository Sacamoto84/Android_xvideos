package com.example.ui.screens.explorer.tab.niches

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
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
import com.example.ui.screens.niche.ScreenRedNiche
import com.example.ui.screens.profile.atom.VerticalScrollbar
import com.example.ui.screens.profile.rememberVisibleRangePercentIgnoringFirstNForLazyColumn
import com.example.ui.screens.ui.atom.ButtonUp
import com.example.ui.screens.ui.lazyrow123.LazyRow123Host
import com.example.ui.screens.ui.lazyrow123.NichePreview2
import com.example.ui.screens.ui.lazyrow123.TypePager
import com.example.ui.screens.ui.sortByOrder.SortByOrder
import com.redgifs.common.ThemeRed
import com.redgifs.common.di.HostDI
import com.redgifs.common.search.SearchNichesRed
import com.redgifs.model.Niche
import com.redgifs.model.Order
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject

object NichesTab : Screen {

    private fun readResolve(): Any = NichesTab

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint(
        "UnusedMaterialScaffoldPaddingParameter",
        "UnusedMaterial3ScaffoldPaddingParameter"
    )
    @Composable
    override fun Content() {
        val vm: ScreenRedExplorerNichesSM = getScreenModel()

        val navigator = LocalNavigator.currentOrThrow


        val state = vm.lazyHost.stateColumn

        val listNiche = vm.lazyHost.pager.collectAsLazyPagingItems() as LazyPagingItems<Niche>

        val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForLazyColumn(
            gridState = state,
            itemsToIgnore = 0
        )

        val haptic = LocalHapticFeedback.current

        if (listNiche.itemCount == 0) return

        Scaffold(bottomBar = {
            Column(Modifier.background(ThemeRed.colorTabLevel1)) {

                //Spacer(modifier = Modifier.height(1.dp))
                HorizontalDivider(color = ThemeRed.colorBorderGray)

                //Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 1.dp, start = 1.dp)
                        .background(ThemeRed.colorTabLevel1), horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    SortByOrder(
                        listOf(
                            Order.NICHES_SUBSCRIBERS_D,
                            Order.NICHES_SUBSCRIBERS_A,
                            Order.NICHES_POST_D,
                            Order.NICHES_POST_A,
                            Order.NICHES_NAME_A_Z,
                            Order.NICHES_NAME_Z_A
                        ),
                        vm.lazyHost.sortType.collectAsStateWithLifecycle().value,
                        onSelect = { vm.lazyHost.changeSortType(it) },
                        containerColor = ThemeRed.colorCommonBackground
                    )

                    vm.search.CustomBasicTextField(
                        value =  vm.search.searchText.collectAsStateWithLifecycle().value,
                        onValueChange = {  vm.search.searchText.value = it },
                        onDone = {  vm.search.searchTextDone.value = it },
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .weight(1f)
                    )


                    ButtonUp {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        vm.lazyHost.gotoUpColumn()
                    }

                }
                Spacer(modifier = Modifier.height(1.dp))
                HorizontalDivider(color = ThemeRed.colorCommonBackground)
                HorizontalDivider(color = ThemeRed.colorBorderGray)
                //HorizontalDivider(color = ThemeRed.colorCommonBackground)
            }
        }, containerColor = ThemeRed.colorCommonBackground2) {

            Box(
                modifier = Modifier
                    .padding(bottom = it.calculateBottomPadding())
                    .fillMaxSize()
            ) {

                LazyColumn(
                    state = state,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        count = listNiche.itemCount,
                    ) { index ->
                        val item = listNiche[index]
                        if (item != null) {
                            Box(modifier = Modifier.padding(vertical = 4.dp)) {
                                NichePreview2(niches = item, onClick = {
                                    navigator.push(ScreenRedNiche(item.id))
                                }, savedRed = vm.hostDI.savedRed)

                                Text(
                                    index.toString(),
                                    color = Color.Gray,
                                    fontFamily = ThemeRed.fontFamilyDMsanss,
                                    modifier = Modifier.padding(end = 16.dp).align(
                                        Alignment.TopEnd
                                    ), fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

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


class ScreenRedExplorerNichesSM @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    val hostDI: HostDI,
    val search: SearchNichesRed
) : ScreenModel {

    val lazyHost =
        LazyRow123Host(
            connectivityObserver = connectivityObserver,
            scope = screenModelScope,
            extraString = "",
            typePager = TypePager.EXPLORER_NICHES,
            startOrder = Order.NICHES_SUBSCRIBERS_D,
            startColumns = 1,
            hostDI = hostDI
        )

}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedExplorerNiches {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedExplorerNichesSM::class)
    abstract fun bindScreenRedExplorerNichesSreenModel(hiltListScreenModel: ScreenRedExplorerNichesSM): ScreenModel
}