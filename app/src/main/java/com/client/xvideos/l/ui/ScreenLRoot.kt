package com.client.xvideos.l.ui

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.client.common.urlVideImage.UrlImage
import com.client.xvideos.l.Album
import com.client.xvideos.l.Luscious
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ExperimentalZoomableApi
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class ScreenLRoot() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalZoomableApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val vm: ScreenLRootSM = getScreenModel()

        val album = vm.album.collectAsStateWithLifecycle().value

        val parsed =
            vm.album.collectAsStateWithLifecycle().value?.parsed?.collectAsStateWithLifecycle()?.value

        val selectZoomItemUrl: String? = null

        var selectedImage by remember { mutableStateOf<String?>(null) }
        var selectedBounds by remember { mutableStateOf<Rect?>(null) }

        val density = LocalDensity.current
        val scope = rememberCoroutineScope()


        Scaffold(
            containerColor = Color(0xFF262626)

        ) {


            //LazyColumn(modifier = Modifier.fillMaxSize().zoomableWithScroll(rememberZoomState())) {

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                //.zoomableWithScroll(rememberZoomState())
            ) {


                item {
                    Column {
                        Row {
                            UrlImage(parsed?.cover?.url.toString(), modifier = Modifier.size(96.dp))
                            Column {
                                Text(parsed?.title.toString(), color = Color.White)
                                Text(parsed?.created.toString(), color = Color.White)
                            }
                        }


                    }

                }


                item {

                    Row {
                        Text(
                            parsed?.number_of_animated_pictures.toString() + " gifs",
                            color = Color.White
                        )
                        Text(" / ", color = Color.White)
                        Text(
                            parsed?.number_of_pictures.toString() + " pictures",
                            color = Color.White
                        )
                    }

                }
                item {
                    Row {
                        Text("Genres:", color = Color.White)
                        parsed?.genres?.forEach {
                            Text(it.title, color = Color.White)
                        }

                    }
                }
                item {
                    Row {
                        Text("Audiences:", color = Color.White)
                        parsed?.audiences?.forEach {
                            Text(it.title, color = Color.White)
                        }
                    }
                }

                item {
                    Text("Tags:", color = Color.White)
                    parsed?.tags?.forEach {
                        Text(it.text, color = Color.White)
                    }
                }

                items(album?.pics ?: emptyList()) {

                    var imageBounds by remember { mutableStateOf<Rect?>(null) }

                    if (it.url_to_original != null) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {

                            val aspect = it.width.toFloat() / it.height

                            val zoomState = rememberZoomState(maxScale = 3f)

                            UrlImageLusciousGifs(
                                it.url_to_original,
                                modifier = Modifier
                                    .padding(2.dp)
                                    .aspectRatio(aspect)
                                    .clipToBounds()
                                    .border(0.5.dp, Color.Gray)
                                    .onGloballyPositioned { coordinates ->
                                        val rect = coordinates.boundsInRoot()
                                        imageBounds = rect
                                    }
                                    .clickable {
                                        selectedImage = it.url_to_original
                                        selectedBounds = imageBounds
                                    },
                                contentScale = ContentScale.FillBounds
                            )

                        }
                    }
                }

            }


            // Полноэкранное изображение с анимацией
            selectedImage?.let { imageUrl ->
                FullScreenImage(
                    imageUrl = imageUrl,
                    startBounds = selectedBounds,
                    onClose = { selectedImage = null }
                )
            }


        }


    }


}





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
    var isDragging by remember { mutableStateOf(false) }

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
            .background(Color.Black)
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



                        do {
                            val event = awaitPointerEvent()
                            val canceled = event.changes.any { it.isConsumed }
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

                                        val clampedOffsetX = (newOffsetX + panChange.x).coerceIn(xBounds.first, xBounds.second)
                                        val clampedOffsetY = (newOffsetY + panChange.y).coerceIn(yBounds.first, yBounds.second)

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
                                                    launch { scaleAnim.animateTo(targetScale, tween(300)) },
                                                    launch { offsetXAnim.animateTo(targetOffsetX, tween(300)) },
                                                    launch { offsetYAnim.animateTo(targetOffsetY, tween(300)) }
                                                ).joinAll()
                                                isResetting = false
                                            }
                                        }
                                    }
                                    // Обработка пана (только если нет зума)
                                    else if (panChange != Offset.Zero && !isResetting && event.changes.size == 1) {
                                        isDragging = true

                                        val currentScale = scaleAnim.value
                                        val (xBounds, yBounds) = calculateBounds(currentScale)

                                        val newOffsetX = (offsetXAnim.value + panChange.x).coerceIn(xBounds.first, xBounds.second)
                                        val newOffsetY = (offsetYAnim.value + panChange.y).coerceIn(yBounds.first, yBounds.second)

                                        scope.launch {
                                            offsetXAnim.snapTo(newOffsetX)
                                            offsetYAnim.snapTo(newOffsetY)
                                        }

                                        // Отслеживаем скорость для инерции
                                        velocityTracker.addPosition(event.changes.first().uptimeMillis, event.changes.first().position)
                                    }

                                    event.changes.forEach { it.consume() }
                                }
                            }
                        } while (!canceled && event.changes.any { it.pressed })

                        isDragging = false

                        // Применяем инерцию после завершения жеста
                        if (pastTouchSlop) {
                            val velocity = velocityTracker.calculateVelocity()

                            if (abs(velocity.x) > 300 || abs(velocity.y) > 300) {
                                val currentScale = scaleAnim.value
                                val (xBounds, yBounds) = calculateBounds(currentScale)

                                scope.launch {
                                    listOf(
                                        launch {
                                            val targetX = decay.calculateTargetValue(offsetXAnim.value, velocity.x)
                                            val clampedTargetX = targetX.coerceIn(xBounds.first, xBounds.second)

                                            if (clampedTargetX != targetX) {
                                                offsetXAnim.animateTo(
                                                    targetValue = clampedTargetX,
                                                    initialVelocity = velocity.x,
                                                    animationSpec = tween(300, easing = EaseOutCubic)
                                                )
                                            } else {
                                                offsetXAnim.animateDecay(
                                                    initialVelocity = velocity.x,
                                                    animationSpec = decay
                                                )
                                            }
                                        },
                                        launch {
                                            val targetY = decay.calculateTargetValue(offsetYAnim.value, velocity.y)
                                            val clampedTargetY = targetY.coerceIn(yBounds.first, yBounds.second)

                                            if (clampedTargetY != targetY) {
                                                offsetYAnim.animateTo(
                                                    targetValue = clampedTargetY,
                                                    initialVelocity = velocity.y,
                                                    animationSpec = tween(300, easing = EaseOutCubic)
                                                )
                                            } else {
                                                offsetYAnim.animateDecay(
                                                    initialVelocity = velocity.y,
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
                detectTapGestures(onTap = { isClosing = true })
            },
        contentAlignment = Alignment.TopStart
    ) {
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
            UrlImageLusciousGifs(
                url = imageUrl,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}




//@Composable
//fun FullScreenImage(
//    imageUrl: String,
//    startBounds: Rect?,
//    onClose: () -> Unit
//) {
//
//
//    val density = LocalDensity.current
//    val configuration = LocalConfiguration.current
//    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
//    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
//
//    val startRect = startBounds ?: return
//
//    val startX = startRect.left
//    val startY = startRect.top
//    val startWidth = startRect.width
//    val startHeight = startRect.height
//    val aspectRatio = startWidth / startHeight
//
//    val targetWidth = screenWidthPx
//    val targetHeight = targetWidth / aspectRatio
//    val targetScale = targetWidth / startWidth
//
//    val targetOffsetX = (screenWidthPx - startWidth * targetScale) / 2
//    val targetOffsetY = (screenHeightPx - startHeight * targetScale) / 2
//
//    val baseWidthDp = with(density) { startWidth.toDp() }
//    val baseHeightDp = with(density) { startHeight.toDp() }
//
//    val scaleAnim = remember { Animatable(1f) }
//    val offsetXAnim = remember { Animatable(startX) }
//    val offsetYAnim = remember { Animatable(startY) }
//
//    val scope = rememberCoroutineScope()
//    var isClosing by remember { mutableStateOf(false) }
//    var isResetting by remember { mutableStateOf(false) }
//
//    // Анимация открытия
//    LaunchedEffect(Unit) {
//        coroutineScope {
//            launch { scaleAnim.animateTo(targetScale, tween(300)) }
//            launch { offsetXAnim.animateTo(targetOffsetX, tween(300)) }
//            launch { offsetYAnim.animateTo(targetOffsetY, tween(300)) }
//        }
//    }
//
//    // Анимация закрытия
//    LaunchedEffect(isClosing) {
//        if (isClosing) {
//            coroutineScope {
//                launch { scaleAnim.animateTo(1f, tween(300)) }
//                launch { offsetXAnim.animateTo(startX, tween(300)) }
//                launch { offsetYAnim.animateTo(startY, tween(300)) }
//            }
//            onClose()
//        }
//    }
//
//    BackHandler{
//        isClosing = true
//    }
//
//
//    val maxOverflowPx = screenWidthPx - 8f
//    val maxOverflowPxY = screenHeightPx - 8f
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black)
//            .pointerInput(Unit) {
//                detectTransformGestures { centroid, pan, zoom, _ ->
//                    if (isResetting) return@detectTransformGestures
//
//                    val oldScale = scaleAnim.value
//                    val newScale = (oldScale * zoom).coerceIn(0.5f, 30f)
//
//                    val offsetX = offsetXAnim.value
//                    val offsetY = offsetYAnim.value
//
//                    val imageWidth = startWidth * newScale
//                    val imageHeight = startHeight * newScale
//
//                    val imageX = (centroid.x - offsetX) / oldScale
//                    val imageY = (centroid.y - offsetY) / oldScale
//
//                    val newOffsetX = centroid.x - imageX * newScale
//                    val newOffsetY = centroid.y - imageY * newScale
//
//                    val minOffsetX = screenWidthPx - imageWidth - maxOverflowPx
//                    val maxOffsetX = maxOverflowPx
//                    val minOffsetY = screenHeightPx - imageHeight - maxOverflowPxY
//                    val maxOffsetY = maxOverflowPxY
//
//                    val clampedOffsetX = (newOffsetX + pan.x).coerceIn(
//                        min(minOffsetX, maxOffsetX),
//                        max(minOffsetX, maxOffsetX)
//                    )
//                    val clampedOffsetY = (newOffsetY + pan.y).coerceIn(
//                        min(minOffsetY, maxOffsetY),
//                        max(minOffsetY, maxOffsetY)
//                    )
//
//                    scope.launch {
//                        scaleAnim.snapTo(newScale)
//                        offsetXAnim.snapTo(clampedOffsetX)
//                        offsetYAnim.snapTo(clampedOffsetY)
//                    }
//
//                    if (newScale < targetScale * 0.8f) {
//                        isResetting = true
//                        scope.launch {
//                            listOf(
//                                launch { scaleAnim.animateTo(targetScale, tween(300)) },
//                                launch { offsetXAnim.animateTo(targetOffsetX, tween(300)) },
//                                launch { offsetYAnim.animateTo(targetOffsetY, tween(300)) }
//                            ).joinAll()
//                            isResetting = false
//                        }
//                    }
//                }
//            }
//            .pointerInput(Unit) {
//                detectTapGestures(onTap = { isClosing = true })
//            },
//        contentAlignment = Alignment.TopStart
//    ) {
//        Box(
//            modifier = Modifier
//                .offset {
//                    IntOffset(
//                        offsetXAnim.value.roundToInt(),
//                        offsetYAnim.value.roundToInt()
//                    )
//                }
//                .graphicsLayer(
//                    scaleX = scaleAnim.value,
//                    scaleY = scaleAnim.value,
//                    transformOrigin = TransformOrigin(0f, 0f)
//                )
//                .size(baseWidthDp, baseHeightDp)
//        ) {
//            UrlImageLusciousGifs(
//                url = imageUrl,
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.FillBounds
//            )
//        }
//    }
//}
//

fun Float.safeCoerceIn(a: Float, b: Float): Float {
    val min = min(a, b)
    val max = max(a, b)
    return this.coerceIn(min, max)
}

class ScreenLRootSM @Inject constructor(
    val luscious: Luscious
) : ScreenModel {

    val album = MutableStateFlow<Album?>(null)

    init {
        screenModelScope.launch {

            if (!luscious.loggedIn) {
                luscious.login()
            }
            album.value = luscious.getAlbum(336743)//(499900)//(374481)
            album
        }
    }


}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLRootBlock {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenLRootSM::class)
    abstract fun bindScreenLRootScreenModel(hiltListScreenModel: ScreenLRootSM): ScreenModel
}
