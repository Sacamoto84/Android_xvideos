package com.client.xvideos.common.fresco

import android.app.Application
import android.util.Log
import com.client.xvideos.common.fresco.FullCustomNetworkFetcher
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.common.internal.Supplier
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import com.facebook.imagepipeline.cache.MemoryCacheParams
import com.facebook.imagepipeline.core.DefaultExecutorSupplier
import com.facebook.imagepipeline.listener.RequestListener
import com.facebook.imagepipeline.request.ImageRequest
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

fun FrescoInit(application: Application) {

    // 1. Создание параметров кэша в памяти
    // Укажите максимальное количество байт, которое может занимать кэш
    val memoryCacheParams = MemoryCacheParams(
        /* maxCacheSize */ 140 * 1024 * 1024, // 140 МБ
        /* maxCacheEntries */ 256,
        /* maxEvictionQueueSize */ Int.MAX_VALUE,
        /* maxEvictionQueueEntries */ Int.MAX_VALUE,
        /* maxCacheEntrySize */ Int.MAX_VALUE
    )

    // 2. Создание поставщика параметров кэша (Supplier)
    // Этот поставщик будет возвращать параметры кэша
    val memoryCacheParamsSupplier = Supplier { memoryCacheParams }

    val diskCacheConfig = DiskCacheConfig.newBuilder(application)
        .setBaseDirectoryPath(application.cacheDir) // Укажите путь
        .setBaseDirectoryName("fresco_cache")
        .setMaxCacheSize(1000L * 1024 * 1024) // 1000 МБ
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

// Применение в конфигурации:
    val customOkHttpClient = OkHttpClient.Builder()
        .connectTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .build()

    val customNetworkFetcher = FullCustomNetworkFetcher(customOkHttpClient)

    val pipelineConfig = OkHttpImagePipelineConfigFactory
        .newBuilder(application, OkHttpClient.Builder().build())
        .setDownsampleEnabled(true)
        .setRequestListeners(listeners as Set<RequestListener>?)
        .setResizeAndRotateEnabledForNetwork(true)
        .setExecutorSupplier(DefaultExecutorSupplier(16))
        .setMainDiskCacheConfig(diskCacheConfig)
        .setBitmapMemoryCacheParamsSupplier(memoryCacheParamsSupplier)
        .setEncodedMemoryCacheParamsSupplier(memoryCacheParamsSupplier) // Опционально: для закодированных данных

        .setNetworkFetcher(customNetworkFetcher)

        //.setCustomFetchSequenceFactories()

//        .setNetworkFetcher (
//
//            object : NetworkFetcher<FetchState> {
//                override fun createFetchState(consumer: Consumer<EncodedImage>, producerContext: ProducerContext) = CryptoFetchState(consumer, producerContext)
//
//                    override fun fetch(fetchState: FetchState, callback: NetworkFetcher.Callback) {
//
//                        Timber.i("!!! iii Fresco CryptoSchemeFetcher fetch")
//
//                        val uri = fetchState.uri
//                        if (uri.scheme == "crypto") {
//                            CryptoSchemeFetcher(poolFactory).fetch(fetchState as CryptoFetchState, callback)
//                        }
//                    }
//
//                override fun shouldPropagate(fetchState: FetchState?): Boolean = true
//
//                override fun onFetchCompletion(
//                    fetchState: FetchState?,
//                    byteSize: Int
//                ) {
//
//                }
//
//                override fun getExtraMap(
//                    fetchState: FetchState?,
//                    byteSize: Int
//                ): Map<String?, String?>? = null
//
//            }

           // if (uri.scheme == "crypto") {
            //    CryptoSchemeFetcher(poolFactory)
           // } else {
          //      DefaultNetworkFetcher()
           // }
          //  )



        .build()

    Fresco.initialize(application, pipelineConfig)

}