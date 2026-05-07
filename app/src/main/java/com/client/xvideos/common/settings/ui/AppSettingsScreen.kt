package com.client.xvideos.common.settings.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
import com.client.xvideos.common.settings.ui.Config_G_0_4
import com.client.xvideos.common.util.getFolderSize
import com.client.xvideos.common.util.toPrettyCount3
import com.client.xvideos.l.model.ThumbnailsSize
import com.client.xvideos.redgifs.common.saved.SavedRed
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject
import com.client.xvideos.common.applock.AccessCodeVisualTransformation
import com.client.xvideos.common.applock.AppLockRepository
import com.client.xvideos.common.applock.DisableAppLockAutofill
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.coil.CoilImageLoaderFactory
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.common.util.formatBytes
import com.client.xvideos.l.theme.ThemeL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

private enum class AppLockDialogMode {
    SET,
    CHANGE,
    DISABLE
}

private data class StorageStat(
    val key: String,
    val title: String,
    val sizeBytes: Long = 0L,
    val fileCount: Int = 0
)

private data class FolderSnapshot(
    val sizeBytes: Long,
    val fileCount: Int
)

private val EmptyStorageStats = listOf(
    StorageStat(key = "X", title = "X"),
    StorageStat(key = "L", title = "L"),
    StorageStat(key = "R", title = "R")
)

private val WhatsAppGreen = Color(0xFF25D366)
private val SettingsRowIconBackground = Color(0xFF1F2C34)
private val SettingsRowTextSecondary = Color(0xFF9AA7AE)
private val SettingsDividerColor = Color(0xFF263238)

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

        val ramCachePercent = Settings.image_cache_ram_percent.field.collectAsStateWithLifecycle().value
        val diskCacheEnabled = Settings.image_cache_disk_enabled.field.collectAsStateWithLifecycle().value
        val diskCacheSizeMb = Settings.image_cache_disk_size_mb.field.collectAsStateWithLifecycle().value
        var diskCacheSizeBytes by remember { mutableLongStateOf(0L) }
        var storageStats by remember { mutableStateOf(EmptyStorageStats) }

        // RedGifs folder sizes
        var sizeRedTotal by remember { mutableLongStateOf(0L) }
        var sizeRedDownload by remember { mutableLongStateOf(0L) }

        suspend fun refreshDiskCacheSize() {
            diskCacheSizeBytes = withContext(Dispatchers.IO) {
                CoilImageLoaderFactory.imageDiskCacheSizeBytes(context)
            }
        }

        suspend fun refreshStorageStats() {
            storageStats = withContext(Dispatchers.IO) {
                loadStorageStats()
            }
        }

        suspend fun refreshRedSizes() {
            sizeRedTotal = withContext(Dispatchers.IO) {
                getFolderSize(File(AppPath.main))
            }
            sizeRedDownload = withContext(Dispatchers.IO) {
                getFolderSize(File(AppPath.r_cache_download))
            }
        }

        LaunchedEffect(Unit) {
            refreshDiskCacheSize()
            refreshStorageStats()
            refreshRedSizes()
        }

        val l_login = Settings.l_login.field.collectAsStateWithLifecycle().value

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
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
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
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(ThemeL.greyBackground)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
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
                    icon = Icons.Outlined.Image,
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
                    icon = Icons.Outlined.Folder,
                    enabled = diskCacheEnabled,
                    onValueChangeFinished = { value ->
                        Settings.image_cache_disk_size_mb.setValue(value)
                        CoilImageLoaderFactory.recreate(context)
                        SnackBar.success("Размер кеша картинок: $value MB")
                    }
                )

                SettingsDivider()

                SettingsValueRow(
                    icon = Icons.Outlined.Folder,
                    text = "Кеш на диске",
                    value = formatBytes(diskCacheSizeBytes)
                )

                SettingsDivider()

                SettingsButtonRowWithDialog(
                    icon = Icons.Filled.Delete,
                    text = "Сброс кеша картинок",
                    value = "Сброс",
                    textDialogTitle = "Очистить кеш картинок",
                    textDialogBody = "Размер на диске: ${formatBytes(diskCacheSizeBytes)}",
                    textDialogButton = "Очистить",
                    onClick = {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                CoilImageLoaderFactory.clearCache(context)
                            }
                            refreshDiskCacheSize()
                            SnackBar.success("Кеш картинок очищен")
                        }
                    }
                )

                SettingsDivider()

                // ═════════════════════════════════════════════════════════════════
                // XVideos
                // ═════════════════════════════════════════════════════════════════
                SettingsSectionTitle("XVideos")

                val xvideosRow2 = Settings.xvideos_row2.field.collectAsStateWithLifecycle().value
                SettingsSwitchRow(
                    icon = Icons.Outlined.Movie,
                    text = "2 столбика",
                    subtitle = if (xvideosRow2) "Включено" else "Выключено",
                    value = xvideosRow2,
                    onValueChange = { Settings.xvideos_row2.setValue(it) }
                )

                SettingsDivider()

                val xvideosShemale = Settings.xvideos_shemale.field.collectAsStateWithLifecycle().value
                SettingsSwitchRow(
                    icon = Icons.Outlined.Movie,
                    text = "Shemale",
                    subtitle = if (xvideosShemale) "Включено" else "Выключено",
                    value = xvideosShemale,
                    onValueChange = { Settings.xvideos_shemale.setValue(it) }
                )

                SettingsDivider()

                // ═════════════════════════════════════════════════════════════════
                // Luscious
                // ═════════════════════════════════════════════════════════════════
                SettingsSectionTitle("Luscious")

                SettingsButtonRowWithDialog(
                    icon = Icons.Outlined.Image,
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
                    icon = Icons.Outlined.Image,
                    text = "Размер миниатюры",
                    value = currentDisplayName
                )
                // Dropdown for thumbnail size
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

                // ═════════════════════════════════════════════════════════════════
                // RedGifs
                // ═════════════════════════════════════════════════════════════════
                SettingsSectionTitle("RedGifs")

                SettingsValueRow(
                    icon = Icons.Outlined.Folder,
                    text = "Размер всех папок Red",
                    value = sizeRedTotal.toPrettyCount3()
                )

                SettingsDivider()

                SettingsValueRow(
                    icon = Icons.Outlined.Folder,
                    text = "Размер папки Download",
                    value = sizeRedDownload.toPrettyCount3()
                )

                SettingsDivider()

                SettingsButtonRowWithDialog(
                    icon = Icons.Filled.Delete,
                    text = "Очистить папку Download",
                    value = "Очистить",
                    textDialogTitle = "Очистка папки Download",
                    textDialogBody = "Подтвердить очистку: ${sizeRedDownload.toPrettyCount3()}",
                    textDialogButton = "Очистить",
                    onClick = {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                File(AppPath.r_cache_download).deleteRecursively()
                            }
                            refreshRedSizes()
                            SnackBar.success("Папка Download очищена")
                        }
                    }
                )

                SettingsDivider()

                // Niches cache
                val isNichesCacheDownloading = vm.savedRed.nichesCache.isDownloading
                val nichesCacheProgress = vm.savedRed.nichesCache.progress
                val nichesCacheSize = vm.savedRed.nichesCache.size
                val nichesCacheLastModifiedHour = vm.savedRed.nichesCache.lastModifiedHour

                SettingsValueRow(
                    icon = Icons.Outlined.Apps,
                    text = "Кеш Niches",
                    value = "$nichesCacheSize • ${nichesCacheLastModifiedHour}h"
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
                    icon = Icons.Outlined.Apps,
                    text = "Обновить кеш Niches",
                    trailing = {
                        Button(onClick = { vm.savedRed.nichesCache.refresh() }) {
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
    }
}

@Composable
private fun ThumbnailSizeSelector(
    currentValue: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val items = ThumbnailsSize.displayNames

    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 72.dp, vertical = 4.dp)) {
        Button(onClick = { expanded = true }) {
            Text(currentValue)
        }
        androidx.compose.material3.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { name ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onSelected(name)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 72.dp, top = 18.dp, bottom = 6.dp),
        color = SettingsRowTextSecondary,
        style = ThemeL.Type.sectionTitle.copy(color = SettingsRowTextSecondary)
    )
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 72.dp),
        color = SettingsDividerColor
    )
}

@Composable
private fun SettingsListItem(
    icon: ImageVector,
    text: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIcon(icon)
        Spacer(Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = ThemeL.textColor,
                style = ThemeL.Type.rowTitle
            )
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = SettingsRowTextSecondary,
                    style = ThemeL.Type.rowSubtitle.copy(color = SettingsRowTextSecondary)
                )
            }
        }
        if (trailing != null) {
            Spacer(Modifier.width(12.dp))
            trailing()
        }
    }
}

@Composable
private fun SettingsIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(SettingsRowIconBackground),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SettingsRowTextSecondary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun SettingsValueRow(
    icon: ImageVector,
    text: String,
    value: String
) {
    SettingsListItem(
        icon = icon,
        text = text,
        subtitle = value
    )
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    text: String,
    subtitle: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit
) {
    SettingsListItem(
        icon = icon,
        text = text,
        subtitle = subtitle,
        trailing = {
            Switch(
                checked = value,
                onCheckedChange = onValueChange
            )
        }
    )
}

@Composable
private fun SettingsButtonRowWithDialog(
    icon: ImageVector,
    text: String,
    value: String,
    textDialogTitle: String,
    textDialogBody: String,
    textDialogButton: String,
    composable: @Composable () -> Unit = {},
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    DialogButton(
        visible = visible,
        title = textDialogTitle,
        body = textDialogBody,
        buttonText = textDialogButton,
        onDismiss = { visible = false },
        onBlockConfirmed = { onClick() },
        composable = composable
    )

    SettingsListItem(
        icon = icon,
        text = text,
        subtitle = value,
        trailing = {
            TextButton(onClick = { visible = true }) {
                Text(value, color = WhatsAppGreen)
            }
        }
    )
}

@Composable
private fun StorageStatisticsSection(stats: List<StorageStat>) {
    val totalBytes = stats.sumOf { it.sizeBytes }
    SettingsValueRow(
        icon = Icons.Outlined.Folder,
        text = "Всего данных",
        value = formatBytes(totalBytes)
    )

    stats.forEach { stat ->
        val progress = if (totalBytes > 0L) {
            (stat.sizeBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
        StorageProgressRow(stat = stat, progress = progress)
    }
}

@Composable
private fun StorageProgressRow(stat: StorageStat, progress: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIcon(storageIcon(stat.key))
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stat.title,
                    color = ThemeL.textColor,
                    style = ThemeL.Type.rowTitle
                )
                Text(
                    text = formatBytes(stat.sizeBytes),
                    color = SettingsRowTextSecondary,
                    style = ThemeL.Type.rowSubtitle.copy(color = SettingsRowTextSecondary)
                )
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = WhatsAppGreen,
                trackColor = Color(0xFF1E3B32)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${sectionSubtitle(stat.key)} • файлов: ${stat.fileCount}",
                color = SettingsRowTextSecondary,
                style = ThemeL.Type.caption.copy(color = SettingsRowTextSecondary)
            )
        }
    }
}

private fun storageIcon(key: String): ImageVector {
    return when (key) {
        "X" -> Icons.Outlined.Movie
        "L" -> Icons.Outlined.Image
        else -> Icons.Outlined.Apps
    }
}

private fun sectionSubtitle(key: String): String {
    return when (key) {
        "X" -> "XVideos"
        "L" -> "Luscious"
        else -> "RedGifs"
    }
}

private fun loadStorageStats(): List<StorageStat> {
    return listOf(
        "X" to File(AppPath.main, "X"),
        "L" to File(AppPath.main, "L"),
        "R" to File(AppPath.main, "Red")
    ).map { (key, folder) ->
        val snapshot = folder.collectSnapshot()
        StorageStat(
            key = key,
            title = key,
            sizeBytes = snapshot.sizeBytes,
            fileCount = snapshot.fileCount
        )
    }
}

private fun File.collectSnapshot(): FolderSnapshot {
    if (!exists()) return FolderSnapshot(sizeBytes = 0L, fileCount = 0)

    var sizeBytes = 0L
    var fileCount = 0
    walkTopDown().forEach { file ->
        if (file.isFile) {
            sizeBytes += file.length()
            fileCount += 1
        }
    }
    return FolderSnapshot(sizeBytes = sizeBytes, fileCount = fileCount)
}

@Composable
private fun AppLockSettingsSection() {
    val context = LocalContext.current.applicationContext
    val appLockEnabled = Settings.app_lock_enabled.field.collectAsStateWithLifecycle().value
    var passwordSet by remember { mutableStateOf(AppLockRepository.isPasswordSet(context)) }
    var dialogMode by remember { mutableStateOf<AppLockDialogMode?>(null) }
    val enabled = appLockEnabled && passwordSet

    LaunchedEffect(appLockEnabled, passwordSet) {
        if (appLockEnabled && !passwordSet) {
            Settings.app_lock_enabled.setValue(false)
        }
    }

    dialogMode?.let { mode ->
        AppLockPasswordDialog(
            mode = mode,
            onDismiss = { dialogMode = null },
            onComplete = {
                passwordSet = AppLockRepository.isPasswordSet(context)
                dialogMode = null
            }
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        SettingsListItem(
            icon = Icons.Outlined.Lock,
            text = "Блокировка при запуске",
            subtitle = if (enabled) "Включена" else "Выключена",
            trailing = {
                Button(
                    onClick = { dialogMode = if (enabled) AppLockDialogMode.CHANGE else AppLockDialogMode.SET }
                ) {
                    Text(if (enabled) "Изменить" else "Задать")
                }
            }
        )

        if (enabled) {
            SettingsListItem(
                icon = Icons.Outlined.Lock,
                text = "Код доступа",
                subtitle = "Отключить блокировку приложения",
                trailing = {
                    TextButton(onClick = { dialogMode = AppLockDialogMode.DISABLE }) {
                        Text("Отключить", color = Color(0xFFFF7A7A))
                    }
                }
            )
        }
    }
}

@Composable
private fun AppLockPasswordDialog(
    mode: AppLockDialogMode,
    onDismiss: () -> Unit,
    onComplete: () -> Unit
) {
    DisableAppLockAutofill()

    val context = LocalContext.current.applicationContext
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val needsCurrentPassword = mode == AppLockDialogMode.CHANGE || mode == AppLockDialogMode.DISABLE
    val needsNewPassword = mode == AppLockDialogMode.SET || mode == AppLockDialogMode.CHANGE
    val canSubmit = when (mode) {
        AppLockDialogMode.SET -> newPassword.length >= 4 && confirmPassword.isNotBlank()
        AppLockDialogMode.CHANGE -> currentPassword.isNotBlank() && newPassword.length >= 4 && confirmPassword.isNotBlank()
        AppLockDialogMode.DISABLE -> currentPassword.isNotBlank()
    }

    fun submit() {
        errorText = null

        if (needsCurrentPassword && !AppLockRepository.verifyPassword(context, currentPassword)) {
            errorText = "Текущий код доступа не подходит"
            return
        }

        if (needsNewPassword && newPassword != confirmPassword) {
            errorText = "Коды доступа не совпадают"
            return
        }

        when (mode) {
            AppLockDialogMode.SET -> {
                AppLockRepository.setPassword(context, newPassword).onSuccess {
                    SnackBar.success("Код доступа включён")
                    onComplete()
                }.onFailure {
                    errorText = it.message ?: "Не удалось сохранить код доступа"
                }
            }
            AppLockDialogMode.CHANGE -> {
                AppLockRepository.setPassword(context, newPassword).onSuccess {
                    SnackBar.success("Код доступа изменён")
                    onComplete()
                }.onFailure {
                    errorText = it.message ?: "Не удалось изменить код доступа"
                }
            }
            AppLockDialogMode.DISABLE -> {
                AppLockRepository.clearPassword(context)
                SnackBar.success("Код доступа отключён")
                onComplete()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ThemeL.greyBackground,
        title = {
            Text(
                when (mode) {
                    AppLockDialogMode.SET -> "Задать код доступа"
                    AppLockDialogMode.CHANGE -> "Изменить код доступа"
                    AppLockDialogMode.DISABLE -> "Отключить код доступа"
                },
                color = ThemeL.textColor
            )
        },
        text = {
            DisableAppLockAutofill()
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (needsCurrentPassword) {
                    PasswordSettingField(
                        value = currentPassword,
                        onValueChange = {
                            currentPassword = it
                            errorText = null
                        },
                        label = "Текущий код доступа",
                        onDone = { if (canSubmit) submit() }
                    )
                }

                if (needsNewPassword) {
                    PasswordSettingField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            errorText = null
                        },
                        label = "Новый код доступа",
                        onDone = { if (canSubmit) submit() }
                    )
                    PasswordSettingField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            errorText = null
                        },
                        label = "Повтор кода доступа",
                        onDone = { if (canSubmit) submit() }
                    )
                }

                errorText?.let {
                    Text(it, color = Color(0xFFFF7A7A), style = ThemeL.Type.dialogBody.copy(color = Color(0xFFFF7A7A)))
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = canSubmit,
                onClick = { submit() }
            ) {
                Text(
                    when (mode) {
                        AppLockDialogMode.DISABLE -> "Отключить"
                        else -> "Сохранить"
                    }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
private fun PasswordSettingField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    onDone: () -> Unit
) {
    DisableAppLockAutofill()

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        visualTransformation = AccessCodeVisualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        textStyle = ThemeL.Type.body.copy(color = Color.White)
    )
}

@Composable
private fun IntSliderSetting(
    text: String,
    value: Int,
    min: Int,
    max: Int,
    step: Int,
    suffix: String,
    icon: ImageVector = Icons.Outlined.Image,
    enabled: Boolean = true,
    onValueChangeFinished: (Int) -> Unit
) {
    var sliderValue by remember(value) { mutableFloatStateOf(value.toFloat()) }
    val currentValue = snapSliderValue(sliderValue, min, max, step)
    val steps = ((max - min) / step - 1).coerceAtLeast(0)

    Column(modifier = Modifier.fillMaxWidth()) {
        SettingsListItem(
            icon = icon,
            text = text,
            subtitle = "$currentValue$suffix"
        )
        Slider(
            value = currentValue.toFloat(),
            onValueChange = { sliderValue = snapSliderValue(it, min, max, step).toFloat() },
            onValueChangeFinished = {
                onValueChangeFinished(snapSliderValue(sliderValue, min, max, step))
            },
            modifier = Modifier.padding(start = 72.dp, end = 16.dp),
            valueRange = min.toFloat()..max.toFloat(),
            steps = steps,
            enabled = enabled
        )
    }
}

private fun snapSliderValue(value: Float, min: Int, max: Int, step: Int): Int {
    val safeStep = step.coerceAtLeast(1)
    val shifted = (value.roundToInt() - min).coerceAtLeast(0)
    return (min + ((shifted + safeStep / 2) / safeStep) * safeStep).coerceIn(min, max)
}
