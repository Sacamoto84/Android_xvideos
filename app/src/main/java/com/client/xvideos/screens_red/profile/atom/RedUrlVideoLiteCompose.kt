package com.client.xvideos.screens_red.profile.atom

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import timber.log.Timber

var exoPlayer: ExoPlayer? = null

@OptIn(UnstableApi::class)
@Composable
fun RedUrlVideoLiteCompose(
    url: String,
    thumnailUrl: String = "",
) {

    val context = LocalContext.current
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
    val lifecycle = lifecycleOwner.value.lifecycle

    var showControls by remember { mutableStateOf(true) }

    var isBuffering by remember { mutableStateOf(true) } // Изначально считаем, что идет буферизация
    var playerError by remember { mutableStateOf<PlaybackException?>(null) }

    var hasStartedPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(url) {
        exoPlayer =
            ExoPlayer.Builder(context)
                .build()
                .also { exoPlayer ->
                    val mediaItem = MediaItem.Builder()
                        .setUri(url)
                        .build()
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.prepare()
                    exoPlayer.volume = 0f
                    exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
                    //exoPlayer.playWhenReady = true // Начать играть как только будет готово

                    exoPlayer.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            isBuffering = playbackState == Player.STATE_BUFFERING
                            Timber.d("RedUrlVideoLiteCompose: Playback State: $playbackState, isBuffering: $isBuffering")
                            if (playbackState == Player.STATE_READY && playerError != null) {
                                playerError = null // Сбрасываем ошибку, если плеер готов
                            }

                            if (playbackState == Player.STATE_READY) {
                                hasStartedPlaying = true
                            }

                        }

                        override fun onPlayerError(error: PlaybackException) {
                            Timber.e(error, "RedUrlVideoLiteCompose: Player Error")
                            playerError = error
                            isBuffering = false // Ошибка - это не буферизация
                        }

                        // Можно также отслеживать onIsLoadingChanged, если нужно более гранулярно
                        override fun onIsLoadingChanged(isLoading: Boolean) {
                            super.onIsLoadingChanged(isLoading)
                            // Если isLoading true И текущее состояние не READY, можно считать это буферизацией
                            // Это может быть полезно для более точного определения загрузки,
                            // но Player.STATE_BUFFERING обычно достаточен.
                            // if (isLoading && exoPlayer.playbackState != Player.STATE_READY) {
                            // isBuffering = true
                            // }
                            Timber.d("RedUrlVideoLiteCompose: Is Loading Changed: $isLoading")
                        }
                    })
                }
    }

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    exoPlayer?.pause()
                }

                Lifecycle.Event.ON_RESUME -> {
                    exoPlayer?.play()
                }

                else -> {
                }
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            exoPlayer?.pause()
            exoPlayer?.release()
            lifecycle.removeObserver(observer)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        when {

            playerError != null -> {
                // Показываем сообщение об ошибке или плейсхолдер ошибки
                PlaceholderContent(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Ошибка загрузки видео" // Или более детальное сообщение из playerError
                )
                // Можно добавить кнопку для повторной попытки
            }

            isBuffering -> {


//                // Показываем плейсхолдер (картинку + индикатор загрузки)
//                Image(
//                    painter = placeholderPainter,
//                    contentDescription = "Загрузка видео...",
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop // Или другой подходящий ContentScale
//                )

                UrlImage(
                    url = thumnailUrl,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                )


                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            else -> {


                //if (!hasStartedPlaying) {

                //}

                if (exoPlayer != null) {

                    // Показываем видео, когда оно не буферизуется и нет ошибок
                    PlayerSurface(
                        player = exoPlayer!!,
                        modifier = Modifier.fillMaxSize(),
                        surfaceType = SURFACE_TYPE_TEXTURE_VIEW //SURFACE_TYPE_TEXTURE_VIEW // или SURFACE_TYPE_SURFACE_VIEW
                        // Вы можете добавить обработчик нажатия для скрытия/показа контролов:
                        // .pointerInput(Unit) {
                        // detectTapGestures { showControls = !showControls }
                        // }
                    )
                    if (showControls) {
                        PlayPauseButton(exoPlayer!!, modifier = Modifier.align(Alignment.CenterEnd))
                    }

                    UrlImage(
                        url = thumnailUrl,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(1f) // убедиться, что над видео
                    )

                }
            }
        }


    }
}


@Composable
fun PlaceholderContent(modifier: Modifier = Modifier, text: String) {
    Box(modifier = modifier.padding(16.dp)) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@OptIn(UnstableApi::class)
@Composable
fun PlayPauseButton(player: Player, modifier: Modifier = Modifier) {
    val state = rememberPlayPauseButtonState(player)
    val icon = if (state.showPlay) Icons.Default.PlayArrow else Icons.Default.Pause
    val contentDescription = if (state.showPlay) "string.lbl_play_state" else "lbl_pause_state"
    val graySemiTransparentBG = Color.Gray.copy(alpha = 0.1f)
    val btnModifier =
        modifier
            .size(48.dp)
            .background(graySemiTransparentBG, CircleShape)
    Row(
        modifier = Modifier.then(modifier),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = state::onClick,
            modifier = btnModifier,
            enabled = state.isEnabled
        ) {
            Icon(icon, contentDescription = contentDescription, tint = Color.White)
        }
    }
}

