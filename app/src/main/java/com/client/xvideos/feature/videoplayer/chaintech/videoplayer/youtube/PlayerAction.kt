package com.client.xvideos.feature.videoplayer.chaintech.videoplayer.youtube

internal enum class PlayerAction(val action: String) {
    READY("onReady"),
    ERROR("onError"),
    VIDEO_LENGTH("onVideoDuration"),
    STATE_UPDATE("onStateChange"),
    PROGRESS("onCurrentTimeChange"),
    VIDEO_ID("onVideoId");

    companion object {
        fun fromAction(name: String): PlayerAction? {
            return entries.firstOrNull { it.action == name }
        }
    }
}