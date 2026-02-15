package com.client.xvideos.common.coil

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.memory.MemoryCache
import coil3.network.cachecontrol.CacheControlCacheStrategy
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.allowHardware
import coil3.request.crossfade
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


data class CoilProgressItem(
    val url : String,
    val bytes: Long,
    val total : Long,
    val done : Boolean
)


object CoilImageLoaderFactory {

    @Volatile
    private var instance: ImageLoader? = null

    fun getImageLoader(context: Context): ImageLoader {
        return instance ?: synchronized(this) {
            instance ?: createImageLoader(context).also { instance = it }
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    private fun createImageLoader(context: Context): ImageLoader {

        val okHttpBuilder = OkHttpClient.Builder()
            .cache(
                okhttp3.Cache(
                    directory = File(context.cacheDir, "http_cache"),
                    maxSize = 500L * 1024L * 1024L // 500 MB
                )
            )
            .addNetworkInterceptor(
                ProgressInterceptor { requestUrl, bytes, total, done ->
                    CoilProgressManager.updateProgress(
                        url = requestUrl,
                        bytes = bytes,
                        total = total.coerceAtLeast(0L),
                        done = done
                    )
                }
            )

        // ← Вот здесь глобальное отключение проверки сертификатов на старых Android
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            try {
                @SuppressLint("CustomX509TrustManager", "TrustAllX509TrustManager")
                val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        override fun checkClientTrusted(
                            chain: Array<out X509Certificate>?,
                            authType: String?
                        ) = Unit

                        override fun checkServerTrusted(
                            chain: Array<out X509Certificate>?,
                            authType: String?
                        ) = Unit

                        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
                    }
                )

                val sslContext = SSLContext.getInstance("TLS").apply {
                    init(null, trustAllCerts, SecureRandom())
                }

                okHttpBuilder.sslSocketFactory(
                    sslContext.socketFactory,
                    trustAllCerts[0] as X509TrustManager
                )

                okHttpBuilder.hostnameVerifier { _, _ -> true }
            } catch (e: Exception) {
                Timber.e(e, "Не удалось настроить trust-all SSL")
            }
        }

        // Try to build OkHttpClient safely. In LayoutLib (Compose Preview), building OkHttpClient
        // can fail with NoClassDefFoundError for Android-specific classes like conscrypt.
        val okHttpClient = try {
            okHttpBuilder.build()
        } catch (e: Throwable) {
            Timber.e(e, "Failed to build custom OkHttpClient (likely in Preview)")
            null
        }

        return ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(AnimatedImageDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
                // Use custom OkHttpClient only if it was built successfully.
                // If not, Coil will fallback to its default network fetcher.
                okHttpClient?.let {
                    add(OkHttpNetworkFetcherFactory(callFactory = { it }))
                }
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(File(context.cacheDir, "image_cache"))
                    .maxSizeBytes(500L * 1024L * 1024L)
                    .build()
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25) // было 0.5 — можно уменьшить, если память жрёт
                    .strongReferencesEnabled(true)
                    .build()
            }
            .allowHardware(true)
            // .logger(DebugLogger()) // включи при отладке
            .build()
    }

    fun clearCache(context: Context) {
        getImageLoader(context).apply {
            memoryCache?.clear()
            diskCache?.clear()
        }
    }
}
