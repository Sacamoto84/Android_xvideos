package com.client.xvideos

import android.app.Application
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.util.PlaybackPreference
import com.kdownloader.KDownloader
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App : Application() {

    lateinit var kDownloader: KDownloader

    override fun onCreate() {
        super.onCreate()
        instance = this
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

