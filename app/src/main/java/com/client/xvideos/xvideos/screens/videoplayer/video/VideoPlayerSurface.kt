package com.client.xvideos.xvideos.screens.videoplayer.video

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.client.xvideos.R
import com.client.xvideos.xvideos.screens.videoplayer.ScreenVideoPlayerSM
import com.client.xvideos.xvideos.screens.videoplayer.atom.VideoQualitySelector
import com.client.xvideos.xvideos.screens.videoplayer.atom.VideoSpeedSelector
import kotlinx.coroutines.DelicateCoroutinesApi

private val orange = Color.parseColor("#FFA800")

@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("UnsafeOptInUsageError")
@Composable
internal fun VideoPlayerSurface(
    vm: ScreenVideoPlayerSM,
    modifier: Modifier = Modifier,
    defaultPlayerView: PlayerView,
    player: ExoPlayer,
    usePlayerController: Boolean,
    handleLifecycle: Boolean,
    surfaceResizeMode: ResizeMode,
    autoDispose: Boolean = true,
) {
    val lifecycleOwner =
        rememberUpdatedState(LocalLifecycleOwner.current)

    AndroidView(
        modifier = modifier,

        factory = {
            defaultPlayerView.apply {
                useController = usePlayerController
                resizeMode = surfaceResizeMode.toPlayerViewResizeMode()
                setBackgroundColor(Color.BLACK)


//                setControllerShowTimeoutMs(0)
//                controllerAutoShow = false // Автоматическое отображение при взаимодействии
//                controllerHideOnTouch = false // Автоматическое скрытие при касании экрана
                // Установить время в миллисекундах (10 секунд)

//                // Подключение кастомной разметки
//                val customControls = LayoutInflater.from(context).inflate(
//                    RR.layout.custom_player_controls,
//                    this,
//                    false
//                )
//                this.addView(customControls)

//                val text: TextView = customControls.findViewById(RR.id.speed)
//                text.text = "2X"

//                // Логика для кнопки изменения соотношения сторон
//                val aspectRatioButton: ImageButton = customControls.findViewById(RR.id.exo_aspect_ratio)
//
//                val aspectRatios = listOf(
//                    AspectRatioFrameLayout.RESIZE_MODE_FIT,
//                    AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH,
//                    AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT,
//                    AspectRatioFrameLayout.RESIZE_MODE_FILL,
//                    AspectRatioFrameLayout.RESIZE_MODE_ZOOM
//                )
//
                var currentMode = 0
//
//                text.setOnClickListener {
//                    currentMode = (currentMode + 1) % aspectRatios.size
//                    this.resizeMode = aspectRatios[currentMode]
//                }

//                ///////////////////////////////////////////////////////////////////////////////
//                //Кнопка изменения отношения сторон
                val customButtonResize = ImageButton(context).apply {
                    setImageResource(R.drawable.resize1) // Ваш значок кнопки
                    contentDescription = "Change Aspect Ratio"
                    setBackgroundResource(android.R.color.transparent) // Убираем фон
                    setOnClickListener {
                        val aspectRatios = listOf(
                            AspectRatioFrameLayout.RESIZE_MODE_FIT,
                            AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH,
                            AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT,
                            AspectRatioFrameLayout.RESIZE_MODE_FILL,
                            AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        )
                        currentMode = (currentMode + 1) % aspectRatios.size
                        resizeMode = aspectRatios[currentMode]
                    }
                }
                ///////////////////////////////////////////////////////////////////////////////

                val t = this
                //Выбор качество видео
                val compose = ComposeView(context)
                compose.setContent {

                    Row(
                        modifier = Modifier.height(50.dp)
                            //.background(androidx.compose.ui.graphics.Color.Magenta)
                        ,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //Изменение качества
                        VideoQualitySelector(vm.quality, list = vm.listFormat) {
                            vm.quality = it
                            val targetId = vm.listFormat.find { el -> el.height == it }?.id
                            if (targetId != null) {
                                vm.switchTrack(targetId)
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        VideoSpeedSelector(vm.speed, onClick = { vm.changePlaybackSpeed(it) })

//                        IconButtonLocal(R.drawable.resize1, sizeIB = 50.dp, sizeI = 40.dp, onClick = {
//                            val a = vm.aspectRatiosClick()
//                            GlobalScope.launch(Dispatchers.Main) {
//                                t.resizeMode = a
//                            }
//                        })

                    }



                }

                //Затенение фона
                this.findViewById<View>(androidx.media3.ui.R.id.exo_controls_background).apply {
                    setBackgroundColor(0x60000000)
                }

                //Кнопка плей пауза
                this.findViewById<ImageButton>(androidx.media3.ui.R.id.exo_play_pause).apply {
                    setColorFilter(orange, PorterDuff.Mode.SRC_IN)
                    scaleX = 1.5f // Увеличивает ширину в 2 раза
                    scaleY = 1.5f // Увеличивает высоту в 2 раза
                }

                //Кнопка перемотки назад
                findViewById<Button>(androidx.media3.ui.R.id.exo_rew_with_amount).apply {
                    foreground?.mutate()?.setColorFilter(orange, PorterDuff.Mode.SRC_IN)
                    setTextColor(orange)
                    scaleX = 1.5f // Увеличивает ширину в 2 раза
                    scaleY = 1.5f // Увеличивает высоту в 2 раза

                    // Получаем LayoutParams текущей кнопки
                    val l = this.layoutParams as ViewGroup.MarginLayoutParams
                    l.marginEnd = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        20.toFloat(),
                        context.resources.displayMetrics
                    ).toInt()
                    layoutParams = l
                }

                //Кнопка перемотки вперед
                findViewById<Button>(androidx.media3.ui.R.id.exo_ffwd_with_amount).apply {
                    foreground?.mutate()?.setColorFilter(orange, PorterDuff.Mode.SRC_IN)
                    setTextColor(orange)
                    scaleX = 1.5f // Увеличивает ширину в 2 раза
                    scaleY = 1.5f // Увеличивает высоту в 2 раза

                    // Получаем LayoutParams текущей кнопки
                    val l = this.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams = l
                    l.marginStart = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        20.toFloat(),
                        context.resources.displayMetrics
                    ).toInt()
                }

                //Прогресс бар
                this.findViewById<ProgressBar>(androidx.media3.ui.R.id.exo_buffering).apply {
                    indeterminateDrawable?.setColorFilter(orange, PorterDuff.Mode.SRC_IN)
                }

                //Кнопка настнойка, выключить
//                findViewById<ImageButton>(androidx.media3.ui.R.id.exo_settings).apply {
//                    visibility = GONE
//                }

                val basicControlView =
                    this.findViewById<LinearLayout>(androidx.media3.ui.R.id.exo_basic_controls)

                basicControlView?.let {
                    val marginInPx = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        8.toFloat(),
                        context.resources.displayMetrics
                    ).toInt()
                    val layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                        marginStart = marginInPx
                        marginEnd = marginInPx
                    }
                    it.addView(customButtonResize, 0, layoutParams)
                    //////////////////////////////////////////////////////////////////
                    it.addView(compose, 0, layoutParams)
                }


            }
        },
    )

    DisposableEffect(
        Unit,
    )
    {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (handleLifecycle) {
                        player.pause()
                    }
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (handleLifecycle) {
                        player.play()
                    }

//                    if (enablePip && player.playWhenReady) {
//                        defaultPlayerView.useController = usePlayerController
//                    }
                }

                Lifecycle.Event.ON_STOP -> {
                    //val isPipMode = context.isActivityStatePipMode()

                    if (handleLifecycle) {
                        //if (handleLifecycle || (enablePip && isPipMode && !isPendingPipMode)) {
                        player.stop()
                    }
                }

                else -> {}
            }
        }
        val lifecycle = lifecycleOwner.value.lifecycle
        lifecycle.addObserver(observer)

        onDispose {
            if (autoDispose) {
                player.release()
                lifecycle.removeObserver(observer)
            }
        }
    }
}
