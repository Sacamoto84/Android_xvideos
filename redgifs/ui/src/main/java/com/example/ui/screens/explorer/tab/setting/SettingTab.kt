package com.example.ui.screens.explorer.tab.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.client.common.AppPath
import com.client.common.getFolderSize
import com.client.common.util.toPrettyCount
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import java.io.File
import javax.inject.Inject

object  SettingTab : Screen {

    private fun readResolve(): Any = SettingTab

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val vm: ScreenRedExplorerSettingSM = getScreenModel()

        val haptic = LocalHapticFeedback.current

        Column {
            Button(onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)}) { Text("TextHandleMove") }

            Text("Size sizeXvideos: ${vm.sizeXvideos.toPrettyCount()}", color = Color.White)

            Text("Size Download: ${vm.sizeRedDownload.toPrettyCount()}", color = Color.White)

        }
    }
}

class ScreenRedExplorerSettingSM @Inject constructor(
) : ScreenModel {

    var sizeXvideos by mutableLongStateOf(0L)
    var sizeRedDownload by mutableLongStateOf(0L)

    init {
        sizeXvideos = getFolderSize(File(AppPath.main))
        sizeRedDownload = getFolderSize(File(AppPath.cache_download_red))
    }

}





@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedExplorerSetting {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedExplorerSettingSM::class)
    abstract fun bindScreenRedExplorerSettingSreenModel(hiltListScreenModel: ScreenRedExplorerSettingSM): ScreenModel
}