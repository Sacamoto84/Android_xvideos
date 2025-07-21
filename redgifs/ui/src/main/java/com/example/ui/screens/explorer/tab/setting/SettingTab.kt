package com.example.ui.screens.explorer.tab.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.client.common.AppPath
import com.client.common.getFolderSize
import com.client.common.sharedPref.Settings
import com.client.common.util.toPrettyCount3
import com.composables.core.HorizontalSeparator
import com.redgifs.common.ThemeRed
import com.redgifs.common.di.HostDI
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

object SettingTab : Screen {

    private fun readResolve(): Any = SettingTab

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val vm: ScreenRedExplorerSettingSM = getScreenModel()

        val haptic = LocalHapticFeedback.current

        val context = LocalContext.current

        val galey0 = vm.settings.gallery_count[0].value.collectAsState().value
        val galey1 = vm.settings.gallery_count[1].value.collectAsState().value
        val galey2 = vm.settings.gallery_count[2].value.collectAsState().value
        val galey3 = vm.settings.gallery_count[3].value.collectAsState().value
        val galey4 = vm.settings.gallery_count[4].value.collectAsState().value

        LaunchedEffect(Unit) {
            vm.sizeXvideos = getFolderSize(File(AppPath.main))
            vm.sizeRedDownload = getFolderSize(File(AppPath.cache_download_red))
        }

        Column(
            modifier = Modifier
                .background(ThemeRed.colorCommonBackground)
                .displayCutoutPadding()
                .fillMaxSize()
        ) {

            Text(
                "Настройки",
                color = Color.White,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontFamily = ThemeRed.fontFamilyDMsanss,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(color = Color.DarkGray)
            ConfigText("Размер всех папок Red:", vm.sizeXvideos.toPrettyCount3())
            HorizontalDivider(color = Color.DarkGray)
            ConfigText("Размер папки Download:", vm.sizeRedDownload.toPrettyCount3())
            HorizontalDivider(color = Color.DarkGray)

            ConfigTextAndButtonWithDialog(
                text = "Очистить папку Download",
                value = "Очистить",
                textDialogTitle = "Очистка папки Download",
                textDialogBody = "Подтвердить очистку: ${vm.sizeRedDownload.toPrettyCount3()}",
                textDialogButton = "Очистить"
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    vm.hostDI.downloadRed.deleteAll{
                    vm.sizeXvideos = getFolderSize(File(AppPath.main))
                    vm.sizeRedDownload = getFolderSize(File(AppPath.cache_download_red))
                }
            }
            HorizontalDivider(color = Color.DarkGray)

            Row(
                modifier = Modifier
                    .padding(start = 8.dp, end = 2.dp)
                    .padding(vertical = 2.dp)
                    .height(48.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Kеш Niches", style = styleTest)

                if (vm.hostDI.savedRed.nichesCache.isDownloading) {
                    CircularProgressIndicator(color = ThemeRed.colorBlue, modifier = Modifier.size(40.dp))
                }

                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .width(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, ThemeRed.colorTabLevel3, RoundedCornerShape(8.dp))
                        .background(ThemeRed.colorBottomBarDivider)
                        .clickable(onClick = { vm.hostDI.savedRed.nichesCache.refresh()}), contentAlignment = Alignment.Center
                ) {
                    Text("Обновить", style = styleTest.copy(fontSize = 18.sp))
                }

            }

            if (vm.hostDI.savedRed.nichesCache.isDownloading) {
                LinearProgressIndicator(
                    progress = { vm.hostDI.savedRed.nichesCache.progress },
                    modifier = Modifier.padding(horizontal = 4.dp).fillMaxWidth(),
                    color = ProgressIndicatorDefaults.linearColor,
                    trackColor = ProgressIndicatorDefaults.linearTrackColor,
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
            }
            else
                Spacer(modifier = Modifier.height(4.dp))

            ConfigText("Размер", "${vm.hostDI.savedRed.nichesCache.size}")
            val minutes = vm.hostDI.savedRed.nichesCache.lastModifiedMinute
            val hour = vm.hostDI.savedRed.nichesCache.lastModifiedHour
            ConfigText("Возраст", "${hour}h")


            HorizontalDivider(color = Color.DarkGray)

            ConfigTextAndCheckBox("Тикток стиль", galey0) { vm.screenModelScope.launch { vm.settings.gallery_count[0].setValue(it) }}
            ConfigTextAndCheckBox("Один столбец", galey1) { vm.screenModelScope.launch { vm.settings.gallery_count[1].setValue(it) }}
            ConfigTextAndCheckBox("Два столбца", galey2) { vm.screenModelScope.launch { vm.settings.gallery_count[2].setValue(it) }}
            ConfigTextAndCheckBox("Три столбца", galey3) { vm.screenModelScope.launch { vm.settings.gallery_count[3].setValue(it) }}
            ConfigTextAndCheckBox("Четыре столбца", galey4) { vm.screenModelScope.launch { vm.settings.gallery_count[4].setValue(it) }}

        }
    }
}

val styleTest = TextStyle(
    fontSize = 20.sp,
    color = Color.White,
    fontFamily = ThemeRed.fontFamilyDMsanss
)

class ScreenRedExplorerSettingSM @Inject constructor(
    val hostDI: HostDI,
    val settings: Settings
) : ScreenModel {

    var sizeXvideos by mutableLongStateOf(0L)
    var sizeRedDownload by mutableLongStateOf(0L)

}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedExplorerSetting {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedExplorerSettingSM::class)
    abstract fun bindScreenRedExplorerSettingSreenModel(hiltListScreenModel: ScreenRedExplorerSettingSM): ScreenModel
}