package com.client.xvideos.screens.favorites

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.client.xvideos.room.AppDatabase
import com.client.xvideos.room.entity.FavoriteGalleryItem
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


class ScreenFavoritesSM @Inject constructor(
    private val db: AppDatabase,
) : ScreenModel {

    val favorites: Flow<List<FavoriteGalleryItem>> = db.favoriteDao().getAll()

    fun addFavorite(item: FavoriteGalleryItem) = screenModelScope.launch {
        db.favoriteDao().insert(item)
    }

    fun removeFavorite(item: FavoriteGalleryItem) = screenModelScope.launch {
        db.favoriteDao().delete(item)
    }

}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleFavorites {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenFavoritesSM::class)
    abstract fun bindScreenFavoritesScreenModel(hiltListScreenModel: ScreenFavoritesSM): ScreenModel
}