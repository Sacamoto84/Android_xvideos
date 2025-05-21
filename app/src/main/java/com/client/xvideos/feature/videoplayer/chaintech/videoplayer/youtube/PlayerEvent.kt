package chaintech.videoplayer.youtube

import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.youtube.PlayerAction
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

sealed class PlayerEvent {
    object Ready : PlayerEvent()

    data class Error(val reason: String) : PlayerEvent()

    data class DurationChanged(val duration: Duration) : PlayerEvent() {
        companion object {
            fun parse(value: String) = value.toDoubleOrNull()?.toDuration(DurationUnit.SECONDS)?.let(::DurationChanged)
        }
    }

    data class State(val status: VideoState) : PlayerEvent() {
        enum class VideoState(val label: String) {
            UNSTARTED("UNSTARTED"),
            ENDED("ENDED"),
            PLAYING("PLAYING"),
            PAUSED("PAUSED"),
            BUFFERING("BUFFERING"),
            QUEUED("CUED");
        }
        companion object {
            fun parse(value: String) = VideoState.entries.firstOrNull { it.label == value }?.let(::State)
        }
    }

    data class Progress(val position: Duration) : PlayerEvent() {
        companion object {
            fun parse(value: String) = value.toDoubleOrNull()?.toDuration(DurationUnit.SECONDS)?.let(::Progress)
        }
    }

    data class VideoId(val id: String) : PlayerEvent() {
        companion object {
            fun parse(value: String?) = value?.let(::VideoId)
        }
    }

    companion object {
        internal fun create(event: PlayerAction?, data: String) = when (event) {
            PlayerAction.READY -> Ready
            PlayerAction.ERROR -> Error(data)
            PlayerAction.VIDEO_LENGTH -> DurationChanged.parse(data)
            PlayerAction.STATE_UPDATE -> State.parse(data)
            PlayerAction.PROGRESS -> Progress.parse(data)
            PlayerAction.VIDEO_ID -> VideoId.parse(data)
            null -> null
        }
    }
}
