package com.client.xvideos.l.featured.saved

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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

data class LCollectionEntity(
    val collection: String,
    val previewUrl: String?
)

class SavedL_Collection(
    private val scope: CoroutineScope,
    private val luscious: Luscious
) {

    val listUrl = mutableStateListOf<PicsDetails>()
    val collectionList = mutableStateListOf<LCollectionEntity>()
    val percentDownload = MutableStateFlow(DOWNLOAD_HIDDEN)

    private val progressLock = Any()
    private val activeFileProgress = mutableMapOf<Int, Float>()
    private var activeCollectionDownloads = 0
    private var totalFiles = 0
    private var finishedFiles = 0
    private var nextFileProgressId = 0

    var currentCollectionName by mutableStateOf<String?>(null)

    //----- Dialogs -----
    var visibleDialog by mutableStateOf(false)
    var visibleDialogCreateNew by mutableStateOf(false)
    var collectionItemGifInfo by mutableStateOf<PicsDetails?>(null)
    //-------------------

    init {
        refreshCollectionList()
    }

    fun refreshCollectionList() {
        try {
            println("!!! SavedL_Collection refreshCollectionList()")
            val collectionRoot = File(AppPath.l_collection)
            collectionRoot.mkdirs()

            val collections = collectionRoot.listFiles()
                ?.filter { it.isDirectory }
                ?.sortedBy { it.name.lowercase() }
                ?.map { folder ->
                    LCollectionEntity(
                        collection = folder.name,
                        previewUrl = resolveCollectionPreviewUrl(folder)
                    )
                }
                ?: emptyList()

            collectionList.clear()
            collectionList.addAll(collections)
            println("!!! SavedL_Collection refreshCollectionList() collections:${collectionList.size}")
        } catch (e: Exception) {
            Timber.e(e, "!!! eee SavedL_Collection refreshCollectionList() РћС€РёР±РєР° РїРѕР»СѓС‡РµРЅРёСЏ СЃРїРёСЃРєР° РєРѕР»Р»РµРєС†РёР№")
            SnackBar.error("РћС€РёР±РєР° РїРѕР»СѓС‡РµРЅРёСЏ СЃРїРёСЃРєР° РєРѕР»Р»РµРєС†РёР№")
        }
    }

    private fun resolveCollectionPreviewUrl(collectionFolder: File): String? {
        val itemFolders = collectionFolder.listFiles()
            ?.filter { it.isDirectory }
            ?.sortedByDescending { it.lastModified() }
            ?: return null

        for (folder in itemFolders) {
            val metadata = readCollectionMetadata(File(folder, METADATA_FILE_NAME))
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
                ?.firstOrNull { it.isFile && it.name != METADATA_FILE_NAME && !it.absolutePath.isLVideoFileUrl() }
            if (fallback != null) {
                return fallback.absolutePath
            }
        }
        return null
    }

    fun createCollection(collectionName: String) {
        println("!!! SavedL_Collection createCollection() collectionName:$collectionName")
        val collectionRoot = File(AppPath.l_collection, collectionName)
        if (collectionRoot.exists()) {
            SnackBar.error("РљРѕР»Р»РµРєС†РёСЏ СѓР¶Рµ СЃСѓС‰РµСЃС‚РІСѓРµС‚")
            return
        }
        collectionRoot.mkdirs()
        SnackBar.success("РљРѕР»Р»РµРєС†РёСЏ $collectionName СЃРѕР·РґР°РЅР°")
        refreshCollectionList()
    }

    fun deleteCollection(collectionName: String) {
        println("!!! SavedL_Collection deleteCollection() collectionName:$collectionName")
        val collectionRoot = File(AppPath.l_collection, collectionName)
        if (collectionRoot.deleteRecursively()) {
            if (currentCollectionName == collectionName) {
                currentCollectionName = null
                listUrl.clear()
            }
            SnackBar.success("РљРѕР»Р»РµРєС†РёСЏ $collectionName СѓРґР°Р»РµРЅР°")
            refreshCollectionList()
        } else {
            SnackBar.error("РћС€РёР±РєР° СѓРґР°Р»РµРЅРёСЏ РєРѕР»Р»РµРєС†РёРё $collectionName")
        }
    }

    fun setCollection(collectionName: String) {
        currentCollectionName = collectionName
        refresh()
    }

    fun add(item: PicsDetails, collectionName: String) {
        println("!!! SavedL_Collection add() item:${item.url_to_original} collection:$collectionName")

        scope.launch(Dispatchers.IO) {
            val result = saveToCollection(item, collectionName)
            withContext(Dispatchers.Main) {
                result
                    .onSuccess {
                        SnackBar.success("Added to collection")
                        refreshCollectionList()
                        if (currentCollectionName == collectionName) {
                            refresh()
                        }
                    }
                    .onFailure {
                        Timber.e(it, ">>> Collection add error")
                        SnackBar.error("РћС€РёР±РєР° РґРѕР±Р°РІР»РµРЅРёСЏ РІ РєРѕР»Р»РµРєС†РёСЋ")
                    }
            }
        }
    }

    fun remove(url: String, collectionName: String) {
        println("!!! SavedL_Collection remove() url:$url collection:$collectionName")
        val collectionRoot = File(AppPath.l_collection, collectionName)
        val folder = findCollectionItemFolder(collectionRoot, url)
        val file = File(url)

        val removed = when {
            folder != null -> folder.deleteRecursively()
            isInside(collectionRoot, file) && file.exists() -> file.delete()
            else -> false
        }

        if (removed) {
            SnackBar.info("Removed from collection")
        } else {
            SnackBar.error("Р¤Р°Р№Р» РЅРµ РЅР°Р№РґРµРЅ: $url")
        }
        if (currentCollectionName == collectionName) {
            refresh()
        }
    }

    fun refresh() {
        val collectionName = currentCollectionName ?: return
        try {
            println("!!! SavedL_Collection refresh() collection:$collectionName")
            val collectionRoot = File(AppPath.l_collection, collectionName)
            collectionRoot.mkdirs()

            val metadataItems = collectionRoot.listFiles()
                ?.filter { it.isDirectory }
                ?.mapNotNull { folder ->
                    val metadata = readCollectionMetadata(File(folder, METADATA_FILE_NAME))
                    if (metadata != null) metadata to folder else null
                }
                ?.sortedByDescending { it.first.savedAt }
                ?.mapNotNull { (metadata, folder) -> metadata.toPicsDetails(folder) }
                ?: emptyList()

            listUrl.clear()
            listUrl.addAll(metadataItems)
            println("!!! SavedL_Collection refresh() files:${listUrl.size}")
        } catch (e: Exception) {
            Timber.e(e, "!!! eee SavedL_Collection refresh() РћС€РёР±РєР° РїРѕР»СѓС‡РµРЅРёСЏ СЃРїРёСЃРєР° РєРѕР»Р»РµРєС†РёРё")
            SnackBar.error("РћС€РёР±РєР° РїРѕР»СѓС‡РµРЅРёСЏ СЃРїРёСЃРєР° РєРѕР»Р»РµРєС†РёРё")
        }
    }

    private suspend fun saveToCollection(item: PicsDetails, collectionName: String): Result<Unit> {
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
            beginCollectionDownload(expectedFileCount)
            progressStarted = true

            val albumId = item.album?.takeIf { it.isNotBlank() && it != "null" }
            val albumDetails = albumId?.toIntOrNull()?.let { fetchAlbumDetails(it) }

            val folderName = buildFolderName(item, mediaUrl)
            val collectionRoot = File(AppPath.l_collection, collectionName)
            collectionRoot.mkdirs()
            val folder = File(collectionRoot, folderName)
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
                            Timber.w(error, "!!! Collection item original media download failed, fallback to previews: $mediaUrl")
                        }
                        .isSuccess
                }

                if (item.is_animated) {
                    previewSources.minByOrNull { it.width * it.height }?.let { preview ->
                        val previewFile = File(folder, "preview.${preview.extension}")
                        runCatching { downloadToFileTracked(client, preview.url, previewFile) }
                            .onSuccess { savedPreviews.add(preview.toSavedPreview(previewFile.name)) }
                            .onFailure { Timber.w(it, "!!! Collection item video preview download failed: ${preview.url}") }
                    }
                } else {
                    previewSources.forEach { preview ->
                        val previewFile = File(folder, "preview.${preview.sizeMarker}.${preview.extension}")
                        runCatching { downloadToFileTracked(client, preview.url, previewFile) }
                            .onSuccess { savedPreviews.add(preview.toSavedPreview(previewFile.name)) }
                            .onFailure { Timber.w(it, "!!! Collection item preview download failed: ${preview.url}") }
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
                writeCollectionMetadata(File(folder, METADATA_FILE_NAME), metadata)
            } catch (e: Exception) {
                if (folder.listFiles().isNullOrEmpty() || !File(folder, METADATA_FILE_NAME).exists()) {
                    folder.deleteRecursively()
                }
                throw e
            } finally {
                client.close()
            }
        }.also {
            if (progressStarted) finishCollectionDownload()
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

    private fun beginCollectionDownload(fileCount: Int) {
        synchronized(progressLock) {
            activeCollectionDownloads += 1
            totalFiles += fileCount.coerceAtLeast(1)
            updateDownloadPercentLocked()
        }
    }

    private fun finishCollectionDownload() {
        val shouldHide: Boolean
        synchronized(progressLock) {
            activeCollectionDownloads = (activeCollectionDownloads - 1).coerceAtLeast(0)
            shouldHide = activeCollectionDownloads == 0
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
                    if (activeCollectionDownloads == 0) {
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
        if (totalFiles <= 0 || activeCollectionDownloads <= 0) {
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
            Timber.w(it, "!!! Collection item album metadata parse failed")
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

    private fun findCollectionItemFolder(root: File, url: String): File? {
        val target = File(url)
        if (isInside(root, target)) {
            val parent = target.parentFile
            if (parent != null && File(parent, METADATA_FILE_NAME).exists()) return parent
        }

        return root.listFiles()
            ?.filter { it.isDirectory }
            ?.firstOrNull { folder ->
                val metadata = readCollectionMetadata(File(folder, METADATA_FILE_NAME)) ?: return@firstOrNull false
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
        const val DEFAULT_BUFFER_SIZE = 8 * 1024
    }
}

