package com.client.xvideos.screens_red.common.favorite.useCase

import com.client.xvideos.AppPath
import com.client.xvideos.feature.redgifs.types.GifsInfo
import java.io.File

fun isFavoriteItem(item: GifsInfo): Boolean {
    val favoriteFile = File(File(AppPath.favorite_red, item.userName), "${item.id}.favorite")
    return favoriteFile.exists()
}