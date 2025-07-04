package com.client.xvideos.red.common.saved.likes

import com.client.xvideos.AppPath
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.red.common.getAllGifsInfoFronPath
import timber.log.Timber
import java.io.File

fun getAllLikesFromDisk(): List<GifsInfo> {
    val rootDir = File(AppPath.likes_red)

    if (!rootDir.exists() || !rootDir.isDirectory) {
        Timber.w("Директория Likes не найдена: ${rootDir.absolutePath}")
        return emptyList()
    }

    return rootDir.listFiles { file -> file.isDirectory }
        ?.flatMap { userDir ->
            getAllGifsInfoFronPath(AppPath.likes_red+"/"+userDir.name, ".likes")
        } ?: emptyList()
}