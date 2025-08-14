package com.client.xvideos.l.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.composeunstyled.DropdownPanelAnchor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun FullScreenImage(
    imageUrl: String,
    startBounds: Rect?,
    onClose: () -> Unit
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    val startRect = startBounds ?: return

    val startX = startRect.left
    val startY = startRect.top
    val startWidth = startRect.width
    val startHeight = startRect.height
    val aspectRatio = startWidth / startHeight

    val targetWidth = screenWidthPx
    val targetHeight = targetWidth / aspectRatio
    val targetScale = targetWidth / startWidth

    val targetOffsetX = (screenWidthPx - startWidth * targetScale) / 2
    val targetOffsetY = (screenHeightPx - startHeight * targetScale) / 2

    val baseWidthDp = with(density) { startWidth.toDp() }
    val baseHeightDp = with(density) { startHeight.toDp() }

    val scaleAnim = remember { Animatable(1f) }
    val offsetXAnim = remember { Animatable(startX) }
    val offsetYAnim = remember { Animatable(startY) }

    val scope = rememberCoroutineScope()
    var isClosing by remember { mutableStateOf(false) }
    var isResetting by remember { mutableStateOf(false) }

    var isDoubleTapZooming by remember { mutableStateOf(false) }

    var success by remember { mutableStateOf(false) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(success) {
        if (success) {
            alphaAnim.animateTo(1f, tween(200))
        }
    }

    // Анимация открытия
    LaunchedEffect(Unit) {
        coroutineScope {
            launch { scaleAnim.animateTo(targetScale, tween(300)) }
            launch { offsetXAnim.animateTo(targetOffsetX, tween(300)) }
            launch { offsetYAnim.animateTo(targetOffsetY, tween(300)) }
        }
    }

    // Анимация закрытия
    LaunchedEffect(isClosing) {
        if (isClosing) {
            coroutineScope {
                launch { scaleAnim.animateTo(1f, tween(300)) }
                launch { offsetXAnim.animateTo(startX, tween(300)) }
                launch { offsetYAnim.animateTo(startY, tween(300)) }
                launch { alphaAnim.animateTo(0f, tween(200)) }
            }
            onClose()
        }
    }

    BackHandler {
        isClosing = true
    }

    val maxOverflowPx = screenWidthPx - 8f
    val maxOverflowPxY = screenHeightPx - 8f

    // Функция для вычисления границ
    fun calculateBounds(scale: Float): Pair<Pair<Float, Float>, Pair<Float, Float>> {
        val imageWidth = startWidth * scale
        val imageHeight = startHeight * scale

        val minOffsetX = screenWidthPx - imageWidth - maxOverflowPx
        val maxOffsetX = maxOverflowPx
        val minOffsetY = screenHeightPx - imageHeight - maxOverflowPxY
        val maxOffsetY = maxOverflowPxY

        return Pair(
            Pair(min(minOffsetX, maxOffsetX), max(minOffsetX, maxOffsetX)),
            Pair(min(minOffsetY, maxOffsetY), max(minOffsetY, maxOffsetY))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Единая обработка всех жестов
                val decay = splineBasedDecay<Float>(this)

                forEachGesture {
                    awaitPointerEventScope {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        var zoom = 1f
                        var pan = Offset.Zero
                        var pastTouchSlop = false
                        val touchSlop = viewConfiguration.touchSlop

                        val velocityTracker = VelocityTracker()
                        velocityTracker.addPosition(down.uptimeMillis, down.position)

                        var wasSinglePointer = true

                        do {
                            val event = awaitPointerEvent()
                            val canceled = event.changes.any { it.isConsumed }

                            // Отслеживаем количество пальцев
                            if (event.changes.size > 1) {
                                wasSinglePointer = false
                            }

                            if (!canceled) {
                                val zoomChange = event.calculateZoom()
                                val panChange = event.calculatePan()
                                val centroid = event.calculateCentroid(useCurrent = false)

                                if (!pastTouchSlop) {
                                    zoom *= zoomChange
                                    pan += panChange

                                    val centroidSize = centroid.getDistanceSquared()
                                    val zoomMotion = abs(1 - zoom) * centroidSize
                                    val panMotion = pan.getDistanceSquared()

                                    if (zoomMotion > touchSlop * touchSlop || panMotion > touchSlop * touchSlop) {
                                        pastTouchSlop = true
                                    }
                                }

                                if (pastTouchSlop) {
                                    // Обработка зума
                                    if (zoomChange != 1f && !isResetting) {
                                        val oldScale = scaleAnim.value
                                        val newScale = (oldScale * zoomChange).coerceIn(0.5f, 30f)

                                        val offsetX = offsetXAnim.value
                                        val offsetY = offsetYAnim.value

                                        val imageX = (centroid.x - offsetX) / oldScale
                                        val imageY = (centroid.y - offsetY) / oldScale

                                        val newOffsetX = centroid.x - imageX * newScale
                                        val newOffsetY = centroid.y - imageY * newScale

                                        val (xBounds, yBounds) = calculateBounds(newScale)

                                        val clampedOffsetX = (newOffsetX + panChange.x).coerceIn(
                                            xBounds.first,
                                            xBounds.second
                                        )
                                        val clampedOffsetY = (newOffsetY + panChange.y).coerceIn(
                                            yBounds.first,
                                            yBounds.second
                                        )

                                        scope.launch {
                                            scaleAnim.snapTo(newScale)
                                            offsetXAnim.snapTo(clampedOffsetX)
                                            offsetYAnim.snapTo(clampedOffsetY)
                                        }

                                        // Проверяем сброс масштаба
                                        if (newScale < targetScale * 0.8f) {
                                            isResetting = true
                                            scope.launch {
                                                listOf(
                                                    launch {
                                                        scaleAnim.animateTo(
                                                            targetScale,
                                                            tween(300)
                                                        )
                                                    },
                                                    launch {
                                                        offsetXAnim.animateTo(
                                                            targetOffsetX,
                                                            tween(300)
                                                        )
                                                    },
                                                    launch {
                                                        offsetYAnim.animateTo(
                                                            targetOffsetY,
                                                            tween(300)
                                                        )
                                                    }
                                                ).joinAll()
                                                isResetting = false
                                            }
                                        }
                                    }
                                    // Обработка пана (только если нет зума)
                                    else if (panChange != Offset.Zero && !isResetting && event.changes.size == 1) {

                                        val currentScale = scaleAnim.value
                                        val (xBounds, yBounds) = calculateBounds(currentScale)

                                        val newOffsetX = (offsetXAnim.value + panChange.x).coerceIn(
                                            xBounds.first,
                                            xBounds.second
                                        )
                                        val newOffsetY = (offsetYAnim.value + panChange.y).coerceIn(
                                            yBounds.first,
                                            yBounds.second
                                        )

                                        scope.launch {
                                            offsetXAnim.snapTo(newOffsetX)
                                            offsetYAnim.snapTo(newOffsetY)
                                        }

                                        // Отслеживаем скорость для инерции
                                        velocityTracker.addPosition(
                                            event.changes.first().uptimeMillis,
                                            event.changes.first().position
                                        )
                                    }

                                    event.changes.forEach { it.consume() }
                                }
                            }
                        } while (!canceled && event.changes.any { it.pressed })

                        // Применяем инерцию после завершения жеста
                        if (pastTouchSlop && wasSinglePointer) {
                            val velocity = velocityTracker.calculateVelocity()

                            // Ограничиваем максимальную скорость в зависимости от масштаба
                            val currentScale = scaleAnim.value
                            val baseMaxVelocity = 3000f // базовая скорость при targetScale
                            val maxVelocity =
                                baseMaxVelocity * (currentScale / targetScale).coerceAtLeast(1f)

                            val clampedVelocityX = velocity.x.coerceIn(-maxVelocity, maxVelocity)
                            val clampedVelocityY = velocity.y.coerceIn(-maxVelocity, maxVelocity)

                            if (abs(clampedVelocityX) > 300 || abs(clampedVelocityY) > 300) {
                                val (xBounds, yBounds) = calculateBounds(currentScale)

                                scope.launch {
                                    listOf(
                                        launch {
                                            val targetX = decay.calculateTargetValue(
                                                offsetXAnim.value,
                                                clampedVelocityX
                                            )
                                            val clampedTargetX =
                                                targetX.coerceIn(xBounds.first, xBounds.second)

                                            if (clampedTargetX != targetX) {
                                                offsetXAnim.animateTo(
                                                    targetValue = clampedTargetX,
                                                    initialVelocity = clampedVelocityX,
                                                    animationSpec = tween(
                                                        300,
                                                        easing = EaseOutCubic
                                                    )
                                                )
                                            } else {
                                                offsetXAnim.animateDecay(
                                                    initialVelocity = clampedVelocityX,
                                                    animationSpec = decay
                                                )
                                            }
                                        },
                                        launch {
                                            val targetY = decay.calculateTargetValue(
                                                offsetYAnim.value,
                                                clampedVelocityY
                                            )
                                            val clampedTargetY =
                                                targetY.coerceIn(yBounds.first, yBounds.second)

                                            if (clampedTargetY != targetY) {
                                                offsetYAnim.animateTo(
                                                    targetValue = clampedTargetY,
                                                    initialVelocity = clampedVelocityY,
                                                    animationSpec = tween(
                                                        300,
                                                        easing = EaseOutCubic
                                                    )
                                                )
                                            } else {
                                                offsetYAnim.animateDecay(
                                                    initialVelocity = clampedVelocityY,
                                                    animationSpec = decay
                                                )
                                            }
                                        }
                                    ).joinAll()
                                }
                            }
                        }
                    }
                }
            }
            .pointerInput(Unit) {
                // Обработка двойного тапа для зума
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        if (isDoubleTapZooming || isResetting) return@detectTapGestures

                        isDoubleTapZooming = true
                        val currentScale = scaleAnim.value
                        val targetZoomScale = targetScale * 2f

                        scope.launch {
                            if (currentScale < targetZoomScale * 0.9f) {
                                // Зумим до 3x с центрированием на точку тапа
                                val currentOffsetX = offsetXAnim.value
                                val currentOffsetY = offsetYAnim.value

                                // Вычисляем координаты в изображении
                                val imageX = (tapOffset.x - currentOffsetX) / currentScale
                                val imageY = (tapOffset.y - currentOffsetY) / currentScale

                                // Новые оффсеты для центрирования точки тапа
                                val newOffsetX = tapOffset.x - imageX * targetZoomScale
                                val newOffsetY = tapOffset.y - imageY * targetZoomScale

                                // Применяем границы
                                val (xBounds, yBounds) = calculateBounds(targetZoomScale)
                                val clampedOffsetX =
                                    newOffsetX.coerceIn(xBounds.first, xBounds.second)
                                val clampedOffsetY =
                                    newOffsetY.coerceIn(yBounds.first, yBounds.second)

                                listOf(
                                    launch { scaleAnim.animateTo(targetZoomScale, tween(300)) },
                                    launch { offsetXAnim.animateTo(clampedOffsetX, tween(300)) },
                                    launch { offsetYAnim.animateTo(clampedOffsetY, tween(300)) }
                                ).joinAll()
                            } else {
                                // Возвращаемся к масштабу по ширине и центральной позиции
                                listOf(
                                    launch { scaleAnim.animateTo(targetScale, tween(300)) },
                                    launch { offsetXAnim.animateTo(targetOffsetX, tween(300)) },
                                    launch { offsetYAnim.animateTo(targetOffsetY, tween(300)) }
                                ).joinAll()
                            }
                            isDoubleTapZooming = false
                        }
                    },
                    onTap = {
                        if (!isDoubleTapZooming) {
                            isClosing = true
                        }
                    }
                )
            },
        contentAlignment = Alignment.TopStart
    ) {

        // Полупрозрачный фон
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = alphaAnim.value))
        )

        IconButton(modifier = Modifier.align(Alignment.TopEnd), onClick = {}) {
            Icon(Icons.Default.MoreVert, tint = Color.White, contentDescription = null)
        }

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        offsetXAnim.value.roundToInt(),
                        offsetYAnim.value.roundToInt()
                    )
                }
                .graphicsLayer(
                    scaleX = scaleAnim.value,
                    scaleY = scaleAnim.value,
                    transformOrigin = TransformOrigin(0f, 0f)
                )
                .size(baseWidthDp, baseHeightDp)
        ) {
            UrlImageLusciousGifsFull(
                url = imageUrl,
                modifier = Modifier,//.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
                onSuccess = { success = it },
            )
        }
    }
}
