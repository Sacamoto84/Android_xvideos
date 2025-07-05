package com.client.xvideos.redgifs.common.saved.creators

import com.client.xvideos.AppPath
import com.client.xvideos.redgifs.network.types.UserInfo
import com.google.gson.Gson
import timber.log.Timber
import java.io.File

fun getAllCreatorsFromDisk(): List<UserInfo>  {
    val dir = File(AppPath.creators_red)

    // Если директории нет или это не каталог — возвращаем пустой список
    if (!dir.exists() || !dir.isDirectory) {
        Timber.w("!!! Каталог creators_red не найден: ${dir.absolutePath}")
        return emptyList()
    }

    val gson = Gson()

    // listFiles может вернуть null, поэтому используем безопасный вызов
    return dir.listFiles { file -> file.extension == "creator" }
        ?.mapNotNull { file ->
            runCatching {
                val json = file.readText(Charsets.UTF_8)
                gson.fromJson(json, UserInfo::class.java)
            }.onFailure { e ->
                Timber.e(e, "!!! Не удалось разобрать файл creator: ${file.name}")
            }.getOrNull()
        }
        ?: emptyList()
}