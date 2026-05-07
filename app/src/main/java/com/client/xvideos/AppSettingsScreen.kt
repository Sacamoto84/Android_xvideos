package com.client.xvideos

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.coil.CoilImageLoaderFactory
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.common.settings.ui.Config_G_0_4
import com.client.xvideos.common.settings.ui.components.AppLockSettingsSection
import com.client.xvideos.common.settings.ui.components.EmptyStorageStats
import com.client.xvideos.common.settings.ui.components.IntSliderSetting
import com.client.xvideos.common.settings.ui.components.SettingsButtonRowWithDialog
import com.client.xvideos.common.settings.ui.components.SettingsDivider
import com.client.xvideos.common.settings.ui.components.SettingsListItem
import com.client.xvideos.common.settings.ui.components.SettingsPreview
import com.client.xvideos.common.settings.ui.components.SettingsSectionTitle
import com.client.xvideos.common.settings.ui.components.SettingsSwitchRow
import com.client.xvideos.common.settings.ui.components.SettingsValueRow
import com.client.xvideos.common.settings.ui.components.StorageStatisticsSection
import com.client.xvideos.common.settings.ui.components.StorageStat
import com.client.xvideos.common.settings.ui.components.ThumbnailSizeSelector
import com.client.xvideos.common.settings.ui.components.loadStorageStats
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.common.util.getFolderSize
import com.client.xvideos.common.util.toPrettyCount3
import com.client.xvideos.l.model.ThumbnailsSize
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.r.common.saved.SavedRed
import com.client.xvideos.ui.theme.XvideosTheme
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class AppSettingsSM @Inject constructor(
    val savedRed: SavedRed
) : ScreenModel

@Module
@InstallIn(SingletonComponent::class)
abstract class AppSettingsModule {
    @Binds
    @IntoMap
    @ScreenModelKey(AppSettingsSM::class)
    abstract fun bindAppSettingsSM(sm: AppSettingsSM): ScreenModel
}

object AppSettingsScreen : Screen {

    private fun readResolve(): Any = AppSettingsScreen

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current.applicationContext
        val scope = rememberCoroutineScope()
        val vm: AppSettingsSM = getScreenModel()

        var diskCacheSizeBytes by remember { mutableLongStateOf(0L) }
        var storageStats by remember { mutableStateOf(EmptyStorageStats) }
        var sizeRedTotal by remember { mutableLongStateOf(0L) }
        var sizeRedDownload by remember { mutableLongStateOf(0L) }

        suspend fun refreshDiskCacheSize() {
            diskCacheSizeBytes = withContext(Dispatchers.IO) {
                CoilImageLoaderFactory.imageDiskCacheSizeBytes(context)
            }
        }

        suspend fun refreshStorageStats() {
            storageStats = withContext(Dispatchers.IO) { loadStorageStats() }
        }

        suspend fun refreshRedSizes() {
            sizeRedTotal = withContext(Dispatchers.IO) { getFolderSize(File(AppPath.main)) }
            sizeRedDownload = withContext(Dispatchers.IO) { getFolderSize(File(AppPath.r_cache_download)) }
        }

        LaunchedEffect(Unit) {
            refreshDiskCacheSize()
            refreshStorageStats()
            refreshRedSizes()
        }

        AppSettingsScreenContent(
            onBack = { navigator.pop() },
            diskCacheSizeBytes = diskCacheSizeBytes,
            storageStats = storageStats,
            sizeRedTotal = sizeRedTotal,
            sizeRedDownload = sizeRedDownload,
            onClearImageCache = {
                scope.launch {
                    withContext(Dispatchers.IO) { CoilImageLoaderFactory.clearCache(context) }
                    refreshDiskCacheSize()
                    SnackBar.success("Кеш картинок очищен")
                }
            },
            onClearDownload = {
                scope.launch {
                    withContext(Dispatchers.IO) { File(AppPath.r_cache_download).deleteRecursively() }
                    refreshRedSizes()
                    SnackBar.success("Папка Download очищена")
                }
            },
            savedRed = vm.savedRed,
            context = context
        )
    }
}

@Composable
private fun AppSettingsScreenContent(
    onBack: () -> Unit,
    diskCacheSizeBytes: Long,
    storageStats: List<StorageStat>,
    sizeRedTotal: Long,
    sizeRedDownload: Long,
    onClearImageCache: () -> Unit,
    onClearDownload: () -> Unit,
    savedRed: SavedRed?,
    context: Context
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .displayCutoutPadding()
                    .height(52.dp)
                    .fillMaxWidth()
                    .background(ThemeL.greyBackground),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(painterResource(R.drawable.arrow_down), contentDescription = null, tint = Color.White)
                }
                Text(
                    "Настройки",
                    modifier = Modifier.weight(1f),
                    color = ThemeL.textColor,
                    style = ThemeL.Type.screenTitle.copy(textAlign = TextAlign.Center)
                )
                Spacer(Modifier.width(48.dp))
            }
        },
        containerColor = ThemeL.greyBackground
    ) { paddingValues ->
        AppSettingsScreenBody(
            modifier = Modifier.padding(paddingValues),
            diskCacheSizeBytes = diskCacheSizeBytes,
            storageStats = storageStats,
            sizeRedTotal = sizeRedTotal,
            sizeRedDownload = sizeRedDownload,
            onClearImageCache = onClearImageCache,
            onClearDownload = onClearDownload,
            savedRed = savedRed,
            context = context
        )
    }
}

@Composable
private fun AppSettingsScreenBody(
    modifier: Modifier = Modifier,
    diskCacheSizeBytes: Long,
    storageStats: List<StorageStat>,
    sizeRedTotal: Long,
    sizeRedDownload: Long,
    onClearImageCache: () -> Unit,
    onClearDownload: () -> Unit,
    savedRed: SavedRed?,
    context: Context
) {
    val ramCachePercent = Settings.image_cache_ram_percent.field.collectAsStateWithLifecycle().value
    val diskCacheEnabled = Settings.image_cache_disk_enabled.field.collectAsStateWithLifecycle().value
    val diskCacheSizeMb = Settings.image_cache_disk_size_mb.field.collectAsStateWithLifecycle().value
    val l_login = Settings.l_login.field.collectAsStateWithLifecycle().value

    val isNichesCacheDownloading = savedRed?.nichesCache?.isDownloading ?: false
    val nichesCacheProgress = savedRed?.nichesCache?.progress ?: 0f
    val nichesCacheSize = savedRed?.nichesCache?.size ?: 0
    val nichesCacheLastModifiedHour = savedRed?.nichesCache?.lastModifiedHour ?: 0L

    Column(
        modifier = modifier
            .background(ThemeL.greyBackground)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {

        SettingsDivider()
        SettingsSectionTitle("Защита")
        AppLockSettingsSection()
        SettingsDivider()

        SettingsSectionTitle("Статистика")
        StorageStatisticsSection(storageStats)
        SettingsDivider()

        SettingsSectionTitle("Кеш")
        IntSliderSetting(
            text = "RAM кеш картинок",
            value = CoilImageLoaderFactory.normalizedRamCachePercent(ramCachePercent),
            min = CoilImageLoaderFactory.MIN_RAM_CACHE_PERCENT,
            max = CoilImageLoaderFactory.MAX_RAM_CACHE_PERCENT,
            step = 1,
            suffix = "%",
            onValueChangeFinished = { value ->
                Settings.image_cache_ram_percent.setValue(value)
                CoilImageLoaderFactory.recreate(context)
                SnackBar.success("RAM кеш картинок: $value%")
            }
        )
        SettingsDivider()

        SettingsSwitchRow(
            icon = R.drawable.icon_luscious,
            text = "Дисковый кеш картинок",
            subtitle = if (diskCacheEnabled) "Включён" else "Выключен",
            value = diskCacheEnabled,
            onValueChange = { enabled ->
                Settings.image_cache_disk_enabled.setValue(enabled)
                CoilImageLoaderFactory.recreate(context)
                SnackBar.success(if (enabled) "Дисковый кеш включен" else "Дисковый кеш выключен")
            }
        )
        SettingsDivider()

        IntSliderSetting(
            text = "Размер дискового кеша",
            value = CoilImageLoaderFactory.normalizedDiskCacheSizeMb(diskCacheSizeMb),
            min = CoilImageLoaderFactory.MIN_DISK_CACHE_SIZE_MB,
            max = CoilImageLoaderFactory.MAX_DISK_CACHE_SIZE_MB,
            step = 50,
            suffix = " MB",
            icon = R.drawable.icon_red,
            enabled = diskCacheEnabled,
            onValueChangeFinished = { value ->
                Settings.image_cache_disk_size_mb.setValue(value)
                CoilImageLoaderFactory.recreate(context)
                SnackBar.success("Размер кеша картинок: $value MB")
            }
        )
        SettingsDivider()

        SettingsValueRow(
            icon = R.drawable.icon_red,
            text = "Кеш на диске",
            value = com.client.xvideos.common.util.formatBytes(diskCacheSizeBytes)
        )
        SettingsDivider()

        SettingsButtonRowWithDialog(
            icon = R.drawable.icon_red,
            text = "Сброс кеша картинок",
            value = "Сброс",
            textDialogTitle = "Очистить кеш картинок",
            textDialogBody = "Размер на диске: ${com.client.xvideos.common.util.formatBytes(diskCacheSizeBytes)}",
            textDialogButton = "Очистить",
            onClick = onClearImageCache
        )
        SettingsDivider()

        // XVideos
        SettingsSectionTitle("XVideos")
        val xvideosRow2 = Settings.xvideos_row2.field.collectAsStateWithLifecycle().value
        SettingsSwitchRow(
            icon = R.drawable.icon_xvideos_white,
            text = "2 столбика",
            subtitle = if (xvideosRow2) "Включено" else "Выключено",
            value = xvideosRow2,
            onValueChange = { Settings.xvideos_row2.setValue(it) }
        )
        SettingsDivider()

        val xvideosShemale = Settings.xvideos_shemale.field.collectAsStateWithLifecycle().value
        SettingsSwitchRow(
            icon = R.drawable.icon_xvideos_white,
            text = "Shemale",
            subtitle = if (xvideosShemale) "Включено" else "Выключено",
            value = xvideosShemale,
            onValueChange = { Settings.xvideos_shemale.setValue(it) }
        )
        SettingsDivider()

        // Luscious
        SettingsSectionTitle("Luscious")
        SettingsButtonRowWithDialog(
            icon = R.drawable.icon_luscious,
            text = "Профиль L",
            value = if (l_login.isBlank()) "Нет" else "Выйти",
            textDialogTitle = "Выйти из профиля L",
            textDialogBody = if (l_login.isBlank()) {
                "Вы не авторизованы в L."
            } else {
                "При следующем открытии L нужно будет снова ввести логин и пароль: $l_login"
            },
            textDialogButton = "Выйти",
            onClick = {
                Settings.l_login.setValue("")
                Settings.l_pass.setValue("")
                SnackBar.success("Профиль L закрыт")
            }
        )
        SettingsDivider()

        val thumbnailSize = Settings.thumbalistSize.field.collectAsStateWithLifecycle().value
        val currentDisplayName = ThumbnailsSize.fromValue(thumbnailSize)?.displayName ?: "?"
        SettingsValueRow(
            icon = R.drawable.icon_luscious,
            text = "Размер миниатюры",
            value = currentDisplayName
        )
        ThumbnailSizeSelector(
            currentValue = currentDisplayName,
            onSelected = { selectedDisplayName ->
                ThumbnailsSize.fromDisplayName(selectedDisplayName)?.apply {
                    Settings.thumbalistSize.setValue(value)
                    SnackBar.success("Размер миниатюры: $displayName")
                }
            }
        )
        SettingsDivider()

        Config_G_0_4("L Gifs", Settings.l_gifsTab_G_0_4)
        Config_G_0_4("L Likes", Settings.l_likesTab_G_0_4)
        Config_G_0_4("L Collection", Settings.l_collectionTab_G_0_4)
        SettingsDivider()

        // RedGifs
        SettingsSectionTitle("RedGifs")
        SettingsValueRow(
            icon = R.drawable.icon_red,
            text = "Размер всех папок Red",
            value = sizeRedTotal.toPrettyCount3()
        )
        SettingsDivider()

        SettingsValueRow(
            icon = R.drawable.icon_red,
            text = "Размер папки Download",
            value = sizeRedDownload.toPrettyCount3()
        )
        SettingsDivider()

        SettingsButtonRowWithDialog(
            icon = R.drawable.icon_red,
            text = "Очистить папку Download",
            value = "Очистить",
            textDialogTitle = "Очистка папки Download",
            textDialogBody = "Подтвердить очистку: ${sizeRedDownload.toPrettyCount3()}",
            textDialogButton = "Очистить",
            onClick = onClearDownload
        )
        SettingsDivider()

        SettingsValueRow(
            icon = R.drawable.icon_red,
            text = "Кеш Niches",
            value = "$nichesCacheSize \u2022 ${nichesCacheLastModifiedHour}h"
        )

        if (isNichesCacheDownloading) {
            LinearProgressIndicator(
                progress = { nichesCacheProgress },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                color = ProgressIndicatorDefaults.linearColor,
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
        }

        SettingsListItem(
            icon = R.drawable.icon_red,
            text = "Обновить кеш Niches",
            trailing = {
                Button(onClick = { savedRed?.nichesCache?.refresh() }) {
                    Text("Обновить")
                }
            }
        )
        SettingsDivider()

        Config_G_0_4("R Explorer", Settings.r_explorerGifsTab_G_0_4)
        Config_G_0_4("R Лайки", Settings.r_likesTab_G_0_4)
        Config_G_0_4("R Коллекция", Settings.r_collectionTab_G_0_4)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF353535,
    device = "spec:width=1080px,height=23400px,dpi=440"
)
@Composable
private fun AppSettingsScreenPreview() {
    val context = LocalContext.current
    Settings.init(context.getSharedPreferences("preview_prefs", 0))
    XvideosTheme {
        Column {
            Row(
                modifier = Modifier
                    .height(52.dp)
                    .fillMaxWidth()
                    .background(ThemeL.greyBackground),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(48.dp))
                Text(
                    "Настройки",
                    modifier = Modifier.weight(1f),
                    color = ThemeL.textColor,
                    style = ThemeL.Type.screenTitle.copy(textAlign = TextAlign.Center)
                )
                Spacer(Modifier.width(48.dp))
            }
            AppSettingsScreenBody(
                diskCacheSizeBytes = 128_000_000L,
                storageStats = EmptyStorageStats,
                sizeRedTotal = 512_000_000L,
                sizeRedDownload = 64_000_000L,
                onClearImageCache = {},
                onClearDownload = {},
                savedRed = null,
                context = context.applicationContext
            )
        }
    }
}
