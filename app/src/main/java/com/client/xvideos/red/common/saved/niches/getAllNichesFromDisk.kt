package com.client.xvideos.red.common.saved.niches

import com.client.xvideos.AppPath
import com.client.xvideos.feature.redgifs.types.NichesInfo
import com.google.gson.Gson
import timber.log.Timber
import java.io.File

fun getAllNichesFromDisk(): List<NichesInfo> {
    val dir = File(AppPath.niches_red)

    // Если директории нет или это не каталог — возвращаем пустой список
    if (!dir.exists() || !dir.isDirectory) {
        Timber.w("!!! Каталог niches_red не найден: ${dir.absolutePath}")
        return emptyList()
    }

    val gson = Gson()

    // listFiles может вернуть null, поэтому используем безопасный вызов
    return dir.listFiles { file -> file.extension == "niches" }
        ?.mapNotNull { file ->
            runCatching {
                val json = file.readText(Charsets.UTF_8)
                gson.fromJson(json, NichesInfo::class.java)
            }.onFailure { e ->
                Timber.e(e, "!!! Не удалось разобрать файл niches: ${file.name}")
            }.getOrNull()
        }
        ?: emptyList()
}