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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.client.xvideos.common.fresco.FrescoUtils
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.common.traficStatistic.AppNetworkSpeedMonitor
import com.client.xvideos.common.util.formatBytes
import com.client.xvideos.common.util.getFolderSize
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.model.ThumbnailsSize
import com.client.xvideos.l.model.enum.AudiencesType
import com.client.xvideos.l.ui.screens.explorer.tab.config.atom.ConfigTextAndButtonL
import com.client.xvideos.l.ui.screens.explorer.tab.config.atom.ConfigTextAndCheckBoxL
import com.client.xvideos.l.ui.screens.explorer.tab.config.atom.ConfigTextAndMenuL
import com.client.xvideos.l.ui.screens.explorer.tab.config.atom.ConfigTextL
import com.client.xvideos.l.ui.screens.explorer.tab.config.atom.ScreenLConfig_Encrypt
import com.client.xvideos.common.snackBar.SnackBarEvent
import com.facebook.drawee.backends.pipeline.Fresco
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import timber.log.Timber
import java.io.File
import javax.inject.Inject


class ScreenLConfigTab : Screen {

    //private fun readResolve(): Any = ScreenLConfigTab

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val haptic = LocalHapticFeedback.current

        val vm: ScreenLExplorerSettingSM = getScreenModel()

        val context = LocalContext.current

        val bitmapCache = Fresco.getImagePipeline().bitmapMemoryCache.sizeInBytes

        val versionText = try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "Версия: ${pInfo.versionName} (${pInfo.versionCode})"
        } catch (e: PackageManager.NameNotFoundException) {
            "Версия: неизвестна"
        }

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
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 24.sp,
                    fontFamily = ThemeL.fontFamilyKarla,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(color = Color.DarkGray)



            ConfigTextL("I want to see this content:")
            AudiencesType.entries.forEach { it ->
                ConfigTextAndCheckBoxL(it.title, true, { })
            }
            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = Color.DarkGray)
            ScreenLConfig_Encrypt()
            HorizontalDivider(color = Color.DarkGray)
            AppNetworkSpeedMonitor()
            HorizontalDivider(color = Color.DarkGray)
            Spacer(Modifier.height(4.dp))
            ConfigTextL("BitmapCache: ${formatBytes(bitmapCache.toLong())}")

            val size = getFolderSize(File(context.cacheDir, "fresco_main_cache").absoluteFile)
            ConfigTextAndButtonL("Дисковый кеш: " + formatBytes(size), "Задать", {}, { })

            ConfigTextAndButtonL( "Очистить кеш картинок", "Очистить", {}, { FrescoUtils.clearCache() })

            // --- Миниатюра ---
            val thumbnailSize = Settings.thumbalistSize.field.collectAsStateWithLifecycle().value
            val currentDisplayName = ThumbnailsSize.fromValue(thumbnailSize)?.displayName ?: "?"
            ConfigTextAndMenuL("Размер миниатюры", currentDisplayName, ThumbnailsSize.displayNames) { selectedDisplayName ->
                ThumbnailsSize.fromDisplayName(selectedDisplayName)?.apply {
                    Settings.thumbalistSize.setValue(value)
                    vm.snackBarEvent.success("Размер миниатюры: $displayName")
                }
            }
            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = Color.DarkGray)

            // ---


            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(vertical = 2.dp)
                    .height(32.dp)
                    .fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                Text(
                    versionText,
                    style = ThemeL.styleTextConfigL.copy(fontSize = 14.sp, color = ThemeL.grey2)
                )
            }

        }


    }

}




class ScreenLExplorerSettingSM @Inject constructor(
    val snackBarEvent: SnackBarEvent
) : ScreenModel {

    var sizeXvideos by mutableLongStateOf(0L)
    var sizeRedDownload by mutableLongStateOf(0L)


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