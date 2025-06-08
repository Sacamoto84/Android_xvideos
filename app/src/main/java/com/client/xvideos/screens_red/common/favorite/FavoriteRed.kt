package com.client.xvideos.screens_red.common.favorite

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateSetOf
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.screens_red.common.favorite.useCase.getAllFavoriteFileNames
import com.client.xvideos.screens_red.common.favorite.useCase.isFavoriteItem
import com.client.xvideos.screens_red.common.favorite.useCase.setFavoriteItem

object FavoriteRed {

    var favoriteList = mutableStateSetOf<String>()

    init {
        refreshFavoriteList()
    }

    fun refreshFavoriteList() {
        favoriteList.clear()
        favoriteList.addAll(getAllFavoriteFileNames())
    }

    fun setFavorite(item: GifsInfo, favorite: Boolean) {
        val result = setFavoriteItem(item, favorite)
        if (result.isSuccess) {
            if (favorite) {
                favoriteList.add(item.id)
            } else {
                favoriteList.remove(item.id)
            }
        }
    }

    fun isFavorite(item: GifsInfo): Boolean {
        return isFavoriteItem(item)
    }

    fun invertFavorite(item: GifsInfo) {
        setFavorite(item, !isFavorite(item))
    }

}