package com.example.ui.screens.ui.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.redgifs.common.ThemeRed


@Composable
fun ButtonUp(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0x80757575), RoundedCornerShape(8.dp))
            .background(ThemeRed.colorCommonBackground)
            .clickable(
                onClick = onClick), contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Filled.ArrowUpward,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier
        )
    }
}

@Preview
@Composable
fun ButtonUpPreview() {
    ButtonUp(onClick = {})
}