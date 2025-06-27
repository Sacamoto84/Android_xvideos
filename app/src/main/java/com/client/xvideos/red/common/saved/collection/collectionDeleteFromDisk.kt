package com.client.xvideos.red.common.saved.collection

import com.client.xvideos.AppPath
import timber.log.Timber
import java.io.File
import java.io.IOException

fun collectionDeleteFromDisk(collectionName: String): Result<Boolean> =
    runCatching {
        val dir = File(AppPath.collection_red, collectionName)

        if (!dir.exists()) {
            Timber.w("Коллекция \"$collectionName\" не найдена: ${dir.absolutePath}")
            return Result.success(false)      // ничего не удаляли
        }

        val deleted = dir.deleteRecursively()
        if (!deleted) {
            throw IOException("Не удалось удалить коллекцию: ${dir.absolutePath}")
        }

        Timber.i("Удалена коллекция: $collectionName")
        Result.success(true)
    }.getOrElse { e ->
        Timber.e(e, "Ошибка при удалении коллекции $collectionName")
        Result.failure(e)
    }


