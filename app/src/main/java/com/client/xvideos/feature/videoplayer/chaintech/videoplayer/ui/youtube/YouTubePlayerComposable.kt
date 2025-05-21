package com.client.xvideos.feature.videoplayer.chaintech.videoplayer.ui.youtube

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host.MediaPlayerHost
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.VideoPlayerConfig

@Composable
fun YouTubePlayerComposable(
    modifier: Modifier = Modifier, // Modifier for the composable
    playerHost: MediaPlayerHost,
    playerConfig: VideoPlayerConfig = VideoPlayerConfig() // Configuration for the player
) {
//    LandscapeOrientation(
//        enableFullEdgeToEdge = playerConfig.enableFullEdgeToEdge,
//        isLandscape = playerHost.isFullScreen
//    ) {
//        if (isDesktop()) {
//            val videoUrl = extractYouTubeVideoId(playerHost.url) ?: playerHost.url
//            DesktopYoutubeComposable(
//                modifier = modifier,
//                videoId = videoUrl
//            )
//        } else {
//            YoutubePlayerWithControl(
//                modifier = if (playerHost.isFullScreen) {
//                    Modifier.fillMaxSize()
//                } else {
//                    modifier
//                },
//                playerHost = playerHost,
//                playerConfig = playerConfig
//            )
//        }
//    }
}