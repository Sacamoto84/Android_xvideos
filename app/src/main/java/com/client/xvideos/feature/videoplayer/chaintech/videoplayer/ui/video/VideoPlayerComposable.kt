package com.client.xvideos.feature.videoplayer.chaintech.videoplayer.ui.video

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host.MediaPlayerHost
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.VideoPlayerConfig
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.util.LandscapeOrientation

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun VideoPlayerComposable(
    modifier: Modifier = Modifier, // Modifier for the composable
    playerHost: MediaPlayerHost,
    playerConfig: VideoPlayerConfig = VideoPlayerConfig(), // Configuration for the player
    newWersion : Boolean = true,
    onClick: () -> Unit = {}
) {
    LandscapeOrientation(
        enableFullEdgeToEdge = playerConfig.enableFullEdgeToEdge,
        isLandscape = playerHost.isFullScreen
    ) {


        if (newWersion){
            VideoPlayerWithControl2(
                modifier = if (playerHost.isFullScreen) {
                    Modifier.fillMaxSize()
                } else { modifier },
                playerHost = playerHost,
                playerConfig = playerConfig,
                onClick = onClick
            )
        }
        else {

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
}








