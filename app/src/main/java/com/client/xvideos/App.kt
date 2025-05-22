package com.client.xvideos

import android.app.Application
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import com.client.xvideos.PermissionScreenActivity.PermissionStorage
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.util.PlaybackPreference
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        PlaybackPreference.initialize(this)
        val config = PRDownloaderConfig.newBuilder()
            .setReadTimeout(30_000)
            .setConnectTimeout(30_000)
            .setDatabaseEnabled(true)
            .build()
        PRDownloader.initialize(this, config)
    }

    companion object {
        lateinit var instance: App
            private set
    }

}

