package com.client.xvideos.l.featured.saved

import com.client.xvideos.l.model.AlbumDetails
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.model.Thumbnails
import com.google.gson.GsonBuilder
import timber.log.Timber
import java.io.File

data class LSavedLikeMetadata(
    val schemaVersion: Int = 1,
    val savedAt: Long = System.currentTimeMillis(),
    val site: String = "luscious",
    val folderName: String,
    val mediaFileName: String,
    val previewFileName: String?,
    val sourceMediaUrl: String,
    val sourcePreviewUrl: String?,
    val sourceOriginalUrl: String?,
    val sourceVideoUrl: String?,
    val albumId: String?,
    val albumTitle: String?,
    val albumDescription: String?,
    val albumUrl: String?,
    val albumDownloadUrl: String?,
    val albumDetails: AlbumDetails?,
    val picture: PicsDetails
)

private val lSavedLikeGson = GsonBuilder().setPrettyPrinting().create()

fun readLSavedLikeMetadata(file: File): LSavedLikeMetadata? {
    return try {
        lSavedLikeGson.fromJson(file.readText(Charsets.UTF_8), LSavedLikeMetadata::class.java)
    } catch (e: Exception) {
        Timber.e(e, "!!! read L like metadata error: ${file.absolutePath}")
        null
    }
}

fun writeLSavedLikeMetadata(file: File, metadata: LSavedLikeMetadata) {
    file.parentFile?.mkdirs()
    file.writeText(lSavedLikeGson.toJson(metadata), Charsets.UTF_8)
}

fun LSavedLikeMetadata.toPicsDetails(folder: File): PicsDetails? {
    val mediaFile = File(folder, mediaFileName)
    if (!mediaFile.exists()) return null

    val previewFile = previewFileName
        ?.let { File(folder, it) }
        ?.takeIf { it.exists() }

    val thumbnails = if (previewFile != null) {
        listOf("large_thumbnail", "small", "xMax").map { size ->
            Thumbnails(
                width = picture.width,
                height = picture.height,
                size = size,
                url = previewFile.absolutePath
            )
        }
    } else {
        picture.thumbnails
    }

    return picture.copy(
        url_to_original = mediaFile.absolutePath,
        url_to_video = if (picture.is_animated) mediaFile.absolutePath else null,
        album = albumId ?: picture.album,
        thumbnails = thumbnails
    )
}
