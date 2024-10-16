package com.client.xvideos.screens.item

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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





