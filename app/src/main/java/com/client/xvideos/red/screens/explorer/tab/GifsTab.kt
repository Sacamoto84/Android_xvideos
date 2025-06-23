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
import com.client.xvideos.red.common.ui.lazyrow123.LazyRow123Host
import com.client.xvideos.red.common.ui.lazyrow123.TypePager
import com.client.xvideos.red.common.ui.sortByOrder.SortByOrder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import timber.log.Timber
import javax.inject.Inject

object GifsTab : Screen {

    private fun readResolve(): Any = GifsTab

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter",
        "UnusedMaterial3ScaffoldPaddingParameter"
    )
    @Composable
    override fun Content() {
        val vm: ScreenRedExplorerGifsSM = getScreenModel()

        Box(modifier = Modifier
            .size(64.dp)
            .background(Color.Green)) { Text("toString()") }

        Scaffold(topBar = {
            SortByOrder(
                listOf(Order.TOP_WEEK, Order.TOP_MONTH, Order.TOP_ALLTIME, Order.TRENDING, Order.LATEST),
                vm.lazyHost.sortType.collectAsStateWithLifecycle().value,
                onSelect = { vm.lazyHost.changeSortType(it) })

        },containerColor = ThemeRed.colorCommonBackground2) {

            Box(modifier = Modifier.padding(top = it.calculateTopPadding()).fillMaxSize()) {

                LazyRow123(
                    host = vm.lazyHost,
                    modifier = Modifier.fillMaxWidth(),
                    onClickOpenProfile = {
                        vm.lazyHost.currentIndexGoto = vm.lazyHost.currentIndex
                        //navigator.push(ScreenRedProfile(it))
                    },
                    gotoPosition = 0,
                    option = emptyList(),
                    contentPadding = PaddingValues(top = 0.dp),
                    contentBeforeList = { }
                )

            }


        }


    }

}


class ScreenRedExplorerGifsSM @Inject constructor(
    connectivityObserver: ConnectivityObserver
) : ScreenModel {

    init {
        Timber.i("!!! \uD83D\uDCE6 ScreenRedExplorerGifsSM::init")
    }

    override fun onDispose() {
        super.onDispose()
        Timber.i("!!!  \uD83D\uDCE6 ScreenRedExplorerGifsSM::onDispose")
    }

    val lazyHost =
        LazyRow123Host(
            connectivityObserver = connectivityObserver, scope = screenModelScope,
            extraString = "", typePager = TypePager.TOP
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