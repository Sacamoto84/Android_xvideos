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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.concurrent.withLock

// Модель состояния загрузки для Compose
data class DownloadItem(
    val url: String,
    val fileName: String = url.substringAfterLast("/").ifEmpty { "unknown" },
    val startTime: Long = System.currentTimeMillis(),
    val status: DownloadStatus = DownloadStatus.DOWNLOADING
) {
    val duration: Long get() = System.currentTimeMillis() - startTime
    val durationSeconds: Long get() = duration / 1000
}

enum class DownloadStatus {
    DOWNLOADING,
    COMPLETED,
    FAILED,
    CANCELLED
}

data class DownloadQueueState(
    val activeDownloads: List<DownloadItem> = emptyList(),
    val maxQueueSize: Int = 4,
    val totalCompleted: Int = 0,
    val totalFailed: Int = 0
) {
    val currentSize: Int get() = activeDownloads.size
    val isQueueFull: Boolean get() = currentSize >= maxQueueSize
    val hasActiveDownloads: Boolean get() = activeDownloads.isNotEmpty()
}

// Глобальный реактивный менеджер очереди загрузок
object DownloadQueueManager {
    private const val MAX_QUEUE_SIZE = 16

    // Очередь активных загрузок (URL -> Pair(Call, startTime))
    private val activeDownloads = LinkedHashMap<String, Pair<Call, Long>>()
    private val queueLock = ReentrantLock()

    // Счетчики для статистики
    private var totalCompleted = 0
    private var totalFailed = 0

    // StateFlow для Compose - основное реактивное состояние
    private val _queueState = MutableStateFlow(DownloadQueueState())
    val queueState: StateFlow<DownloadQueueState> = _queueState.asStateFlow()

    /**
     * Обновляет StateFlow с текущим состоянием очереди
     * Вызывается после каждого изменения состояния
     */
    private fun updateState() {
        val downloadItems = activeDownloads.entries.map { (url, callPair) ->
            DownloadItem(
                url = url,
                fileName = url.substringAfterLast("/").ifEmpty { "unknown" },
                startTime = callPair.second,
                status = DownloadStatus.DOWNLOADING
            )
        }

        val newState = DownloadQueueState(
            activeDownloads = downloadItems,
            maxQueueSize = MAX_QUEUE_SIZE,
            totalCompleted = totalCompleted,
            totalFailed = totalFailed
        )

        _queueState.value = newState

        Timber.d("📊 Queue state updated: ${downloadItems.size} active downloads, ✅${totalCompleted} completed, ❌${totalFailed} failed")
    }

    /**
     * Добавляет URL в очередь загрузок
     * @param url URL для загрузки
     * @param call Call объект для возможности отмены
     * @return true если добавлено успешно
     */
    fun addToQueue(url: String, call: Call): Boolean {
        queueLock.withLock {
            val currentTime = System.currentTimeMillis()

            // Если URL уже в очереди, отменяем старый и добавляем новый
            activeDownloads[url]?.let { (existingCall, _) ->
                Timber.d("🔄 Cancelling duplicate request for: $url")
                try {
                    existingCall.cancel()
                } catch (e: Exception) {
                    Timber.w(e, "Error cancelling duplicate call")
                }
            }

            // Проверяем размер очереди - если заполнена, удаляем самую старую
            if (activeDownloads.size >= MAX_QUEUE_SIZE && !activeDownloads.containsKey(url)) {
                val oldestEntry = activeDownloads.entries.first()
                Timber.d("🚨 Queue full, cancelling oldest request: ${oldestEntry.key}")
                try {
                    oldestEntry.value.first.cancel()
                    activeDownloads.remove(oldestEntry.key)
                    totalFailed++ // Считаем принудительно отмененные как неудачные
                } catch (e: Exception) {
                    Timber.w(e, "Error cancelling oldest call")
                }
            }

            // Добавляем новую загрузку с текущим временем
            activeDownloads[url] = Pair(call, currentTime)

            Timber.d("➕ Added to queue: $url (queue size: ${activeDownloads.size}/${MAX_QUEUE_SIZE})")

            // Обновляем StateFlow для реактивного UI
            updateState()
            return true
        }
    }

    /**
     * Удаляет URL из очереди (вызывается при завершении загрузки)
     * @param url URL для удаления
     * @param success true если загрузка успешна, false если произошла ошибка
     */
    fun removeFromQueue(url: String, success: Boolean = true) {
        queueLock.withLock {
            val removed = activeDownloads.remove(url)
            if (removed != null) {
                val duration = System.currentTimeMillis() - removed.second

                if (success) {
                    totalCompleted++
                    Timber.d("✅ Download completed: $url (${duration}ms)")
                } else {
                    totalFailed++
                    Timber.d("❌ Download failed: $url (${duration}ms)")
                }

                Timber.d("➖ Removed from queue: $url (queue size: ${activeDownloads.size}/${MAX_QUEUE_SIZE})")

                // Обновляем StateFlow для реактивного UI
                updateState()
            }
        }
    }

    /**
     * Проверяет, находится ли URL в очереди
     */
    fun isInQueue(url: String): Boolean {
        queueLock.withLock {
            return activeDownloads.containsKey(url)
        }
    }

    /**
     * Отменяет все активные загрузки
     */
    fun cancelAll() {
        queueLock.withLock {
            val cancelledCount = activeDownloads.size
            if (cancelledCount == 0) {
                Timber.d("🤷 No downloads to cancel")
                return
            }

            Timber.d("🚫 Cancelling all downloads ($cancelledCount items)")

            activeDownloads.values.forEach { (call, startTime) ->
                try {
                    call.cancel()
                    val duration = System.currentTimeMillis() - startTime
                    Timber.d("🚫 Cancelled download after ${duration}ms")
                } catch (e: Exception) {
                    Timber.w(e, "Error cancelling call during cancelAll")
                }
            }

            activeDownloads.clear()
            totalFailed += cancelledCount

            // Обновляем StateFlow для реактивного UI
            updateState()
        }
    }

    /**
     * Отменяет конкретную загрузку по URL
     * @param url URL загрузки для отмены
     * @return true если загрузка была найдена и отменена
     */
    fun cancelDownload(url: String): Boolean {
        queueLock.withLock {
            val downloadInfo = activeDownloads[url]
            return if (downloadInfo != null) {
                try {
                    val (call, startTime) = downloadInfo
                    call.cancel()
                    activeDownloads.remove(url)
                    totalFailed++

                    val duration = System.currentTimeMillis() - startTime
                    Timber.d("🚫 Cancelled specific download: $url (after ${duration}ms)")

                    // Обновляем StateFlow для реактивного UI
                    updateState()
                    true
                } catch (e: Exception) {
                    Timber.w(e, "Error cancelling specific download: $url")
                    false
                }
            } else {
                Timber.w("⚠️ Attempted to cancel non-existent download: $url")
                false
            }
        }
    }

    /**
     * Сбрасывает статистику (но не отменяет активные загрузки)
     */
    fun resetStatistics() {
        queueLock.withLock {
            val oldCompleted = totalCompleted
            val oldFailed = totalFailed

            totalCompleted = 0
            totalFailed = 0

            Timber.d("📊 Statistics reset: was ✅$oldCompleted/❌$oldFailed, now ✅0/❌0")

            // Обновляем StateFlow для реактивного UI
            updateState()
        }
    }

    /**
     * Получает размер текущей очереди
     */
    fun getQueueSize(): Int {
        queueLock.withLock {
            return activeDownloads.size
        }
    }

    /**
     * Получает список всех URL в очереди (для отладки)
     */
    fun getQueueUrls(): List<String> {
        queueLock.withLock {
            return activeDownloads.keys.toList()
        }
    }

    /**
     * Получает детальную информацию об активных загрузках
     */
    fun getActiveDownloadsInfo(): List<DownloadItem> {
        queueLock.withLock {
            return activeDownloads.entries.map { (url, callPair) ->
                DownloadItem(
                    url = url,
                    fileName = url.substringAfterLast("/").ifEmpty { "unknown" },
                    startTime = callPair.second,
                    status = DownloadStatus.DOWNLOADING
                )
            }
        }
    }

    /**
     * Получает общую статистику одним вызовом
     * @return Triple(активные, завершенные, неудачные)
     */
    fun getStatistics(): Triple<Int, Int, Int> {
        queueLock.withLock {
            return Triple(activeDownloads.size, totalCompleted, totalFailed)
        }
    }

    /**
     * Проверяет, заполнена ли очередь до максимума
     */
    fun isQueueFull(): Boolean {
        queueLock.withLock {
            return activeDownloads.size >= MAX_QUEUE_SIZE
        }
    }

    /**
     * Получает максимальный размер очереди
     */
    fun getMaxQueueSize(): Int = MAX_QUEUE_SIZE

    /**
     * Получает информацию о загрузке по URL
     * @param url URL для поиска
     * @return DownloadItem если найден, null если не найден
     */
    fun getDownloadInfo(url: String): DownloadItem? {
        queueLock.withLock {
            val downloadInfo = activeDownloads[url] ?: return null
            return DownloadItem(
                url = url,
                fileName = url.substringAfterLast("/").ifEmpty { "unknown" },
                startTime = downloadInfo.second,
                status = DownloadStatus.DOWNLOADING
            )
        }
    }

    /**
     * Получает время ожидания самой старой загрузки
     * @return время в миллисекундах или null если очередь пуста
     */
    fun getOldestDownloadDuration(): Long? {
        queueLock.withLock {
            if (activeDownloads.isEmpty()) return null
            val oldestStartTime = activeDownloads.values.minOf { it.second }
            return System.currentTimeMillis() - oldestStartTime
        }
    }

}

// Обновленный FullCustomNetworkFetcher с интеграцией реактивной очереди
class FullCustomNetworkFetcher(
    private val okHttpClient: OkHttpClient
) : NetworkFetcher<OkHttpNetworkFetcher.OkHttpNetworkFetchState> {

    // Пул потоков для криптографических операций
    private val cryptoExecutor = Executors.newFixedThreadPool(4)

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

        Timber.d("🚀 Fresco fetch initiated: $url")

        // Проверяем, не находится ли URL уже в очереди
        if (DownloadQueueManager.isInQueue(url)) {
            Timber.d("⚠️ URL already in queue, skipping duplicate: $url")
            // Можно либо отменить дубликат, либо просто завершить
            callback.onFailure(IOException("URL already being downloaded: $url"))
            return
        }

        // Чтение из папки крипто (локальные файлы не идут через очередь)
        if (url.contains("likesCrypto")) {
            fetchCryptoFile(url, callback, fetchState)
            return
        }

        // Оптимизированная сетевая загрузка с контролем очереди
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

                Timber.d("🔐 Fetching crypto file: $fileName")

                if (!file.exists()) {
                    Timber.w("❌ Crypto file not found: $fileName")
                    callback.onFailure(IOException("File not found: $fileName"))
                    return@submit
                }

                // КРИТИЧНО: Используем буферизованный InputStream для лучшей производительности
                BufferedInputStream(FileInputStream(file), 8192).use { fis ->
                    val iv = ByteArray(Crypto.IV_SIZE)
                    val read = fis.read(iv)
                    if (read != Crypto.IV_SIZE) throw IOException("Cannot read IV from crypto file")

                    val key = Password.key ?: throw IllegalStateException("Crypto key is null")
                    val cipher = Cipher.getInstance(Password.CIPHER_ALGORITHM)
                    cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(Crypto.TAG_SIZE, iv))

                    // ОПТИМИЗАЦИЯ: Используем буферизованный CipherInputStream
                    val decryptedStream = BufferedInputStream(
                        CipherInputStream(fis, cipher),
                        16384 // Больший буфер для декриптации
                    )

                    fetchState.submitTime = System.nanoTime()
                    callback.onResponse(decryptedStream, 200)
                    Timber.d("✅ Crypto file decrypted successfully: $fileName")
                }

            } catch (e: Exception) {
                Timber.e(e, "❌ Crypto file fetch error: $url")
                callback.onFailure(e)
            }
        }
    }

    private fun fetchNetworkFile(
        url: String,
        callback: NetworkFetcher.Callback,
        fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState
    ) {
        // Создаем обертку над callback для интеграции с очередью
        val queueCallback = QueueIntegratedCallback(url, callback)

        val request = Request.Builder()
            .url(url)
            // ОПТИМИЗАЦИЯ: Добавляем заголовки для лучшей производительности
            .addHeader("Accept-Encoding", "gzip, deflate, br") // Улучшенное сжатие
            .addHeader("Connection", "keep-alive") // Переиспользование соединений
            .addHeader("Cache-Control", "max-age=3600") // Кеширование на час
            .addHeader("User-Agent", "FullCustomNetworkFetcher/1.0") // Идентификация
            // При необходимости добавить авторизацию:
            //.addHeader("Authorization", "Bearer ${getAuthToken()}")
            .build()

        fetchState.submitTime = System.nanoTime()
        val call = okHttpClient.newCall(request)

        // КЛЮЧЕВОЕ: Добавляем в очередь загрузок ПЕРЕД началом запроса
        val addedToQueue = DownloadQueueManager.addToQueue(url, call)

        if (!addedToQueue) {
            Timber.w("⚠️ Failed to add to queue: $url")
            callback.onFailure(IOException("Failed to add to download queue"))
            return
        }

        Timber.d("📥 Starting network request: $url (queue: ${DownloadQueueManager.getQueueSize()}/${DownloadQueueManager.getMaxQueueSize()})")

        call.enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                val isCancelled = call.isCanceled()

                // Удаляем из очереди при ошибке (success = false)
                DownloadQueueManager.removeFromQueue(url, success = false)

                if (isCancelled) {
                    Timber.d("🚫 Request cancelled: $url")
                    // Для отмененных запросов используем специальное исключение
                    queueCallback.onFailure(IOException("Request was cancelled", e))
                } else {
                    Timber.w(e, "❌ Network request failed: $url")
                    queueCallback.onFailure(e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                var success = false
                try {
                    if (response.isSuccessful) {
                        response.body?.let { responseBody ->
                            val contentLength = responseBody.contentLength()
                            Timber.d("📦 Response received: $url (${contentLength} bytes, ${response.code})")

                            // ОПТИМИЗАЦИЯ: Используем буферизованный поток
                            val inputStream = BufferedInputStream(
                                responseBody.byteStream(),
                                16384 // 16KB буфер для лучшей производительности
                            )

                            queueCallback.onResponse(inputStream, response.code)
                            success = true

                        } ?: run {
                            val error = IOException("Empty response body from: $url")
                            Timber.w("⚠️ Empty response body: $url")
                            queueCallback.onFailure(error)
                        }
                    } else {
                        val error = IOException("HTTP ${response.code}: ${response.message} for: $url")
                        Timber.w("❌ HTTP error ${response.code}: $url")
                        queueCallback.onFailure(error)
                    }
                } catch (e: Exception) {
                    Timber.e(e, "❌ Response processing error: $url")
                    queueCallback.onFailure(e)
                } finally {
                    // КЛЮЧЕВОЕ: Удаляем из очереди при завершении (успешном или нет)
                    DownloadQueueManager.removeFromQueue(url, success = success)

                    if (success) {
                        Timber.d("✅ Network download completed: $url")
                    }
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
        val queueStats = DownloadQueueManager.getStatistics()

        return mapOf(
            "responseTime" to "${responseTime / 1_000_000}ms",
            "imageSize" to "${byteSize / 1024}KB",
            "throughput" to "${(byteSize * 1_000_000_000L / responseTime) / 1024}KB/s",
            // Добавляем информацию об очереди в метрики
            "queueSize" to "${queueStats.first}",
            "queueMaxSize" to "${DownloadQueueManager.getMaxQueueSize()}",
            "totalCompleted" to "${queueStats.second}",
            "totalFailed" to "${queueStats.third}",
            "queueFull" to "${DownloadQueueManager.isQueueFull()}"
        )
    }

    /**
     * Вспомогательный класс для интеграции callback с системой очереди
     */
    private class QueueIntegratedCallback(
        private val url: String,
        private val originalCallback: NetworkFetcher.Callback
    ) : NetworkFetcher.Callback {

        override fun onResponse(response: InputStream, responseLength: Int) {
            originalCallback.onResponse(response, responseLength)
        }

        override fun onFailure(throwable: Throwable) {
            originalCallback.onFailure(throwable)
        }

        override fun onCancellation() {
            //TODO("Not yet implemented")
        }
    }
}

// Конфигурация OkHttpClient для максимальной производительности с очередью
fun createOptimizedOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        // КЛЮЧЕВЫЕ ОПТИМИЗАЦИИ для работы с очередью:
        .connectionPool(ConnectionPool(25, 5, TimeUnit.MINUTES)) // Еще больше соединений
        .connectTimeout(15, TimeUnit.SECONDS) // Увеличенный таймаут
        .readTimeout(30, TimeUnit.SECONDS) // Увеличенный таймаут для больших файлов
        .writeTimeout(15, TimeUnit.SECONDS)

        // Параллельные запросы (должно соответствовать размеру очереди)
        .dispatcher(Dispatcher().apply {
            maxRequests = 16 // Больше общих запросов
            maxRequestsPerHost = 8 // Больше запросов на один хост
        })

        // Включаем максимальное сжатие
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Accept", "image/webp,image/apng,image/*,*/*;q=0.8")
            chain.proceed(requestBuilder.build())
        }

        // Интерцептор для мониторинга очереди (только для DEBUG)
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor { chain ->
                    val request = chain.request()
                    val startTime = System.currentTimeMillis()

                    try {
                        val response = chain.proceed(request)
                        val endTime = System.currentTimeMillis()
                        Timber.d("🔍 Request completed: ${request.url} in ${endTime - startTime}ms (${response.code})")
                        response
                    } catch (e: Exception) {
                        val endTime = System.currentTimeMillis()
                        Timber.w("🔍 Request failed: ${request.url} in ${endTime - startTime}ms - ${e.message}")
                        throw e
                    }
                }
            }
        }

//        // Подробное логирование только для отладки (отключить в релизе!)
//        .addInterceptor(HttpLoggingInterceptor().apply {
//            level = if (BuildConfig.DEBUG) {
//                HttpLoggingInterceptor.Level.HEADERS
//            } else {
//                HttpLoggingInterceptor.Level.NONE
//            }
//        })

        // Повторные попытки отключены (управляется очередью)
        .retryOnConnectionFailure(false)
        .build()
}