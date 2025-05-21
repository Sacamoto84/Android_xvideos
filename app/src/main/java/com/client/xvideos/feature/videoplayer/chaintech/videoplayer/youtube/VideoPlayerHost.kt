package chaintech.videoplayer.youtube

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.youtube.PlayerCommand
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume
import kotlin.time.Duration

sealed class VideoState {
    object Idle : VideoState()
    object Initialized : VideoState()

    data class Playing(
        val videoId: String,
        val totalDuration: Duration = Duration.ZERO,
        val currentTime: Duration = Duration.ZERO,
        val isPlaying: Boolean = false,
        val playbackStatus: PlayerEvent.State.VideoState = PlayerEvent.State.VideoState.UNSTARTED
    ) : VideoState()

    data class Failed(val errorMessage: String) : VideoState()
}

// Class to manage YouTube host state
@Stable
internal class VideoPlayerHost {
    private val commandLock = Mutex()

    var playerState by mutableStateOf<VideoState>(VideoState.Idle)
        private set

    var isBuffering by mutableStateOf(true)
        private set

    internal var pendingCommand by mutableStateOf<PlayerCommand?>(null)
    private var commandContinuation: CancellableContinuation<Unit>? = null

    suspend fun load(videoId: String) = executeCommand(PlayerCommand.LoadVideo(videoId))
    suspend fun play() = executeCommand(PlayerCommand.Play)
    suspend fun pause() = executeCommand(PlayerCommand.Pause)
    suspend fun seekTo(position: Duration) = executeCommand(PlayerCommand.SeekTo(position))
    suspend fun mute() = executeCommand(PlayerCommand.Mute)
    suspend fun unmute() = executeCommand(PlayerCommand.Unmute)
    suspend fun setSpeed(rate: Float) = executeCommand(PlayerCommand.PlaybackSpeed(rate))

    private suspend fun executeCommand(command: PlayerCommand) {
        commandLock.withLock {
            try {
                suspendCancellableCoroutine {
                    this.commandContinuation = it
                    this.pendingCommand = command
                }
            } finally {
                this.pendingCommand = null
                this.commandContinuation = null
            }
        }
    }

    internal fun complete() {
        commandContinuation?.resume(Unit)
    }

    internal fun update(event: PlayerEvent) {
        playerState = when (event) {
            is PlayerEvent.Error -> {
                isBuffering = false
                VideoState.Failed(event.reason)
            }

            is PlayerEvent.VideoId -> {
                isBuffering = false
                (playerState as? VideoState.Playing)?.copy(videoId = event.id)
                    ?: VideoState.Playing(event.id)
            }

            PlayerEvent.Ready -> {
                VideoState.Initialized
            }

            is PlayerEvent.State -> {
                isBuffering = event.status == PlayerEvent.State.VideoState.BUFFERING
                (playerState as? VideoState.Playing)?.copy(
                    isPlaying = event.status == PlayerEvent.State.VideoState.PLAYING,
                    playbackStatus = event.status
                ) ?: VideoState.Failed("Invalid state")
            }

            is PlayerEvent.Progress -> {
                isBuffering = false
                (playerState as? VideoState.Playing)?.copy(currentTime = event.position)
                    ?: VideoState.Failed("Invalid state")
            }

            is PlayerEvent.DurationChanged -> {
                isBuffering = false
                (playerState as? VideoState.Playing)?.copy(totalDuration = event.duration)
                    ?: VideoState.Playing(videoId = "", totalDuration = event.duration)
            }
        }
    }
}