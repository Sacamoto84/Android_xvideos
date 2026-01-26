package com.client.xvideos

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Badge
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.Navigator
import com.client.xvideos.common.eventBus.Event
import com.client.xvideos.common.eventBus.EventBus
import com.client.xvideos.common.snackbar.UiMessage
import com.client.xvideos.common.traficStatistic.AppNetworkSpeedMonitorLite
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.ui.screens.explorer.L_ScreenExplorer
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.ui.UiSnackbarVisuals
import com.client.xvideos.redgifs.ui.show
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import net.engawapg.lib.zoomable.ExperimentalZoomableApi
import javax.inject.Inject

//Глубина погружения навигации
var depth by mutableIntStateOf(0)

val LocalRootScreenModel = staticCompositionLocalOf<ScreenRootSM> { error("No ScreenRootSM provided") }

/**
 * Глобальная ссылка на основной навигатор для доступа из любого места
 * ```kotlin
 * val mainNavigator = LocalMainNavigator.current
 * mainNavigator?.let { nav ->
 *     if (nav.lastItem !is ScreenLExplorer) {
 *         nav.replaceAll(ScreenLExplorer())
 *     }
 * }
 * ```
 */
val LocalMainNavigator = staticCompositionLocalOf<Navigator?> { null }

object ScreenRoot : Screen {

    private fun readResolve(): Any = ScreenRoot

    override val key: ScreenKey = "ScreenRoot"

    @OptIn(ExperimentalZoomableApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val haptic = LocalHapticFeedback.current
        val vm: ScreenRootSM = getScreenModel()
        val snackBarHostState = remember { SnackbarHostState() }

        // Создаем отдельный навигатор для внутренней навигации
        var mainNavigator: Navigator? = null

        LaunchedEffect(Unit) {
            //Timber.i("~~~ LaunchedEffect started — start collecting")
            EventBus.events
                //.onEach { Timber.i("~~~ EventBus emitted: $it") }
                .filterIsInstance<Event.ShowSnackBar>()
                .collect { event ->
                    //Timber.i("~~~ Event.SnackBarRaw collected: ${event.message}")
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    snackBarHostState.show(event.message)
                }
        }

        CompositionLocalProvider(
            LocalRootScreenModel provides vm,
            LocalMainNavigator provides mainNavigator
        ) {
            Scaffold(
                floatingActionButtonPosition = FabPosition.Start,

                floatingActionButton = {

                    // Отслеживаем изменения в навигаторе для обновления Badge
                    var navigationDepth by remember { mutableIntStateOf(0) }

                    LaunchedEffect(mainNavigator) {
                        mainNavigator?.let { nav ->
                            // Подписываемся на изменения в стеке навигации
                            snapshotFlow { nav.items.size }
                                .collect { stackSize ->
                                    // Глубина = размер стека - 1 (так как ScreenLExplorer = 0)
                                    navigationDepth = if (stackSize > 1) stackSize - 1 else 0
                                }
                        }
                    }

                    if (depth > 0) {
                        Box {
                            SmallFloatingActionButton(
                                onClick = {
                                    // Возврат к домашнему экрану через основной навигатор
                                    mainNavigator?.let { nav ->
                                        // Проверяем, не находимся ли мы уже на домашнем экране
                                        if (nav.lastItem !is L_ScreenExplorer) {
                                            // Очищаем весь стек и переходим к домашнему экрану
                                            nav.replaceAll(L_ScreenExplorer())
                                        }
                                    }
                                },
                                content = {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = "Home"
                                    )
                                }
                            )

                            // Badge показываем только если есть глубина навигации
                            if (navigationDepth > 0) {
                                Badge(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 4.dp, y = (-4).dp),
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = navigationDepth.toString(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },
                containerColor = ThemeL.greyBackground,

                snackbarHost = {
                    Box(modifier = Modifier.zIndex(Float.MAX_VALUE)) {
                        SnackbarHost(snackBarHostState) { data ->

                            val uiMsg = (data.visuals as? UiSnackbarVisuals)?.ui ?: UiMessage.Info( data.visuals.message )

                            val (bg, fg, icon) = when (uiMsg) {
                                is UiMessage.Success -> Triple(Color(0xFF0F9960), Color.White, Icons.Default.Check)
                                is UiMessage.Error -> Triple(Color(0xFFD13913), Color.White, Icons.Default.ErrorOutline)
                                is UiMessage.Info -> Triple(Color(0xFF137CBD), Color.White, Icons.Default.Info)
                                is UiMessage.Warning -> Triple(Color(0xFFFF8E0C), Color.White, Icons.Default.Info)
                            }
                            LaunchedEffect(data) {
                                when (uiMsg) {
                                    is UiMessage.Success -> { delay(2000); data.dismiss() }
                                    is UiMessage.Error   -> { delay(5000); data.dismiss() }
                                    is UiMessage.Info    -> { delay(2000); data.dismiss() }
                                    is UiMessage.Warning -> { delay(2000); data.dismiss() }
                                }
                            }


                            Surface(
                                modifier = Modifier
                                    .zIndex(Float.MAX_VALUE)
                                    .wrapContentWidth()
                                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                                color = bg,
                                contentColor = fg,
                                shape = RoundedCornerShape(12.dp),
                                tonalElevation = 6.dp,
                                shadowElevation = 6.dp,
                            ) {
                                Row(
                                    Modifier
                                        .zIndex(Float.MAX_VALUE)
                                        .padding(horizontal = 12.dp, vertical = 12.dp)
                                        .zIndex(Float.MAX_VALUE),
                                    verticalAlignment = Alignment.CenterVertically
                                )
                                {
                                    Icon(icon, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text( data.visuals.message, Modifier, fontFamily = ThemeRed.fontFamilyDMsanss )
                                    data.visuals.actionLabel?.let { label ->
                                        TextButton(onClick = { data.performAction() }) { Text(label) }
                                    }
                                }
                            }


                        }
                    }
                }
            )
            { paddingValues ->

                // Основной навигатор приложения
                Navigator(screen = MenuScreen) { nav ->
                    mainNavigator = nav
                    nav.lastItem.Content()
                }

                // Оверлей рисуется поверх Scaffold
                vm.overlayContent.value?.let { content ->
                    Box( modifier = Modifier.fillMaxSize()
                    //.background(Color.Transparent.copy(alpha = 0.95f))
                    ) { content() }
                }

            }

            AppNetworkSpeedMonitorLite()
        }
    }

}



class ScreenRootSM @Inject constructor() : ScreenModel
{
    // состояние для фуллскрин-оверлея
    private val _overlayContent = mutableStateOf<(@Composable () -> Unit)?>(null)
    val overlayContent: State<(@Composable () -> Unit)?> = _overlayContent

    fun showOverlay(content: @Composable () -> Unit) {
        _overlayContent.value = content
    }

    fun hideOverlay() {
        _overlayContent.value = null
    }

}

// Расширение для удобного доступа к домашней навигации из любого экрана
@Composable
fun navigateToHome() {
    val mainNavigator = LocalMainNavigator.current
    mainNavigator?.let { nav ->
        if (nav.lastItem !is L_ScreenExplorer) {
            nav.replaceAll(L_ScreenExplorer())
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRoot {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRootSM::class)
    abstract fun bindScreenRootScreenModel(hiltListScreenModel: ScreenRootSM): ScreenModel
}




