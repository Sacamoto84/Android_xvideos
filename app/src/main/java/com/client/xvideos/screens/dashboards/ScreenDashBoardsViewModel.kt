package com.client.xvideos.screens.dashboards

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

class ScreenDashBoardsScreenModel @Inject constructor(

): ScreenModel{

    val pagerState: PagerState = PagerState(1){20000}

    fun openItem(url : String,  navigator: Navigator){
        navigator.push(ScreenItem(url))
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleDashBoards {

    @Binds
    @IntoMap
    @ScreenModelKey(ScreenDashBoardsScreenModel::class)
    abstract fun bindScreenDashBoardsScreenModel(hiltListScreenModel: ScreenDashBoardsScreenModel): ScreenModel

}