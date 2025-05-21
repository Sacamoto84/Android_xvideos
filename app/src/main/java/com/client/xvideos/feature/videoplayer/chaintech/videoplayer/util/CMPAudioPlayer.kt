package chaintech.videoplayer.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host.MediaPlayerError
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.PlayerSpeed

@Composable
internal expect fun CMPAudioPlayer(
    modifier: Modifier,
    url: String,
    isPause: Boolean,
    totalTime: ((Int) -> Unit),
    currentTime: ((Int) -> Unit),
    isSliding: Boolean,
    seekToTime: Float?,
    loop: Boolean,
    loadingState: (Boolean) -> Unit,
    speed: PlayerSpeed,
    volume: Float,
    didEndAudio: () -> Unit,
    error: (MediaPlayerError) -> Unit
)