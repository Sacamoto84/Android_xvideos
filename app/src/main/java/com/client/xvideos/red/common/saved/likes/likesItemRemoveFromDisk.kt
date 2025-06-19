package com.client.xvideos.red.common.saved.likes

import com.client.xvideos.AppPath
import com.client.xvideos.feature.redgifs.types.GifsInfo
import timber.log.Timber
import java.io.File
import java.io.IOException

fun likesItemRemoveFromDisk(item: GifsInfo): Result<Boolean> {

    return try {
        Timber.i("!!! удалить лайк GIFS -> removeLike() id:${item.id} userName:${item.userName}")

        val likesDir = File(AppPath.likes_red, item.userName)
        val likesFile = File(likesDir, "${item.id}.likes")

        if (likesFile.exists()) {
            val deleted = likesFile.delete()
            if (!deleted) {
                return Result.failure(IOException("Не удалось удалить файл: ${likesFile.absolutePath}"))
            }
        }

        Result.success(true)
    } catch (e: Exception) {
        Timber.e(e, "Ошибка при удалении лайка GIF")
        Result.failure(e)
    }



}