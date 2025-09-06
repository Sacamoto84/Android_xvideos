package com.client.xvideos.common.fresco

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.Priority
import com.facebook.imagepipeline.core.ImagePipeline
import com.facebook.imagepipeline.request.ImageRequestBuilder
import timber.log.Timber
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

// Утилитарные функции для работы с Fresco
object FrescoUtils {

    fun clearCache() {
        Fresco.getImagePipeline().clearCaches()
    }

    fun clearMemoryCache() {
        Fresco.getImagePipeline().clearMemoryCaches()
    }

    fun pauseInBackground() {
        Fresco.getImagePipeline().pause()
    }

    fun resume() {
        Fresco.getImagePipeline().resume()
    }

    fun prefetchImage(uri: String, context: Context) {
        val imageRequest = ImageRequestBuilder
            .newBuilderWithSource(Uri.parse(uri))
            .build()

        Fresco.getImagePipeline().prefetchToBitmapCache(imageRequest, context)
    }
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
