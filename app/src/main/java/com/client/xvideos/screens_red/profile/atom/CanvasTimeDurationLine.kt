package com.client.xvideos.screens_red.profile.atom

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun CanvasTimeDurationLine(currentTime: Float, duration: Int, timeA: Float = 0f, timeB: Float = 1f , timeABEnable : Boolean, play : Boolean = false) {

    var last by remember { mutableFloatStateOf(0f) }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)

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

            val progressRatioA = canvasWidth * (timeA.toFloat() / duration.toFloat()).coerceIn( 0f,1f )
            val progressRatioB = canvasWidth * (timeB.toFloat() / duration.toFloat()).coerceIn( 0f,1f )

            val progressWidth = canvasWidth * progressRatio.coerceIn(0f, 1f)

            if (!timeABEnable) {
                drawLine(
                    color = Color(0xFFE73538), // Цвет активного прогресса
                    start = Offset(x = 0f, y = canvasHeight / 2),
                    end = Offset(x = progressWidth, y = canvasHeight / 2),
                    strokeWidth = canvasHeight,
                    cap = StrokeCap.Square // Закругленные концы
                )
            }




           // if (timeABEnable){

            //Индикатор
            drawLine(
                color = Color(0xFFE73538), // Цвет активного прогресса
                start = Offset(x = progressWidth, y = canvasHeight / 2 - 20),
                end = Offset(x = progressWidth, y = canvasHeight / 2),
                strokeWidth = canvasHeight*3,
                cap = StrokeCap.Round // Закругленные концы
            )


                drawLine(
                    color = Color(0xFF8BC34A), // Цвет активного прогресса
                    start = Offset(x = progressRatioA, y = canvasHeight / 2 - canvasHeight),
                    end = Offset(x = progressRatioB, y = canvasHeight / 2 - canvasHeight),
                    strokeWidth = canvasHeight,
                    cap = StrokeCap.Square // Закругленные концы
                )



           // }



        }


    }

}

@Composable
fun CanvasTimeDurationLine(
    currentTime: Float,
    duration: Int,
    timeA: Float = 0f,
    timeB: Float = 1f,
    timeABEnable: Boolean,
    play: Boolean = false,
    onSeek: (Float) -> Unit,              // 🔹 при перетаскивании
    onSeekFinished: (() -> Unit)? = null  // 🔹 когда отпустили
) {
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp) // Даем побольше места для перетаскивания
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        val newTime = (offset.x/ size.width) * duration
                        onSeek(newTime.coerceIn(0f, duration.toFloat()).toInt().toFloat())
                    },
                    onDrag = { change, _ ->
                        val newTime = (change.position.x / size.width) * duration
                        onSeek(newTime.coerceIn(0f, duration.toFloat()).toInt().toFloat())
                    },
                    onDragEnd = {
                        isDragging = false
                        onSeekFinished?.invoke()
                    }
                )
            }
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Фон
            drawLine(
                color = Color(0x80909090),
                start = Offset(x = 0f, y = canvasHeight / 2),
                end = Offset(x = canvasWidth, y = canvasHeight / 2),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Square
            )

            if (duration > 0) {
                val progressRatio = currentTime / duration
                val progressRatioA = canvasWidth * (timeA / duration).coerceIn(0f, 1f)
                val progressRatioB = canvasWidth * (timeB / duration).coerceIn(0f, 1f)
                val progressWidth = canvasWidth * progressRatio.coerceIn(0f, 1f)

                if (!timeABEnable) {
                    drawLine(
                        color = Color(0xFFE73538),
                        start = Offset(x = 0f, y = canvasHeight / 2),
                        end = Offset(x = progressWidth, y = canvasHeight / 2),
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Square
                    )
                }

                // Индикатор (ползунок)
//                drawCircle(
//                    color = Color(0xFFE73538),
//                    center = Offset(progressWidth, canvasHeight / 2),
//                    radius = 8.dp.toPx()
//                )

                //Индикатор
                drawLine(
                    color = Color(0xFFE73538), // Цвет активного прогресса
                    start = Offset(x = progressWidth, y = 2.dp.toPx()),
                    end = Offset(x = progressWidth, y = canvasHeight - 2.dp.toPx()),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round // Закругленные концы
                )


                // Отрезок A-B
                drawLine(
                    color = Color(0xFF8BC34A),
                    start = Offset(x = progressRatioA, y = canvasHeight / 2 - 4.dp.toPx()),
                    end = Offset(x = progressRatioB, y = canvasHeight / 2 - 4.dp.toPx()),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Square
                )

            }
        }
    }
}

