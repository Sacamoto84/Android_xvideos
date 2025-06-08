package com.client.xvideos.screens_red.profile.bottom_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.client.xvideos.feature.Downloader
import com.client.xvideos.screens_red.ThemeRed
import com.client.xvideos.screens_red.common.downloaderIndicator.DownloadIndicator
import com.client.xvideos.screens_red.profile.ScreenRedProfileSM
import com.client.xvideos.screens_red.profile.bottom_bar.line1.FeedControls_Container_Line1
import com.client.xvideos.screens_red.profile.bottom_bar.line0.FeedControls_Container_Line0
import com.composables.core.HorizontalSeparator

@Composable
fun BottomBar(vm: ScreenRedProfileSM) {

    Column {

        //Индикатор загрузки
        DownloadIndicator()

        FeedControls_Container_Line0(vm)
        HorizontalSeparator(Color.Transparent, thickness = 4.dp)
        FeedControls_Container_Line1(vm)
    }
}