package com.client.xvideos.redgifs.ui.ui.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.Text
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.ui.theme.XvideosTheme

@Composable
fun TabBarPoints(count: Int, screenType: Boolean) {
    Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row {
            if (count == 0) {
                Text(
                    "T",
                    color = if (screenType) Color.White else Color.Gray,
                    fontFamily = ThemeRed.fontFamilyPopinsSemiBold,
                    fontSize = 12.sp
                )
            } else {
                repeat(count) {
                    Box(
                        modifier = Modifier
                            .padding(end = 2.dp)
                            .clip(CircleShape)
                            .size(4.dp)
                            .background(if (screenType) Color.White else Color.Gray)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF303030)
@Composable
fun TabBarPointsPreview() {
    XvideosTheme {
        TabBarPoints(count = 3, screenType = true)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF303030)
@Composable
fun TabBarPointsZeroPreview() {
    XvideosTheme {
        TabBarPoints(count = 0, screenType = false)
    }
}