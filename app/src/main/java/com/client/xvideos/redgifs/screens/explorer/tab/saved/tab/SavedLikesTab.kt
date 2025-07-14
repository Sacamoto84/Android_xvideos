package com.client.xvideos.redgifs.screens.explorer.tab.saved.tab

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.client.xvideos.redgifs.common.ui.lazyrow123.LazyRow123
import com.client.xvideos.redgifs.common.ui.lazyrow123.LazyRow123Host
import com.client.xvideos.redgifs.common.ui.lazyrow123.TypePager
import com.client.xvideos.redgifs.screens.profile.ScreenRedProfile
import com.client.xvideos.redgifs.screens.profile.atom.VerticalScrollbar
import com.client.xvideos.redgifs.screens.profile.rememberVisibleRangePercentIgnoringFirstNForGrid
import com.redgifs.common.di.HostDI
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.DelicateCoroutinesApi
import javax.inject.Inject


object SavedLikesTab : Screen {

    private fun readResolve(): Any = SavedLikesTab

    override val key: ScreenKey = uniqueScreenKey

    val column = mutableIntStateOf(2)

    fun addColumn() {
        column.intValue += 1
        if(column.intValue > 3)
            column.intValue = 1
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: ScreenSavedLikesSM = getScreenModel()

        LaunchedEffect(column.intValue) {
            vm.likedHost.columns = column.intValue
        }

        LaunchedEffect(vm.hostDI.savedRed.likes.list){
            //vm.likedHost.refresh()
        }

        val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForGrid(
            gridState = vm.likedHost.state, itemsToIgnore = 0, numberOfColumns = column.intValue
        )

        Box(modifier = Modifier.fillMaxSize()) {

            LazyRow123(
                host = vm.likedHost,
                modifier = Modifier.fillMaxSize(),
                onClickOpenProfile = {
                    //vm.likedHost.currentIndexGoto = vm.likedHost.currentIndex
                    navigator.push(ScreenRedProfile(it))
                },
                //gotoPosition = vm.likedHost.currentIndexGoto,
                contentPadding = PaddingValues(0.dp),
                contentBeforeList = { },

                isRunLike = true

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

class ScreenSavedLikesSM @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    val hostDI : HostDI

) : ScreenModel {

    @OptIn(DelicateCoroutinesApi::class)
    val likedHost = LazyRow123Host(
        connectivityObserver = connectivityObserver,
        scope = screenModelScope,
        typePager = TypePager.SAVED_LIKES,
        hostDI = hostDI
    )

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedSavedLikes {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenSavedLikesSM::class)
    abstract fun bindScreenRedSavedLikesScreenModel(hiltListScreenModel: ScreenSavedLikesSM): ScreenModel
}
