package com.client.xvideos

import android.app.Application
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.util.PlaybackPreference
import com.kdownloader.KDownloader
import dagger.hilt.android.HiltAndroidApp
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

fun allowAllSSL() {
    try {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
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
class App : Application() {

    lateinit var kDownloader: KDownloader

    override fun onCreate() {
        super.onCreate()
        instance = this

        allowAllSSL()

        PlaybackPreference.initialize(this)

        kDownloader = KDownloader.create(applicationContext)

//        val config = PRDownloaderConfig.newBuilder()
//            .setReadTimeout(30_000)
//            .setConnectTimeout(30_000)
//            .setDatabaseEnabled(true)
//            .build()
//        PRDownloader.initialize(this, config)
    }

    companion object {
        lateinit var instance: App
            private set
    }

}

