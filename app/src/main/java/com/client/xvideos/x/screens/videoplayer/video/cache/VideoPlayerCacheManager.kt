/*
 * Copyright 2023 Dora Lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.client.xvideos.screens.videoplayer.video.cache

import android.annotation.SuppressLint
import android.content.Context
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

/**
 * Manage video player cache.
 */
object VideoPlayerCacheManager {
    private const val VIDEO_CACHE_DIR_NAME = "video"

    @Volatile
    private var cacheInstance: Cache? = null
    private var configuredMaxCacheBytes: Long? = null

    /**
     * Set the cache for video player.
     * It can only be set once in the app, and it is shared and used by multiple video players.
     *
     * @param context Current activity context.
     * @param maxCacheBytes Sets the maximum cache capacity in bytes. If the cache builds up as much as the set capacity, it is deleted from the oldest cache.
     */
    @SuppressLint("UnsafeOptInUsageError")
    @Synchronized
    fun initialize(context: Context, maxCacheBytes: Long) {
        if (cacheInstance != null) {
            return
        }
        configuredMaxCacheBytes = maxCacheBytes

        cacheInstance = SimpleCache(
            videoCacheDir(context),
            LeastRecentlyUsedCacheEvictor(maxCacheBytes),
            StandaloneDatabaseProvider(context),
        )
    }

    /**
     * Gets the ExoPlayer cache instance. If null, the cache to be disabled.
     */
    internal fun getCache(): Cache? = cacheInstance

    fun cacheSizeBytes(context: Context): Long {
        return directorySizeBytes(videoCacheDir(context))
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Synchronized
    fun clearCache(context: Context) {
        val maxCacheBytes = configuredMaxCacheBytes
        (cacheInstance as? SimpleCache)?.release()
        cacheInstance = null
        videoCacheDir(context).deleteRecursively()
        if (maxCacheBytes != null) {
            initialize(context, maxCacheBytes)
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
