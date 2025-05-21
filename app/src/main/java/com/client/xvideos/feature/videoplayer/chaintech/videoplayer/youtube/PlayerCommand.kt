package com.client.xvideos.feature.videoplayer.chaintech.videoplayer.youtube

import kotlin.time.Duration

sealed interface PlayerCommand {
    fun toJsCommand(): String

    data class LoadVideo(val videoId: String, val startAt: Duration = Duration.ZERO) : PlayerCommand {
        override fun toJsCommand(): String = "loadVideo('$videoId', ${startAt.inWholeSeconds});"
    }

    object Play : PlayerCommand {
        override fun toJsCommand(): String = "playVideo();"
    }

    object Pause : PlayerCommand {
        override fun toJsCommand(): String = "pauseVideo();"
    }

    data class SeekTo(val position: Duration) : PlayerCommand {
        override fun toJsCommand(): String = "seekTo(${position.inWholeSeconds});"
    }

    data class SeekBy(val offset: Duration) : PlayerCommand {
        override fun toJsCommand(): String = "seekBy(${offset.inWholeSeconds});"
    }

    object Mute : PlayerCommand {
        override fun toJsCommand(): String = "mute();"
    }

    object Unmute : PlayerCommand {
        override fun toJsCommand(): String = "unMute();"
    }

    data class PlaybackSpeed(val rate: Float) : PlayerCommand {
        override fun toJsCommand(): String = "setPlaybackRate($rate);"
    }
}