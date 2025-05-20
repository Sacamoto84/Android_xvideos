package com.client.xvideos.screens_red.profile.atom

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

@Composable
fun VerticalScrollbar(scrollPercent: Pair<Float, Float>) { // Пример параметра

    BoxWithConstraints (
        modifier = Modifier
            .fillMaxSize()
    ) {
        val trackTotalHeightPx = this@BoxWithConstraints.constraints.maxHeight
        val trackTotalWidthPx = this@BoxWithConstraints.constraints.maxWidth

        val currentRange = scrollPercent

        Canvas(modifier = Modifier.fillMaxSize()) {
            // Убедимся, что start не больше end, и есть что отображать
            if (currentRange.second > currentRange.first && trackTotalHeightPx > 0) {
                val startFraction = currentRange.first.coerceIn(0f, 1f)
                val endFraction = currentRange.second.coerceIn(0f, 1f)

                val indicatorOffsetYPx: Float = trackTotalHeightPx * startFraction
                // Рассчитываем высоту видимой части в пикселях
                val indicatorHeightPx: Float = (trackTotalHeightPx * (endFraction - startFraction))
                    .coerceAtLeast(0f) // Высота не может быть отрицательной

                // Предохранитель, чтобы индикатор не вышел за пределы трека снизу
                val actualIndicatorHeightPx = indicatorHeightPx.coerceAtMost(trackTotalHeightPx - indicatorOffsetYPx)

                if (actualIndicatorHeightPx > 0f) { // Рисуем только если есть реальная высота
                    drawRect(
                        color = Color.Gray, // Цвет вашего индикатора
                        topLeft = Offset(x = 0f, y = indicatorOffsetYPx),
                        size = Size(width = trackTotalWidthPx.toFloat(), height = actualIndicatorHeightPx)
                    )
                }
            }
        }
    }
}