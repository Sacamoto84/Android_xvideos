package com.client.xvideos.common.fresco

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun QueueStatisticsCardLite(queueState: DownloadQueueState) {

    val animatedProgress by animateFloatAsState(
        targetValue = queueState.currentSize.toFloat() / queueState.maxQueueSize.toFloat(),
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "progress"
    )

    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = Modifier.fillMaxWidth(),
        color = when {
            queueState.isQueueFull -> MaterialTheme.colorScheme.error
            queueState.currentSize > queueState.maxQueueSize * 0.7 -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.primary
        },
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )

}

@Preview
@Composable
fun QueueStatisticsCardLitePreview() {
    val queueState = DownloadQueueState(
        //currentSize = 2,
        maxQueueSize = 5
    )
    QueueStatisticsCardLite(queueState = queueState)
}

// Вспомогательные функции
private fun formatDuration(millis: Long): String {
    val seconds = millis / 1000
    return when {
        seconds < 60 -> "${seconds}s"
        seconds < 3600 -> "${seconds / 60}m ${seconds % 60}s"
        else -> "${seconds / 3600}h ${(seconds % 3600) / 60}m"
    }
}

