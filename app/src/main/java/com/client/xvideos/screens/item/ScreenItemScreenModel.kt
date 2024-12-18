package com.client.xvideos.screens.item

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import cafe.adriel.voyager.navigator.Navigator
import com.client.xvideos.model.HTML5PlayerConfig
import com.client.xvideos.net.readHtmlFromURL
import com.client.xvideos.parcer.parseHTML5Player
import com.client.xvideos.parcer.parserItemVideo
import com.client.xvideos.screens.tags.ScreenTags
import com.client.xvideos.screens.item.model.TagsModel
import com.client.xvideos.parcer.parserItemVideoTags
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
import timber.log.Timber


@UnstableApi
class ScreenItemScreenModel @AssistedInject constructor(
    @Assisted val url: String,
    @ApplicationContext context: Context,
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(url: String): ScreenItemScreenModel
    }

    var passedString: String = ""

    val a: MutableState<HTML5PlayerConfig?> = mutableStateOf(HTML5PlayerConfig())

    //val mediaItem: MediaItem?

    var tags by mutableStateOf(TagsModel(emptyList(), emptyList(), emptyList()))

    init {
        runBlocking {

            val s = readHtmlFromURL(url)
            val script = parserItemVideo(s)
            a.value = script?.let { parseHTML5Player(it) }
            a

            //Получить список тегов
            tags = parserItemVideoTags(s)
            tags

            //mediaItem = a.value?.let { MediaItem.fromUri(it.videoHLS) }
            passedString = a.value?.videoHLS.toString()

//            if (mediaItem != null) {
//                playerM3.player.setMediaItem(mediaItem)
//            }
//            playerM3.player.prepare()
//            playerM3.player.play()

        }
    }


    /////////////////////////////////////////////////////////
    /**
     * ## Открыть экран с нужным тегом
     */
    fun openTag(tag: String, navigator: Navigator) {
        navigator.push(ScreenTags(tag))
    }
    /////////////////////////////////////////////////////////


    /**
     * Общая продолжительность видео
     */
    var totalDuration by mutableLongStateOf(0L)

    /**
     * Текущее время видео
     */
    var currentTime by mutableLongStateOf(0L)

    var bufferedPercentage by mutableIntStateOf(0)

    var isPlaying by mutableStateOf(false)

    var playerE by mutableStateOf<Player?>(null)

    var playbackState by  mutableIntStateOf(0)

    val trackSelector =  DefaultTrackSelector(context)

    data class FORMAT(
        val id: Int,
        val width: Int,
        val height: Int,
        val bitrate: Int,
        val isSelect: Boolean,
    )

    val listFormat = mutableStateListOf<FORMAT>()

    var quality by mutableIntStateOf(0)


    @OptIn(UnstableApi::class)
    fun switchTrack(player: Player, trackIndex: Int) {

        player.stop()
        player.seekTo(player.currentPosition)

        // Создаем TrackSelectionOverride для новой дорожки
        val trackGroup = player.currentTracks.groups[0].mediaTrackGroup
        val override = TrackSelectionOverride(trackGroup, listOf(trackIndex))

        player.trackSelectionParameters =
            player.trackSelectionParameters
                .buildUpon()
                .setOverrideForType(
                    override
                )
                .build()

        player.prepare()
        player.play()
        // }

        Timber.d("Switched to track: Track: $trackIndex")
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleItem {

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(ScreenItemScreenModel.Factory::class)
    abstract fun bindHiltDetailsScreenModelFactory(
        hiltDetailsScreenModelFactory: ScreenItemScreenModel.Factory,
    ): ScreenModelFactory

}





