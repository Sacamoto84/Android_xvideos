package com.client.xvideos.common.traficStatistic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.client.xvideos.App
import com.client.xvideos.common.coil.CoilProgressItem
import com.client.xvideos.common.coil.CoilProgressManager
import com.client.xvideos.common.util.formatBytes
import com.client.xvideos.common.util.formatSpeed
import com.client.xvideos.l.theme.ThemeL
import kotlinx.coroutines.delay

@Composable
fun AppNetworkSpeedMonitorLite() {

    //return

    val context = LocalContext.current
    val application = context.applicationContext as App
    val trafficData by application.networkTrafficMonitor.trafficFlow.collectAsStateWithLifecycle()

//    val rawProgress by remember{
//        derivedStateOf {
//            CoilProgressManager.progressMap.filter { !it.value.done }.size
//        }
//    }
//
////    // Debounce: обновляем UI не чаще 100–200 мс
//    val progress by produceState(rawProgress) {
//        while (true) {
//            value = rawProgress
//            delay(200)
//        }
//    }
//
    val formattedSpeed = remember (trafficData.downloadSpeed) {
        formatSpeed(trafficData.downloadSpeed)
    }

    val formattedBytes = remember(trafficData.sessionDownloaded) {
        formatBytes(trafficData.sessionDownloaded)
    }

    Row(
        modifier = Modifier.padding(end = 16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.End
    ) {

        Box {
            Text(
                formattedSpeed,
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = ThemeL.fontFamilyKarla,
                modifier = Modifier.offset(0.5.dp, 0.5.dp)
            )

            Text(
                formattedSpeed,
                color = ThemeL.textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = ThemeL.fontFamilyKarla
            )
        }

        Box {

            Text(
                " / $formattedBytes",
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = ThemeL.fontFamilyKarla,
                modifier = Modifier.offset(0.5.dp, 0.5.dp)
            )

            Text(
                " / $formattedBytes",
                color = ThemeL.textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = ThemeL.fontFamilyKarla
            )
        }
    }

}