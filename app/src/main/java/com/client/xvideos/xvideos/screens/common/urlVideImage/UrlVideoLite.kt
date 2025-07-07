package com.client.xvideos.xvideos.screens.common.urlVideImage

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.client.xvideos.xvideos.screens.videoplayer.deprecated.ComposableLifecycle
import com.client.xvideos.xvideos.screens.videoplayer.deprecated.buildMediaSource
import timber.log.Timber
import androidx.core.net.toUri

/**
 * Показ видео по кругу по url
 */
@OptIn(UnstableApi::class)
@Composable
fun UrlVideoLite(url: String) {

    val context = LocalContext.current

    var player: Player? by remember {
        mutableStateOf(null)
    }

    val playerView = remember {
        PlayerView(context).apply {
            this.player = player
            controllerAutoShow = false
            hideController()
            keepScreenOn = true
            setUseController(false)
        }
    }

    playerView.player = player

    DisposableEffect(Unit) {
        onDispose {
            Timber.i("!!! DisposableEffect")
            playerView.player = null
            player?.release()
            player = null
        }
    }

    ComposableLifecycle { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {

                player = ExoPlayer.Builder(context).build().apply {
                    setMediaSource(
                        buildMediaSource(
                            url.toUri(),
                            DefaultHttpDataSource.Factory(),
                            null
                        )
                    )

                    repeatMode = REPEAT_MODE_ALL
                    volume = 0f
                    playWhenReady = true
                    prepare()
                    playerView.onResume()

                    //player?.volume = 0f

                    //player?.prepare()
                    //player?.play()
                }
            }

            Lifecycle.Event.ON_RESUME -> {
            }

            Lifecycle.Event.ON_PAUSE -> {
            }

            Lifecycle.Event.ON_STOP -> {
                playerView.apply {
                    player?.release()
                    onPause()
                    player = null
                }
            }

            else -> {}
        }
    }

    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .background(Color.Black),
        factory = { playerView }
    )

}


