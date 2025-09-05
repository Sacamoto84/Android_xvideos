package com.client.xvideos.common.traficStatistic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.client.xvideos.App
import com.client.xvideos.l.ThemeL
import kotlin.math.roundToInt

@Composable
fun AppNetworkSpeedMonitor() {

    val context = LocalContext.current
    val application = context.applicationContext as App
    val trafficData by application.networkTrafficMonitor.trafficFlow.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Заголовок
        Text(
            text = "Трафик приложения",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = ThemeL.textColor,
            fontFamily = ThemeL.fontFamilyKarla,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Карточка скорости

            Column(
                modifier = Modifier.padding(0.dp)
            ) {
                Text( text = "Текущая скорость", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = ThemeL.textColor )
                Spacer(modifier = Modifier.height(8.dp))
                SpeedRow( label = "⬇ Скачивание:", value = formatSpeed(trafficData.downloadSpeed) )
                Spacer(modifier = Modifier.height(6.dp))
                SpeedRow( label = "⬆ Загрузка:", value = formatSpeed(trafficData.uploadSpeed) )
            }

            HorizontalDivider(color = ThemeL.grey3, modifier = Modifier.fillMaxWidth(0.75f).align(Alignment.CenterHorizontally))

            Column( modifier = Modifier.padding(0.dp))
            {
                Text( text = "За текущую сессию", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = ThemeL.textColor )
                Spacer(modifier = Modifier.height(8.dp))
                SpeedRow( label = "⬇ Скачано:", value = formatBytes(trafficData.sessionDownloaded) )
                Spacer(modifier = Modifier.height(6.dp))
                SpeedRow( label = "⬆ Загружено:", value = formatBytes(trafficData.sessionUploaded) )
                Spacer(modifier = Modifier.height(6.dp))
                SpeedRow( label = "📊 Всего:",  value = formatBytes(trafficData.sessionDownloaded + trafficData.sessionUploaded) )
            }

            // Карточка общих объемов
            Column( modifier = Modifier.padding(0.dp) )
            {
                Text(text = "За все время", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = ThemeL.textColor )
                Spacer(modifier = Modifier.height(8.dp))
                SpeedRow( label = "⬇ Скачано:", value = formatBytes(trafficData.totalDownloaded) )
                Spacer(modifier = Modifier.height(6.dp))
                SpeedRow( label = "⬆ Загружено:", value = formatBytes(trafficData.totalUploaded) )
                Spacer(modifier = Modifier.height(6.dp))
                SpeedRow( label = "📊 Всего:", value = formatBytes(trafficData.totalDownloaded + trafficData.totalUploaded) )
            }

    }
}

@Preview
@Composable
fun AppNetworkSpeedMonitorPreview() {
    AppNetworkSpeedMonitor()
}

@Composable
fun SpeedRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = ThemeL.textColor
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ThemeL.textColor
        )
    }
}

@Preview
@Composable
fun SpeedRowPreview() {
    SpeedRow( label = "Download:", value = "10.5 MBs" )
}

// Функция для форматирования скорости
fun formatSpeed(bytesPerSecond: Long): String {
    return when {
        bytesPerSecond < 0 -> "0 Bs"
        bytesPerSecond < 1024 -> "$bytesPerSecond Bs"
        bytesPerSecond < 1024 * 1024 -> "${(bytesPerSecond / 1024.0).roundToInt()} KBs"
        bytesPerSecond < 1024 * 1024 * 1024 -> "${(bytesPerSecond / (1024.0 * 1024.0) * 10).roundToInt() / 10.0} MBs"
        else -> "${(bytesPerSecond / (1024.0 * 1024.0 * 1024.0) * 100).roundToInt() / 100.0} GBs"
    }
}

// Функция для форматирования объема данных
fun formatBytes(bytes: Long): String {
    return when {
        bytes < 0 -> "0 B"
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${(bytes / 1024.0).roundToInt()} KB"
        bytes < 1024 * 1024 * 1024 -> "${(bytes / (1024.0 * 1024.0) * 10).roundToInt() / 10.0} MB"
        else -> "${(bytes / (1024.0 * 1024.0 * 1024.0) * 100).roundToInt() / 100.0} GB"
    }
}
