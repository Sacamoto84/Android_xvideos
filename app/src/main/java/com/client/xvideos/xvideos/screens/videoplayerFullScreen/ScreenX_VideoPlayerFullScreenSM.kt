package com.client.xvideos.xvideos.screens.videoplayerFullScreen

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
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import cafe.adriel.voyager.navigator.Navigator
import com.client.xvideos.common.room.AppDatabase
import com.client.xvideos.common.room.entity.CacheUrlStringRamEntity
import com.client.xvideos.xvideos.feature.net.readHtmlFromURLDirect
import com.client.xvideos.xvideos.model.HTML5PlayerConfig
import com.client.xvideos.xvideos.parcer.parseHTML5Player
import com.client.xvideos.xvideos.parcer.parserItemVideo
import com.client.xvideos.xvideos.screens.videoplayer.FORMAT
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
class ScreenX_VideoPlayerFullScreenSM @AssistedInject constructor(
    @Assisted val url: String,
    @ApplicationContext context: Context,
    val db : AppDatabase
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory { fun create(url: String): ScreenX_VideoPlayerFullScreenSM }

    override fun onDispose() {
        super.onDispose()
        Timber.e("!!! ScreenX_VideoPlayerFullScreenSM onDispose")
    }

    var playerE by mutableStateOf<Player?>(null)

    var passedString: String = ""

    val a: MutableState<HTML5PlayerConfig?> = mutableStateOf(HTML5PlayerConfig())

    init {
        runBlocking {
            Timber.e("!!! ScreenVideoPlayerSM init()")

            val res = db.cacheUrlStringRamDao().get(url)

            val s = if (res == null) {
                val content = readHtmlFromURLDirect(url)
                db.cacheUrlStringRamDao().insert(CacheUrlStringRamEntity(
                    url = url,
                    content = content
                ))
                content
            }
            else
                res.content

            val script = parserItemVideo(s)
            a.value = script?.let { parseHTML5Player(it) }
            passedString = a.value?.videoHLS.toString()
            playerE = null
        }
    }


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

    //val trackSelector = DefaultTrackSelector(context)

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
    @ScreenModelFactoryKey(ScreenX_VideoPlayerFullScreenSM.Factory::class)
    abstract fun bindHiltDetailsScreenModelFactory(
        hiltDetailsScreenModelFactory: ScreenX_VideoPlayerFullScreenSM.Factory,
    ): ScreenModelFactory

}





