package com.client.xvideos.screens_red.profile.bottom_bar

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.client.xvideos.screens_red.ThemeRed
import com.client.xvideos.screens_red.profile.ScreenRedProfileSM
import com.client.xvideos.screens_red.profile.bottom_bar.feedControl.Red_Profile_FeedControls_Container
import com.client.xvideos.screens_red.profile.bottom_bar.line0.Red_Profile_FeedControls_Container_Line0
import com.composables.core.HorizontalSeparator

@Composable
fun Red_Profile_Bottom_Bar(vm: ScreenRedProfileSM) {
    Column {
        HorizontalSeparator(ThemeRed.colorBottomBarDivider, thickness = 2.dp)
        Red_Profile_FeedControls_Container_Line0(vm)
        HorizontalSeparator(Color.Transparent, thickness = 4.dp)
        Red_Profile_FeedControls_Container(vm)
    }
}