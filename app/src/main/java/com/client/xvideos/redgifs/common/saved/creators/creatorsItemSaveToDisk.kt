package com.client.xvideos.redgifs.common.saved.creators

import com.client.xvideos.AppPath
import com.client.xvideos.redgifs.network.types.UserInfo
import com.google.gson.Gson
import timber.log.Timber
import java.io.File
import java.io.IOException

fun creatorsItemSaveToDisk(item : UserInfo): Result<Boolean>  {
    return try {
        Timber.i("!!! creatorsItemSaveToDisk() id:${item.username}")

        // Создаем директорию <userName>/block, если её нет
        val dir = File(AppPath.creators_red)

        if (!dir.exists()) {
            val created = dir.mkdirs()
            if (!created) { return Result.failure(IOException("Не удалось создать директорию: ${dir.absolutePath}")) }
        }

        // Создаем файл-блокировку
        val file = File(dir, "${item.username}.creator")

        // Сохраняем URL как JSON в файл
        val gson = Gson()
        val json = gson.toJson(item)
        file.writeText(json, Charsets.UTF_8)
        Result.success(true)
    } catch (e: Exception) {
        Timber.e(e, "Ошибка при сохранении креатора на диск")
        Result.failure(e)
    }
}