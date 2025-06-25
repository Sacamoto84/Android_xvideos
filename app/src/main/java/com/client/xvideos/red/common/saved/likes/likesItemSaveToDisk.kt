package com.client.xvideos.red.common.saved.likes

import com.client.xvideos.AppPath
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.google.gson.Gson
import timber.log.Timber
import java.io.File
import java.io.IOException

fun likesItemSaveToDisk(item: GifsInfo): Result<Boolean> {
    return try {
        Timber.i("!!! Сохранить лайк GIFS -> likesItemSaveToDisk() id:${item.id} userName:${item.userName} url:${item.urls.hd}")

        // Создаем директорию <userName>/block, если её нет
        val likesDir = File(AppPath.likes_red, item.userName)

        if (!likesDir.exists()) {
            val created = likesDir.mkdirs()
            if (!created) { return Result.failure(IOException("Не удалось создать директорию: ${likesDir.absolutePath}")) }
        }

        // Создаем файл-блокировку
        val likesFile = File(likesDir, "${item.id}.likes")

        // Сохраняем URL как JSON в файл
        val gson = Gson()
        val json = gson.toJson(item)
        likesFile.writeText(json, Charsets.UTF_8)
        Result.success(true)
    } catch (e: Exception) {
        Timber.e(e, "Ошибка при сохранении лайка GIF")
        Result.failure(e)
    }
}