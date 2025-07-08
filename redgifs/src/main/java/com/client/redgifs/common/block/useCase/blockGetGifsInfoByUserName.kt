package com.client.redgifs.common.block.useCase

import com.client.common.AppPath
import com.client.redgifs.network.types.GifsInfo
import com.google.gson.Gson
import timber.log.Timber
import java.io.File

fun blockGetGifsInfoByUserName(userName: String = "lilijunex"): List<GifsInfo> {
    val blockDir = File(AppPath.block_red, userName)

    if (!blockDir.exists() || !blockDir.isDirectory) {
        Timber.w("Директория блокировок не найдена: ${blockDir.absolutePath}")
        return emptyList()
    }

    val gson = Gson()
    val blockedGifs = mutableListOf<GifsInfo>()

    blockDir.listFiles { file ->
        file.isFile && file.name.endsWith(".block")
    }?.forEach { file ->
        try {
            val json = file.readText(Charsets.UTF_8)
            val gifInfo = gson.fromJson(json, GifsInfo::class.java)
            blockedGifs.add(gifInfo)
        } catch (e: Exception) {
            Timber.e(e, "Ошибка чтения файла блокировки: ${file.name}")
        }
    }

    return blockedGifs
}
