package com.client.xvideos.red.common.block.useCase

import com.client.xvideos.AppPath
import timber.log.Timber
import java.io.File

fun blockGetGifsByUserNameAsListString(userName: String): List<String> {
    val blockDir = File(AppPath.block_red, userName)

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
