package com.client.xvideos.xvideos.screens.common.bottomKeyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Preview(showBackground = true)
@Composable
fun PreviewKeyboardNumber() {
   KeyboardButtonNumber("1") { }
}

@Composable
fun KeyboardButtonNumber(text: String, theme :KeyboardNumberTheme = KeyboardNumberTheme() , onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(theme.buttonPadding)
            .height(theme.buttonHeight)
            .width(theme.buttonWidth)
            .clip(RoundedCornerShape(theme.buttonCornerRadius))
            .background(theme.buttonColor)
            .clickable { onClick.invoke() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color(0xFFDEE1EF), fontSize = 24.sp)
    }
}