package com.example.ui.screens.explorer.tab.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.redgifs.common.ThemeRed

@Composable
fun ConfigTextAndButtonWithDialog(
    text: String,
    value: String,
    textDialogTitle: String,
    textDialogBody: String,
    textDialogButton: String,
    onClick: () -> Unit
)
{

    var visible by remember { mutableStateOf(false) }

    DialogButton(
        visible = visible,
        title = textDialogTitle,
        body = textDialogBody,
        buttonText = textDialogButton,
        onDismiss = { visible = false },
        onBlockConfirmed = {onClick.invoke()})

    Row(
        modifier = Modifier
            .padding(start = 8.dp, end = 2.dp)
            .padding(vertical = 2.dp)
            .height(48.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, style = styleTest)
        Box(
            modifier = Modifier
                .height(48.dp)
                .width(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, ThemeRed.colorTabLevel3, RoundedCornerShape(8.dp))
                .background(ThemeRed.colorBottomBarDivider)
                .clickable(onClick = { visible = true }), contentAlignment = Alignment.Center
        ) {
            Text(value, style = styleTest.copy(fontSize = 18.sp))
        }
    }
}

