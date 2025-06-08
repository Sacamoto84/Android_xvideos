package com.client.xvideos.screens_red.common.favorite.useCase

import com.client.xvideos.AppPath
import java.io.File

fun getAllFavoriteFileNames(): List<String> {
    val rootDir = File(AppPath.favorite_red)

    if (!rootDir.exists() || !rootDir.isDirectory) return emptyList()

    return rootDir.walkTopDown()
        .filter { it.isFile && it.name.endsWith(".favorite") }
        .map { it.name }
        .toList()
}