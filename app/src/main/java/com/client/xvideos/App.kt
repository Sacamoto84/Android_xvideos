package com.client.xvideos

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import androidx.compose.runtime.ExperimentalComposeRuntimeApi
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.client.xvideos.common.coil.CoilImageLoaderFactory
import com.client.xvideos.common.eventBus.Event
import com.client.xvideos.common.eventBus.EventBus
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.common.traficStatistic.NetworkTrafficMonitor
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

fun allowAllSSL() {
    try {
        val trustAllCerts = arrayOf<TrustManager>(
            @SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, SecureRandom())

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
        HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@HiltAndroidApp
class App : Application(), SingletonImageLoader.Factory {

    //val ksafe = KSafe(applicationContext, lazyLoad = true)

    override fun newImageLoader(context: Context): ImageLoader {
        return CoilImageLoaderFactory.getImageLoader(this)
    }

    // Сохраняем оригинальный обработчик
    private var originalHandler: Thread.UncaughtExceptionHandler? = null

    lateinit var networkTrafficMonitor: NetworkTrafficMonitor
        private set

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @OptIn(DelicateCoroutinesApi::class, ExperimentalComposeRuntimeApi::class)
    override fun onCreate() {
        super.onCreate()

        instance = this

        if (BuildConfig.DEBUG) Timber.plant(DebugTree())

        // Инициализируем монитор трафика
        networkTrafficMonitor = NetworkTrafficMonitor()
        networkTrafficMonitor.startMonitoring()

        // Сохраняем оригинальный обработчик ПЕРЕД установкой нашего
        originalHandler = Thread.getDefaultUncaughtExceptionHandler()

        // Устанавливаем наш обработчик исключений
//        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
//            try {
//                Timber.e(throwable, "🚨 UNCAUGHT EXCEPTION in thread: ${thread.name}")
//
//                // Логируем код нашего приложения
//                val ourCodeElements = throwable.stackTrace
//                    .filter { it.className.contains("com.client.xvideos") }
//
//                if (ourCodeElements.isNotEmpty()) {
//                    Timber.e("📍 Your code locations:")
//                    ourCodeElements.forEach { element ->
//                        Timber.e("   ${element.className}.${element.methodName}:${element.lineNumber}")
//                    }
//                } else {
//                    Timber.e("📍 No code from our app found in stack trace")
//                }
//
//                // Логируем suppressed exceptions
//                throwable.suppressedExceptions.forEach { suppressed ->
//                    Timber.e(suppressed, "🔗 Suppressed exception:")
//                }
//
//                // Логируем цепочку причин
//                var cause = throwable.cause
//                var level = 1
//                while (cause != null) {
//                    Timber.e(cause, "🔗 Caused by (level $level):")
//
//                    // Ищем наш код в причине
//                    cause.stackTrace
//                        .filter { it.className.contains("com.client.xvideos") }
//                        .forEach { element ->
//                            Timber.e("   📍 In cause: ${element.className}.${element.methodName}:${element.lineNumber}")
//                        }
//
//                    cause = cause.cause
//                    level++
//
//                    // Защита от бесконечных циклов
//                    if (level > 10) break
//                }
//
//            } catch (loggingException: Exception) {
//                // Если логирование падает, выводим в System.err
//                System.err.println("Failed to log exception: $loggingException")
//                loggingException.printStackTrace()
//            } finally {
//                // Всегда вызываем оригинальный обработчик
//                originalHandler?.uncaughtException(thread, throwable)
//            }
//        }

        // Настроить SLF4J для использования Timber
        // Настроить SLF4J для использования Timber
        //System.setProperty("slf4j.provider", "com.arcao.slf4j.timber.TimberLoggerProvider")

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            allowAllSSL()
        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        Settings.init(prefs)

//        val loggingInterceptor = Interceptor { chain ->
//            val request = chain.request()
//            val startTime = System.currentTimeMillis()
//
//            // Получаем информацию о том, откуда вызван запрос
//            val callerInfo = Thread.currentThread().stackTrace
//                .drop(2) // пропускаем первые системные вызовы
//                .firstOrNull { it.className.contains("com.client.xvideos") }
//                ?.let { "${it.className}.${it.methodName}:${it.lineNumber}" }
//                ?: "Unknown caller"
//
//            Log.d("OkHttp", "🌐 REQUEST: ${request.method} ${request.url}")
//            Log.d("OkHttp", "📱 Called from: $callerInfo")
//            Log.d("OkHttp", "📋 Headers: ${request.headers}")
//
//            try {
//                val response = chain.proceed(request)
//                val endTime = System.currentTimeMillis()
//                val duration = endTime - startTime
//
//                Log.d("OkHttp", "✅ RESPONSE: ${response.code} ${response.message} (${duration}ms)")
//                response
//
//            } catch (e: Exception) {
//                val endTime = System.currentTimeMillis()
//                val duration = endTime - startTime
//
//                Log.e("OkHttp", "❌ REQUEST FAILED after ${duration}ms")
//                Log.e("OkHttp", "📱 Called from: $callerInfo")
//                Log.e("OkHttp", "🔍 URL: ${request.url}")
//                Log.e("OkHttp", "💥 Exception: ${e.javaClass.simpleName}: ${e.message}")
//
//                throw e
//            }
//        }

        // Enable only for debug flavor to avoid perf regressions in release
        //Composer.setDiagnosticStackTraceEnabled(BuildConfig.DEBUG)


        if (PermissionScreenActivity.PermissionStorage.hasPermissions(this)) {
//
////            val savedRed = savedRed.get()
//
////            savedRed.refreshTagList()
////
////            blockRed.get().refresh()
//
////            savedRed.likes.refresh()
////            savedRed.niches.refresh()
////            savedRed.creators.refresh()
////            savedRed.collections.refreshCollectionList()
//
//            GlobalScope.launch {
////                clearOldCache(redGifsDb.get().cacheMediaResponseDao())
//                dbL.get().postJsonRamDao().deleteAll()
//            }
//
        }


        scope.launch {
            EventBus.events.collect { event ->
                if (event is Event.Log) {
                    //saveLogToFile(event.message)
                }
            }
        }

    }


    override fun onTerminate() {
        super.onTerminate()
        networkTrafficMonitor.destroy()
    }

    companion object {
        lateinit var instance: App
            private set
    }

}

