package com.client.xvideos.common.fresco

import android.app.Application
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.facebook.cache.common.CacheErrorLogger
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.common.internal.Supplier
import com.facebook.common.webp.WebpBitmapFactory
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.ControllerListener
import com.facebook.imageformat.DefaultImageFormats
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import com.facebook.imagepipeline.cache.MemoryCacheParams
import com.facebook.imagepipeline.common.Priority
import com.facebook.imagepipeline.core.DefaultExecutorSupplier
import com.facebook.imagepipeline.core.DownsampleMode
import com.facebook.imagepipeline.core.ImagePipeline
import com.facebook.imagepipeline.core.MemoryChunkType
import com.facebook.imagepipeline.decoder.DefaultImageDecoder
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.listener.RequestListener
import com.facebook.imagepipeline.producers.ProducerContext
import com.facebook.imagepipeline.producers.ProducerListener2
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import okhttp3.OkHttpClient
import timber.log.Timber
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun FrescoInit(application: Application) {







    // 1. Создание параметров кэша в памяти
    // Укажите максимальное количество байт, которое может занимать кэш
    val bitmapCacheParams = MemoryCacheParams(
        /* maxCacheSize */ Runtime.getRuntime().maxMemory().toInt() / 16, // 25% от доступной памяти
        /* maxCacheEntries */ 512,
        /* maxEvictionQueueSize */ Int.MAX_VALUE,
        /* maxEvictionQueueEntries */ Int.MAX_VALUE,
        /* maxCacheEntrySize */ Int.MAX_VALUE
    )
    val bitmapCacheParamsSupplier = Supplier { bitmapCacheParams }


    val encodedCacheParams = MemoryCacheParams(
        /* maxCacheSize */  32 * 1024 * 1024, // 32MB для закодированных изображений
        /* maxCacheEntries */ 512,
        /* maxEvictionQueueSize */ Int.MAX_VALUE,
        /* maxEvictionQueueEntries */ Int.MAX_VALUE,
        /* maxCacheEntrySize */ Int.MAX_VALUE
    )
    val encodedCacheParamsSupplier = Supplier { encodedCacheParams }

    val diskCacheConfigMain = DiskCacheConfig.newBuilder(application)
        .setBaseDirectoryPath(application.cacheDir) // Укажите путь
        .setBaseDirectoryName("fresco_main_cache")
        .setMaxCacheSize(2000L * 1024 * 1024) // 2000 МБ
        .setMaxCacheSizeOnLowDiskSpace(1000L * 1024 * 1024)
        .setMaxCacheSizeOnVeryLowDiskSpace(500L * 1024 * 1024)
        .setCacheErrorLogger { category, clazz, message, cause ->
            Timber.e("!!! eee Fresco DiskCacheConfig error: $message category: $category clazz: $clazz cause: $cause")
        }
        .setVersion(1)
        .build()


    class MyRequestLoggingListener : RequestListener {

        override fun onRequestStart(
            request: ImageRequest,
            callerContext: Any?,
            requestId: String,
            isPrefetch: Boolean
        ) {
            Log.d("Fresco", "!!! iii Request started: $requestId")
        }

        override fun onRequestSuccess(
            request: ImageRequest,
            requestId: String,
            isPrefetch: Boolean
        ) {
            Log.d("Fresco", "!!! iii Request success: $requestId")
        }

        override fun onRequestFailure(
            request: ImageRequest,
            requestId: String,
            throwable: Throwable,
            isPrefetch: Boolean
        ) {
            Log.e("Fresco", "!!! iii Request failed: $requestId", throwable)
        }

        override fun onRequestCancellation(requestId: String) {
            Log.d("Fresco", "!!! iii Request cancelled: $requestId")
        }

        override fun onProducerStart(requestId: String, producerName: String) {
            // Опционально логировать начало работы продюсера
        }

        override fun onProducerEvent(
            requestId: String?,
            producerName: String?,
            eventName: String?
        ) {
            //TODO("Not yet implemented")
        }

        override fun onProducerFinishWithSuccess(
            requestId: String,
            producerName: String,
            extraMap: MutableMap<String, String>?
        ) {
            // Опционально логировать успешное завершение продюсера
        }

        override fun onProducerFinishWithFailure(
            requestId: String,
            producerName: String,
            throwable: Throwable,
            extraMap: MutableMap<String, String>?
        ) {
            // Опционально логировать ошибку продюсера
        }

        override fun onProducerFinishWithCancellation(
            requestId: String,
            producerName: String,
            extraMap: MutableMap<String, String>?
        ) {
            // Опционально логировать отмену продюсера
        }

        override fun onUltimateProducerReached(
            requestId: String?,
            producerName: String?,
            successful: Boolean
        ) {
            //TODO("Not yet implemented")
        }

        override fun requiresExtraMap(requestId: String): Boolean = false
    }

    val listeners = HashSet<RequestListener?>()
    listeners.add(MyRequestLoggingListener())


    val customNetworkFetcher = FullCustomNetworkFetcher(createOptimizedOkHttpClient())

    val pipelineConfig = OkHttpImagePipelineConfigFactory
        .newBuilder(application, OkHttpClient.Builder().build())

        .setRequestListeners(listeners as Set<RequestListener>?)

        .setResizeAndRotateEnabledForNetwork(true)
        .setExecutorSupplier(DefaultExecutorSupplier(16))

        .setMainDiskCacheConfig(diskCacheConfigMain)
        //.setBitmapMemoryCacheParamsSupplier(bitmapCacheParamsSupplier)
        //.setEncodedMemoryCacheParamsSupplier(encodedCacheParamsSupplier) // Опционально: для закодированных данных


        // Прогрессивные JPEG
        .setProgressiveJpegConfig(SimpleProgressiveJpegConfig())

        // Включаем экспериментальные оптимизации
        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY) // Используем DirectByteBuffer


        // КЛЮЧЕВЫЕ ОПТИМИЗАЦИИ:
        //.setDownsampleEnabled(true) // Автоматическое уменьшение размера
        .setDownsampleMode(DownsampleMode.NEVER)
        .setResizeAndRotateEnabledForNetwork(true) // Ресайз для сетевых изображений

        .setNetworkFetcher(customNetworkFetcher)
        .build()

    Fresco.initialize(application, pipelineConfig)

    val preloader = ImagePreloader(Fresco.getImagePipeline())
}

@RequiresApi(Build.VERSION_CODES.N)
// Дополнительная оптимизация - предзагрузка изображений
class ImagePreloader(private val imagePipeline: ImagePipeline) {

    private val preloadExecutor = Executors.newFixedThreadPool(4)

    fun preloadImages(urls: List<String>) {
        preloadExecutor.submit {
            urls.forEach { url ->
                try {
                    val imageRequest = ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(url))
                        .setRequestPriority(Priority.LOW) // Низкий приоритет для предзагрузки
                        .build()

                    imagePipeline.prefetchToDiskCache(imageRequest, null)
                } catch (e: Exception) {
                    Timber.w(e, "Preload failed for: $url")
                }
            }
        }
    }

    fun preloadImagesWithCallback(urls: List<String>, onComplete: () -> Unit) {
        preloadExecutor.submit {
            val futures = urls.map { url ->
                CompletableFuture.runAsync {
                    val imageRequest = ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(url))
                        .setRequestPriority(Priority.LOW)
                        .build()
                    imagePipeline.prefetchToDiskCache(imageRequest, null)
                }
            }

            CompletableFuture.allOf(*futures.toTypedArray()).join()
            onComplete()
        }
    }
}