package com.client.xvideos.screens_red.profile.atom

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import timber.log.Timber

@OptIn(UnstableApi::class)
@Composable
fun RedUrlVideoLiteAndroid(url: String) {

    val context = LocalContext.current

    // Создаем и запоминаем ExoPlayer. Ключ `url` обеспечит его пересоздание при смене url.
    val exoPlayer = remember(url) { // Используем `url` как ключ для `remember`
        Timber.d("Creating new ExoPlayer for URL: $url")
        ExoPlayer.Builder(context).build().apply {
            setMediaSource(
                buildHlsMediaSource1(
                    url.toUri(),
                    DefaultHttpDataSource.Factory(),
                )
            )
            repeatMode = Player.REPEAT_MODE_ALL
            volume = 0f // Видео будет без звука
            playWhenReady = true // Начать воспроизведение, как только будет готово
            //Looper.prepare()


            // >>> ДОБАВЛЕНИЕ СЛУШАТЕЛЯ <<<
            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Timber.e(
                        error,
                        "!!! ExoPlayer Error: CodeName=${error.errorCodeName}, Message=${error.message}"
                    )
                    // Здесь вы можете более детально обработать ошибку,
                    // например, показать сообщение пользователю или отправить лог на сервер.
                    // error.errorCode даст вам числовой код ошибки.
                    // error.cause может содержать вложенное исключение с деталями.
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    val stateString = when (playbackState) {
                        Player.STATE_IDLE -> "IDLE"
                        Player.STATE_BUFFERING -> "BUFFERING"
                        Player.STATE_READY -> "READY"
                        Player.STATE_ENDED -> "ENDED"
                        else -> "UNKNOWN_STATE"
                    }
                    Timber.d("!!! ExoPlayer PlaybackState: $stateString ($playbackState)")
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    Timber.d("!!! ExoPlayer IsPlaying: $isPlaying")
                }

                // Вы можете переопределить и другие методы Player.Listener по необходимости,
                // например, onTimelineChanged, onMediaItemTransition, onPlayerErrorChanged и т.д.
                // Полный список методов смотрите в документации Player.Listener.
            })


        }
    }

    // PlayerView создается один раз и запоминается.
    val playerView = remember {
        PlayerView(context).apply {
            // player будет установлен в DisposableEffect/LaunchedEffect при его изменении
            controllerAutoShow = false
            hideController()
            keepScreenOn = true
            setUseController(false)
        }
    }

    // Эффект для управления жизненным циклом плеера и его связи с PlayerView
    DisposableEffect(exoPlayer) { // Ключ `exoPlayer` - при смене экземпляра плеера эффект перезапустится
        Timber.d("DisposableEffect: Attaching player ${exoPlayer.hashCode()} to PlayerView ${playerView.hashCode()}")
        playerView.player = exoPlayer // Привязываем текущий плеер к PlayerView
        playerView.onResume()      // Сообщаем PlayerView, что он активен
        playerView.player?.prepare()
        playerView.player?.play()

        onDispose {
            Timber.d("DisposableEffect: Releasing player ${exoPlayer.hashCode()} from PlayerView ${playerView.hashCode()}")
            playerView.onPause()   // Сообщаем PlayerView, что он уходит на паузу
            playerView.player = null // Отвязываем плеер
            exoPlayer.release()    // Освобождаем ресурсы плеера
        }
    }

    // Если нужно реагировать на события жизненного цикла самого Composable (например, родительского экрана)
    // для приостановки/возобновления, можно использовать LifecycleEventObserver.
    // Однако, для простого проигрывания видео в цикле при видимости Composable,
    // playWhenReady=true и управление в DisposableEffect может быть достаточно.
    // ComposableLifecycle здесь удален, так как его логика пересекалась и конфликтовала.

    AndroidView(
        factory = { playerView },
        modifier = Modifier // Используем переданный modifier
            .fillMaxWidth() // Можно оставить здесь, если это всегда нужно
            .background(Color.Black) // Можно оставить здесь
    )

}

@OptIn(UnstableApi::class)
fun buildHlsMediaSource1(
    uri: Uri,
    dataSourceFactory: DefaultHttpDataSource.Factory,
): HlsMediaSource {
    val mediaItem = MediaItem.fromUri(uri)
    return HlsMediaSource.Factory(dataSourceFactory)
        .createMediaSource(mediaItem)
}

