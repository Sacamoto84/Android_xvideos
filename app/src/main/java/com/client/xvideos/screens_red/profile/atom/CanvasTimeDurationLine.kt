package com.client.xvideos.screens_red.profile.atom

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun CanvasTimeDurationLine(currentTime: Int, duration: Int) {

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp) // Задаем высоту Canvas
        //.padding(top = 4.dp) // Небольшой отступ от текста времени
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height // Будет равно progressHeight

        // 1. Рисуем фон (полная длительность)
        drawLine(
            color = Color(0xFF909090), // Цвет фона прогресс-бара
            start = Offset(x = 0f, y = canvasHeight / 2),
            end = Offset(x = canvasWidth, y = canvasHeight / 2),
            strokeWidth = canvasHeight,
            cap = StrokeCap.Square // Закругленные концы
        )

        // 2. Рассчитываем и рисуем текущий прогресс
        if (duration > 0) { // Убедимся, что длительность известна и не равна нулю
            val progressRatio = currentTime.toFloat() / duration.toFloat()
            val progressWidth = canvasWidth * progressRatio.coerceIn(
                0f,
                1f
            ) // Ограничиваем от 0 до 1

            drawLine(
                color = Color(0xFFE73538), // Цвет активного прогресса
                start = Offset(x = 0f, y = canvasHeight / 2),
                end = Offset(x = progressWidth, y = canvasHeight / 2),
                strokeWidth = canvasHeight,
                cap = StrokeCap.Square // Закругленные концы
            )
        }
    }

}