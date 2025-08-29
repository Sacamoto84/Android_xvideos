package com.client.xvideos.l.ui.screens.screenRoot

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.ui.screens.screenAlbumList.ScreenLAlbumList
import com.client.xvideos.l.ui.screens.screenSavedAlbums.ScreenLSavedAlbums
import com.example.ui.screens.UiSnackbarVisuals
import com.example.ui.screens.show
import com.redgifs.common.ThemeRed
import com.redgifs.common.di.HostDI
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

val LocalRootLScreenModel = staticCompositionLocalOf<ScreenLRootSM> {
    error("No ScreenLRootSM provided")
}

class ScreenLRoot() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalZoomableApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val haptic = LocalHapticFeedback.current

        val vm: ScreenLRootSM = getScreenModel()

        val drawerState = rememberDrawerState(DrawerValue.Closed)

        val snackbarHostState = remember { SnackbarHostState() }

        val snackBarEvent = vm.hostDI.snackBarEvent

        LaunchedEffect(Unit) {
            vm.snackbarEvents.collect { message ->
                snackbarHostState.showSnackbar(message)
            }
        }

        LaunchedEffect(Unit) {
            snackBarEvent.messages.receiveAsFlow().collect { message ->
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                snackbarHostState.show(message)
            }
        }

        CompositionLocalProvider(LocalRootLScreenModel provides vm) {
            Scaffold(
                containerColor = ThemeL.greyBackground,
                snackbarHost = {
                    SnackbarHost(snackbarHostState) { data ->


                        val uiMsg = (data.visuals as? UiSnackbarVisuals)?.ui
                            ?: UiMessage.Info(data.visuals.message)

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
                                    delay(2000)
                                    data.dismiss()
                                }

                                is UiMessage.Error -> {
                                    delay(5000)
                                    data.dismiss()
                                }

                                is UiMessage.Info -> {
                                    delay(2000)
                                    data.dismiss()
                                }
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),

                            color = bg,
                            contentColor = fg,
                            shape = RoundedCornerShape(12.dp),
                            tonalElevation = 6.dp,
                            shadowElevation = 6.dp,
                        ) {
                            Row(
                                Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(icon, contentDescription = null)
                                Spacer(Modifier.width(8.dp))

                                Text(
                                    data.visuals.message, Modifier//.weight(1f)
                                    , fontFamily = ThemeRed.fontFamilyDMsanss
                                )

                                data.visuals.actionLabel?.let { label ->
                                    TextButton(onClick = { data.performAction() }) {
                                        Text(label)
                                    }
                                }
                            }
                        }


                    }
                }
            ) { paddingValues ->
                //Navigator( screen = ScreenLAlbumList(556543))
                Navigator( screen = ScreenLSavedAlbums())
            }
        }
    }

}


class ScreenLRootSM @Inject constructor(
    val hostDI: HostDI
) : ScreenModel {
    private val _snackbarEvents = Channel<String>(64)
    val snackbarEvents = _snackbarEvents.receiveAsFlow()

    @OptIn(DelicateCoroutinesApi::class)
    fun showSnackbar(message: String) {
        GlobalScope.launch {
            _snackbarEvents.send(message)
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

