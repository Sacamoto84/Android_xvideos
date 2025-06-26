package com.client.xvideos.red.common.saved.collection

import com.client.xvideos.AppPath
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.red.common.saved.collection.model.CollectionEntity
import com.google.gson.Gson
import timber.log.Timber
import java.io.File
import java.io.IOException

fun readAllCollections(): Result<List<CollectionEntity>> =
    runCatching {
        val root = File(AppPath.collection_red)
        if (!root.exists()) throw IOException("Каталог коллекций не найден: ${root.absolutePath}")
        // ➊ берём все подпапки (это и есть «коллекции»)
        root.listFiles { f -> f.isDirectory }?.map { dir ->
            // ➋ список *.collection-файлов внутри папки
            val items = dir.listFiles { f ->
                f.isFile && f.extension == "collection"
            }?.mapNotNull { file ->
                // каждая запись — JSON GifsInfo
                try {
                    Gson().fromJson(file.readText(Charsets.UTF_8),   GifsInfo::class.java)
                } catch (ex: Exception) {
                    Timber.w(ex, "Файл пропущен: ${file.name}")
                    null            // пропускаем битые JSON-ы
                }
            } ?: emptyList()

            CollectionEntity(dir.name, items)

        }?.sortedBy { it.collection } ?: emptyList()
    }
