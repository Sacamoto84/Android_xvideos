package com.client.xvideos.redgifs.ui.explorer.tab.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.common.settings.ui.ConfigText
import com.client.xvideos.common.settings.ui.ConfigTextAndButtonWithDialog
import com.client.xvideos.common.settings.ui.Config_G_0_4
import com.client.xvideos.common.util.getFolderSize
import com.client.xvideos.common.util.toPrettyCount3
import com.client.xvideos.redgifs.common.ThemeRed
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

object R_ScreenSettingTab : Screen {

    private fun readResolve(): Any = R_ScreenSettingTab

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val vm: ScreenRedExplorerSettingSM = getScreenModel()

        val haptic = LocalHapticFeedback.current

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                vm.sizeXvideos = getFolderSize(File(AppPath.main))
                vm.sizeRedDownload = getFolderSize(File(AppPath.r_cache_download))
            }
        }

        StatelessR_ScreenSettingTab(
            sizeXvideos = vm.sizeXvideos,
            sizeRedDownload = vm.sizeRedDownload,
            onClearDownloadClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                vm.downloadRed.deleteAll {
                    vm.sizeXvideos = getFolderSize(File(AppPath.main))
                    vm.sizeRedDownload = getFolderSize(File(AppPath.r_cache_download))
                }
            },
            isNichesCacheDownloading = vm.savedRed.nichesCache.isDownloading,
            onRefreshNichesCacheClick = { vm.savedRed.nichesCache.refresh() },
            nichesCacheProgress = vm.savedRed.nichesCache.progress,
            nichesCacheSize = vm.savedRed.nichesCache.size,
            nichesCacheLastModifiedHour = vm.savedRed.nichesCache.lastModifiedHour
        )
    }
}

@Composable
fun StatelessR_ScreenSettingTab(
    sizeXvideos: Long,
    sizeRedDownload: Long,
    onClearDownloadClick: () -> Unit,
    isNichesCacheDownloading: Boolean,
    onRefreshNichesCacheClick: () -> Unit,
    nichesCacheProgress: Float,
    nichesCacheSize: Int,
    nichesCacheLastModifiedHour: Long
) {
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
        ConfigText("Размер всех папок Red:", sizeXvideos.toPrettyCount3())
        HorizontalDivider(color = Color.DarkGray)
        ConfigText("Размер папки Download:", sizeRedDownload.toPrettyCount3())
        HorizontalDivider(color = Color.DarkGray)

        ConfigTextAndButtonWithDialog(
            text = "Очистить папку Download",
            value = "Очистить",
            textDialogTitle = "Очистка папки Download",
            textDialogBody = "Подтвердить очистку: ${sizeRedDownload.toPrettyCount3()}",
            textDialogButton = "Очистить",
            onClick = onClearDownloadClick
        )
        HorizontalDivider(color = Color.DarkGray)



        ///////////////////////////////////
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

            if (isNichesCacheDownloading) {
                CircularProgressIndicator(color = ThemeRed.colorBlue, modifier = Modifier.size(40.dp))
            }

            Box(
                modifier = Modifier
                    .height(48.dp)
                    .width(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, ThemeRed.colorTabLevel3, RoundedCornerShape(8.dp))
                    .background(ThemeRed.colorBottomBarDivider)
                    .clickable(onClick = onRefreshNichesCacheClick), contentAlignment = Alignment.Center
            ) {
                Text("Обновить", style = styleTest.copy(fontSize = 18.sp))
            }

        }

        if (isNichesCacheDownloading) {
            LinearProgressIndicator(
                progress = { nichesCacheProgress },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .fillMaxWidth(),
                color = ProgressIndicatorDefaults.linearColor,
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
        } else
            Spacer(modifier = Modifier.height(4.dp))

        ConfigText("Размер", "$nichesCacheSize")
        ConfigText("Возраст", "${nichesCacheLastModifiedHour}h")
        ///////////////////////////////////




        HorizontalDivider(color = Color.DarkGray)

        Config_G_0_4("Explorer", Settings.r_explorerGifsTab_G_0_4)

        Config_G_0_4("Лайки", Settings.r_likesTab_G_0_4)

        Config_G_0_4("Коллекция", Settings.r_collectionTab_G_0_4)


    }
}






@Preview(showBackground = true)
@Composable
fun R_ScreenSettingTabPreview() {
    // Initialize Settings for the preview to avoid UninitializedPropertyAccessException
    // This is necessary because Settings.r_likesTab_G_0_4 accesses a lateinit SharedPreferences
    val context = LocalContext.current
    Settings.init(context.getSharedPreferences("preview_prefs", 0))

    StatelessR_ScreenSettingTab(
        sizeXvideos = 123456789,
        sizeRedDownload = 123456,
        onClearDownloadClick = {},
        isNichesCacheDownloading = true,
        onRefreshNichesCacheClick = {},
        nichesCacheProgress = 0.5f,
        nichesCacheSize = 987,
        nichesCacheLastModifiedHour = 2L
    )
}

val styleTest = TextStyle(
    fontSize = 20.sp,
    color = Color.White,
    fontFamily = ThemeRed.fontFamilyDMsanss
)

class ScreenRedExplorerSettingSM @Inject constructor(
    val downloadRed: com.client.xvideos.redgifs.common.downloader.DownloadRed,
    val savedRed: com.client.xvideos.redgifs.common.saved.SavedRed,
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
