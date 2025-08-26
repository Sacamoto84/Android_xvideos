package com.client.xvideos

import android.annotation.SuppressLint
import android.app.Application
import android.preference.PreferenceManager
import android.util.Log
import com.client.common.AppPath
import com.client.common.sharedPref.Settings
import com.client.xvideos.PermissionScreenActivity.PermissionStorage
import com.client.xvideos.l.db.AppLDatabase
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.Database
import com.couchbase.lite.DatabaseConfiguration
import com.couchbase.lite.DatabaseConfigurationFactory
import com.couchbase.lite.Collection
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.newConfig
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import com.redgifs.common.block.BlockRed
import com.redgifs.common.saved.SavedRed
import com.redgifs.db.AppRedGifsDatabase
import com.redgifs.db.dao.clearOldCache
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
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
class App : Application() {

    @Inject
    lateinit var redGifsDb: javax.inject.Provider<AppRedGifsDatabase>

    @Inject
    lateinit var blockRed: javax.inject.Provider<BlockRed>

    @Inject
    lateinit var savedRed: javax.inject.Provider<SavedRed>

    @Inject
    lateinit var dbL: javax.inject.Provider<AppLDatabase>


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        instance = this
        if (BuildConfig.DEBUG)
            Timber.plant(DebugTree())

//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
//            allowAllSSL()
//        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        Settings.init(prefs)

        val pipelineConfig =
            OkHttpImagePipelineConfigFactory
                .newBuilder(this, OkHttpClient.Builder().build())
                .setDiskCacheEnabled(true)
                .setDownsampleEnabled(true)
                .setResizeAndRotateEnabledForNetwork(true)
                .build()

        Fresco.initialize(this, pipelineConfig)

        if (PermissionStorage.hasPermissions(this)) {

            val savedRed = savedRed.get()

            savedRed.refreshTagList()

            blockRed.get().refresh()

            savedRed.likes.refresh()
            savedRed.niches.refresh()
            savedRed.creators.refresh()
            savedRed.collections.refreshCollectionList()

            GlobalScope.launch {
                clearOldCache(redGifsDb.get().cacheMediaResponseDao())
                dbL.get().postJsonRamDao().deleteAll()
            }


            CouchbaseLite.init(this)
            Log.i("TAG", "!!! CBL Initialized")
            val db = Database(
                "getting-started", DatabaseConfigurationFactory.newConfig(
                    databasePath = AppPath.albums_l,
                    fullSync = false
                )
            )
            // create the collection "Verlaine" in the default scope ("_default")
            var collection1: Collection? = db.createCollection("Verlaine")

            collection1 = db.getCollection("Verlaine")


            val mutableDocument = MutableDocument()
                .setFloat("version", 2.0f)
                .setString("language", "Java")
            collection1?.save(mutableDocument)


//            collection1 = db.defaultScope.getCollection("Verlaine")
//
//// create the collection "Verlaine" in the scope "Television"
//            var collection2: Collection? = db.createCollection("Television", "Verlaine")
//// both of these retrieve  collection2 created above
//            collection2 = db.getCollection("Television", "Verlaine")
//            collection2 = db.getScope("Television")!!.getCollection("Verlaine")

        }


    }


    companion object {
        lateinit var instance: App
            private set
    }

}

