package com.client.xvideos

import android.app.Application
import com.client.xvideos.feature.videoplayer.util.PlaybackPreference
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        PlaybackPreference.initialize(this)
    }

    companion object {
        lateinit var instance: App
            private set
    }

}

