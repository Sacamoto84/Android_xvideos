package com.client.xvideos.red.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
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
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.Navigator
import com.client.xvideos.red.ThemeRed
import com.client.xvideos.red.common.downloader.ui.DownloadIndicator
import com.client.xvideos.red.common.saved.SavedRed
import com.client.xvideos.red.common.saved.collection.ui.DaialogNewCollection
import com.client.xvideos.red.common.saved.collection.ui.DialogCollection
import com.client.xvideos.red.common.snackBar.SnackBarEvent
import com.client.xvideos.red.common.snackBar.UiMessage
import com.client.xvideos.red.screens.explorer.ScreenRedExplorer
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

        val root: ScreenRedRootSM = getScreenModel()
        val snackbarHostState = remember { SnackbarHostState() }

        val scope = rememberCoroutineScope()

        val haptic = LocalHapticFeedback.current

        LaunchedEffect(Unit) {
            root.snackbarEvents.collect { message ->
                snackbarHostState.showSnackbar(message)
            }
        }

        LaunchedEffect(Unit) {
            SnackBarEvent.messages.receiveAsFlow().collect { message ->
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                snackbarHostState.show(message)
            }
        }

        BackHandler { }

        if (SavedRed.collectionVisibleDialog) {
            DialogCollection(
                visible = SavedRed.collectionVisibleDialog,
                onDismiss = { SavedRed.collectionVisibleDialog = false },
                onBlockConfirmed = {
                    SavedRed.collectionVisibleDialogCreateNew = true
                },
                onSelectCollection = { collection ->
                    root.screenModelScope.launch {
                        if ((SavedRed.collectionItemGifInfo != null)) {
                            SavedRed.addCollection(SavedRed.collectionItemGifInfo!!, collection)
                            SavedRed.collectionItemGifInfo = null
                            SnackBarEvent.success("Элемент добавлен в коллекцию")
                            delay(800)
                            SavedRed.collectionVisibleDialog = false
                        }
                    }
                }
            )
        }

        if (SavedRed.collectionVisibleDialogCreateNew) {
            DaialogNewCollection(
                visible = SavedRed.collectionVisibleDialogCreateNew,
                onDismiss = {
                    SavedRed.collectionVisibleDialogCreateNew = false
                    SavedRed.collectionVisibleDialog = true
                },
                onBlockConfirmed = { collection ->
                    if ((collection != "")) {
                        SavedRed.createCollection(collection)
                        SavedRed.collectionVisibleDialogCreateNew = false
                    }
                }
            )
        }

        CompositionLocalProvider(LocalRootScreenModel provides root) {
            Scaffold(
                bottomBar = { DownloadIndicator() },
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
