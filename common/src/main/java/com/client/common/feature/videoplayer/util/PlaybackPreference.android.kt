package com.client.common.feature.videoplayer.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.client.common.feature.videoplayer.host.MediaPlayerHost
import com.client.common.feature.videoplayer.model.VideoPlayerConfig
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.security.MessageDigest

private val Context.dataStore by preferencesDataStore(name = "playback_prefs")

class PlaybackPreference(private val context: Context) {

    fun savePlaybackPosition(videoUrl: String, position: Float) {
        val key = getHashedKey(videoUrl)
        runBlocking {
            try {
                context.dataStore.edit { prefs ->
                    prefs[floatPreferencesKey(key)] = position
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log the error
            }
        }
    }

    fun getPlaybackPosition(videoUrl: String): Float {
        val key = getHashedKey(videoUrl)
        return runBlocking {
            try {
                context.dataStore.data.firstOrNull()?.get(floatPreferencesKey(key)) ?: 0f
            } catch (e: Exception) {
                e.printStackTrace() // Log the error
                0f // Return default value to prevent crashes
            }
        }
    }

    companion object {
        private var instance: PlaybackPreference? = null

        fun getInstance(): PlaybackPreference? {
            return instance
        }

        fun initialize(context: Context) {
            instance = PlaybackPreference(context.applicationContext)
        }
    }

    private fun getHashedKey(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hashBytes = md.digest(input.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }.take(32) // Limit to 32 chars
    }
}

internal fun saveCurrentPosition(host: MediaPlayerHost, url: String) {
    val currentTime = host.currentTime
    val totalTime = host.totalTime
    val threshold = when {
        totalTime >= 3600 -> 0.95
        totalTime <= 60 -> 0.85
        else -> 0.9
    }

    if (currentTime > 0 && currentTime < (totalTime * threshold)) {
        PlaybackPreference.getInstance()
            ?.savePlaybackPosition(url, currentTime.toFloat())
    }
}

internal fun getSeekTime(host: MediaPlayerHost, config: VideoPlayerConfig): Float? {
    if (host.totalTime > 0 && !config.isLiveStream) {
        if(host.playFromTime != null) {
            val time = host.playFromTime
            host.playFromTime = null
            PlaybackPreference.getInstance()?.savePlaybackPosition(host.url, 0f)
            return time
        } else {
            if(config.enableResumePlayback) {
                val lastTime =
                    PlaybackPreference.getInstance()?.getPlaybackPosition(host.url)
                lastTime?.let {
                    PlaybackPreference.getInstance()?.savePlaybackPosition(host.url, 0f)
                    if (lastTime > 0) {
                        return lastTime
                    }
                }
            }
        }
    }
    return null
}