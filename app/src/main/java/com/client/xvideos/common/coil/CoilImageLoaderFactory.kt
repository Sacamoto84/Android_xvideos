package com.client.xvideos.common.coil

import android.content.Context
import android.os.Build
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import okhttp3.OkHttpClient
import java.io.File

object CoilImageLoaderFactory {

    @Volatile
    private var instance: ImageLoader? = null

    fun getImageLoader(context: Context): ImageLoader {
        return instance ?: synchronized(this) {
            instance ?: createImageLoader(context).also { instance = it }
        }
    }

    private fun createImageLoader(context: Context): ImageLoader {
        val okHttpClient = OkHttpClient.Builder()
            // Настройка HTTP кеша
            .cache(
                okhttp3.Cache(
                    directory = File(context.cacheDir, "http_cache"),
                    maxSize = 500L * 1024L * 1024L // 500 MB
                )
            )
            .build()

        return ImageLoader.Builder(context)
            .components {
                // Поддержка GIF и анимаций
                if (Build.VERSION.SDK_INT >= 28) {
                    add(AnimatedImageDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
                // OkHttp для сетевых запросов
                add(OkHttpNetworkFetcherFactory(callFactory = { okHttpClient }))
            }
            // Настройка дискового кеша
            .diskCache {
                DiskCache.Builder()
                    .directory(File(context.cacheDir, "image_cache"))
                    .maxSizeBytes(2500L * 1024L * 1024L) // 2500 MB
                    .build()
            }
            // Настройка кеша в памяти
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25) // 25% доступной памяти
                    .strongReferencesEnabled(true)
                    .build()
            }
            // Включить кросс-фейд по умолчанию
            .crossfade(true)
            // Разрешить использование hardware bitmaps (быстрее, но нельзя редактировать)
            //    .allowHardware(true)
            // Включить логирование (для отладки)
            // .logger(DebugLogger())
            .build()
    }

    // Метод для очистки кеша
    fun clearCache(context: Context) {
        getImageLoader(context).apply {
            memoryCache?.clear()
            diskCache?.clear()
        }
    }
}