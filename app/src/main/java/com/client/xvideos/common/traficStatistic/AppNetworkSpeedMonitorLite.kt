package com.client.xvideos.common.traficStatistic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.client.xvideos.App
import com.client.xvideos.l.ThemeL

@Composable
fun AppNetworkSpeedMonitorLite() {

    val context = LocalContext.current
    val application = context.applicationContext as App
    val trafficData by application.networkTrafficMonitor.trafficFlow.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier.padding(end = 16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.End
    ) {
        Text(
            formatSpeed(trafficData.downloadSpeed),
            color = ThemeL.textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = ThemeL.fontFamilyKarla
        )

        Text(
            " / ${formatBytes(trafficData.sessionDownloaded)}",
            color = ThemeL.textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = ThemeL.fontFamilyKarla
        )
    }

}