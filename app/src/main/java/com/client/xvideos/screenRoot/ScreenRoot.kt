package com.client.xvideos.screenRoot

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Badge
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.Navigator
import com.client.xvideos.MenuScreen
import com.client.xvideos.common.eventBus.Event
import com.client.xvideos.common.eventBus.EventBus
import com.client.xvideos.common.snackbar.show
import com.client.xvideos.common.traficStatistic.AppNetworkSpeedMonitorLite
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.ui.screens.explorer.L_ScreenExplorer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.flow.filterIsInstance
import net.engawapg.lib.zoomable.ExperimentalZoomableApi
import javax.inject.Inject

//Глубина погружения навигации
var depth by mutableIntStateOf(0)

val LocalRootScreenModel = staticCompositionLocalOf<ScreenRootSM> { error("No ScreenRootSM provided") }

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

        var mainNavigator by remember { mutableStateOf<Navigator?>(null) }

        LaunchedEffect(Unit) {
            EventBus.events
                .filterIsInstance<Event.ShowSnackBar>()
                .collect { event ->
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
                    HomeFloatingActionButton(mainNavigator)
                },
                containerColor = ThemeL.greyBackground,
                snackbarHost = {
                    RootSnackbarHost(snackBarHostState)
                }
            ) { paddingValues ->
                Navigator(screen = MenuScreen) { nav ->
                    mainNavigator = nav
                    nav.lastItem.Content()
                }

                vm.overlayContent.value?.let { content ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        content()
                    }
                }
            }
            AppNetworkSpeedMonitorLite()
        }
    }
}

@Composable
private fun HomeFloatingActionButton(mainNavigator: Navigator?) {
    var navigationDepth by remember { mutableIntStateOf(0) }

    LaunchedEffect(mainNavigator) {
        mainNavigator?.let { nav ->
            snapshotFlow { nav.items.size }
                .collect { stackSize ->
                    navigationDepth = if (stackSize > 1) stackSize - 1 else 0
                }
        }
    }

    if (depth > 0) {
        Box {
            SmallFloatingActionButton(
                onClick = {
                    mainNavigator?.let { nav ->
                        if (nav.lastItem !is L_ScreenExplorer) {
                            nav.replaceAll(L_ScreenExplorer())
                        }
                    }
                },
                content = {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "Home")
                }
            )

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
}


class ScreenRootSM @Inject constructor() : ScreenModel {
    private val _overlayContent = mutableStateOf<(@Composable () -> Unit)?>(null)
    val overlayContent: State<(@Composable () -> Unit)?> = _overlayContent

    fun showOverlay(content: @Composable () -> Unit) {
        _overlayContent.value = content
    }

    fun hideOverlay() {
        _overlayContent.value = null
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenRootModule {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRootSM::class)
    abstract fun bindScreenRootSM(sm: ScreenRootSM): ScreenModel
}
