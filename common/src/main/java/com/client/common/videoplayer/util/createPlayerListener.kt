package com.client.common.videoplayer.util

import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.client.common.videoplayer.host.MediaPlayerError
import java.util.concurrent.TimeUnit

internal fun createPlayerListener(
    isSliding: Boolean,
    totalTime: (Int) -> Unit,
    currentTime: (Float) -> Unit,
    loadingState: (Boolean) -> Unit,
    didEndVideo: () -> Unit,
    loop: Boolean,
    exoPlayer: ExoPlayer,
    error: (MediaPlayerError) -> Unit,
    poster: (Boolean) -> Unit
): Player.Listener {

    return object : Player.Listener {

        override fun onRenderedFirstFrame() {
            poster(false) // всё, можно скрыть заглушку
        }

        //
        override fun onEvents(player: Player, events: Player.Events) {
            if (!isSliding) {
                totalTime(
                    TimeUnit.MILLISECONDS.toSeconds(player.duration).coerceAtLeast(0L).toInt()
                )
                currentTime( TimeUnit.MILLISECONDS.toSeconds(player.currentPosition).coerceAtLeast(0L).toFloat() )
            }
        }

        //
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    loadingState(true)
                }

                Player.STATE_READY -> {
                    loadingState(false)
                    //stateReady(true)
                }

                Player.STATE_ENDED -> {
                    loadingState(false)
                    didEndVideo()
                    exoPlayer.seekTo(0)
                    if (loop) exoPlayer.play()
                }

                Player.STATE_IDLE -> {
                    loadingState(false)
                }
            }
        }

        //
        override fun onPlayerError(error: PlaybackException) {
            error(MediaPlayerError.PlaybackError(error.message ?: "Unknown playback error"))
        }

    }
}