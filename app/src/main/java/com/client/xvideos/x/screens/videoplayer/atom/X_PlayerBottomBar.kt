package com.client.xvideos.x.screens.videoplayer.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.common.videoplayer.host.MediaPlayerHost

/**
 * Нижняя панель плеера X: пауза/воспроизведение, перемотка, время и переход
 * в полноэкранный режим.
 */
@Composable
fun X_PlayerBottomBar(
    host: MediaPlayerHost,
    onFullScreen: () -> Unit,
) {
    val total = host.totalTime.toFloat().coerceAtLeast(0f)
    // Пока палец на слайдере, показываем его позицию, а не текущее время плеера.
    var dragPosition by remember { mutableStateOf<Float?>(null) }
    val position = (dragPosition ?: host.currentTime).coerceIn(0f, total.coerceAtLeast(0f))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { host.togglePlayPause() }) {
            Icon(
                imageVector = if (host.isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                contentDescription = if (host.isPaused) "Воспроизвести" else "Пауза",
                tint = Color.White
            )
        }
        Text(formatTime(position), color = Color.White, fontSize = 12.sp, modifier = Modifier.width(44.dp))
        Slider(
            value = position,
            onValueChange = { dragPosition = it },
            onValueChangeFinished = {
                dragPosition?.let { host.seekTo(it) }
                dragPosition = null
            },
            valueRange = 0f..total.coerceAtLeast(1f),
            enabled = total > 0f,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.Red,
                inactiveTrackColor = Color.Gray
            ),
            modifier = Modifier.weight(1f)
        )
        Text(formatTime(total), color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(start = 6.dp))
        IconButton(onClick = onFullScreen) {
            Icon(Icons.Filled.Fullscreen, contentDescription = "Во весь экран", tint = Color.White)
        }
    }
}

private fun formatTime(seconds: Float): String {
    val total = seconds.toInt().coerceAtLeast(0)
    return "%d:%02d".format(total / 60, total % 60)
}
