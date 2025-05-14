package com.client.xvideos.screens.dashboards

import androidx.compose.foundation.pager.PagerState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.navigator.Navigator
import com.client.xvideos.feature.preference.PreferencesRepository
import com.client.xvideos.screens.videoplayer.ScreenVideoPlayer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class ScreenDashBoardsScreenModel @Inject constructor(
    private val pref: PreferencesRepository,
) : ScreenModel {

    /** Количество колонок true-2 false-1 */
    val countRow = pref.flowRow2
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), false)

    val pagerState: PagerState = PagerState(0) { 20000 }

    fun openVideoPlayer(url: String, navigator: Navigator) {
        navigator.push(ScreenVideoPlayer(url))
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