package com.client.xvideos.screens_red.common.favorite.useCase

import com.client.xvideos.AppPath
import java.io.File

fun getFavoriteFileNamesForUser(userName: String): List<String> {
    val userDir = File(AppPath.favorite_red, userName)

    if (!userDir.exists() || !userDir.isDirectory) return emptyList()

    return userDir.listFiles { file ->
        file.isFile && file.name.endsWith(".favorite")
    }?.map { it.nameWithoutExtension } ?: emptyList()
}