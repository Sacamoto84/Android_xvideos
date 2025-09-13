package com.client.xvideos.l.ui.screens.explorer.tab.config.atom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.client.xvideos.l.theme.ThemeL

@Composable
fun ConfigTextL(text: String) {
    Box( modifier = Modifier.padding(horizontal = 8.dp).padding(vertical = 2.dp).height(32.dp).fillMaxWidth() ) {
        Text(text, style = ThemeL.styleTextConfigL)
    }
}