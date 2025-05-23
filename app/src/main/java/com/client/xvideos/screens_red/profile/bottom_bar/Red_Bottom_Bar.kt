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
import com.client.xvideos.screens_red.ThemeRed
import com.client.xvideos.screens_red.profile.ScreenRedProfileSM
import com.client.xvideos.screens_red.profile.bottom_bar.feedControl.Red_Profile_FeedControls_Container
import com.client.xvideos.screens_red.profile.bottom_bar.line0.Red_Profile_FeedControls_Container_Line0
import com.composables.core.HorizontalSeparator

@Composable
fun Red_Profile_Bottom_Bar(vm: ScreenRedProfileSM) {
    val percentDownload = vm.downloader.percent.collectAsStateWithLifecycle().value

    Column {

        //Индикатор загрузки
        when(percentDownload) {
          in  0f..1f -> LinearProgressIndicator(progress = percentDownload,  modifier = Modifier.fillMaxWidth().height(2.dp), color = Color.Green)
          -2f -> HorizontalSeparator(ThemeRed.colorBottomBarDivider, thickness = 2.dp)
          -3f -> Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color.Red))
        }

        Red_Profile_FeedControls_Container_Line0(vm)
        HorizontalSeparator(Color.Transparent, thickness = 4.dp)
        Red_Profile_FeedControls_Container(vm)
    }
}