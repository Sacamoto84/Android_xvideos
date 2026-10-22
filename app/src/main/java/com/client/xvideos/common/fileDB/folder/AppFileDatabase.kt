package com.client.xvideos.common.fileDB.folder

import com.client.xvideos.common.AppPath
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppFileDatabase @Inject constructor() {
    private val root = AppPath.file_db
    private val volatileCacheMutex = Mutex()
    private var volatileCachesCleared = false

    val cacheUrlStringRam = FileStringCacheTable(FolderTable("$root/cache_url_string_ram"))
    val cacheUrlStringRom = FileStringCacheTable(FolderTable("$root/cache_url_string_rom"))
    val lAlbumPictureCache = FileStringCacheTable(FolderTable("$root/l_album_picture_cache"))
    val lAlbumBundleCache = FileStringCacheTable(FolderTable("$root/l_album_bundle_cache"))
    val rCacheMediaResponse = FileStringCacheTable(FolderTable("$root/r_cache_media_response"))

    val rSearchHistoryExplorerTable = FolderTable("$root/r_search_history_explorer")
    val rSearchHistoryNichesTable = FolderTable("$root/r_search_history_niches")

    suspend fun clearVolatileCachesOnProcessStart() {
        volatileCacheMutex.withLock {
            if (volatileCachesCleared) return
            cacheUrlStringRam.deleteAll()
            volatileCachesCleared = true
        }
    }

    /**
     * Удаляет давно устаревшие ответы Redgifs, чтобы папка кэша не росла бесконечно.
     * Свежесть для показа проверяется отдельно (TTL в RedApi); здесь — только уборка.
     */
    suspend fun trimExpiredCaches(nowMs: Long = System.currentTimeMillis()) {
        rCacheMediaResponse.deleteOld(nowMs - R_MEDIA_RESPONSE_MAX_AGE_MS)
    }

    companion object {
        /** Сколько хранить ответ Redgifs как запасной вариант на случай ошибки сети. */
        const val R_MEDIA_RESPONSE_MAX_AGE_MS = 7L * 24 * 60 * 60 * 1000
    }
}
