package com.client.redgifs.common.saved.collection

import com.client.xvideos.AppPath
import timber.log.Timber
import java.io.File
import java.io.IOException

fun collectionItemDeleteFromDisk(itemId: String, collectionName: String): Result<Boolean> {
    return try {
        Timber.i("!!! удалить лайк GIFS -> deleteItem() id:$itemId из коллекции:$collectionName")

        // Папка с коллекцией
        val dir = File(AppPath.collection_red, collectionName)

        // Файл-блокировка, созданный при сохранении
        val likesFile = File(dir, "$itemId.collection")

        if (likesFile.exists()) {
            // Пытаемся удалить
            if (!likesFile.delete()) {
                return Result.failure(IOException("Не удалось удалить файл: ${likesFile.absolutePath}"))
            }
        }

        /*  ──────────────────────────────────────────────────────────────
            При желании можно убрать пустую директорию коллекции:
            if (dir.isDirectory && dir.list()?.isEmpty() == true) dir.delete()
           ────────────────────────────────────────────────────────────── */

        Result.success(true)
    } catch (e: Exception) {
        Timber.e(e, "Ошибка при удалении лайка GIF")
        Result.failure(e)
    }
}