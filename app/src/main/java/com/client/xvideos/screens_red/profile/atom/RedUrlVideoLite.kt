package com.client.xvideos.screens_red.profile.atom

import androidx.compose.runtime.Composable

enum class RedUrlVideoLiteEngine { CHAINTECH, ANDROID, COMPOSE }

@Composable // Добавлено
fun RedUrlVideoLite(
    url: String,
    thumbnailUrl: String = "",
    engine: RedUrlVideoLiteEngine = RedUrlVideoLiteEngine.CHAINTECH,
    play: Boolean,
) {
    when (engine) { // when более идиоматичен для такого переключения
        RedUrlVideoLiteEngine.ANDROID -> RedUrlVideoLiteAndroid(url = url)
        RedUrlVideoLiteEngine.COMPOSE -> RedUrlVideoLiteCompose(url = url, thumnailUrl = thumbnailUrl)
        RedUrlVideoLiteEngine.CHAINTECH -> RedUrlVideoLiteChaintech(url = url, thumnailUrl = thumbnailUrl, play)
    }
}







