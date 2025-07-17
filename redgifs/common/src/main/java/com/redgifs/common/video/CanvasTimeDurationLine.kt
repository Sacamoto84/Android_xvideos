package com.redgifs.common.video

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.client.common.util.toMinSec
import com.client.common.util.toTwoDecimalPlacesWithColon
import com.redgifs.common.ThemeRed
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.cancellation.CancellationException


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

@Preview
@Composable
fun CanvasTimeDurationLinePreview() {
    CanvasTimeDurationLine(currentTime = 50f, duration = 100, timeA = 20f, timeB = 80f, timeABEnable = true, play = false)
}


@Composable
fun CanvasTimeDurationLine1(
    currentTime: Float,
    duration: Int,
    modifier: Modifier = Modifier,
    timeA: Float = 0f,
    timeB: Float = 1f,
    timeABEnable: Boolean,
    visibleAB : Boolean = true,
    play: Boolean = false,
    onSeek: (Float) -> Unit,              // 🔹 при перетаскивании
    onSeekFinished: (() -> Unit)? = null  // 🔹 когда отпустили
) {

    var isDragging by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth().height(32.dp)
            .pointerInput(Unit) {
                coroutineScope {
                    while (true) {
                        awaitPointerEventScope {
                            val down = awaitFirstDown()

                            val downX = down.position.x
                            val newTime = (downX / size.width) * duration
                            onSeek(newTime.coerceIn(0f, duration.toFloat()))

                            var drag: PointerInputChange? = null
                            try {
                                drag = awaitTouchSlopOrCancellation(down.id) { change, _ ->
                                    // Начало drag
                                    isDragging = true
                                    change.consume()
                                }
                            } catch (_: CancellationException) {}

                            if (drag != null) {
                                // Мы начали перетаскивать
                                horizontalDrag(drag.id) { change ->
                                    val dragX = change.position.x
                                    val newTimeDrag = (dragX / size.width) * duration
                                    onSeek(newTimeDrag.coerceIn(0f, duration.toFloat()))
                                    change.consume()
                                }
                                isDragging = false
                                onSeekFinished?.invoke()
                            } else {
                                // Просто тап, без драггинга
                                onSeekFinished?.invoke()
                            }
                        }
                    }
                }
            },

        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(currentTime.toDouble().toMinSec(), color = Color.White, fontSize= 12.sp, fontFamily = ThemeRed.fontFamilyPopinsRegular, modifier = Modifier.width(48.dp).background(Color.Green))


        Canvas(
            modifier = Modifier.then(modifier).fillMaxSize().weight(1f)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Фон
            drawLine(
                color = Color(0x80909090),
                start = Offset(x = 0f, y = canvasHeight / 2),
                end = Offset(x = canvasWidth, y = canvasHeight / 2),
                strokeWidth = 2.dp.toPx(),
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
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Square
                    )
                }

                // Индикатор (ползунок)
                drawCircle(
                    color = Color(0xFFE73538),
                    center = Offset(progressWidth, canvasHeight / 2),
                    radius = 3.dp.toPx()
                )

                //Индикатор
//                drawLine(
//                    color = Color(0xFFE73538), // Цвет активного прогресса
//                    start = Offset(x = progressWidth, y = 2.dp.toPx()),
//                    end = Offset(x = progressWidth, y = canvasHeight - 2.dp.toPx()),
//                    strokeWidth = 4.dp.toPx(),
//                    cap = StrokeCap.Round // Закругленные концы
//                )


                // Отрезок A-B
                if (visibleAB) {
                    drawLine(
                        color = Color(0xFF8BC34A),
                        start = Offset(x = progressRatioA, y = canvasHeight / 2 - 4.dp.toPx()),
                        end = Offset(x = progressRatioB, y = canvasHeight / 2 - 4.dp.toPx()),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Square
                    )
                }

            }
        }

        Text(duration.toDouble().toMinSec(), color = Color.White,fontSize= 18.sp, modifier = Modifier.width(48.dp).background(Color.Green))

    }
}

