package com.client.xvideos.red.screens.explorer.tab

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.client.xvideos.feature.connectivityObserver.ConnectivityObserver
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.red.ThemeRed
import com.client.xvideos.red.common.ui.lazyrow123.LazyRow123
import com.client.xvideos.red.common.ui.lazyrow123.LazyRow123ExplorerNiches
import com.client.xvideos.red.common.ui.lazyrow123.LazyRow123Host
import com.client.xvideos.red.common.ui.lazyrow123.TypePager
import com.client.xvideos.red.common.ui.sortByOrder.SortByOrder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject

object NichesTab : Screen {

    private fun readResolve(): Any = NichesTab

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter",
        "UnusedMaterial3ScaffoldPaddingParameter"
    )
    @Composable
    override fun Content() {
        val vm: ScreenRedExplorerNichesSM = getScreenModel()

        Box(modifier = Modifier
            .size(64.dp)
            .background(Color.Green)) { Text("toString()") }

        Scaffold(topBar = {
            SortByOrder(
                listOf(Order.NICHES_SUBSCRIBERS, Order.NICHES_POST, Order.NICHES_NAME_A_Z, Order.NICHES_NAME_Z_A),
                vm.lazyHost.sortType.collectAsStateWithLifecycle().value,
                onSelect = { vm.lazyHost.changeSortType(it) })

        },containerColor = ThemeRed.colorCommonBackground2) {

            Box(modifier = Modifier.padding(top = it.calculateTopPadding()).fillMaxSize()) {

                LazyRow123ExplorerNiches(
                    host = vm.lazyHost,
                    modifier = Modifier.fillMaxWidth(),
                    onClickOpenProfile = {
                        vm.lazyHost.currentIndexGoto = vm.lazyHost.currentIndex
                        //navigator.push(ScreenRedProfile(it))
                    },
                    gotoPosition = vm.lazyHost.currentIndexGoto,
                    option = emptyList(),
                    contentPadding = PaddingValues(top = 0.dp),
                    contentBeforeList = { }
                )

            }


        }


    }

}


class ScreenRedExplorerNichesSM @Inject constructor(
    connectivityObserver: ConnectivityObserver
) : ScreenModel {

    val lazyHost =
        LazyRow123Host(
            connectivityObserver = connectivityObserver, scope = screenModelScope,
            extraString = "", typePager = TypePager.EXPLORER_NICHES, startOrder = Order.NICHES_SUBSCRIBERS, startColumns = 1
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