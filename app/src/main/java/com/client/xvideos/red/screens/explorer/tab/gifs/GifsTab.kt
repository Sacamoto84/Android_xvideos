package com.client.xvideos.red.screens.explorer.tab.gifs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.red.ThemeRed
import com.client.xvideos.red.common.ui.lazyrow123.LazyRow123
import com.client.xvideos.red.common.ui.lazyrow123.LazyRow123Host
import com.client.xvideos.red.common.ui.lazyrow123.TypePager
import com.client.xvideos.red.common.ui.sortByOrder.SortByOrder
import com.client.xvideos.red.screens.LocalRootScreenModel
import com.client.xvideos.red.screens.profile.ScreenRedProfile
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

object GifsTab : Screen {

    private fun readResolve(): Any = GifsTab

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val vm: ScreenRedExplorerGifsSM = getScreenModel()

        val  navigator = LocalNavigator.currentOrThrow

        //val root: ScreenRedRootSM = getScreenModel<ScreenRedRootSM>()

        val root = LocalRootScreenModel.current

//        LaunchedEffect(Unit) {
//            root.showSnackbar("Snackbar из вложенного экрана")
//        }

        Scaffold(bottomBar = {
            Row(modifier = Modifier.background(ThemeRed.colorCommonBackground2)) {
               //
                SortByOrder(
                    listOf(
                        Order.TOP_WEEK,
                        Order.TOP_MONTH,
                        Order.TOP_ALLTIME,
                        Order.TRENDING,
                        Order.LATEST
                    ),
                    vm.lazyHost.sortType.collectAsStateWithLifecycle().value,
                    onSelect = { vm.lazyHost.changeSortType(it) })
                //
                //Button(onClick = {root.showSnackbar("Snackbar из вложенного экрана")}){}
            }
        }, containerColor = ThemeRed.colorCommonBackground) {

            Box(modifier = Modifier
                .padding(bottom = it.calculateBottomPadding())
                .fillMaxSize()) {

                LazyRow123(
                    host = vm.lazyHost,
                    modifier = Modifier.fillMaxSize(),
                    onClickOpenProfile = { name ->
                        vm.lazyHost.currentIndexGoto = vm.lazyHost.currentIndex
                        navigator.push(ScreenRedProfile(name))
                    },
                    gotoPosition = 0,
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

    val isConnected = connectivityObserver.isConnected.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000L), false)
    val lazyHost = LazyRow123Host(connectivityObserver = connectivityObserver, scope = screenModelScope, extraString = "", typePager = TypePager.TOP)

}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedExplorerGifs {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedExplorerGifsSM::class)
    abstract fun bindScreenRedExplorerGifsSreenModel(hiltListScreenModel: ScreenRedExplorerGifsSM): ScreenModel
}