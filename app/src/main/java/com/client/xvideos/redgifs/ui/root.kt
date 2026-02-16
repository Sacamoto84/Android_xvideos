package com.client.xvideos.redgifs.ui

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.Navigator
import com.client.xvideos.common.transition.SlideTransition
import com.client.xvideos.redgifs.common.di.HostDI
import com.client.xvideos.redgifs.common.saved.DialogCollection
import com.client.xvideos.redgifs.ui.explorer.ScreenRedExplorer
import com.client.xvideos.screen.LocalRootScreenModel
import com.client.xvideos.screen.ScreenRootSM
import com.redgifs.common.block.ui.DialogBlock
import com.redgifs.common.downloader.ui.DownloadIndicator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import timber.log.Timber
import javax.inject.Inject

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

        BackHandler { Timber.i("iii BackHandler Root") }


        //Диалог коллекции
        if (savedRed.collections.collectionVisibleDialog) {
            DialogCollection(
                visible = savedRed.collections.collectionVisibleDialog,
                onDismiss = { savedRed.collections.collectionVisibleDialog = false },
                onBlockConfirmed = {
                    savedRed.collections.collectionVisibleDialogCreateNew = true
                },
                onSelectCollection = { collection ->
                    savedRed.collections.addCollection(
                        savedRed.collections.collectionItemGifInfo!!,
                        collection
                    )
                    savedRed.collections.collectionVisibleDialog = false
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                savedRed = savedRed
            )
        }

        //Диалог для блокировки
        if (vm.hostDI.block.blockVisibleDialog) {
            DialogBlock(
                visible = vm.hostDI.block.blockVisibleDialog,
                onDismiss = { vm.hostDI.block.blockVisibleDialog = false },
                onBlockConfirmed = {
                    // if (blockItem != null) {
                    //     vm.hostDI.block.blockItem(blockItem!!)
                    //     blockItem = null
                    // }
                }
            )
        }

        CompositionLocalProvider(LocalRootScreenModel provides ScreenRootSM()) {
            Scaffold(
                modifier = Modifier.imePadding(),
                bottomBar = { DownloadIndicator(percentDownload) }) {
                Navigator(ScreenRedExplorer()) { navigator ->
                    SlideTransition(navigator)
                }
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
