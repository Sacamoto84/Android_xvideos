package com.client.xvideos.screens_red.use_case.block

import com.client.xvideos.AppPath
import timber.log.Timber
import java.io.File

/**
 * Сканирует все директории пользователей в `AppPath.cache_download_red` и собирает
 * ID всех заблокированных GIF-файлов, используя `useCaseGetBlockedGifIds`.
 *
 * @return Список всех ID заблокированных GIF-файлов.
 */
fun useCaseGetAllBlockedGifs(): List<String> {

    val rootDir = File(AppPath.cache_download_red)

    if (!rootDir.exists() || !rootDir.isDirectory) {
        Timber.w("Директория кэша не найдена: ${rootDir.absolutePath}")
        return emptyList()
    }

    return rootDir.listFiles { file -> file.isDirectory }
        ?.flatMap { userDir -> useCaseGetBlockedGifIds(userDir.name) }
        ?: emptyList()
}
