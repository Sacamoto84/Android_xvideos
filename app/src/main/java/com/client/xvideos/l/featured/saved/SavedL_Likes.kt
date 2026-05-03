package com.client.xvideos.l.featured.saved

import androidx.compose.runtime.mutableStateListOf
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.l.model.AlbumDetails
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.model.isLVideoFileUrl
import com.client.xvideos.l.model.lDownloadUrl
import com.client.xvideos.l.model.lMediaRequestHeaders
import com.client.xvideos.l.model.lUrlExtension
import com.client.xvideos.l.model.lUrlFileName
import com.client.xvideos.l.net.Luscious
import com.client.xvideos.l.net.graphQl.getAlbumInfo
import com.client.xvideos.l.repository.RepositoryUriConfig
import com.google.gson.Gson
import com.google.gson.JsonParser
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.isSuccess
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest

class SavedL_Likes(
    private val luscious: Luscious,
    private val scope: CoroutineScope
) {

    val listUrl = mutableStateListOf<PicsDetails>()

    init {
        refresh()
    }

    fun add(item: PicsDetails) {
        println("!!! SavedL_Likes addLikes() item:${item.url_to_original}")

        scope.launch(Dispatchers.IO) {
            val result = saveLike(item)
            withContext(Dispatchers.Main) {
                result
                    .onSuccess {
                        SnackBar.success("Like")
                        refresh()
                    }
                    .onFailure {
                        Timber.e(it, ">>> Download Likes error")
                        SnackBar.error("Ошибка добавления лайка")
                    }
            }
        }
    }

    fun remove(url: String) {
        println("!!! SavedL_Likes removeLikes() url:$url")
        val root = File(AppPath.l_likes)
        val folder = findSavedLikeFolder(root, url)
        val file = File(url)

        val removed = when {
            folder != null -> folder.deleteRecursively()
            isInside(root, file) && file.exists() -> file.delete()
            else -> false
        }

        if (removed) {
            SnackBar.info("Unlike")
        } else {
            SnackBar.error("Файл не найден: $url")
        }
        refresh()
    }

    fun refresh() {
        try {
            println("!!! SavedL_Likes refresh()")
            val root = File(AppPath.l_likes)
            root.mkdirs()

            val metadataItems = root.listFiles()
                ?.filter { it.isDirectory }
                ?.mapNotNull { folder ->
                    val metadata = readLSavedLikeMetadata(File(folder, METADATA_FILE_NAME))
                    if (metadata != null) metadata to folder else null
                }
                ?.sortedByDescending { it.first.savedAt }
                ?.mapNotNull { (metadata, folder) -> metadata.toPicsDetails(folder) }
                ?: emptyList()

            listUrl.clear()
            listUrl.addAll(metadataItems)
            println("!!! SavedL_Likes refresh() files:${listUrl.size}")
        } catch (e: Exception) {
            Timber.e(e, "!!! eee SavedL_Likes refresh() Ошибка получения списка likes")
            SnackBar.error("Ошибка получения списка likes")
        }
    }

    private suspend fun saveLike(item: PicsDetails): Result<Unit> = runCatching {
        val mediaUrl = item.lDownloadUrl() ?: error("Missing media url")
        val albumId = item.album?.takeIf { it.isNotBlank() && it != "null" }
        val albumDetails = albumId?.toIntOrNull()?.let { fetchAlbumDetails(it) }
        val previewUrl = item.selectPreviewUrl()

        val folderName = buildFolderName(item, mediaUrl)
        val folder = File(AppPath.l_likes, folderName)
        folder.mkdirs()

        val mediaExtension = mediaUrl.lUrlExtension().ifBlank { if (item.is_animated) "mp4" else "jpg" }
        val previewExtension = previewUrl?.lUrlExtension()?.ifBlank { "jpg" } ?: "jpg"
        val mediaFile = File(folder, buildLocalFileName(mediaUrl, "media", mediaExtension))
        val previewFile = previewUrl?.let { File(folder, buildLocalFileName(it, "preview", previewExtension)) }

        val client = createClient()
        try {
            downloadToFile(client, mediaUrl, mediaFile)

            var savedPreviewFile: File? = null
            if (previewUrl != null && !sameCleanUrl(previewUrl, mediaUrl)) {
                savedPreviewFile = previewFile
                runCatching { downloadToFile(client, previewUrl, previewFile!!) }
                    .onFailure { Timber.w(it, "!!! L like preview download failed: $previewUrl") }
                    .onFailure { savedPreviewFile = null }
            } else if (!item.is_animated) {
                savedPreviewFile = mediaFile
            }

            val metadata = LSavedLikeMetadata(
                folderName = folder.name,
                mediaFileName = mediaFile.name,
                previewFileName = savedPreviewFile?.name,
                sourceMediaUrl = mediaUrl,
                sourcePreviewUrl = previewUrl,
                sourceOriginalUrl = item.url_to_original,
                sourceVideoUrl = item.url_to_video,
                albumId = albumId,
                albumTitle = albumDetails?.title,
                albumDescription = albumDetails?.description,
                albumUrl = albumDetails?.url?.let { Luscious.HOME + it },
                albumDownloadUrl = albumDetails?.download_url?.let { Luscious.HOME + it },
                albumDetails = albumDetails,
                picture = item
            )
            writeLSavedLikeMetadata(File(folder, METADATA_FILE_NAME), metadata)
        } catch (e: Exception) {
            if (folder.listFiles().isNullOrEmpty() || !File(folder, METADATA_FILE_NAME).exists()) {
                folder.deleteRecursively()
            }
            throw e
        } finally {
            client.close()
        }
    }

    private fun createClient(): HttpClient {
        return HttpClient(OkHttp) {
            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 60_000
            }
            defaultRequest {
                headers {
                    lMediaRequestHeaders().forEach { (key, value) -> append(key, value) }
                }
            }
        }
    }

    private suspend fun downloadToFile(client: HttpClient, url: String, file: File) {
        if (file.exists() && file.length() > 0L) return
        file.parentFile?.mkdirs()

        val tempFile = File(file.parentFile, "${file.name}.part")
        try {
            val response: HttpResponse = client.get(url)
            if (!response.status.isSuccess()) {
                throw IOException("HTTP error: ${response.status.value}")
            }
            response.bodyAsChannel().toInputStream().use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output, bufferSize = 8192)
                }
            }
            if (file.exists() && !file.delete()) {
                throw IOException("Cannot replace file: ${file.absolutePath}")
            }
            if (!tempFile.renameTo(file)) {
                tempFile.copyTo(file, overwrite = true)
                tempFile.delete()
            }
        } catch (e: Exception) {
            tempFile.delete()
            throw e
        }
    }

    private suspend fun fetchAlbumDetails(albumId: Int): AlbumDetails? {
        val query = getAlbumInfo(albumId)
        val cached = luscious.repository.openURI(query, config = RepositoryUriConfig.CACHE_ROM)
        val cachedAlbum = cached.getOrNull()?.parseAlbumDetails()
        if (cachedAlbum != null) return cachedAlbum

        if (cached.isSuccess) {
            luscious.repository.deleteCache(query, RepositoryUriConfig.CACHE_ROM)
        }

        return luscious.repository.openURI(query, config = RepositoryUriConfig.DIRECT)
            .getOrNull()
            ?.parseAlbumDetails()
    }

    private fun String.parseAlbumDetails(): AlbumDetails? {
        return runCatching {
            val get = JsonParser.parseString(this).asJsonObject["data"]
                ?.asJsonObject
                ?.get("album")
                ?.asJsonObject
                ?.get("get")
                ?.asJsonObject
                ?: return null
            Gson().fromJson(get, AlbumDetails::class.java)
        }.onFailure {
            Timber.w(it, "!!! L like album metadata parse failed")
        }.getOrNull()
    }

    private fun PicsDetails.selectPreviewUrl(): String? {
        val preferred = listOf("large_thumbnail", "small", "xMax")
        val bySize = preferred.firstNotNullOfOrNull { size ->
            thumbnails
                ?.firstOrNull { it.size == size }
                ?.url
                ?.takeIf { it.isNotBlank() && !it.isLVideoFileUrl() }
        }
        return bySize
            ?: thumbnails
                ?.firstOrNull { !it.url.isNullOrBlank() && !it.url.isLVideoFileUrl() }
                ?.url
            ?: url_to_original?.takeIf { !is_animated && it.isNotBlank() && !it.isLVideoFileUrl() }
    }

    private fun buildFolderName(item: PicsDetails, mediaUrl: String): String {
        val album = item.album?.takeIf { it.isNotBlank() && it != "null" } ?: "no_album"
        val baseName = mediaUrl.lUrlFileName()
            .substringBeforeLast('.', missingDelimiterValue = mediaUrl.lUrlFileName())
            .sanitizeFilePart()
            .take(60)
            .ifBlank { "media" }
        return "${album.sanitizeFilePart()}_${mediaUrl.sha256().take(12)}_$baseName"
    }

    private fun buildLocalFileName(url: String, prefix: String, fallbackExtension: String): String {
        val cleanName = url.lUrlFileName()
            .sanitizeFilePart()
            .take(90)
            .ifBlank { "$prefix.$fallbackExtension" }
        val nameWithExtension = if (cleanName.substringAfterLast('.', "").isBlank()) {
            "$cleanName.$fallbackExtension"
        } else {
            cleanName
        }
        return "${prefix}_$nameWithExtension"
    }

    private fun findSavedLikeFolder(root: File, url: String): File? {
        val target = File(url)
        if (isInside(root, target)) {
            val parent = target.parentFile
            if (parent != null && File(parent, METADATA_FILE_NAME).exists()) return parent
        }

        return root.listFiles()
            ?.filter { it.isDirectory }
            ?.firstOrNull { folder ->
                val metadata = readLSavedLikeMetadata(File(folder, METADATA_FILE_NAME)) ?: return@firstOrNull false
                val mediaPath = File(folder, metadata.mediaFileName).absolutePath
                val previewPath = metadata.previewFileName?.let { File(folder, it).absolutePath }
                url == mediaPath ||
                        url == previewPath ||
                        url == metadata.sourceMediaUrl ||
                        url == metadata.sourceOriginalUrl ||
                        url == metadata.sourceVideoUrl
            }
    }

    private fun isInside(root: File, file: File): Boolean {
        return runCatching {
            val rootPath = root.canonicalFile.absolutePath
            val filePath = file.canonicalFile.absolutePath
            filePath == rootPath || filePath.startsWith(rootPath + File.separator)
        }.getOrDefault(false)
    }

    private fun sameCleanUrl(a: String, b: String): Boolean {
        return a.substringBefore('?').substringBefore('#') == b.substringBefore('?').substringBefore('#')
    }

    private fun String.sanitizeFilePart(): String {
        return replace(Regex("[^A-Za-z0-9._-]"), "_").trim('_')
    }

    private fun String.sha256(): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private companion object {
        const val METADATA_FILE_NAME = "metadata.json"
    }
}
