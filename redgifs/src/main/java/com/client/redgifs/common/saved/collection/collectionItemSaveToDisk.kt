package com.client.redgifs.common.saved.collection

import com.client.xvideos.AppPath
import com.client.xvideos.redgifs.network.types.GifsInfo
import com.google.gson.Gson
import timber.log.Timber
import java.io.File
import java.io.IOException

fun collectionItemSaveToDisk(item: GifsInfo, collectionName: String): Result<Boolean> {
    return try {
        Timber.i("!!! сохранить лайк GIFS -> likesItem() id:${item.id} userName:${item.userName} url:${item.urls.hd}")

        // Создаем директорию <userName>/block, если её нет
        val dir = File(AppPath.collection_red+"/"+collectionName)

        if (!dir.exists()) {
            val created = dir.mkdirs()
            if (!created) { return Result.failure(IOException("Не удалось создать директорию: ${dir.absolutePath}")) }
        }

        // Создаем файл-блокировку
        val likesFile = File(dir, "${item.id}.collection")

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