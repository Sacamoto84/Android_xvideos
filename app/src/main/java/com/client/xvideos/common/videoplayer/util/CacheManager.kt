package com.client.xvideos.common.videoplayer.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DefaultDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import com.client.xvideos.common.settings.Settings
import java.io.File

@UnstableApi
internal object CacheManager {
    private const val VIDEO_CACHE_DIR_NAME = "video_cache"

    private var cache: SimpleCache? = null
    private var databaseProvider: DefaultDatabaseProvider? = null
    private var databaseHelper: SQLiteOpenHelper? = null
    private var activePlayers = 0
    private var configuredMaxCacheBytes: Long? = null

    @Synchronized
    fun getCache(context: Context): SimpleCache {

        if (cache == null) {
            val maxCacheSizeBytes = VideoCacheSettings.maxSizeBytes(Settings.video_cache_disk_size_mb.field.value)

            val cacheDir = videoCacheDir(context).apply {
                if (!exists()) mkdirs()
            }

            if (databaseHelper == null) {
                databaseHelper = createSQLiteOpenHelper(context.applicationContext)
            }

            if (databaseProvider == null) {
                databaseProvider = DefaultDatabaseProvider(databaseHelper!!)
            }

            cache = SimpleCache(
                cacheDir,
                LeastRecentlyUsedCacheEvictor(maxCacheSizeBytes),
                databaseProvider!!
            )
            configuredMaxCacheBytes = maxCacheSizeBytes

        }
        activePlayers++
        return cache!!
    }

    fun cacheSizeBytes(context: Context): Long {
        return directorySizeBytes(videoCacheDir(context))
    }

    @Synchronized
    fun clearCache(context: Context) {
        releaseCache()
        activePlayers = 0
        videoCacheDir(context).deleteRecursively()
    }

    @Synchronized
    fun applyConfiguredSize() {
        val maxCacheSizeBytes = VideoCacheSettings.maxSizeBytes(Settings.video_cache_disk_size_mb.field.value)
        if (configuredMaxCacheBytes == maxCacheSizeBytes) return

        configuredMaxCacheBytes = maxCacheSizeBytes
        if (activePlayers <= 0) {
            releaseCache()
        }
    }

    @Synchronized
    fun release() {
        activePlayers--
        if (activePlayers <= 0) {
            releaseCache()
        }
    }

    private fun releaseCache() {
        try {
            cache?.release()
        } catch (_: Exception) {

        } finally {
            cache = null // Allow garbage collection
            databaseHelper?.close()
            databaseHelper = null
            databaseProvider = null // Allow garbage collection
        }
    }

    private fun createSQLiteOpenHelper(context: Context): SQLiteOpenHelper {
        return object : SQLiteOpenHelper(
            context.applicationContext,
            "media3_cache.db",
            null,
            1
        ) {
            override fun onCreate(db: SQLiteDatabase?) {
                db?.execSQL("CREATE TABLE IF NOT EXISTS cache_metadata (id INTEGER PRIMARY KEY, key TEXT, value TEXT);")
            }

            override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
                db?.execSQL("DROP TABLE IF EXISTS cache_metadata")
                onCreate(db)
            }
        }
    }

    private fun videoCacheDir(context: Context): File {
        return File(context.applicationContext.cacheDir, VIDEO_CACHE_DIR_NAME)
    }

    private fun directorySizeBytes(dir: File): Long {
        return dir.listFiles()?.sumOf { file ->
            if (file.isFile) file.length() else directorySizeBytes(file)
        } ?: 0L
    }

}
