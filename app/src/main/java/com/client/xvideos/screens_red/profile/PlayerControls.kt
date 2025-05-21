package com.client.xvideos.screens_red.profile

// Интерфейс для внешних действий (можно вынести в отдельный файл)
interface PlayerControls {
    fun forward(seconds: Int)
    fun rewind(seconds: Int)
    fun seekTo(positionSeconds: Int)
    fun stop(positionSeconds: Int)
    fun pause()
    fun play()
}