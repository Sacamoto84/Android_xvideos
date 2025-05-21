package com.client.xvideos.feature.videoplayer.chaintech.videoplayer.youtube

import chaintech.videoplayer.youtube.PlayerEvent

internal object PlayerEventParser {

    private val eventPattern = "ytplayer://([A-z]+)(\\?data=([A-z\\d._-]+))*".toRegex()

    fun parse(url: String?): PlayerEvent? {
        val match = eventPattern.matchEntire(url.orEmpty()) ?: return null
        val eventType = PlayerAction.fromAction(match.groupValues[1])
        val data = match.groupValues[3]
        return PlayerEvent.Companion.create(eventType, data)
    }
}