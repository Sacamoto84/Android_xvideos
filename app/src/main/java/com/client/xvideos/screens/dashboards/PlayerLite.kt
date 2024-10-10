package com.client.xvideos.screens.dashboards

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.SHOW_BUFFERING_WHEN_PLAYING
import com.client.xvideos.screens.item.util.buildMediaSource

@OptIn(UnstableApi::class)
@Composable
fun PlayerLite(passedString: String) {
    val context = LocalContext.current

    var player: Player? by remember {
        mutableStateOf(null)
    }

    val playerView = remember {
        PlayerView(context).apply {
            this.player = player
        }
    }

    DisposableEffect(key1 = player) {
        playerView.player = player
        onDispose {
            playerView.player = null
            //player = null
        }
    }

    playerView.keepScreenOn = true
    playerView.setShowBuffering(SHOW_BUFFERING_WHEN_PLAYING)

    player = ExoPlayer.Builder(context).build().apply {
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
        val uri = Uri.parse(passedString)
        val mediaSource = buildMediaSource(uri, defaultHttpDataSourceFactory, null)
        setMediaSource(mediaSource)
        playWhenReady = true
        prepare()
    }

    // Установка режима повтора
    player!!.repeatMode = Player.REPEAT_MODE_ALL

    //player!!.play()

    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .height(128.dp)
        .background(Color.Cyan),
        factory = { playerView }
    )

}