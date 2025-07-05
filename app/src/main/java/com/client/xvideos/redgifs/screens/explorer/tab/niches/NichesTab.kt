package com.client.xvideos.redgifs.screens.explorer.tab.niches

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.client.xvideos.feature.connectivityObserver.ConnectivityObserver
import com.client.xvideos.redgifs.network.types.Niche
import com.client.xvideos.redgifs.network.types.Order
import com.client.xvideos.redgifs.ThemeRed
import com.client.xvideos.redgifs.common.ui.lazyrow123.LazyRow123Host
import com.client.xvideos.redgifs.common.ui.lazyrow123.NichePreview2
import com.client.xvideos.redgifs.common.ui.lazyrow123.TypePager
import com.client.xvideos.redgifs.common.ui.sortByOrder.SortByOrder
import com.client.xvideos.redgifs.screens.niche.ScreenRedNiche
import com.client.xvideos.redgifs.screens.profile.atom.VerticalScrollbar
import com.client.xvideos.redgifs.screens.profile.rememberVisibleRangePercentIgnoringFirstNForLazyColumn
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
            gridState = state, itemsToIgnore = 0
        )

        if (listNiche.itemCount == 0) return

        Scaffold(bottomBar = {
            SortByOrder(
                listOf(
                    Order.NICHES_SUBSCRIBERS,
                    Order.NICHES_POST,
                    Order.NICHES_NAME_A_Z,
                    Order.NICHES_NAME_Z_A
                ),
                vm.lazyHost.sortType.collectAsStateWithLifecycle().value,
                onSelect = { vm.lazyHost.changeSortType(it) })

        }, containerColor = ThemeRed.colorCommonBackground2) {

            Box(modifier = Modifier
                .padding(bottom = it.calculateBottomPadding())
                .fillMaxSize()) {

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
                                })
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
    connectivityObserver: ConnectivityObserver
) : ScreenModel {

    val lazyHost =
        LazyRow123Host(
            connectivityObserver = connectivityObserver,
            scope = screenModelScope,
            extraString = "",
            typePager = TypePager.EXPLORER_NICHES,
            startOrder = Order.NICHES_SUBSCRIBERS,
            startColumns = 1
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