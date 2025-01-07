package com.client.xvideos.screens.item

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MOVIE
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import cafe.adriel.voyager.navigator.Navigator
import com.client.xvideos.model.HTML5PlayerConfig
import com.client.xvideos.net.readHtmlFromURL
import com.client.xvideos.parcer.parseHTML5Player
import com.client.xvideos.parcer.parserItemVideo
import com.client.xvideos.parcer.parserItemVideoTags
import com.client.xvideos.screens.item.model.TagsModel
import com.client.xvideos.screens.itemFullScreen.ScreenFullItem
import com.client.xvideos.screens.tags.ScreenTags
import com.client.xvideos.screens.item.video.cache.VideoPlayerCacheManager
import com.client.xvideos.screens.item.video.controller.VideoPlayerControllerConfig
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
class ScreenModel_Item @AssistedInject constructor(
    @Assisted val url: String,
    @ApplicationContext context: Context,
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(url: String): ScreenModel_Item
    }

    init {
        Timber.e("!!! ScreenModel_Item init()")
    }

    override fun onDispose() {
        super.onDispose()
        Timber.e("!!! ScreenModel_Item onDispose")
    }

    var playerE by mutableStateOf<Player?>(null)

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

            passedString = a.value?.videoHLS.toString()

            playerE = null

        }
    }

    var isFullScreen by mutableStateOf(false)


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


    var playbackState by mutableIntStateOf(0)

    val trackSelector = DefaultTrackSelector(context)

    data class FORMAT(
        val id: Int,
        val width: Int,
        val height: Int,
        val bitrate: Int,
        val isSelect: Boolean,
    )

    val listFormat = mutableStateListOf<FORMAT>()

    var quality by mutableIntStateOf(0)




    /**
     * Скорость воспроизведения
     */
    var speed by mutableFloatStateOf(1.0f)

    ///////////////////////////////////////////////
    /**
     * ## Изменить номер дорожки
     */
    @OptIn(UnstableApi::class)
    fun switchTrack(trackIndex: Int) {
        if (playerE == null) return

        val player = playerE!!

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
    ///////////////////////////////////////////////
    /**
     * ## Изменение скорости воспроизведения
     */
    fun changePlaybackSpeed(speed: Float) {
        if (playerE == null) return
        val player = playerE!!
        val params = PlaybackParameters(speed) // Создаем параметры с новой скоростью
        player.playbackParameters = params
        Timber.d("Playback speed changed to $speed")
        this.speed = speed
    }

    ///////////////////////////////////////////////
    //Открыть плее в полном окне
    fun openFullScreen(navigator: Navigator) {
        if (playerE == null) return
        navigator.push(ScreenFullItem(url))
    }





    // Блок соотношения сторон
    private var currentAspectRatios = 0
    private val aspectRatios = listOf(
                            AspectRatioFrameLayout.RESIZE_MODE_FIT,
                            AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH,
                            AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT,
                            AspectRatioFrameLayout.RESIZE_MODE_FILL,
                            AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        )

    fun aspectRatiosClick():Int{
        currentAspectRatios = (currentAspectRatios + 1) % aspectRatios.size
        return aspectRatios[currentAspectRatios]
    }


}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleItem {

    @OptIn(UnstableApi::class)
    @Binds
    @IntoMap
    @ScreenModelFactoryKey(ScreenModel_Item.Factory::class)
    abstract fun bindHiltDetailsScreenModelFactory(
        hiltDetailsScreenModelFactory: ScreenModel_Item.Factory,
    ): ScreenModelFactory

}





