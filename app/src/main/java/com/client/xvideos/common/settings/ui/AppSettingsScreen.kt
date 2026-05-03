package com.client.xvideos.common.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.common.coil.CoilImageLoaderFactory
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.common.util.formatBytes
import com.client.xvideos.l.theme.ThemeL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

object AppSettingsScreen : Screen {

    private fun readResolve(): Any = AppSettingsScreen

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current.applicationContext
        val scope = rememberCoroutineScope()

        val ramCachePercent = Settings.image_cache_ram_percent.field.collectAsStateWithLifecycle().value
        val diskCacheEnabled = Settings.image_cache_disk_enabled.field.collectAsStateWithLifecycle().value
        val diskCacheSizeMb = Settings.image_cache_disk_size_mb.field.collectAsStateWithLifecycle().value
        var diskCacheSizeBytes by remember { mutableLongStateOf(0L) }

        suspend fun refreshDiskCacheSize() {
            diskCacheSizeBytes = withContext(Dispatchers.IO) {
                CoilImageLoaderFactory.imageDiskCacheSizeBytes(context)
            }
        }

        LaunchedEffect(Unit) {
            refreshDiskCacheSize()
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
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                    Text(
                        "Настройки",
                        modifier = Modifier.weight(1f),
                        color = ThemeL.textColor,
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 24.sp,
                            fontFamily = ThemeL.fontFamilyKarla,
                            textAlign = TextAlign.Center
                        )
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
                HorizontalDivider(color = Color.DarkGray)

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

                HorizontalDivider(color = Color.DarkGray)

                ConfigTextAndCheckBox(
                    text = "Дисковый кеш картинок",
                    value = diskCacheEnabled,
                    onValueChange = { enabled ->
                        Settings.image_cache_disk_enabled.setValue(enabled)
                        CoilImageLoaderFactory.recreate(context)
                        SnackBar.success(if (enabled) "Дисковый кеш включен" else "Дисковый кеш выключен")
                    }
                )

                HorizontalDivider(color = Color.DarkGray)

                IntSliderSetting(
                    text = "Размер дискового кеша",
                    value = CoilImageLoaderFactory.normalizedDiskCacheSizeMb(diskCacheSizeMb),
                    min = CoilImageLoaderFactory.MIN_DISK_CACHE_SIZE_MB,
                    max = CoilImageLoaderFactory.MAX_DISK_CACHE_SIZE_MB,
                    step = 50,
                    suffix = " MB",
                    enabled = diskCacheEnabled,
                    onValueChangeFinished = { value ->
                        Settings.image_cache_disk_size_mb.setValue(value)
                        CoilImageLoaderFactory.recreate(context)
                        SnackBar.success("Размер кеша картинок: $value MB")
                    }
                )

                HorizontalDivider(color = Color.DarkGray)

                ConfigText("Размер кеша на диске", formatBytes(diskCacheSizeBytes))

                HorizontalDivider(color = Color.DarkGray)

                ConfigTextAndButtonWithDialog(
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
            }
        }
    }
}

@Composable
private fun IntSliderSetting(
    text: String,
    value: Int,
    min: Int,
    max: Int,
    step: Int,
    suffix: String,
    enabled: Boolean = true,
    onValueChangeFinished: (Int) -> Unit
) {
    var sliderValue by remember(value) { mutableFloatStateOf(value.toFloat()) }
    val currentValue = snapSliderValue(sliderValue, min, max, step)
    val steps = ((max - min) / step - 1).coerceAtLeast(0)

    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .padding(vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, style = styleTextConfig.copy(color = if (enabled) ThemeL.textColor else Color.Gray))
            Text("$currentValue$suffix", style = styleTextConfig.copy(color = if (enabled) ThemeL.textColor else Color.Gray))
        }
        Slider(
            value = currentValue.toFloat(),
            onValueChange = { sliderValue = snapSliderValue(it, min, max, step).toFloat() },
            onValueChangeFinished = {
                onValueChangeFinished(snapSliderValue(sliderValue, min, max, step))
            },
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
