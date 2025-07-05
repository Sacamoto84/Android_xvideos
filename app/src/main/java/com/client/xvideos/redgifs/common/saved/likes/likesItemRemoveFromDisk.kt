package com.client.xvideos.redgifs.common.saved.likes

import com.client.xvideos.AppPath
import com.client.xvideos.redgifs.network.types.GifsInfo
import timber.log.Timber
import java.io.File
import java.io.IOException

fun likesItemRemoveFromDisk(item: GifsInfo): Result<Boolean> {

    return try {
        Timber.i("!!! удалить лайк GIFS -> removeLike() id:${item.id} userName:${item.userName}")

        val likesDir = File(AppPath.likes_red, item.userName)
        val likesFile = File(likesDir, "${item.id}.likes")

        // 1. Удаляем сам файл лайка, если существует
        if (likesFile.exists()) {
            if (!likesFile.delete()) {
                return Result.failure(IOException("Не удалось удалить файл: ${likesFile.absolutePath}"))
            }
        }

        // 2. Проверяем, остались ли ещё файлы в каталоге
        if (likesDir.exists() && likesDir.list()?.isEmpty() == true) {
            if (!likesDir.delete()) {
                return Result.failure(IOException("Не удалось удалить пустую директорию: ${likesDir.absolutePath}"))
            }
        }

        Result.success(true)
    } catch (e: Exception) {
        Timber.e(e, "Ошибка при удалении лайка GIF")
        Result.failure(e)
    }



}