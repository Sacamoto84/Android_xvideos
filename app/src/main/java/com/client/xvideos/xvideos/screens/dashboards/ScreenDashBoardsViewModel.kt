package com.client.xvideos.xvideos.screens.dashboards

import androidx.compose.foundation.pager.PagerState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.navigator.Navigator
import com.client.common.feature.preference.PreferencesRepository
import com.client.xvideos.feature.room.AppDatabase
import com.client.xvideos.feature.room.entity.FavoriteWithItem
import com.client.xvideos.feature.room.entity.Favorites
import com.client.xvideos.feature.room.entity.Items
import com.client.xvideos.xvideos.model.GalleryItem
import com.client.xvideos.xvideos.screens.videoplayer.ScreenVideoPlayer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScreenDashBoardsScreenModel @Inject constructor(
    private val pref: PreferencesRepository,
   val db : AppDatabase
) : ScreenModel {

    /** Количество колонок true-2 false-1 */
    val countRow = pref.flowRow2
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), false)

    val pagerState: PagerState = PagerState(0) { 20000 }

    fun openVideoPlayer(url: String, navigator: Navigator) {
        navigator.push(ScreenVideoPlayer(url))
    }

    val getAll: Flow<List<FavoriteWithItem>> = db.favoriteDao().getAllFavoritesWithItemsOrderDateDesc().stateIn( screenModelScope, SharingStarted.WhileSubscribed(5000), emptyList() )

    fun addFavorite(cell: GalleryItem) = screenModelScope.launch {

        val item = Items(
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

        val favorite = Favorites(
            id = cell.id,
            itemId = cell.id
        )

        db.itemsDao().insertItem(item)
        db.favoriteDao().insertFavorite(favorite)
    }

    fun removeFavorite(item: Long) = screenModelScope.launch {
        db.favoriteDao().deleteByItemId(item)
    }

    fun isFavorite(id : Long): Boolean{
        return db.favoriteDao().isFavorite(id)
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