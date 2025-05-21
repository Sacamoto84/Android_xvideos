package chaintech.videoplayer.ui.video

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host.MediaPlayerHost
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.VideoPlayerConfig
import chaintech.videoplayer.ui.youtube.YouTubePlayerComposable
import chaintech.videoplayer.util.LandscapeOrientation
import chaintech.videoplayer.util.extractYouTubeVideoId
import chaintech.videoplayer.util.isDesktop

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
            if (isDesktop()) {
                DesktopVideoPlayer(
                    modifier = if (playerHost.isFullScreen) {
                        Modifier.fillMaxSize()
                    } else {
                        modifier
                    },
                    playerHost = playerHost,
                    playerConfig = playerConfig
                )
            } else {
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
        }
    } else {
        YouTubePlayerComposable(
            modifier = modifier,
            playerHost = playerHost,
            playerConfig = playerConfig
        )
    }
}








