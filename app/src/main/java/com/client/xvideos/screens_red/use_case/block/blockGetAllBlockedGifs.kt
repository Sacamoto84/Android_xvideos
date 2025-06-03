package com.client.xvideos.screens_red.use_case.block

import com.client.xvideos.AppPath
import com.client.xvideos.feature.redgifs.types.GifsInfo
import timber.log.Timber
import java.io.File

/**
 * Сканирует все директории пользователей в `AppPath.cache_download_red` и собирает
 * ID всех заблокированных GIF-файлов, используя `useCaseGetBlockedGifIds`.
 *
 * @return Список всех ID заблокированных GIF-файлов.
 */
fun blockGetAllBlockedGifs(): List<String> {

    val rootDir = File(AppPath.cache_download_red)

    if (!rootDir.exists() || !rootDir.isDirectory) {
        Timber.w("Директория кэша не найдена: ${rootDir.absolutePath}")
        return emptyList()
    }

    return rootDir.listFiles { file -> file.isDirectory }
        ?.flatMap { userDir -> blockGetGifsByUserNameAsListString(userDir.name) }
        ?: emptyList()
}

/**
 * Сканирует все директории пользователей в `AppPath.cache_download_red` и собирает
 * все объекты GifsInfo, восстановленные из .block файлов.
 *
 * @return Список всех GifsInfo, считанных из .block файлов во всех пользовательских директориях.
 */
fun blockGetAllBlockedGifsInfo(): List<GifsInfo> {
    val rootDir = File(AppPath.cache_download_red)

    if (!rootDir.exists() || !rootDir.isDirectory) {
        Timber.w("Директория кэша не найдена: ${rootDir.absolutePath}")
        return emptyList()
    }

    return rootDir.listFiles { file -> file.isDirectory }
        ?.flatMap { userDir ->
            blockGetGifsInfoByUserName(userDir.name)
        } ?: emptyList()
}
