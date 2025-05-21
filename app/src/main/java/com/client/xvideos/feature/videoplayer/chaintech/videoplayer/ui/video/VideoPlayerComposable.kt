package chaintech.videoplayer.ui.video

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host.MediaPlayerHost
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.VideoPlayerConfig
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.ui.youtube.YouTubePlayerComposable
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.util.LandscapeOrientation
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.ui.video.VideoPlayerWithControl
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.util.extractYouTubeVideoId
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.util.isDesktop

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun VideoPlayerComposable(
    modifier: Modifier = Modifier, // Modifier for the composable
    playerHost: MediaPlayerHost,
    playerConfig: VideoPlayerConfig = VideoPlayerConfig() // Configuration for the player
) {
    val videoUrl = extractYouTubeVideoId(playerHost.url)
    if (videoUrl == null) {
        LandscapeOrientation(
            enableFullEdgeToEdge = playerConfig.enableFullEdgeToEdge,
            isLandscape = playerHost.isFullScreen
        ) {

                VideoPlayerWithControl(
                    modifier = if (playerHost.isFullScreen) {
                        Modifier.fillMaxSize()
                    } else {
                        modifier
                    },
                    playerHost = playerHost,
                    playerConfig = playerConfig
                )

        }
    } else {
        YouTubePlayerComposable(
            modifier = modifier,
            playerHost = playerHost,
            playerConfig = playerConfig
        )
    }
}








