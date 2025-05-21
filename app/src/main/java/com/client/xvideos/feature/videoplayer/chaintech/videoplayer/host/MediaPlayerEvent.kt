package com.client.xvideos.feature.videoplayer.chaintech.videoplayer.host

sealed class MediaPlayerEvent {
    data class MuteChange(val isMuted: Boolean) : MediaPlayerEvent()
    data class PauseChange(val isPaused: Boolean) : MediaPlayerEvent()
    data class BufferChange(val isBuffering: Boolean) : MediaPlayerEvent()
    data class CurrentTimeChange(val currentTime: Int) : MediaPlayerEvent()
    data class TotalTimeChange(val totalTime: Int) : MediaPlayerEvent()
    data class FullScreenChange(val isFullScreen: Boolean) : MediaPlayerEvent()
    object MediaEnd : MediaPlayerEvent()
}

sealed class MediaPlayerError(val message: String) {
    object VlcNotFound : MediaPlayerError("VLC library not found.")
    data class InitializationError(val details: String) : MediaPlayerError(details)
    data class PlaybackError(val details: String) : MediaPlayerError(details)
    data class ResourceError(val details: String) : MediaPlayerError(details)
}

data class DrmConfig(
    val keyId: String,
    val key: String
)