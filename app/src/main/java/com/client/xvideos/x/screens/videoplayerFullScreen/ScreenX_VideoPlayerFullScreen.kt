package com.client.xvideos.x.screens.videoplayerFullScreen

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.common.util.UnstableApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.R
import com.client.xvideos.common.eventBus.Event
import com.client.xvideos.common.eventBus.EventBus
import com.client.xvideos.common.videoplayer.host.MediaPlayerHost
import com.client.xvideos.common.videoplayer.ui.ComposeVideoPlayer
import com.client.xvideos.x.screens.videoplayer.atom.X_PlayerBottomBar

class ScreenX_VideoPlayerFullScreen(val url: String, val position: Long = -1L) : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(UnstableApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = getScreenModel<ScreenX_VideoPlayerFullScreenSM, ScreenX_VideoPlayerFullScreenSM.Factory> { factory ->
            factory.create(url, position)
        }

        if (vm.passedString == "") {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val host = remember(vm.passedString) {
                MediaPlayerHost(
                    mediaUrl = vm.passedString,
                    isLooping = false,
                )
            }

            // rememberExoPlayerWithLifecycle стартует с 0 → стартовую позицию
            // выставляем сами, когда медиа готово (totalTime > 0).
            var seekedOnce by remember { mutableStateOf(false) }
            LaunchedEffect(host.totalTime) {
                if (!seekedOnce && position > 0L && host.totalTime > 0) {
                    host.seekTo(position / 1000f)
                    seekedOnce = true
                }
            }

            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                ComposeVideoPlayer(
                    playerHost = host,
                    modifier = Modifier.fillMaxSize(),
                    onTap = { host.togglePlayPause() },
                    overlay = {
                        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                            X_PlayerBottomBar(
                                host = host,
                                fullScreenIcon = R.drawable.exo_ic_fullscreen_exit,
                                onFullScreen = {
                                    EventBus.postEvent(
                                        Event.X_FullScreenExitPosition((host.currentTime * 1000).toLong())
                                    )
                                    navigator.pop()
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}
