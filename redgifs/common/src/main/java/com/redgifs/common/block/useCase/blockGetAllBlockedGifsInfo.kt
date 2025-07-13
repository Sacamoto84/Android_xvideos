package com.redgifs.common.block.useCase

import com.client.common.AppPath
import com.redgifs.model.GifsInfo
import timber.log.Timber
import java.io.File

/**
 * Сканирует все директории пользователей в `AppPath.block_red` и собирает
 * все объекты GifsInfo, восстановленные из .block файлов.
 *
 * @return Список всех GifsInfo, считанных из .block файлов во всех пользовательских директориях.
 */
fun blockGetAllBlockedGifsInfo(): List<GifsInfo> {
    val rootDir = File(AppPath.block_red)

    if (!rootDir.exists() || !rootDir.isDirectory) {
        Timber.w("Директория кэша не найдена: ${rootDir.absolutePath}")
        return emptyList()
    }

    return rootDir.listFiles { file -> file.isDirectory }
        ?.flatMap { userDir ->
            blockGetGifsInfoByUserName(userDir.name)
        } ?: emptyList()
}
