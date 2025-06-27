package com.client.xvideos.red.common.saved.collection

import com.client.xvideos.AppPath
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.google.gson.Gson
import timber.log.Timber
import java.io.File
import java.io.IOException

fun collectionCreateToDisk(collectionName: String): Result<Boolean> {
    return try {
        Timber.i("!!! Создать коллекцию  collectionCreateToDisk() collectionName:$collectionName")

        // Создаем директорию <userName>/block, если её нет
        val dir = File(AppPath.collection_red+"/"+collectionName)

        if (!dir.exists()) {
            val created = dir.mkdirs()
            if (!created) { return Result.failure(IOException("Не удалось создать директорию: ${dir.absolutePath}")) }
        }

        Result.success(true)
    } catch (e: Exception) {
        Timber.e(e, "Ошибка при создании коллекции $collectionName")
        Result.failure(e)
    }
}

