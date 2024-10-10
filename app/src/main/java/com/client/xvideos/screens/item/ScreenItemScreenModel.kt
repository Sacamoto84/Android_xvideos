package com.client.xvideos.screens.item

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject


class ScreenItemScreenModel @Inject constructor(): ScreenModel {

    init{

    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleItem{

    @Binds
    @IntoMap
    @ScreenModelKey(ScreenItemScreenModel::class)
    abstract fun bindScreenItemScreenModel(hiltListScreenModel: ScreenItemScreenModel): ScreenModel

}