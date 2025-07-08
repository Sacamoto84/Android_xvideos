package com.client.common.feature.videoplayer.host

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.client.common.feature.videoplayer.model.PlayerSpeed
import com.client.common.feature.videoplayer.model.ScreenResize
import com.client.common.feature.videoplayer.util.AudioTrack
import com.client.common.feature.videoplayer.util.M3U8Helper
import com.client.common.feature.videoplayer.util.SubtitleTrack
import com.client.common.feature.videoplayer.util.VideoQuality
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediaPlayerHost(
    mediaUrl: String = "",
    isPaused: Boolean = false,
    isMuted: Boolean = false,
    initialSpeed: PlayerSpeed = PlayerSpeed.X1,
    initialVideoFitMode: ScreenResize = ScreenResize.FILL,
    isLooping: Boolean = true,
    startTimeInSeconds: Float? = null,
    isFullScreen: Boolean = false,
    headers: Map<String, String>? = null,
    drmConfig: DrmConfig? = null,
) {
    // Internal states
    internal var url by mutableStateOf(mediaUrl)
    internal var speed by mutableStateOf(initialSpeed)
    internal var videoFitMode by mutableStateOf(initialVideoFitMode)
    internal var seekToTime: Float? by mutableStateOf(null)
    internal var isSliding by mutableStateOf(false)
    internal var isPaused by mutableStateOf(isPaused)
    internal var isMuted by mutableStateOf(isMuted)
    internal var isLooping by mutableStateOf(isLooping)
    internal var totalTime by mutableStateOf(0) // Total video duration
    internal var currentTime by mutableFloatStateOf(0f) // Current playback position
    internal var isBuffering by mutableStateOf(true)
    internal var playFromTime: Float? by mutableStateOf(startTimeInSeconds)
    internal var volumeLevel by mutableStateOf(if (isMuted) 0f else 1f) // Range 0.0 to 1.0
    internal var isFullScreen by mutableStateOf(isFullScreen)
    internal var headers by mutableStateOf(headers)
    internal var drmConfig by mutableStateOf(drmConfig)
    var qualityOptions by mutableStateOf(emptyList<VideoQuality>())
    var selectedQuality by mutableStateOf<VideoQuality?>(null)
    var audioTrackOptions by mutableStateOf(emptyList<AudioTrack>())
    var selectedAudioTrack by mutableStateOf<AudioTrack?>(null)
    var subTitlesOptions by mutableStateOf(emptyList<SubtitleTrack>())
    var selectedsubTitle by mutableStateOf<SubtitleTrack?>(null)

    private var lastVolumeLevel by mutableStateOf(1f)

    private val m3u8Helper = M3U8Helper()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    var onEvent: ((MediaPlayerEvent) -> Unit)? = null
    var onError: ((MediaPlayerError) -> Unit)? = null

    init {
        scope.launch(Dispatchers.IO) {
            fetchAndUpdateMediaInfo(url)
        }
    }

    // Public actions
    fun loadUrl(mediaUrl: String, headers: Map<String, String>? = null, drmConfig: DrmConfig? = null) {
        if (url != mediaUrl) {
            url = mediaUrl
            scope.launch(Dispatchers.IO) {
                fetchAndUpdateMediaInfo(mediaUrl)
            }
        }
        this.headers = headers
        this.drmConfig = drmConfig
    }

    fun play() {
        isPaused = false
        onEvent?.invoke(MediaPlayerEvent.PauseChange(isPaused))
    }

    fun pause() {
        isPaused = true
        onEvent?.invoke(MediaPlayerEvent.PauseChange(isPaused))
    }

    fun togglePlayPause() {
        isPaused = !isPaused
        onEvent?.invoke(MediaPlayerEvent.PauseChange(isPaused))
    }

    fun mute() {
        if (!isMuted) {
            lastVolumeLevel = volumeLevel // Store current volume before muting
            volumeLevel = 0f
            isMuted = true
            onEvent?.invoke(MediaPlayerEvent.MuteChange(isMuted))
        }
    }

    fun unmute() {
        if (isMuted) {
            volumeLevel = lastVolumeLevel // Restore previous volume
            isMuted = false
            onEvent?.invoke(MediaPlayerEvent.MuteChange(isMuted))
        }
    }

    fun toggleMuteUnmute() {
        if (isMuted) {
            unmute()
        } else {
            mute()
        }
    }

    fun setSpeed(speed: PlayerSpeed) {
        this.speed = speed
    }

    @Deprecated(
        message = "Use seekTo(seconds: Float?) instead for better precision.",
        replaceWith = ReplaceWith("seekTo(seconds.toFloat())")
    )
    fun seekTo(seconds: Int?) {
        isSliding = true
        seekToTime = seconds?.toFloat()
        isSliding = false
    }

    fun seekTo(seconds: Float?) {
        isSliding = true
        seekToTime = seconds
        isSliding = false
    }

    fun setVideoFitMode(mode: ScreenResize) {
        videoFitMode = mode
    }

    fun setLooping(isLooping: Boolean) {
        this.isLooping = isLooping
    }

    fun toggleLoop() {
        this.isLooping = !this.isLooping
    }

    fun setVolume(level: Float) {
        volumeLevel = level.coerceIn(0f, 1f)
        if (!isMuted) {
            lastVolumeLevel = volumeLevel // Update last volume only if not muted
        }
    }

    fun setFullScreen(isFullScreen: Boolean) {
        this.isFullScreen = isFullScreen
        onEvent?.invoke(MediaPlayerEvent.FullScreenChange(isFullScreen))
    }

    fun toggleFullScreen() {
        this.isFullScreen = !this.isFullScreen
        onEvent?.invoke(MediaPlayerEvent.FullScreenChange(this.isFullScreen))
    }
    fun setVideoQuality(quality: VideoQuality?) {
        this.selectedQuality = quality
    }

    fun updateVideoQualityOptions(options: List<VideoQuality>) {
        this.qualityOptions = options
    }

    fun setAudioTrack(track: AudioTrack?) {
        this.selectedAudioTrack = track
    }

    fun updateAudioTrackOptions(options: List<AudioTrack>) {
        this.audioTrackOptions = options
    }

    fun setSubTitle(subTitle: SubtitleTrack?) {
        this.selectedsubTitle = subTitle
    }

    fun updateSubTitleOptions(options: List<SubtitleTrack>) {
        this.subTitlesOptions = options
    }

    internal fun setBufferingStatus(isBuffering: Boolean) {
        this.isBuffering = isBuffering
        onEvent?.invoke(MediaPlayerEvent.BufferChange(isBuffering))
    }

    // Internal-only setters for time values
    internal fun updateTotalTime(time: Int) {
        if (totalTime != time) {
            totalTime = time
            onEvent?.invoke(MediaPlayerEvent.TotalTimeChange(totalTime))
        }
    }

    internal fun updateCurrentTime(time: Float) {
        if(currentTime != time) {
            currentTime = time
            onEvent?.invoke(MediaPlayerEvent.CurrentTimeChange(currentTime))
        }
    }

    internal fun triggerMediaEnd() {
        onEvent?.invoke(MediaPlayerEvent.MediaEnd)
    }

    internal fun triggerError(error: MediaPlayerError) {
        onError?.invoke(error)
    }

    private suspend fun fetchAndUpdateMediaInfo(videoUrl: String) {
        setVideoQuality(null)
        setAudioTrack(null)
        setSubTitle(null)
        if (videoUrl.endsWith(".m3u8", ignoreCase = true)) {
            val m3u8Data = m3u8Helper.fetchM3U8Data(videoUrl)

            withContext(Dispatchers.Main) {
                updateVideoQualityOptions(m3u8Data.videoQualities)
                updateAudioTrackOptions(m3u8Data.audioTracks)
                updateSubTitleOptions(m3u8Data.subtitleTracks)
            }
        } else {
            withContext(Dispatchers.Main) {
                updateVideoQualityOptions(emptyList())
                updateAudioTrackOptions(emptyList())
                updateSubTitleOptions(emptyList())
            }
        }
    }
}
