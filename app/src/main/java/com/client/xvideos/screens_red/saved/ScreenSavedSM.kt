package com.client.xvideos.screens_red.saved

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.client.xvideos.feature.connectivityObserver.ConnectivityObserver
import com.client.xvideos.screens_red.top_this_week.ScreenRedTopThisWeekSM
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject




class ScreenSavedSM @Inject constructor(
    connectivityObserver: ConnectivityObserver
) : ScreenModel {





}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedSaved {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenSavedSM::class)
    abstract fun bindScreenRedSavedScreenModel(hiltListScreenModel: ScreenSavedSM): ScreenModel
}
