package chaintech.videoplayer.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host.DrmConfig
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host.MediaPlayerError
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.PlayerSpeed
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.ScreenResize

@Composable
internal expect fun CMPPlayer(
    modifier: Modifier,
    url: String,
    isPause: Boolean,
    totalTime: ((Int) -> Unit),
    currentTime: ((Int) -> Unit),
    isSliding: Boolean,
    seekToTime: Float?,
    speed: PlayerSpeed,
    size: ScreenResize,
    bufferCallback: ((Boolean) -> Unit),
    didEndVideo: (() -> Unit),
    loop: Boolean,
    volume: Float,
    isLiveStream: Boolean,
    error: (MediaPlayerError) -> Unit,
    headers: Map<String, String>?,
    drmConfig: DrmConfig?,
    selectedQuality: VideoQuality?,
    selectedAudioTrack: AudioTrack?,
    selectedSubTitle: SubtitleTrack?
)


