package com.client.xvideos.l.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.common.fresco.DownloadQueueManager
import com.client.xvideos.common.fresco.QueueStatisticsCardLite
import com.client.xvideos.common.traficStatistic.AppNetworkSpeedMonitorLite
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.ui.screens.explorer.ScreenLExplorer
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.common.di.HostDI
import com.client.xvideos.redgifs.ui.UiSnackbarVisuals
import com.client.xvideos.redgifs.ui.show
import com.redgifs.common.snackBar.UiMessage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ExperimentalZoomableApi
import javax.inject.Inject

val LocalRootLScreenModel =
    staticCompositionLocalOf<ScreenLRootSM> { error("No ScreenLRootSM provided") }

// Глобальная ссылка на основной навигатор для доступа из любого места
val LocalMainNavigator =
    staticCompositionLocalOf<Navigator?> { null }

class ScreenLRoot() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalZoomableApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val haptic = LocalHapticFeedback.current

        val vm: ScreenLRootSM = getScreenModel()

        val snackbarHostState = remember { SnackbarHostState() }

        val snackBarEvent = vm.hostDI.snackBarEvent

        val navigator = LocalNavigator.currentOrThrow

        // Создаем отдельный навигатор для внутренней навигации
        var mainNavigator: Navigator? = null

        LaunchedEffect(Unit) {
            vm.snackbarEvents.collect { message ->
                snackbarHostState.showSnackbar(
                    message
                )
            }
        }

        LaunchedEffect(Unit) {
            snackBarEvent.messages.receiveAsFlow().collect { message ->
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                snackbarHostState.show(message)
            }
        }

        val queueState by DownloadQueueManager.queueState.collectAsState()

        CompositionLocalProvider(LocalRootLScreenModel provides vm) {
            Scaffold(
                floatingActionButtonPosition = FabPosition.Start,

                floatingActionButton = {

                    if (depth > 0) {
                        SmallFloatingActionButton(
                            onClick = {
                                // Возврат к домашнему экрану через основной навигатор
                                mainNavigator?.let { nav ->
                                    // Проверяем, не находимся ли мы уже на домашнем экране
                                    if (nav.lastItem !is ScreenLExplorer) {
                                        // Очищаем весь стек и переходим к домашнему экрану
                                        nav.replaceAll(ScreenLExplorer())
                                    }
                                }
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Scroll to top"
                                )
                            }

                        )
                    }
                },
                containerColor = ThemeL.greyBackground,
                snackbarHost = {
                    Box(modifier = Modifier.zIndex(Float.MAX_VALUE)) {
                        SnackbarHost(snackbarHostState) { data ->
                            val uiMsg = (data.visuals as? UiSnackbarVisuals)?.ui ?: UiMessage.Info(
                                data.visuals.message
                            )
                            val (bg, fg, icon) = when (uiMsg) {
                                is UiMessage.Success -> Triple(
                                    Color(0xFF0F9960),
                                    Color.White,
                                    Icons.Default.Check
                                )

                                is UiMessage.Error -> Triple(
                                    Color(0xFFD13913),
                                    Color.White,
                                    Icons.Default.ErrorOutline
                                )

                                is UiMessage.Info -> Triple(
                                    Color(0xFF137CBD),
                                    Color.White,
                                    Icons.Default.Info
                                )
                            }
                            LaunchedEffect(data) {
                                when (uiMsg) {
                                    is UiMessage.Success -> {
                                        delay(2000); data.dismiss()
                                    }

                                    is UiMessage.Error -> {
                                        delay(5000); data.dismiss()
                                    }

                                    is UiMessage.Info -> {
                                        delay(2000); data.dismiss()
                                    }
                                }
                            }
                            Surface(
                                modifier = Modifier
                                    .zIndex(Float.MAX_VALUE)
                                    .wrapContentWidth()
                                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                                    .zIndex(Float.MAX_VALUE),
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
                                    Text(
                                        data.visuals.message,
                                        Modifier,
                                        fontFamily = ThemeRed.fontFamilyDMsanss
                                    )
                                    data.visuals.actionLabel?.let { label ->
                                        TextButton(onClick = { data.performAction() }) { Text(label) }
                                    }
                                }
                            }
                        }
                    }
                }
            ) { paddingValues ->

                //Navigator(screen = ScreenLExplorer())

                // Основной навигатор приложения
                Navigator(screen = ScreenLExplorer()) { nav ->
                    mainNavigator = nav // Сохраняем ссылку на навигатор
                    nav.lastItem.Content()
                }

                QueueStatisticsCardLite(queueState = queueState)

                // Оверлей рисуется поверх Scaffold
                vm.overlayContent.value?.let { content ->
                    Box(
                        modifier = Modifier
                            //.zIndex(10f)
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.95f))
                    ) { content() }
                }


            }



            AppNetworkSpeedMonitorLite()
        }
    }

}

//Глубина погружения навигации
var depth by mutableIntStateOf(0)

class ScreenLRootSM @Inject constructor(
    val hostDI: HostDI
) : ScreenModel {
    private val _snackbarEvents = Channel<String>(64)
    val snackbarEvents = _snackbarEvents.receiveAsFlow()

    @OptIn(DelicateCoroutinesApi::class)
    fun showSnackbar(message: String) {
        GlobalScope.launch { _snackbarEvents.send(message) }
    }


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
        if (nav.lastItem !is ScreenLExplorer) {
            nav.replaceAll(ScreenLExplorer())
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


enum class SelectIndex(val value: Int) {
    Unselect(-1),
    Default(0),
    Manga(1),
    Hentai(2),
    Porn(3)
}

