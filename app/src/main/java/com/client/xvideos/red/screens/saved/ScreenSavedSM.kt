package com.client.xvideos.red.screens.saved

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.client.xvideos.feature.connectivityObserver.ConnectivityObserver
import com.client.xvideos.red.common.ui.lazyrow123.LazyRow123Host
import com.client.xvideos.red.common.ui.lazyrow123.TypePager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject


class ScreenSavedSM @Inject constructor(
    connectivityObserver: ConnectivityObserver
) : ScreenModel {

    val likedHost =  LazyRow123Host(connectivityObserver = connectivityObserver, scope = screenModelScope, typePager = TypePager.LIKES)

    init {
        likedHost.columns = 2
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedSaved {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenSavedSM::class)
    abstract fun bindScreenRedSavedScreenModel(hiltListScreenModel: ScreenSavedSM): ScreenModel
}
