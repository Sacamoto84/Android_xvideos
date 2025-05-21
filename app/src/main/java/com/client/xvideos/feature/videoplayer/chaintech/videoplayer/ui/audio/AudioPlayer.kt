package com.client.xvideos.feature.videoplayer.chaintech.videoplayer.ui.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.util.CMPAudioPlayer

@Composable
fun AudioPlayer(
    playerHost: MediaPlayerHost
) {
    LaunchedEffect(playerHost.totalTime) {
        if (playerHost.totalTime > 0 ) {
            playerHost.playFromTime?.let {
                playerHost.isSliding = true
                playerHost.seekToTime = it
                playerHost.isSliding = false
                playerHost.playFromTime = null
            }
        }
    }

    CMPAudioPlayer(
        modifier = Modifier,
        url = playerHost.url,
        isPause = playerHost.isPaused,
        totalTime = { playerHost.updateTotalTime(it)}, // Update total time of the audio
        currentTime = {
            if (!playerHost.isSliding) {
                playerHost.updateCurrentTime(it)
                playerHost.seekToTime = null
            }
        },
        isSliding = playerHost.isSliding, // Pass seek bar sliding state
        seekToTime = playerHost.seekToTime, // Pass seek bar slider time
        loop = playerHost.isLooping,
        loadingState = { playerHost.setBufferingStatus(it) },
        speed = playerHost.speed,
        volume = playerHost.volumeLevel,
        didEndAudio = {
            playerHost.triggerMediaEnd()
        },
        error = { playerHost.triggerError(it) }
    )
}