package com.client.xvideos.redgifs.ui.explorer.tab.saved.tab

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.client.xvideos.common.connectivityObserver.ConnectivityObserver
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.redgifs.common.di.HostDI
import com.client.xvideos.redgifs.ui.profile.ScreenRedProfile
import com.client.xvideos.redgifs.ui.ui.lazyrow123.LazyRow123
import com.client.xvideos.redgifs.ui.ui.lazyrow123.LazyRow123Host
import com.client.xvideos.redgifs.ui.ui.lazyrow123.model.TypePager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject

object R_Screen_Saved_LikesTab : Screen {

    private fun readResolve(): Any = R_Screen_Saved_LikesTab

    override val key: ScreenKey = uniqueScreenKey

    //@Transient
    //val columnSelect = ColumnSelect(Settings.l_likesTab_column_current_count)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: ScreenSavedLikesSM = getScreenModel()

        val columnSelect = Settings.r_likesTab_column_current_count.field.collectAsStateWithLifecycle().value

        val pager = vm.likedHost.pager.collectAsLazyPagingItems()

        //Изменение количества отображаемых элементов
        LaunchedEffect(columnSelect) { vm.likedHost.columns = columnSelect }

        LaunchedEffect(vm.hostDI.savedRed.likes.list){ pager.refresh() }

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF303030))) {

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

        }


    }



}

class ScreenSavedLikesSM @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    val hostDI : HostDI

) : ScreenModel {

    val likedHost = LazyRow123Host(
        connectivityObserver = connectivityObserver,
        scope = screenModelScope,
        typePager = TypePager.R_SAVED_LIKES,
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
