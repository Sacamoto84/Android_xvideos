package com.client.xvideos.common.traficStatistic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.client.xvideos.App
import com.client.xvideos.common.util.formatBytes
import com.client.xvideos.common.util.formatSpeed
import com.client.xvideos.l.theme.ThemeL

@Composable
fun AppNetworkSpeedMonitorLite() {

    val context = LocalContext.current
    val application = context.applicationContext as App
    val trafficData by application.networkTrafficMonitor.trafficFlow.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier.padding(end = 16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.End
    ) {
        Box {
            Text(
                formatSpeed(trafficData.downloadSpeed),
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = ThemeL.fontFamilyKarla,
                modifier = Modifier.offset(0.5.dp, 0.5.dp)
            )

            Text(
                formatSpeed(trafficData.downloadSpeed),
                color = ThemeL.textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = ThemeL.fontFamilyKarla
            )
        }

        Box {

            Text(
                " / ${formatBytes(trafficData.sessionDownloaded)}",
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = ThemeL.fontFamilyKarla,
                modifier = Modifier.offset(0.5.dp, 0.5.dp)
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

}