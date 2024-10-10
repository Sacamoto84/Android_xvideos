package com.client.xvideos.screens.item.util

import android.content.Context
import android.net.Uri
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun initPlayer(context: Context, passedString : String): Player {
    return ExoPlayer.Builder(context).build().apply {
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
        val uri = Uri.parse(passedString)
        val mediaSource = buildMediaSource(uri, defaultHttpDataSourceFactory, null)
        setMediaSource(mediaSource)
        playWhenReady = true
        prepare()
    }
}