package com.client.xvideos.l.ui.screens.explorer.tab.config

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.common.settings.ui.Config_G_0_4
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.common.traficStatistic.AppNetworkSpeedMonitor
import com.client.xvideos.common.util.formatBytes
import com.client.xvideos.common.util.getFolderSize
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.model.ThumbnailsSize
import com.client.xvideos.l.model.enum.AudiencesType
import com.client.xvideos.l.repository.Repository
import com.client.xvideos.l.ui.screens.explorer.tab.config.atom.ConfigTextAndButtonL
import com.client.xvideos.l.ui.screens.explorer.tab.config.atom.ConfigTextAndButtonWithDialogL
import com.client.xvideos.l.ui.screens.explorer.tab.config.atom.ConfigTextAndCheckBoxL
import com.client.xvideos.l.ui.screens.explorer.tab.config.atom.ConfigTextAndMenuL
import com.client.xvideos.l.ui.screens.explorer.tab.config.atom.ConfigTextL
import com.client.xvideos.ui.theme.XvideosTheme
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class L_ScreenConfigTab : Screen {

    //private fun readResolve(): Any = ScreenLConfigTab

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val haptic = LocalHapticFeedback.current

        val vm: ScreenLExplorerSettingSM = getScreenModel()

        val context = LocalContext.current

        val versionText = try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "Версия: ${pInfo.versionName} (${pInfo.versionCode})"
        } catch (e: PackageManager.NameNotFoundException) {
            "Версия: неизвестна"
        }

        val size = getFolderSize(File(context.cacheDir, "fresco_main_cache").absoluteFile)
        val diskCacheSizeText = formatBytes(size)

        val thumbnailSize = Settings.thumbalistSize.field.collectAsStateWithLifecycle().value
        val currentDisplayName = ThumbnailsSize.fromValue(thumbnailSize)?.displayName ?: "?"

        ScreenLConfigTabContent(
            versionText = versionText,
            diskCacheSizeText = diskCacheSizeText,
            thumbnailSizeDisplayName = currentDisplayName,
            onLogout = vm::logout,
            onThumbnailSizeSelected = { selectedDisplayName ->
                ThumbnailsSize.fromDisplayName(selectedDisplayName)?.apply {
                    Settings.thumbalistSize.setValue(value)
                    SnackBar.success("Размер миниатюры: $displayName")
                }
            }
        )
    }

}


@Composable
private fun ScreenLConfigTabContent(
    versionText: String,
    diskCacheSizeText: String,
    thumbnailSizeDisplayName: String,
    onLogout: () -> Unit,
    onThumbnailSizeSelected: (String) -> Unit
) {
    val savedLogin = Settings.l_login.field.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .background(ThemeL.greyBackground)
            .displayCutoutPadding()
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            "Настройки",
            color = ThemeL.textColor,
            style = ThemeL.Type.screenTitle.copy(textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth()
        )
        HorizontalDivider(color = Color.DarkGray)

        ConfigTextL("I want to see this content:")
        AudiencesType.entries.forEach { it ->
            ConfigTextAndCheckBoxL(it.title, true, { })
        }
        Spacer(Modifier.height(4.dp))
        HorizontalDivider(color = Color.DarkGray)

        ConfigTextAndButtonWithDialogL(
            text = "Профиль L",
            value = if (savedLogin.isBlank()) "Нет" else "Выйти",
            textDialogTitle = "Выйти из профиля L",
            textDialogBody = if (savedLogin.isBlank()) {
                "Вы не авторизованы в L."
            } else {
                "При следующем открытии L нужно будет снова ввести логин и пароль: $savedLogin"
            },
            textDialogButton = "Выйти",
            onClick = {
                onLogout()
            }
        )
        HorizontalDivider(color = Color.DarkGray)

        AppNetworkSpeedMonitor()
        HorizontalDivider(color = Color.DarkGray)
        Spacer(Modifier.height(4.dp))

        ConfigTextAndButtonL("Дисковый кеш: $diskCacheSizeText", "Задать", {}, { })

        ConfigTextAndMenuL(
            "Размер миниатюры",
            thumbnailSizeDisplayName,
            ThumbnailsSize.displayNames,
            onThumbnailSizeSelected
        )
        Spacer(Modifier.height(4.dp))
        HorizontalDivider(color = Color.DarkGray)

        Config_G_0_4("Likes", Settings.l_likesTab_G_0_4)

        HorizontalDivider(color = Color.DarkGray)
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(vertical = 2.dp)
                .height(32.dp)
                .fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            Text(
                versionText,
                style = ThemeL.Type.caption.copy(color = ThemeL.grey2)
            )
        }
    }
}


class ScreenLExplorerSettingSM @Inject constructor(
    private val repository: Repository
) : ScreenModel {

    var sizeXvideos by mutableLongStateOf(0L)
    var sizeRedDownload by mutableLongStateOf(0L)

    fun logout() {
        repository.logout()
        SnackBar.success("Профиль L закрыт")
    }


    init {
        Timber.i("iii ScreenLExplorerSettingSM init")
    }

    override fun onDispose() {
        super.onDispose()
        Timber.i("iii ScreenLExplorerSettingSM onDispose")
    }

}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLExplorerSetting {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenLExplorerSettingSM::class)
    abstract fun bindScreenLExplorerSettingSreenModel(hiltListScreenModel: ScreenLExplorerSettingSM): ScreenModel
}

@Preview
@Composable
private fun ScreenLConfigTabPreview() {
    XvideosTheme {
        ScreenLConfigTabContent(
            versionText = "Версия: 1.0.0 (1)",
            diskCacheSizeText = "128 MB",
            thumbnailSizeDisplayName = "Medium",
            onLogout = {},
            onThumbnailSizeSelected = {}
        )
    }
}
