package com.client.xvideos.l.ui.screens.explorer.tab.config.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.common.setting.styleTextConfig
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.ui.screens.explorer.tab.config.styleTextConfigL

@Composable
fun ConfigTextAndButtonL(
    text: String,
    value: String,
    composableIcon: @Composable () -> Unit = {},
    onClick: () -> Unit
) {


    Row(
        modifier = Modifier
            .padding(start = 8.dp, end = 2.dp)
            .padding(vertical = 2.dp)
            .height(48.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, style = styleTextConfigL)


        Row {
            Box(
                modifier = Modifier.padding(end = 8.dp).size(48.dp), contentAlignment = Alignment.Center
            ) {
                composableIcon()
            }

            Box(
                modifier = Modifier
                    .height(48.dp)
                    .width(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF333333), RoundedCornerShape(8.dp))
                    .background(ThemeL.grey4)
                    .clickable(onClick = {onClick()}), contentAlignment = Alignment.Center
            ) {
                Text(value, style = styleTextConfig.copy(fontSize = 18.sp))
            }
        }
    }
}

@Preview
@Composable
fun ConfigTextAndButtonPreview() {
    ConfigTextAndButtonWithDialogL(
        text = "Sample Text",
        value = "100",
        textDialogTitle = "Dialog Title",
        textDialogBody = "This is a sample dialog body.",
        textDialogButton = "Confirm",
        {},
        onClick = {
            // Handle click action for preview
            println("Button clicked in preview")
        },
    )
}