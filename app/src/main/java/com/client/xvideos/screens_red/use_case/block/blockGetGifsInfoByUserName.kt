package com.client.xvideos.screens_red.use_case.block

import com.client.xvideos.AppPath
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.google.gson.Gson
import timber.log.Timber
import java.io.File

/**
 * Возвращает список объектов GifsInfo, восстановленных из файлов .block
 * в директории `<cache_download_red>/<userName>/block`.
 *
 * @param userName Имя пользователя, чьи GIF-элементы проверяются на блокировку.
 * @return Список объектов GifsInfo, восстановленных из JSON в .block файлах.
 */
fun blockGetGifsInfoByUserName(userName: String = "lilijunex"): List<GifsInfo> {
    val blockDir = File(AppPath.cache_download_red, "$userName/block")

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

/**
 * Возвращает список ID заблокированных GIF-файлов (имена файлов с расширением .block)
 * из директории `<cache_download_red>/<userName>/block`.
 *
 * @param userName Имя пользователя, чьи GIF-элементы проверяются на блокировку.
 * @return Список строк с ID заблокированных GIF (без расширения .block).
 */
fun blockGetGifsByUserNameAsListString(userName: String): List<String> {
    val blockDir = File(AppPath.cache_download_red, "$userName/block")

    if (!blockDir.exists() || !blockDir.isDirectory) {
        Timber.w("Директория блокировок не найдена: ${blockDir.absolutePath}")
        return emptyList()
    }

    return blockDir.listFiles { file ->
        file.isFile && file.name.endsWith(".block")
    }?.map { file ->
        file.name.removeSuffix(".block")
    } ?: emptyList()
}