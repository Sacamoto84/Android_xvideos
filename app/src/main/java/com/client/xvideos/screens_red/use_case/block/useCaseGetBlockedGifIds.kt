package com.client.xvideos.screens_red.use_case.block

import com.client.xvideos.AppPath
import timber.log.Timber
import java.io.File

/**
 * Возвращает список ID заблокированных GIF-файлов (имена файлов с расширением .block)
 * из директории `<cache_download_red>/<userName>/block`.
 *
 * @param userName Имя пользователя, чьи GIF-элементы проверяются на блокировку.
 * @return Список строк с ID заблокированных GIF (без расширения .block).
 */
fun useCaseGetBlockedGifIds(userName: String): List<String> {
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
