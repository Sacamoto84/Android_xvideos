package com.client.xvideos.screens.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.client.xvideos.feature.room.AppDatabase
import com.client.xvideos.feature.room.entity.FavoriteWithItem
import com.client.xvideos.feature.room.entity.Items
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


class ScreenProfileSM @Inject constructor(
    private val db: AppDatabase,
) : ScreenModel {

    val favorites: Flow<List<FavoriteWithItem>> = db.favoriteDao().getAllFavoritesWithItemsOrderDateDesc()

    fun addFavorite(item: Items) = screenModelScope.launch {
        //db.favoriteDao().insert(item)
    }

    fun removeFavorite(item: Items) = screenModelScope.launch {
        //db.favoriteDao().delete(item)
    }

}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleProfile {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenProfileSM::class)
    abstract fun bindScreenProfileScreenModel(hiltListScreenModel: ScreenProfileSM): ScreenModel
}