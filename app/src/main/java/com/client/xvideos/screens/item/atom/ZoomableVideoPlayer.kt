package com.client.xvideos.screens.item.atom

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import com.client.xvideos.noRippleClickable

import com.client.xvideos.screens.item.ScreenModel_Item
import com.client.xvideos.video.RepeatMode
import com.client.xvideos.video.VideoPlayer
import com.client.xvideos.video.controller.VideoPlayerControllerConfig
import com.client.xvideos.video.uri.VideoPlayerMediaItem
import timber.log.Timber
import java.util.concurrent.TimeUnit

@OptIn(UnstableApi::class)
@Composable
fun ZoomableVideoPlayer(
    vm: ScreenModel_Item,
    videoUri: String,
    modifier: Modifier = Modifier,
) {

    Timber.i("!!! ZoomableVideoPlayer url:$videoUri")

    //val activity = LocalContext.current as Activity
    //activity.requestedOrientation = SCREEN_ORIENTATION_PORTRAIT

    var once by remember { mutableStateOf(false) }

    var isDragging by remember { mutableStateOf(false) }
    var dragAmount by remember { mutableFloatStateOf(0f) } // Текущее смещение во время жеста

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        verticalArrangement = Arrangement.Bottom
    ) {

        VideoPlayer(
            onFullScreenExit = {
                Timber.i("!!! onFullScreenExit")
                vm.isFullScreen = false
            },
            onFullScreenEnter = {
                Timber.i("!!! onFullScreenEnter")
                vm.isFullScreen = true
            },
            defaultFullScreeen = vm.isFullScreen,
            trackSelector = vm.trackSelector,
            mediaItems = listOf(
                VideoPlayerMediaItem.NetworkMediaItem(
                    url = videoUri,
                    mediaMetadata = MediaMetadata.Builder().setTitle("Widevine HLS: Example")
                        .build(),
                    mimeType = MimeTypes.APPLICATION_M3U8,
                )
            ),
            handleLifecycle = true,
            autoPlay = true,
            usePlayerController = false,
            enablePip = false,
            handleAudioFocus = true,
            controllerConfig = VideoPlayerControllerConfig(
                showSpeedAndPitchOverlay = false,
                showSubtitleButton = false,
                showCurrentTimeAndTotalTime = true,
                showBufferingProgress = true,
                showForwardIncrementButton = true,
                showBackwardIncrementButton = true,
                showBackTrackButton = false,
                showNextTrackButton = false,
                showRepeatModeButton = true,
                controllerShowTimeMilliSeconds = 1_000,
                controllerAutoShow = true,
                showFullScreenButton = true,
            ),
            volume = 0.0f,  // volume 0.0f to 1.0f
            repeatMode = RepeatMode.NONE,       // or RepeatMode.ALL, RepeatMode.ONE
            onCurrentTimeChanged = { // long type, current player time (millisec)
                //Timber.tag("CurrentTime").e(it.toString())
                //currentTime = it
            },
            playerInstance = { // ExoPlayer instance (Experimental)

                vm.playerE = this

                addListener(

                    object : Player.Listener {

                        override fun onTracksChanged(tracks: Tracks) {
                            // Update UI using current tracks.
                            if (tracks.groups.size == 0) return

                            Timber.i("!!! onTracksChanged " + tracks.groups[0])

                            vm.listFormat.clear()

                            val group = tracks.groups[0]
                            for (j in 0 until group.length) {
                                val format = group.getTrackFormat(j)

                                vm.listFormat.add(
                                    ScreenModel_Item.FORMAT(
                                        id = j,
                                        width = format.width,
                                        height = format.height,
                                        bitrate = format.bitrate,
                                        isSelect = group.isTrackSelected(j)
                                    )
                                )
                                Timber.d("!!! Group: 0, Format: $j, Resolution: ${format.width}x${format.height}, Bitrate: ${format.bitrate}")
                            }
                            vm.listFormat.sortBy { it.height }

                            if (!once) {
                                vm.quality = vm.listFormat.last().height
                                once = true
                            }

                        }


                    }



                )

                addAnalyticsListener(
                    object : AnalyticsListener {





                        @OptIn(UnstableApi::class)
                        override fun onEvents(player: Player, events: AnalyticsListener.Events) {
                            super.onEvents(player, events)
                            vm.totalDuration = player.duration.coerceAtLeast(0L)
                            vm.currentTime = player.currentPosition.coerceAtLeast(0L)
                            vm.bufferedPercentage = player.bufferedPercentage
                            vm.isPlaying = player.isPlaying
                            vm.playbackState = player.playbackState

//                            if (vm.playerE == null) {
//                                vm.playerE = player
//                            }

                            // If no video or image track: show shutter, hide image view.
                            // Otherwise: do nothing to wait for first frame or image.

                            if (events.contains(AnalyticsListener.EVENT_RENDERED_FIRST_FRAME)) {
                                // Hide shutter, hide image view.
                            }
                        }



                    }

                )
            },
            modifier = Modifier
                .weight(1f)
//                    .graphicsLayer(
//                        scaleX = scale,
//                        scaleY = scale,
//                        translationX = offset.x,
//                        translationY = offset.y
//                    )
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            isDragging = true
                            dragAmount = 0f
                        },
                        onDragCancel = {
                            isDragging = false
                            dragAmount = 0f
                        },
                        onDragEnd = {
                            isDragging = false
                            if (dragAmount != 0f) {
                                val seekOffsetMs =
                                    (dragAmount / 100).toLong() * 1000 // Перевод пикселей в миллисекунды
                                if (vm.playerE != null) {
                                    val newPosition =
                                        (vm.playerE!!.currentPosition + seekOffsetMs).coerceIn(
                                            0,
                                            vm.playerE!!.duration
                                        )
                                    vm.playerE!!.seekTo(newPosition)
                                }
                            }
                        },

                        onHorizontalDrag = { change, dragAmount1 ->
                            change.consume() // Сообщаем системе, что жест обработан
                            dragAmount += dragAmount1
                        }

                    )
                }
                .noRippleClickable { if (vm.isPlaying) vm.playerE?.pause() else vm.playerE?.play() }
        )



        if(!vm.isFullScreen)
        Box(modifier = Modifier) {
            //Блок кнопок
            ItemPlayerBottomControl(
                modifier = Modifier,
                bufferedPercentage = { vm.bufferedPercentage },
                currentTime = { vm.currentTime },
                onSeekChanged = { timeMs: Float ->
                    //playerE?.pause()
                    vm.currentTime = timeMs.toLong()
                    Timber.i("!!! onSeekChanged  ${timeMs.toLong()}")
                },
                onValueChangedFinished = {
                    Timber.i("!!! onValueChangedFinished ${it.toLong()}")
                    vm.playerE?.seekTo(it.toLong())
                    //playerE?.playWhenReady = true
                },

                isPlaying = { vm.isPlaying },
                onPlayClick = { if (vm.isPlaying) vm.playerE?.pause() else vm.playerE?.play() },
                vm = vm,
            )
        }





    }


}

@SuppressLint("DefaultLocale")
fun Long.formatMinSec(): String {
    return if (this == 0L) {
        "..."
    } else {
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(this),
            TimeUnit.MILLISECONDS.toSeconds(this) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(this)
                    )
        )
    }
}