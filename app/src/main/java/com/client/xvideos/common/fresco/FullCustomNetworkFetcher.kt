package com.client.xvideos.common.fresco

import com.client.xvideos.BuildConfig
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.encrypting.Crypto
import com.client.xvideos.common.encrypting.Password
import com.facebook.imagepipeline.backends.okhttp3.OkHttpNetworkFetcher
import com.facebook.imagepipeline.image.EncodedImage
import com.facebook.imagepipeline.producers.Consumer
import com.facebook.imagepipeline.producers.NetworkFetcher
import com.facebook.imagepipeline.producers.ProducerContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class FullCustomNetworkFetcher(
    private val okHttpClient: OkHttpClient
) : NetworkFetcher<OkHttpNetworkFetcher.OkHttpNetworkFetchState> {

    // Пул потоков для криптографических операций
    private val cryptoExecutor = Executors.newFixedThreadPool(4)

    // Кеш для ключей шифрования
    private val keyCache = ConcurrentHashMap<String, SecretKey>()

    override fun createFetchState(
        consumer: Consumer<EncodedImage>,
        context: ProducerContext
    ): OkHttpNetworkFetcher.OkHttpNetworkFetchState {
        return OkHttpNetworkFetcher.OkHttpNetworkFetchState(consumer, context)
    }

    override fun fetch(
        fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState,
        callback: NetworkFetcher.Callback
    ) {
        val imageRequest = fetchState.context.imageRequest
        val url = imageRequest.sourceUri.toString()

        Timber.d("Fresco fetch: $url")

        // Чтение из папки крипто (оптимизированная версия)
        if (url.contains("likesCrypto")) {
            fetchCryptoFile(url, callback, fetchState)
            return
        }

        // Оптимизированная сетевая загрузка
        fetchNetworkFile(url, callback, fetchState)
    }

    private fun fetchCryptoFile(
        url: String,
        callback: NetworkFetcher.Callback,
        fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState
    ) {
        cryptoExecutor.submit {
            try {
                val fileName = url.substringAfterLast("/")
                val file = File(AppPath.likesCrypto_l, fileName)

                if (!file.exists()) {
                    callback.onFailure(IOException("File not found: $fileName"))
                    return@submit
                }

                // КРИТИЧНО: Используем буферизованный InputStream для лучшей производительности
                BufferedInputStream(FileInputStream(file), 8192).use { fis ->
                    val iv = ByteArray(Crypto.IV_SIZE)
                    val read = fis.read(iv)
                    if (read != Crypto.IV_SIZE) throw IOException("Cannot read IV")

                    val key = Password.key ?: throw IllegalStateException("Key is null")
                    val cipher = Cipher.getInstance(Password.CIPHER_ALGORITHM)
                    cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(Crypto.TAG_SIZE, iv))

                    // ОПТИМИЗАЦИЯ: Используем буферизованный CipherInputStream
                    val decryptedStream = BufferedInputStream(
                        CipherInputStream(fis, cipher),
                        16384 // Больший буфер для декриптации
                    )

                    callback.onResponse(decryptedStream, 200)
                }

                fetchState.submitTime = System.nanoTime()

            } catch (e: Exception) {
                Timber.e(e, "Crypto file fetch error")
                callback.onFailure(e)
            }
        }
    }

    private fun fetchNetworkFile(
        url: String,
        callback: NetworkFetcher.Callback,
        fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState
    ) {
        val request = Request.Builder()
            .url(url)
            // ОПТИМИЗАЦИЯ: Добавляем заголовки для лучшей производительности
            .addHeader("Accept-Encoding", "gzip, deflate") // Сжатие
            .addHeader("Connection", "keep-alive") // Переиспользование соединений
            .addHeader("Cache-Control", "max-age=3600") // Кеширование на час
            // При необходимости добавить авторизацию:
            //.addHeader("Authorization", "Bearer ${getAuthToken()}")
            .build()

        fetchState.submitTime = System.nanoTime()
        val call = okHttpClient.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Timber.w(e, "Network request failed for: $url")
                callback.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        response.body?.let { responseBody ->
                            // ОПТИМИЗАЦИЯ: Используем буферизованный поток
                            val inputStream = BufferedInputStream(
                                responseBody.byteStream(),
                                16384 // 16KB буфер
                            )
                            callback.onResponse(inputStream, response.code)
                        } ?: callback.onFailure(IOException("Empty response body"))
                    } else {
                        callback.onFailure(IOException("HTTP ${response.code}: ${response.message}"))
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Response processing error")
                    callback.onFailure(e)
                }
                // НЕ закрываем response здесь - это делает Fresco
            }
        })
    }

    override fun shouldPropagate(fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState): Boolean {
        return false
    }

    override fun onFetchCompletion(
        fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState,
        byteSize: Int
    ) {
        fetchState.responseTime = System.nanoTime()
    }

    override fun getExtraMap(
        fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState,
        byteSize: Int
    ): Map<String, String> {
        val responseTime = fetchState.responseTime - fetchState.submitTime
        return mapOf(
            "responseTime" to "${responseTime / 1_000_000}ms", // Конвертируем в миллисекунды
            "imageSize" to "${byteSize / 1024}KB",
            "throughput" to "${(byteSize * 1_000_000_000L / responseTime) / 1024}KB/s"
        )
    }
}

// Конфигурация OkHttpClient для максимальной производительности
fun createOptimizedOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        // КЛЮЧЕВЫЕ ОПТИМИЗАЦИИ:
        .connectionPool(ConnectionPool(20, 5, TimeUnit.MINUTES)) // Больше соединений
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS) // Увеличенный таймаут для больших файлов
        .writeTimeout(15, TimeUnit.SECONDS)

        // Параллельные запросы
        .dispatcher(Dispatcher().apply {
            maxRequests = 64 // Больше параллельных запросов
            maxRequestsPerHost = 32 // Больше запросов на один хост
        })

        // Включаем сжатие
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("Accept-Encoding", "gzip, deflate, br")
            chain.proceed(requestBuilder.build())
        }

//        // Логирование только для отладки (отключить в релизе!)
//        .addInterceptor(HttpLoggingInterceptor().apply {
//            level = if (BuildConfig.DEBUG) {
//                HttpLoggingInterceptor.Level.HEADERS
//            } else {
//                HttpLoggingInterceptor.Level.NONE
//            }
//        })

        // Повторные попытки
        .retryOnConnectionFailure(true)

        .build()
}


//class FullCustomNetworkFetcher(
//    private val okHttpClient: OkHttpClient
//) : NetworkFetcher<OkHttpNetworkFetcher.OkHttpNetworkFetchState> {
//
//    override fun createFetchState(
//        consumer: Consumer<EncodedImage>,
//        context: ProducerContext
//    ): OkHttpNetworkFetcher.OkHttpNetworkFetchState {
//        return OkHttpNetworkFetcher.OkHttpNetworkFetchState(consumer, context)
//    }
//
//    override fun fetch(
//        fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState,
//        callback: NetworkFetcher.Callback
//    ) {
//
//        val imageRequest = fetchState.context.imageRequest
//        val url = imageRequest.sourceUri.toString()
//
//        Timber.Forest.i("!!! iii Fresco FullCustomNetworkFetcher fetch $url")
//
//        //Чтение из папки крипто
//        if (url.contains("likesCrypto")) {
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    // https://likesCrypto/имя файла
//                    val fileName = url.substringAfterLast("/")
//
//                    val file = File(AppPath.likesCrypto_l, fileName)
//                    if (!file.exists()) {
//                        callback.onFailure(IOException("!!! eee Fresco File not found: $url"))
//                    }
//
//                    val key = Password.key ?: throw IllegalStateException("Key is null")
//
//                    FileInputStream(file).use { fis ->
//                        val iv = ByteArray(Crypto.IV_SIZE)
//                        val read = fis.read(iv)
//                        if (read != Crypto.IV_SIZE) throw IOException("Cannot read IV")
//
//                        val cipher = Cipher.getInstance(Password.CIPHER_ALGORITHM)
//                        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(Crypto.TAG_SIZE, iv))
//
//                        CipherInputStream(fis, cipher).use { cis ->
//                            callback.onResponse(cis, 200)
//                        }
//                    }
//
//                    fetchState.submitTime = System.nanoTime()
//                } catch (e: Exception) {
//                    Timber.e("!!! eee Fresco FullCustomNetworkFetcher Exception: $e")
//                    callback.onFailure(e)
//                }
//            }
//            return
//        }
//
//
//        val request = Request.Builder()
//            .url(url)
//            //.addHeader("Authorization", "Bearer ${getAuthToken()}")
//            //.addHeader("Custom-Header", "custom_value")
//            .build()
//
//        val call = okHttpClient.newCall(request)
//
//        call.enqueue(object : Callback {
//
//            override fun onFailure(call: Call, e: IOException) {  callback.onFailure(e) }
//
//            override fun onResponse(call: Call, response: Response) {
//                try {
//                    if (response.isSuccessful) {
//                        response.body?.byteStream()?.let { inputStream ->
//                            callback.onResponse(inputStream, response.code)
//                        } ?: callback.onFailure(IOException("Empty response"))
//                    } else {
//                        callback.onFailure( IOException("HTTP ${response.code}: ${response.message}") )
//                    }
//                } catch (e: Exception) {
//                    callback.onFailure(e)
//                } finally {
//                    response.close()
//                }
//            }
//        })
//
//        fetchState.submitTime = System.nanoTime()
//    }
//
//    override fun shouldPropagate(fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState): Boolean {
//        return false
//    }
//
//    override fun onFetchCompletion(
//        fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState,
//        byteSize: Int
//    ) {
//        fetchState.responseTime = System.nanoTime()
//    }
//
//    override fun getExtraMap(
//        fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState,
//        byteSize: Int
//    ): Map<String, String>? {
//        return mapOf(
//            "responseTime" to "${fetchState.responseTime - fetchState.submitTime}",
//            "imageSize" to byteSize.toString()
//        )
//    }
//
//    private fun getAuthToken(): String {
//        return "your_token_here"
//    }
//}