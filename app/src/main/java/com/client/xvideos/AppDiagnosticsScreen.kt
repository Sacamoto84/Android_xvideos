package com.client.xvideos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.common.diagnostics.AppDiagnostics
import com.client.xvideos.common.diagnostics.DiagnosticEvent
import com.client.xvideos.common.diagnostics.DiagnosticsStatus
import com.client.xvideos.common.settings.ui.components.SettingsDivider
import com.client.xvideos.common.settings.ui.components.SettingsListItem
import com.client.xvideos.common.settings.ui.components.SettingsRowTextSecondary
import com.client.xvideos.common.settings.ui.components.SettingsSectionTitle
import com.client.xvideos.common.settings.ui.components.SettingsValueRow
import com.client.xvideos.common.settings.ui.components.WhatsAppGreen
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.common.util.formatBytes
import com.client.xvideos.l.theme.ThemeL
import kotlinx.coroutines.launch

object AppDiagnosticsScreen : Screen {
    private fun readResolve(): Any = AppDiagnosticsScreen

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current.applicationContext
        val clipboard = LocalClipboardManager.current
        val scope = rememberCoroutineScope()
        val events by AppDiagnostics.events.collectAsStateWithLifecycle()
        var status by remember { mutableStateOf<DiagnosticsStatus?>(null) }

        LaunchedEffect(events.size) {
            status = AppDiagnostics.collectStatus(context)
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
                        Icon(painterResource(R.drawable.arrow_down), contentDescription = null, tint = Color.White)
                    }
                    Text(
                        "Диагностика",
                        modifier = Modifier.weight(1f),
                        color = ThemeL.textColor,
                        style = ThemeL.Type.screenTitle.copy(textAlign = TextAlign.Center)
                    )
                    Spacer(Modifier.width(48.dp))
                }
            },
            containerColor = ThemeL.greyBackground
        ) { paddingValues ->
            DiagnosticsBody(
                modifier = Modifier.padding(paddingValues),
                status = status,
                events = events,
                onRefresh = {
                    scope.launch {
                        status = AppDiagnostics.collectStatus(context)
                        SnackBar.success("Диагностика обновлена")
                    }
                },
                onCopyReport = {
                    scope.launch {
                        val report = AppDiagnostics.buildReport(context)
                        clipboard.setText(AnnotatedString(report))
                        status = AppDiagnostics.collectStatus(context)
                        SnackBar.success("Отчёт скопирован")
                    }
                },
                onClear = {
                    AppDiagnostics.clear()
                    scope.launch {
                        status = AppDiagnostics.collectStatus(context)
                    }
                    SnackBar.success("Журнал диагностики очищен")
                }
            )
        }
    }
}

@Composable
private fun DiagnosticsBody(
    modifier: Modifier,
    status: DiagnosticsStatus?,
    events: List<DiagnosticEvent>,
    onRefresh: () -> Unit,
    onCopyReport: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = modifier
            .background(ThemeL.greyBackground)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onCopyReport, modifier = Modifier.weight(1f)) {
                Text("Скопировать отчёт")
            }
            TextButton(onClick = onRefresh) {
                Text("Обновить", color = WhatsAppGreen)
            }
            TextButton(onClick = onClear) {
                Text("Очистить", color = Color(0xFFFF7A7A))
            }
        }

        SettingsDivider()
        SettingsSectionTitle("Состояние")
        DiagnosticsStatusSection(status)

        SettingsDivider()
        SettingsSectionTitle("Последние события")
        if (events.isEmpty()) {
            SettingsListItem(
                icon = R.drawable.memory_24,
                text = "Журнал пуст",
                subtitle = "Ошибки L, HTML challenge и плеера появятся здесь после возникновения"
            )
        } else {
            events.asReversed().take(60).forEach { event ->
                DiagnosticEventRow(event)
                SettingsDivider()
            }
        }
    }
}

@Composable
private fun DiagnosticsStatusSection(status: DiagnosticsStatus?) {
    if (status == null) {
        SettingsListItem(
            icon = R.drawable.memory_24,
            text = "Загрузка",
            subtitle = "Собираю состояние приложения"
        )
        return
    }

    SettingsValueRow(
        icon = R.drawable.icon_luscious,
        text = "L логин",
        value = if (status.lLoginConfigured) "Задан: ${status.lLoginMasked}" else "Не задан"
    )
    SettingsDivider()

    SettingsValueRow(
        icon = R.drawable.key_24,
        text = "L пароль",
        value = if (status.lPasswordConfigured) "Задан" else "Не задан"
    )
    SettingsDivider()

    SettingsValueRow(
        icon = R.drawable.icon_luscious,
        text = "Кэш картинок",
        value = formatBytes(status.imageCacheBytes)
    )
    SettingsDivider()

    SettingsValueRow(
        icon = R.drawable.play_circle,
        text = "Кэш видео",
        value = formatBytes(status.videoCacheBytes)
    )
    SettingsDivider()

    SettingsValueRow(
        icon = R.drawable.icon_red,
        text = "R Download",
        value = formatBytes(status.redDownloadBytes)
    )
    SettingsDivider()

    SettingsValueRow(
        icon = R.drawable.memory_24,
        text = "Событий в журнале",
        value = status.totalEvents.toString()
    )
}

@Composable
private fun DiagnosticEventRow(event: DiagnosticEvent) {
    SettingsListItem(
        icon = when (event.type) {
            "L HTML", "L network", "L album page" -> R.drawable.icon_luscious
            "Player" -> R.drawable.play_circle
            else -> R.drawable.memory_24
        },
        text = "${event.type} • ${event.title}",
        subtitle = buildString {
            append(AppDiagnostics.formatTime(event.timeMs))
            append(" • ")
            append(event.message)
            if (event.albumId != null || event.page != null) {
                append("\n")
                append("albumId=${event.albumId ?: "-"} page=${event.page ?: "-"}")
            }
            event.details?.takeIf { it.isNotBlank() }?.let {
                append("\n")
                append(it)
            }
        },
        trailing = {
            Text(
                text = event.type,
                color = SettingsRowTextSecondary,
                style = ThemeL.Type.caption.copy(color = SettingsRowTextSecondary)
            )
        }
    )
}
