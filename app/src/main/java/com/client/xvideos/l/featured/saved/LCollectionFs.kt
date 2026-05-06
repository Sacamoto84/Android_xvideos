package com.client.xvideos.l.featured.saved

import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.model.isLVideoFileUrl
import java.io.File

/**
 * Список коллекций L: имя, превью (если найдено) и количество элементов.
 */
data class LCollectionEntity(
    val collection: String,
    val previewUrl: String?,
    val itemsCount: Int
)

/**
 * Читает список коллекций из корня [collectionsRoot]. Каждая коллекция — это
 * директория первого уровня. Превью берётся из метаданных первого подходящего
 * элемента, размер — это число элементов с валидным `metadata.json`.
 */
internal fun lReadCollections(collectionsRoot: File): List<LCollectionEntity> {
    collectionsRoot.mkdirs()
    return collectionsRoot.listFiles()
        ?.filter { it.isDirectory }
        ?.sortedBy { it.name.lowercase() }
        ?.map { folder ->
            LCollectionEntity(
                collection = folder.name,
                previewUrl = lResolveCollectionPreviewUrl(folder),
                itemsCount = lResolveCollectionItemsCount(folder)
            )
        }
        ?: emptyList()
}

/**
 * Читает все элементы одной коллекции в виде [PicsDetails], отсортированных по
 * убыванию даты сохранения.
 */
internal fun lReadCollectionItems(collectionFolder: File): List<PicsDetails> {
    collectionFolder.mkdirs()
    return collectionFolder.listFiles()
        ?.filter { it.isDirectory }
        ?.mapNotNull { folder ->
            val metadata = readCollectionMetadata(File(folder, L_METADATA_FILE_NAME))
            if (metadata != null) metadata to folder else null
        }
        ?.sortedByDescending { it.first.savedAt }
        ?.mapNotNull { (metadata, folder) -> metadata.toPicsDetails(folder) }
        ?: emptyList()
}

/**
 * Находит локальный путь к превью первой коллекции. Возвращает локальный путь
 * (не URL), либо `null`, если ни в одном элементе нет валидного изображения.
 */
private fun lResolveCollectionPreviewUrl(collectionFolder: File): String? {
    val itemFolders = collectionFolder.listFiles()
        ?.filter { it.isDirectory }
        ?.sortedByDescending { it.lastModified() }
        ?: return null

    for (folder in itemFolders) {
        val metadata = readCollectionMetadata(File(folder, L_METADATA_FILE_NAME))
        if (metadata != null) {
            metadata.previewFiles
                ?.sortedByDescending { it.width * it.height }
                ?.forEach { preview ->
                    val candidate = File(folder, preview.fileName)
                    if (candidate.exists() && !candidate.absolutePath.isLVideoFileUrl()) {
                        return candidate.absolutePath
                    }
                }

            metadata.previewFileName?.let { previewFileName ->
                val candidate = File(folder, previewFileName)
                if (candidate.exists() && !candidate.absolutePath.isLVideoFileUrl()) {
                    return candidate.absolutePath
                }
            }

            val mediaFile = File(folder, metadata.mediaFileName)
            if (mediaFile.exists() && !mediaFile.absolutePath.isLVideoFileUrl()) {
                return mediaFile.absolutePath
            }
        }

        val fallback = folder.listFiles()
            ?.firstOrNull { it.isFile && it.name != L_METADATA_FILE_NAME && !it.absolutePath.isLVideoFileUrl() }
        if (fallback != null) {
            return fallback.absolutePath
        }
    }
    return null
}

private fun lResolveCollectionItemsCount(collectionFolder: File): Int {
    return collectionFolder.listFiles()
        ?.count { it.isDirectory && File(it, L_METADATA_FILE_NAME).exists() }
        ?: 0
}

/**
 * Ищет папку элемента коллекции по списку идентификаторов. Идентификатором
 * может быть локальный путь или один из исходных URL'ов. Сначала проверяется
 * прямое попадание идентификатора в [root] (быстрый путь), затем —
 * содержимое `metadata.json` каждой папки (медленный путь).
 */
internal fun lFindCollectionItemFolder(root: File, identifiers: List<String>): File? {
    val normalizedIdentifiers = identifiers
        .filter { it.isNotBlank() }
        .flatMap { listOf(it, it.lToFilePath()) }
        .toSet()

    normalizedIdentifiers.forEach { identifier ->
        val target = File(identifier)
        if (lIsInside(root, target)) {
            val parent = target.parentFile
            if (parent != null && File(parent, L_METADATA_FILE_NAME).exists()) return parent
        }
    }

    return root.listFiles()
        ?.filter { it.isDirectory }
        ?.firstOrNull { folder ->
            val metadata = readCollectionMetadata(File(folder, L_METADATA_FILE_NAME))
                ?: return@firstOrNull false
            val metadataIdentifiers = buildSet {
                add(File(folder, metadata.mediaFileName).absolutePath)
                metadata.previewFileName?.let { add(File(folder, it).absolutePath) }
                metadata.previewFiles?.forEach {
                    add(File(folder, it.fileName).absolutePath)
                    add(it.sourceUrl)
                }
                add(metadata.sourceMediaUrl)
                metadata.sourcePreviewUrl?.let { add(it) }
                metadata.sourceOriginalUrl?.let { add(it) }
                metadata.sourceVideoUrl?.let { add(it) }
                metadata.picture.url_to_original?.let { add(it) }
                metadata.picture.url_to_video?.let { add(it) }
                metadata.picture.thumbnails?.forEach { thumbnail ->
                    thumbnail.url?.let { add(it) }
                }
            }.flatMap { listOf(it, it.lToFilePath()) }.toSet()

            normalizedIdentifiers.any { it in metadataIdentifiers }
        }
}

/**
 * Ищет папку лайка по любому из его идентификаторов
 * (локальный путь к media/preview либо один из исходных URL).
 */
internal fun lFindLikeFolder(root: File, url: String): File? {
    val target = File(url)
    if (lIsInside(root, target)) {
        val parent = target.parentFile
        if (parent != null && File(parent, L_METADATA_FILE_NAME).exists()) return parent
    }

    return root.listFiles()
        ?.filter { it.isDirectory }
        ?.firstOrNull { folder ->
            val metadata = readLSavedLikeMetadata(File(folder, L_METADATA_FILE_NAME))
                ?: return@firstOrNull false
            val mediaPath = File(folder, metadata.mediaFileName).absolutePath
            val previewPath = metadata.previewFileName?.let { File(folder, it).absolutePath }
            val previewPaths = metadata.previewFiles
                ?.map { File(folder, it.fileName).absolutePath }
                ?: emptyList()
            url == mediaPath ||
                    url == previewPath ||
                    url in previewPaths ||
                    url == metadata.sourceMediaUrl ||
                    url == metadata.sourceOriginalUrl ||
                    url == metadata.sourceVideoUrl
        }
}
