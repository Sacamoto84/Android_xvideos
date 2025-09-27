package com.client.xvideos.redgifs.ui

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.Navigator
import com.client.xvideos.common.collectionDB.ui.DaialogNewCollection
import com.client.xvideos.redgifs.common.di.HostDI
import com.client.xvideos.redgifs.ui.explorer.ScreenRedExplorer
import com.redgifs.common.downloader.ui.DownloadIndicator
import com.redgifs.common.saved.DialogCollection
import com.client.xvideos.common.eventBus.UiMessage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
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

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val vm: ScreenRedRootSM = getScreenModel()

        val scope = rememberCoroutineScope()

        val haptic = LocalHapticFeedback.current

        val savedRed = vm.hostDI.savedRed

        val percentDownload = vm.hostDI.downloadRed.downloader.percent.collectAsStateWithLifecycle().value
        val snackBarEvent = vm.hostDI.snackBarEvent


        BackHandler { Timber.i("iii BackHandler Root") }

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
                bottomBar = { DownloadIndicator(percentDownload) }) {
                Navigator(ScreenRedExplorer())
            }
        }
    }
}

class ScreenRedRootSM @Inject constructor(
    val hostDI: HostDI
) : ScreenModel {

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedRootBlock {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedRootSM::class)
    abstract fun bindScreenRedRootScreenModel(hiltListScreenModel: ScreenRedRootSM): ScreenModel
}
