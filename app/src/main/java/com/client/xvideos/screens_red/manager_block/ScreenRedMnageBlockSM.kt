package com.client.xvideos.screens_red.manager_block

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject

class ScreenRedManageBlockSM @Inject constructor() : ScreenModel {





}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedMnageBlock {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedManageBlockSM::class)
    abstract fun bindScreenRedMnageBlockScreenModel(hiltListScreenModel: ScreenRedManageBlockSM): ScreenModel
}


