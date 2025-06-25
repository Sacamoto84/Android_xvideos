package com.client.xvideos.red.common.saved.niches

import com.client.xvideos.AppPath
import com.client.xvideos.feature.redgifs.types.NichesInfo
import com.google.gson.Gson
import timber.log.Timber
import java.io.File
import java.io.IOException

fun nickesItemSaveToDisk(nickesInfo: NichesInfo): Result<Boolean>  {
    return try {
        Timber.i("!!! nickesItemSaveToDisk() id:${nickesInfo.id} userName:${nickesInfo.name}")

        // Создаем директорию <userName>/block, если её нет
        val dir = File(AppPath.niches_red)

        if (!dir.exists()) {
            val created = dir.mkdirs()
            if (!created) { return Result.failure(IOException("Не удалось создать директорию: ${dir.absolutePath}")) }
        }

        // Создаем файл-блокировку
        val file = File(dir, "${nickesInfo.id}.niches")

        // Сохраняем URL как JSON в файл
        val gson = Gson()
        val json = gson.toJson(nickesInfo)
        file.writeText(json, Charsets.UTF_8)
        Result.success(true)
    } catch (e: Exception) {
        Timber.e(e, "Ошибка при сохранении лайка GIF")
        Result.failure(e)
    }
}
