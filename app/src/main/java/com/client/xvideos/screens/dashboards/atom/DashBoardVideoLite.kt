package com.client.xvideos.screens.dashboards.atom

import android.app.Activity
import android.net.Uri
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
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.client.xvideos.screens.item.deprecated.ComposableLifecycle
import com.client.xvideos.screens.item.deprecated.buildMediaSource
import timber.log.Timber


@OptIn(UnstableApi::class)
@Composable
fun DashBoardVideoLite(passedString: String) {

    val context = LocalContext.current
    val activity = LocalContext.current as Activity


    //val trackSelector =  DefaultTrackSelector(context)


    var player: Player? by remember {
        mutableStateOf(null)
    }

//    LaunchedEffect(Unit) {
//        player = ExoPlayer.Builder(context).build().apply {
//            val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
//            val uri = Uri.parse(passedString)
//            val mediaSource = buildMediaSource(uri, defaultHttpDataSourceFactory, null)
//            setMediaSource(mediaSource)
//            playWhenReady = true
//            prepare()
//        }
//    }

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
                            Uri.parse(passedString),
                            DefaultHttpDataSource.Factory(),
                            null
                        )
                    )
                    playWhenReady = true
                    prepare()

                    playerView.onResume()

                    //player?.volume = 0f
                    //player?.repeatMode = REPEAT_MODE_ONE
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


