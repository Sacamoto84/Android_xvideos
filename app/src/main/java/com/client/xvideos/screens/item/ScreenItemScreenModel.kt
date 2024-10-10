package com.client.xvideos.screens.item

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
import android.net.Uri
import android.os.Build
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.SHOW_BUFFERING_WHEN_PLAYING
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import com.client.xvideos.model.HTML5PlayerConfig
import com.client.xvideos.net.readHtmlFromURL
import com.client.xvideos.parcer.parseHTML5Player
import com.client.xvideos.parcer.parserItemVideo
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.runBlocking
import androidx.media3.common.C
import com.client.xvideos.screens.item.util.ComposableLifecycle
import com.client.xvideos.screens.item.util.buildMediaSource
import com.client.xvideos.screens.item.util.createPlayerView
import com.client.xvideos.screens.item.util.initPlayer



class ScreenItemScreenModel @AssistedInject constructor(
    @Assisted val url: String,
    @ApplicationContext appContext: Context
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(url: String): ScreenItemScreenModel
    }

    var passedString: String = ""

    val a: MutableState<HTML5PlayerConfig?> = mutableStateOf(HTML5PlayerConfig())

    //val mediaItem: MediaItem?

    init {
        runBlocking {
            val s = readHtmlFromURL(url)
            val script = parserItemVideo(s)
            a.value = script?.let { parseHTML5Player(it) }
            a

            //mediaItem = a.value?.let { MediaItem.fromUri(it.videoHLS) }
            passedString = a.value?.videoHLS.toString()

//            if (mediaItem != null) {
//                playerM3.player.setMediaItem(mediaItem)
//            }
//            playerM3.player.prepare()
//            playerM3.player.play()

        }
    }







}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleItem {

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(ScreenItemScreenModel.Factory::class)
    abstract fun bindHiltDetailsScreenModelFactory(
        hiltDetailsScreenModelFactory: ScreenItemScreenModel.Factory
    ): ScreenModelFactory

}




