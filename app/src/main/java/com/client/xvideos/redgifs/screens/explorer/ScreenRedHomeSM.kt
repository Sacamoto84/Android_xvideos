package com.client.xvideos.redgifs.screens.explorer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.client.xvideos.feature.room.AppDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject


class ScreenRedExplorerSM @Inject constructor(
    private val db: AppDatabase,
) : ScreenModel {

   var screenType by mutableIntStateOf(0)


  var count by mutableIntStateOf(0)





}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedExplorer {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedExplorerSM::class)
    abstract fun bindScreenRedExplorerSreenModel(hiltListScreenModel: ScreenRedExplorerSM): ScreenModel
}