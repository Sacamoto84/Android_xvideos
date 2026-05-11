package com.client.xvideos

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.OptIn
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.client.xvideos.common.settings.ui.components.SettingsSwitchRow
import com.client.xvideos.common.settings.ui.components.SettingsValueRow
import com.client.xvideos.common.settings.ui.components.StorageStatisticsSection
import com.client.xvideos.common.settings.ui.components.StorageStat
import com.client.xvideos.common.settings.ui.components.ThumbnailSizeSelector
import com.client.xvideos.common.settings.ui.components.WhatsAppGreen
import com.client.xvideos.common.settings.ui.components.loadStorageStats
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.common.util.formatBytes
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

        var imageCacheSizeBytes by remember { mutableLongStateOf(0L) }
        var storageStats by remember { mutableStateOf(EmptyStorageStats) }
        var sizeRedTotal by remember { mutableLongStateOf(0L) }
        var sizeRedDownload by remember { mutableLongStateOf(0L) }

        suspend fun refreshImageCacheSize() {
            imageCacheSizeBytes = withContext(Dispatchers.IO) {
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
            refreshImageCacheSize()
            refreshStorageStats()
            refreshRedSizes()
        }

        AppSettingsScreenContent(
            onBack = { navigator.pop() },
            imageCacheSizeBytes = imageCacheSizeBytes,
            storageStats = storageStats,
            sizeRedTotal = sizeRedTotal,
            sizeRedDownload = sizeRedDownload,
            onClearImageCache = {
                scope.launch {
                    withContext(Dispatchers.IO) { CoilImageLoaderFactory.clearCache(context) }
                    refreshImageCacheSize()
                    SnackBar.success("Кэш картинок очищен")
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
    imageCacheSizeBytes: Long,
    storageStats: List<StorageStat>,
    sizeRedTotal: Long,
    sizeRedDownload: Long,
    onClearImageCache: () -> Unit,
    onClearDownload: () -> Unit,
    savedRed: SavedRed?,
    context: Context
) {
    var currentPage by rememberSaveable { mutableStateOf(SettingsPage.Main) }
    val closeCurrentPage = {
        if (currentPage == SettingsPage.Main) {
            onBack()
        } else {
            currentPage = SettingsPage.Main
        }
    }

    BackHandler(enabled = currentPage != SettingsPage.Main) {
        currentPage = SettingsPage.Main
    }

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
                IconButton(onClick = closeCurrentPage) {
                    Icon(painterResource(R.drawable.arrow_down), contentDescription = null, tint = Color.White)
                }
                Text(
                    currentPage.title,
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
            currentPage = currentPage,
            onOpenPage = { currentPage = it },
            imageCacheSizeBytes = imageCacheSizeBytes,
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
    currentPage: SettingsPage = SettingsPage.Main,
    onOpenPage: (SettingsPage) -> Unit = {},
    imageCacheSizeBytes: Long,
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
        if (currentPage == SettingsPage.Main) {
            SettingsPage.detailPages.forEachIndexed { index, page ->
                if (index > 0) SettingsDivider()
                SettingsNavigationRow(
                    page = page,
                    onClick = { onOpenPage(page) }
                )
            }
        } else {
            SettingsDivider()
            when (currentPage) {
                SettingsPage.Main -> Unit
                SettingsPage.Privacy -> AppLockSettingsSection()
                SettingsPage.Cache -> CacheSettingsSection(
                    ramCachePercent = ramCachePercent,
                    diskCacheEnabled = diskCacheEnabled,
                    diskCacheSizeMb = diskCacheSizeMb,
                    imageCacheSizeBytes = imageCacheSizeBytes,
                    onClearImageCache = onClearImageCache,
                    context = context
                )
                SettingsPage.L -> LSettingsSection(lLogin = l_login)
                SettingsPage.Red -> RSettingsSection(
                    sizeRedTotal = sizeRedTotal,
                    sizeRedDownload = sizeRedDownload,
                    onClearDownload = onClearDownload,
                    savedRed = savedRed,
                    isNichesCacheDownloading = isNichesCacheDownloading,
                    nichesCacheProgress = nichesCacheProgress,
                    nichesCacheSize = nichesCacheSize,
                    nichesCacheLastModifiedHour = nichesCacheLastModifiedHour
                )
                SettingsPage.X -> XSettingsSection()
                SettingsPage.Storage -> StorageStatisticsSection(storageStats)
            }
        }
    }
}

private enum class SettingsPage(
    val title: String,
    @DrawableRes val icon: Int,
    val subtitle: String
) {
    Main(
        title = "Настройки",
        icon = R.drawable.memory_24,
        subtitle = ""
    ),
    Privacy(
        title = "Приватность",
        icon = R.drawable.icon_red,
        subtitle = "Пароль и блокировка приложения"
    ),
    Cache(
        title = "Кэш",
        icon = R.drawable.hard_disk_24,
        subtitle = "RAM, изображения и очистка"
    ),
    L(
        title = "L",
        icon = R.drawable.icon_luscious,
        subtitle = "Профиль, миниатюры и колонки"
    ),
    Red(
        title = "R",
        icon = R.drawable.icon_red,
        subtitle = "Размеры папок, Downloads и Niches cache"
    ),
    X(
        title = "X",
        icon = R.drawable.icon_xvideos_white,
        subtitle = "Отображение и фильтры X"
    ),
    Storage(
        title = "Хранилище",
        icon = R.drawable.hard_drive_2_24,
        subtitle = "Статистика по X, L и R"
    );

    companion object {
        val detailPages: List<SettingsPage>
            get() = values().filter { it != Main }
    }
}

@Composable
private fun SettingsNavigationRow(
    page: SettingsPage,
    onClick: () -> Unit
) {
    SettingsListItem(
        icon = page.icon,
        text = page.title,
        subtitle = page.subtitle,
        onClick = onClick,
        trailing = {
            TextButton(onClick = onClick) {
                Text("Открыть", color = WhatsAppGreen)
            }
        }
    )
}

@Composable
private fun CacheSettingsSection(
    ramCachePercent: Int,
    diskCacheEnabled: Boolean,
    diskCacheSizeMb: Int,
    imageCacheSizeBytes: Long,
    onClearImageCache: () -> Unit,
    context: Context
) {
    IntSliderSetting(
        text = "RAM кэш картинок",
        value = CoilImageLoaderFactory.normalizedRamCachePercent(ramCachePercent),
        min = CoilImageLoaderFactory.MIN_RAM_CACHE_PERCENT,
        max = CoilImageLoaderFactory.MAX_RAM_CACHE_PERCENT,
        step = 1,
        suffix = "%",
        icon = R.drawable.memory_24,
        onValueChangeFinished = { value ->
            Settings.image_cache_ram_percent.setValue(value)
            CoilImageLoaderFactory.recreate(context)
            SnackBar.success("RAM кэш картинок: $value%")
        }
    )
    SettingsDivider()

    SettingsSwitchRow(
        icon = R.drawable.hard_disk_24,
        text = "Дисковый кэш картинок",
        subtitle = if (diskCacheEnabled) "Включён" else "Выключен",
        value = diskCacheEnabled,
        onValueChange = { enabled ->
            Settings.image_cache_disk_enabled.setValue(enabled)
            CoilImageLoaderFactory.recreate(context)
            SnackBar.success(if (enabled) "Дисковый кэш включен" else "Дисковый кэш выключен")
        }
    )
    SettingsDivider()

    IntSliderSetting(
        text = "Лимит кэша картинок",
        value = CoilImageLoaderFactory.normalizedDiskCacheSizeMb(diskCacheSizeMb),
        min = CoilImageLoaderFactory.MIN_DISK_CACHE_SIZE_MB,
        max = CoilImageLoaderFactory.MAX_DISK_CACHE_SIZE_MB,
        step = 50,
        suffix = " MB",
        icon = R.drawable.hard_drive_2_24,
        enabled = diskCacheEnabled,
        onValueChangeFinished = { value ->
            Settings.image_cache_disk_size_mb.setValue(value)
            CoilImageLoaderFactory.recreate(context)
            SnackBar.success("Размер кэша картинок: $value MB")
        }
    )
    SettingsDivider()

    SettingsValueRow(
        icon = R.drawable.icon_luscious,
        text = "Кэш картинок на диске",
        value = formatBytes(imageCacheSizeBytes)
    )
    SettingsDivider()

    SettingsButtonRowWithDialog(
        icon = R.drawable.icon_luscious,
        text = "Очистить кэш картинок",
        value = "Очистить",
        textDialogTitle = "Очистить кэш картинок",
        textDialogBody = "Размер на диске: ${formatBytes(imageCacheSizeBytes)}",
        textDialogButton = "Очистить",
        onClick = onClearImageCache
    )
}

@Composable
private fun LSettingsSection(lLogin: String) {
    SettingsButtonRowWithDialog(
        icon = R.drawable.icon_luscious,
        text = "Профиль L",
        value = if (lLogin.isBlank()) "Нет" else "Выйти",
        textDialogTitle = "Выйти из профиля L",
        textDialogBody = if (lLogin.isBlank()) {
            "Вы не авторизованы в L."
        } else {
            "При следующем открытии L нужно будет снова ввести логин и пароль: $lLogin"
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
}

@Composable
private fun RSettingsSection(
    sizeRedTotal: Long,
    sizeRedDownload: Long,
    onClearDownload: () -> Unit,
    savedRed: SavedRed?,
    isNichesCacheDownloading: Boolean,
    nichesCacheProgress: Float,
    nichesCacheSize: Int,
    nichesCacheLastModifiedHour: Long
) {
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
        text = "Кэш Niches",
        value = "$nichesCacheSize \u2022 ${nichesCacheLastModifiedHour}h"
    )

    if (isNichesCacheDownloading) {
        LinearProgressIndicator(
            progress = { nichesCacheProgress },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            color = WhatsAppGreen,
            trackColor = Color(0xFF1E3B32),
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )
    }
    SettingsDivider()

    SettingsListItem(
        icon = R.drawable.icon_red,
        text = "Обновить кэш Niches",
        subtitle = if (isNichesCacheDownloading) "Идёт обновление" else "Данные для поиска и фильтров R",
        trailing = {
            Button(
                enabled = savedRed != null && !isNichesCacheDownloading,
                onClick = { savedRed?.nichesCache?.refresh() }
            ) {
                Text("Обновить")
            }
        }
    )
}

@Composable
private fun XSettingsSection() {
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
                imageCacheSizeBytes = 128_000_000L,
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
