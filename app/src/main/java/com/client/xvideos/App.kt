package com.client.xvideos

import android.app.Application
import com.client.xvideos.feature.room.AppDatabase
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.util.PlaybackPreference
import com.redgifs.common.block.BlockRed
import com.redgifs.common.saved.SavedRed
import com.kdownloader.KDownloader
import com.redgifs.db.AppRedGifsDatabase
import com.redgifs.db.dao.clearOldCache
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

fun allowAllSSL() {
    try {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {
            }

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
class App : Application() {

    //lateinit var kDownloader: KDownloader

    @Inject
    lateinit var db: AppDatabase

    @Inject
    lateinit var redGifsDb: AppRedGifsDatabase

    @Inject
    lateinit var blockRed: BlockRed

    @Inject
    lateinit var savedRed: SavedRed

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        instance = this
        //if (BuildConfig.DEBUG)
            Timber.plant(DebugTree())
        //allowAllSSL()
        PlaybackPreference.initialize(this)
        //kDownloader = KDownloader.create(applicationContext)

        savedRed.refreshTagList()


        //blockRed.refreshBlockList()

        savedRed.likes.refresh()
        savedRed.niches.refresh()
        savedRed.creators.refresh()
        savedRed.collections.refreshCollectionList()

        GlobalScope.launch {
            clearOldCache( redGifsDb.cacheMediaResponseDao())
        }

    }

    companion object {
        lateinit var instance: App
            private set
    }

}

