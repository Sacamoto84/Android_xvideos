package com.client.xvideos.common.videoplayer

import androidx.compose.runtime.compositionLocalOf

/**
 * Разрешено ли сейчас воспроизведение.
 *
 * Приложение выставляет `false`, пока контент закрыт экраном блокировки: плеер
 * ставится на паузу и не возобновляется сам по ON_RESUME.
 */
val LocalPlaybackAllowed = compositionLocalOf { true }
