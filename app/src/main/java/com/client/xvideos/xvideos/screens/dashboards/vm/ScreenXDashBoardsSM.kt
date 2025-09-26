package com.client.xvideos.xvideos.screens.dashboards.vm

import androidx.compose.foundation.pager.PagerState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.navigator.Navigator
import com.client.xvideos.common.sharedPref.Settings
import com.client.xvideos.xvideos.model.ItemsX
import com.client.xvideos.xvideos.feature.saved.SavedX
import com.client.xvideos.xvideos.screens.videoplayer.ScreenVideoPlayer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScreenXDashBoardsScreenModel @Inject constructor(
    val saved : SavedX
) : ScreenModel {

    /** Количество колонок true-2 false-1 */
    val countRow = Settings.xvideos_row2

    val pagerState: PagerState = PagerState(0) { 20000 }

    fun openVideoPlayer(url: String, navigator: Navigator) {
        navigator.push(ScreenVideoPlayer(url))
    }

    fun addFavorite(cell: ItemsX) = screenModelScope.launch {

        val item = ItemsX(
            id = cell.id,
            title = cell.title,
            duration = cell.duration,
            views = cell.views,
            channel = cell.channel,
            previewImage = cell.previewImage,
            previewVideo = cell.previewVideo,
            href = cell.href,
            nameProfile = cell.nameProfile,
            linkProfile = cell.linkProfile,
        )

        saved.favorites.add(item)

    }

    fun removeFavorite(cell: ItemsX) = screenModelScope.launch {

        val item = ItemsX(
            id = cell.id,
            title = cell.title,
            duration = cell.duration,
            views = cell.views,
            channel = cell.channel,
            previewImage = cell.previewImage,
            previewVideo = cell.previewVideo,
            href = cell.href,
            nameProfile = cell.nameProfile,
            linkProfile = cell.linkProfile,
        )
        saved.favorites.remove(item)

    }

    fun isFavorite(id : Long): Boolean{
        return saved.favorites.list.find { it.id == id } != null
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleDashBoards {

    @Binds
    @IntoMap
    @ScreenModelKey(ScreenXDashBoardsScreenModel::class)
    abstract fun bindScreenDashBoardsScreenModel(hiltListScreenModel: ScreenXDashBoardsScreenModel): ScreenModel

}