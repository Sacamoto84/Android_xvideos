package com.client.xvideos.l.ui.screens.explorer.tab.config.atom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.client.xvideos.l.ui.screens.explorer.tab.config.styleTextConfigL

@Composable
fun ConfigTextL(text: String) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .padding(vertical = 2.dp)
            .height(32.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, style = styleTextConfigL)
    }
}