package com.client.xvideos.redgifs.common.saved.creators

import com.client.xvideos.AppPath
import timber.log.Timber
import java.io.File
import java.io.IOException

fun creatorsItemRemoveFromDisk(id: String): Result<Boolean> {
    return try {
        Timber.i("!!! creatorsItemRemoveFromDisk() id:$id")

        val dir  = File(AppPath.creators_red)
        val file = File(dir, "$id.creator")

        // Если файла нет — считаем, что цель достигнута
        if (!file.exists()) {
            return Result.success(true)
        }

        // Пытаемся удалить файл
        if (!file.delete()) {
            return Result.failure(IOException("Не удалось удалить файл: ${file.absolutePath}"))
        }

        Result.success(true)
    } catch (e: Exception) {
        Timber.e(e, "Ошибка при удалении creator-файла")
        Result.failure(e)
    }
}