package com.client.xvideos.redgifs.screens.manager_block.bottomr_bar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.redgifs.common.ThemeRed
import com.composables.core.HorizontalSeparator

@Composable
fun BottomrBar() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        HorizontalSeparator(ThemeRed.colorBottomBarDivider, thickness = 2.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {


        }
    }


}