package com.client.xvideos.feature.videoplayer.chaintech.videoplayer.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DefaultDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@UnstableApi
internal object CacheManager {
    private var cache: SimpleCache? = null
    private var databaseProvider: DefaultDatabaseProvider? = null
    private var databaseHelper: SQLiteOpenHelper? = null
    private var activePlayers = 0

    @Synchronized
    fun getCache(context: Context): SimpleCache {
        if (cache == null) {
            val cacheSize = 100 * 1024 * 1024 // 100 MB
            val cacheDir = File(context.applicationContext.cacheDir, "video_cache").apply {
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
                LeastRecentlyUsedCacheEvictor(cacheSize.toLong()),
                databaseProvider!!
            )
        }
        activePlayers++
        return cache!!
    }

    @Synchronized
    fun release() {
        activePlayers--
        if (activePlayers <= 0) {
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

}