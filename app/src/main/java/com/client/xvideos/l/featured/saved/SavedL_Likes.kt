package com.client.xvideos.l.featured.saved

import androidx.compose.runtime.mutableStateListOf
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.l.model.AlbumDetails
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.model.Thumbnails
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
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
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
    val percentDownload = MutableStateFlow(DOWNLOAD_HIDDEN)

    private val progressLock = Any()
    private val activeFileProgress = mutableMapOf<Int, Float>()
    private var activeLikeDownloads = 0
    private var totalFiles = 0
    private var finishedFiles = 0
    private var nextFileProgressId = 0

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

    private suspend fun saveLike(item: PicsDetails): Result<Unit> {
        var progressStarted = false
        return runCatching {
            val previewSources = item.previewSources()
            val mediaUrl = item.lDownloadUrl()
                ?: previewSources.maxByOrNull { it.width * it.height }?.url
                ?: error("Missing media url")
            val expectedFileCount = if (item.is_animated) {
                1 + if (previewSources.isNotEmpty()) 1 else 0
            } else {
                1 + previewSources.size
            }
            beginLikeDownload(expectedFileCount)
            progressStarted = true

            val albumId = item.album?.takeIf { it.isNotBlank() && it != "null" }
            val albumDetails = albumId?.toIntOrNull()?.let { fetchAlbumDetails(it) }

            val folderName = buildFolderName(item, mediaUrl)
            val folder = File(AppPath.l_likes, folderName)
            folder.mkdirs()

            val mediaExtension = if (item.is_animated) mediaUrl.videoExtension() else mediaUrl.imageExtension()
            val mediaFile = File(folder, "media.$mediaExtension")
            val savedPreviews = mutableListOf<LSavedLikePreview>()

            val client = createClient()
            try {
                val mediaSaved = if (item.is_animated) {
                    downloadToFileTracked(client, mediaUrl, mediaFile)
                    true
                } else {
                    runCatching { downloadToFileTracked(client, mediaUrl, mediaFile) }
                        .onFailure { error ->
                            mediaFile.delete()
                            Timber.w(error, "!!! L like original media download failed, fallback to previews: $mediaUrl")
                        }
                        .isSuccess
                }

                if (item.is_animated) {
                    previewSources.minByOrNull { it.width * it.height }?.let { preview ->
                        val previewFile = File(folder, "preview.${preview.extension}")
                        runCatching { downloadToFileTracked(client, preview.url, previewFile) }
                            .onSuccess { savedPreviews.add(preview.toSavedPreview(previewFile.name)) }
                            .onFailure { Timber.w(it, "!!! L like video preview download failed: ${preview.url}") }
                    }
                } else {
                    previewSources.forEach { preview ->
                        val previewFile = File(folder, "preview.${preview.sizeMarker}.${preview.extension}")
                        runCatching { downloadToFileTracked(client, preview.url, previewFile) }
                            .onSuccess { savedPreviews.add(preview.toSavedPreview(previewFile.name)) }
                            .onFailure { Timber.w(it, "!!! L like preview download failed: ${preview.url}") }
                    }
                }

                if (!mediaSaved && savedPreviews.isEmpty()) {
                    error("Missing downloaded media and previews")
                }

                val metadata = LSavedLikeMetadata(
                    folderName = folder.name,
                    mediaFileName = mediaFile.name,
                    previewFileName = savedPreviews.minByOrNull { it.width * it.height }?.fileName,
                    previewFiles = savedPreviews,
                    sourceMediaUrl = mediaUrl,
                    sourcePreviewUrl = savedPreviews.minByOrNull { it.width * it.height }?.sourceUrl,
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
        }.also {
            if (progressStarted) finishLikeDownload()
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

    private suspend fun downloadToFileTracked(client: HttpClient, url: String, file: File) {
        val progressId = startFileDownload()
        try {
            downloadToFile(
                client = client,
                url = url,
                file = file,
                onProgress = { downloadedBytes, totalBytes ->
                    updateFileDownload(
                        progressId = progressId,
                        fraction = when {
                            totalBytes != null && totalBytes > 0L -> downloadedBytes.toFloat() / totalBytes.toFloat()
                            downloadedBytes > 0L -> 0.05f
                            else -> 0f
                        }
                    )
                }
            )
        } finally {
            finishFileDownload(progressId)
        }
    }

    private suspend fun downloadToFile(client: HttpClient, url: String, file: File) {
        downloadToFile(client, url, file) { _, _ -> }
    }

    private suspend fun downloadToFile(
        client: HttpClient,
        url: String,
        file: File,
        onProgress: (downloadedBytes: Long, totalBytes: Long?) -> Unit
    ) {
        if (file.exists() && file.length() > 0L) return
        file.parentFile?.mkdirs()

        val tempFile = File(file.parentFile, "${file.name}.part")
        try {
            val response: HttpResponse = client.get(url)
            if (!response.status.isSuccess()) {
                throw IOException("HTTP error: ${response.status.value}")
            }
            val totalBytes = response.headers[HttpHeaders.ContentLength]?.toLongOrNull()
            var downloadedBytes = 0L
            response.bodyAsChannel().toInputStream().use { input ->
                FileOutputStream(tempFile).use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        output.write(buffer, 0, count)
                        downloadedBytes += count
                        onProgress(downloadedBytes, totalBytes)
                    }
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

    private fun beginLikeDownload(fileCount: Int) {
        synchronized(progressLock) {
            activeLikeDownloads += 1
            totalFiles += fileCount.coerceAtLeast(1)
            updateDownloadPercentLocked()
        }
    }

    private fun finishLikeDownload() {
        val shouldHide: Boolean
        synchronized(progressLock) {
            activeLikeDownloads = (activeLikeDownloads - 1).coerceAtLeast(0)
            shouldHide = activeLikeDownloads == 0
            if (shouldHide) {
                activeFileProgress.clear()
                finishedFiles = totalFiles
                percentDownload.value = DOWNLOAD_DONE
                totalFiles = 0
                finishedFiles = 0
            } else {
                updateDownloadPercentLocked()
            }
        }

        if (shouldHide) {
            scope.launch {
                delay(DOWNLOAD_DONE_VISIBLE_MS)
                synchronized(progressLock) {
                    if (activeLikeDownloads == 0) {
                        percentDownload.value = DOWNLOAD_HIDDEN
                    }
                }
            }
        }
    }

    private fun startFileDownload(): Int {
        return synchronized(progressLock) {
            val progressId = nextFileProgressId++
            activeFileProgress[progressId] = 0f
            updateDownloadPercentLocked()
            progressId
        }
    }

    private fun updateFileDownload(progressId: Int, fraction: Float) {
        synchronized(progressLock) {
            if (progressId in activeFileProgress) {
                activeFileProgress[progressId] = fraction.coerceIn(0f, 1f)
                updateDownloadPercentLocked()
            }
        }
    }

    private fun finishFileDownload(progressId: Int) {
        synchronized(progressLock) {
            if (activeFileProgress.remove(progressId) != null) {
                finishedFiles = (finishedFiles + 1).coerceAtMost(totalFiles)
                updateDownloadPercentLocked()
            }
        }
    }

    private fun updateDownloadPercentLocked() {
        if (totalFiles <= 0 || activeLikeDownloads <= 0) {
            percentDownload.value = DOWNLOAD_HIDDEN
            return
        }

        val activeProgress = activeFileProgress.values.sum()
        percentDownload.value = ((finishedFiles + activeProgress) / totalFiles.toFloat()).coerceIn(0f, 1f)
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

    private fun PicsDetails.previewSources(): List<PreviewSource> {
        return thumbnails
            ?.mapNotNull { it.toPreviewSource() }
            ?.distinctBy { it.url.substringBefore('?').substringBefore('#') }
            ?.sortedBy { it.width * it.height }
            ?: emptyList()
    }

    private fun Thumbnails.toPreviewSource(): PreviewSource? {
        val sourceUrl = url?.takeIf { it.isNotBlank() && !it.isLVideoFileUrl() } ?: return null
        return PreviewSource(
            url = sourceUrl,
            width = width,
            height = height,
            size = size,
            sizeMarker = sourceUrl.previewSizeMarker(width, height),
            extension = sourceUrl.imageExtension()
        )
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

    private fun isInside(root: File, file: File): Boolean {
        return runCatching {
            val rootPath = root.canonicalFile.absolutePath
            val filePath = file.canonicalFile.absolutePath
            filePath == rootPath || filePath.startsWith(rootPath + File.separator)
        }.getOrDefault(false)
    }

    private fun String.imageExtension(): String {
        return lUrlExtension()
            .lowercase()
            .takeIf { it in setOf("jpg", "jpeg", "png", "webp", "gif") }
            ?: "jpg"
    }

    private fun String.videoExtension(): String {
        return lUrlExtension()
            .lowercase()
            .takeIf { it in setOf("mp4", "webm", "m4v", "mov") }
            ?: "mp4"
    }

    private fun String.previewSizeMarker(width: Int, height: Int): String {
        return Regex("\\.(\\d+x\\d+)\\.[^.]+$")
            .find(lUrlFileName())
            ?.groupValues
            ?.getOrNull(1)
            ?: "${width}x${height}"
    }

    private fun String.sanitizeFilePart(): String {
        return replace(Regex("[^A-Za-z0-9._-]"), "_").trim('_')
    }

    private fun String.sha256(): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private data class PreviewSource(
        val url: String,
        val width: Int,
        val height: Int,
        val size: String?,
        val sizeMarker: String,
        val extension: String
    ) {
        fun toSavedPreview(fileName: String): LSavedLikePreview {
            return LSavedLikePreview(
                fileName = fileName,
                sourceUrl = url,
                width = width,
                height = height,
                size = size
            )
        }
    }

    private companion object {
        const val METADATA_FILE_NAME = "metadata.json"
        const val DOWNLOAD_HIDDEN = -2f
        const val DOWNLOAD_DONE = 1f
        const val DOWNLOAD_DONE_VISIBLE_MS = 400L
    }
}
