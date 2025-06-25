package com.client.xvideos.red.common.saved.niches

import com.client.xvideos.AppPath
import timber.log.Timber
import java.io.File
import java.io.IOException

fun nickesItemRemoveFromDisk(id: String): Result<Boolean> {
    return try {
        Timber.i("!!! nickesItemRemoveFromDisk() id:$id")

        val dir  = File(AppPath.niches_red)
        val file = File(dir, "$id.niches")

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
        Timber.e(e, "Ошибка при удалении niches-файла")
        Result.failure(e)
    }
}