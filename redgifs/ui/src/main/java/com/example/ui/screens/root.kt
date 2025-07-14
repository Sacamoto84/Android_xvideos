package com.example.ui.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.Navigator
import com.client.xvideos.redgifs.screens.explorer.ScreenRedExplorer
import com.redgifs.common.ThemeRed
import com.redgifs.common.di.HostDI
import com.redgifs.common.downloader.ui.DownloadIndicator
import com.redgifs.common.saved.collection.ui.DaialogNewCollection
import com.redgifs.common.saved.collection.ui.DialogCollection
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
import javax.inject.Inject

val LocalRootScreenModel = staticCompositionLocalOf<ScreenRedRootSM> {
    error("No ScreenRedRootSM provided")
}

suspend fun SnackbarHostState.show(ui: UiMessage) = showSnackbar(UiSnackbarVisuals(ui))

data class UiSnackbarVisuals(
    val ui: UiMessage,
    override val actionLabel: String? = null,
) : SnackbarVisuals {

    override val withDismissAction: Boolean = actionLabel != null

    override val duration: SnackbarDuration =
        if (withDismissAction) SnackbarDuration.Indefinite
        else SnackbarDuration.Short

    override val message: String get() = ui.text
}

class ScreenRedRoot() : Screen {

    override val key: ScreenKey = "ScreenRedTopThisWeek"

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val vm: ScreenRedRootSM = getScreenModel()
        val snackbarHostState = remember { SnackbarHostState() }

        val scope = rememberCoroutineScope()

        val haptic = LocalHapticFeedback.current

        val savedRed = vm.hostDI.savedRed

        val percentDownload = vm.hostDI.downloadRed.downloader.percent.collectAsStateWithLifecycle().value
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

        BackHandler { }

        if (savedRed.collections.collectionVisibleDialog) {
            DialogCollection(
                visible = savedRed.collections.collectionVisibleDialog,
                onDismiss = { savedRed.collections.collectionVisibleDialog = false },
                onBlockConfirmed = {
                    savedRed.collections.collectionVisibleDialogCreateNew = true
                },
                onSelectCollection = { collection ->
                    vm.screenModelScope.launch {
                        if ((savedRed.collections.collectionItemGifInfo != null)) {
                            savedRed.collections.addCollection(
                                savedRed.collections.collectionItemGifInfo!!,
                                collection
                            )
                            savedRed.collections.collectionItemGifInfo = null
                            snackBarEvent.success("Элемент добавлен в коллекцию")
                            delay(800)
                            savedRed.collections.collectionVisibleDialog = false
                        }
                    }
                },
                savedRed = savedRed
            )
        }

        if (savedRed.collections.collectionVisibleDialogCreateNew) {
            DaialogNewCollection(
                visible = savedRed.collections.collectionVisibleDialogCreateNew,
                onDismiss = {
                    savedRed.collections.collectionVisibleDialogCreateNew = false
                    savedRed.collections.collectionVisibleDialog = true
                },
                onBlockConfirmed = { collection ->
                    if ((collection != "")) {
                        savedRed.collections.createCollection(collection)
                        savedRed.collections.collectionVisibleDialogCreateNew = false
                    }
                }
            )
        }

        CompositionLocalProvider(LocalRootScreenModel provides vm) {
            Scaffold(
                modifier = Modifier.imePadding(),
                bottomBar = { DownloadIndicator(percentDownload) },
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

                }) {
                Navigator(ScreenRedExplorer())
            }
        }
    }
}

class ScreenRedRootSM @Inject constructor(
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
abstract class ScreenModuleRedRootBlock {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedRootSM::class)
    abstract fun bindScreenRedRootScreenModel(hiltListScreenModel: ScreenRedRootSM): ScreenModel
}
