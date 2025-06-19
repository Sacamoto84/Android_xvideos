package com.client.xvideos.red.screens.profile.bottom_bar

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.client.xvideos.red.common.downloader.ui.DownloadIndicator
import com.client.xvideos.red.screens.profile.ScreenRedProfileSM
import com.client.xvideos.red.screens.profile.bottom_bar.line1.FeedControls_Container_Line1
import com.client.xvideos.red.screens.profile.bottom_bar.line0.FeedControls_Container_Line0
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