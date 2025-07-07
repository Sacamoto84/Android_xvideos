package com.client.redgifs.common

import com.client.xvideos.redgifs.network.types.GifsInfo
import com.google.gson.Gson
import timber.log.Timber
import java.io.File


// Получить список всех объектов GifsInfo из директории path, для папок со складированием всего в path/*. filter
fun getAllGifsInfoFronPath(path: String, filter : String = ".block"): List<GifsInfo> {

    val dir = File(path)

    if (!dir.exists() || !dir.isDirectory) {
        Timber.w("Директория getAllGifsInfoFronPath не найдена: ${dir.absolutePath}")
        return emptyList()
    }

    val gson = Gson()
    val blockedGifs = mutableListOf<GifsInfo>()

    dir.listFiles { file ->
        file.isFile && file.name.endsWith(filter)
    }?.forEach { file ->
        try {
            val json = file.readText(Charsets.UTF_8)
            val gifInfo = gson.fromJson(json, GifsInfo::class.java)
            blockedGifs.add(gifInfo)
        } catch (e: Exception) {
            Timber.e(e, "Ошибка чтения файла: ${file.name}")
        }
    }

    return blockedGifs
}