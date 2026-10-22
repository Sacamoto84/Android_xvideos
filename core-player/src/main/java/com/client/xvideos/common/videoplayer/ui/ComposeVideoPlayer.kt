package com.client.xvideos.common.videoplayer.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.client.xvideos.common.videoplayer.host.MediaPlayerHost
import com.client.xvideos.common.videoplayer.util.CMPPlayer2

/**
 * Плеер, управляемый через [MediaPlayerHost]: состояние (пауза, позиция, громкость,
 * скорость) читается из хоста, события плеера пишутся обратно в него.
 *
 * @param onTap одиночный тап по видео.
 * @param overlay элементы поверх видео (панель управления, теги и т.п.).
 */
@Composable
fun ComposeVideoPlayer(
    playerHost: MediaPlayerHost,
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {},
    overlay: @Composable BoxScope.() -> Unit = {},
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(onTap) { detectTapGestures(onTap = { onTap() }) }
        ) {
            CMPPlayer2(
                modifier = Modifier.fillMaxSize(),
                url = playerHost.url,
                isPause = playerHost.isPaused,
                totalTime = { playerHost.updateTotalTime(it) },
                currentTime = {
                    if (!playerHost.isSliding) {
                        playerHost.updateCurrentTime(it)
                        playerHost.seekToTime = null
                    }
                },
                isSliding = playerHost.isSliding,
                seekToTime = playerHost.seekToTime,
                speed = playerHost.speed,
                size = playerHost.videoFitMode,
                bufferCallback = { playerHost.setBufferingStatus(it) },
                didEndVideo = {
                    playerHost.triggerMediaEnd()
                    if (!playerHost.isLooping) playerHost.pause()
                },
                loop = playerHost.isLooping,
                volume = playerHost.volumeLevel,
                isLiveStream = false,
                error = { playerHost.triggerError(it) },
                headers = playerHost.headers,
                drmConfig = playerHost.drmConfig,
                selectedQuality = playerHost.selectedQuality,
                autoRotate = false,
                poster = { playerHost.poster = it }
            )
        }
        overlay()
    }
}
