package com.client.xvideos.screens_red.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.client.xvideos.feature.room.AppDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject


class ScreenRedHomeSM @Inject constructor(
    private val db: AppDatabase,
) : ScreenModel {



}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedHome {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedHomeSM::class)
    abstract fun bindScreenRedHomecreenModel(hiltListScreenModel: ScreenRedHomeSM): ScreenModel
}