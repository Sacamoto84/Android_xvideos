package com.client.xvideos.red.common.block.useCase

import com.client.xvideos.AppPath
import timber.log.Timber
import java.io.File

/**
 * Сканирует все директории пользователей в `AppPath.block_red` и собирает
 * ID всех заблокированных GIF-файлов, используя `useCaseGetBlockedGifIds`.
 *
 * @return Список всех ID заблокированных GIF-файлов.
 */
fun blockGetAllBlockedGifs(): List<String> {

    val rootDir = File(AppPath.block_red)

    if (!rootDir.exists() || !rootDir.isDirectory) {
        Timber.w("Директория кэша не найдена: ${rootDir.absolutePath}")
        return emptyList()
    }

    return rootDir.listFiles { file -> file.isDirectory }
        ?.flatMap { userDir -> blockGetGifsByUserNameAsListString(userDir.name) }
        ?: emptyList()
}
