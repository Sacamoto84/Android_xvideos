package com.client.xvideos.red.screens.explorer.tab.saved.tab

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.client.xvideos.red.common.expand_menu_video.impl.ExpandMenuVideoImpl
import com.client.xvideos.red.common.saved.SavedRed
import com.client.xvideos.red.common.ui.lazyrow123.LazyRow123
import com.client.xvideos.red.common.ui.lazyrow123.LazyRow123Host
import com.client.xvideos.red.common.ui.lazyrow123.TypePager
import com.client.xvideos.red.screens.profile.ScreenRedProfile
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject


object SavedLikesTab : Screen {

    private fun readResolve(): Any = SavedLikesTab

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: ScreenSavedLikesSM = getScreenModel()

        LaunchedEffect(SavedRed.likesList){
            vm.likedHost.refresh()
        }

        LazyRow123(
            host = vm.likedHost,
            modifier = Modifier.fillMaxSize(),
            onClickOpenProfile = {
                vm.likedHost.currentIndexGoto = vm.likedHost.currentIndex
                navigator.push(ScreenRedProfile(it))
            },
            gotoPosition = vm.likedHost.currentIndexGoto,
            option = ExpandMenuVideoImpl.expandMenuVideoListLikes,
            contentPadding = PaddingValues(0.dp),
            contentBeforeList = { },

            onRun3 = {
                vm.likedHost.refresh()
            }
        )

    }
}

class ScreenSavedLikesSM @Inject constructor(
    connectivityObserver: ConnectivityObserver
) : ScreenModel {
    val likedHost = LazyRow123Host(
        connectivityObserver = connectivityObserver,
        scope = screenModelScope,
        typePager = TypePager.SAVED_LIKES
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
