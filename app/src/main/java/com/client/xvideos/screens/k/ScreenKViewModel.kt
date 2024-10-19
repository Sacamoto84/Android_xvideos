package com.client.xvideos.screens.k

import androidx.compose.foundation.pager.PagerState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.navigator.Navigator
import com.client.xvideos.screens.item.ScreenItem
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject

class ScreenKScreenModel @Inject constructor(

): ScreenModel {



}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleK {

    @Binds
    @IntoMap
    @ScreenModelKey(ScreenKScreenModel::class)
    abstract fun bindScreenKScreenModel(hiltListScreenModel: ScreenKScreenModel): ScreenModel

}