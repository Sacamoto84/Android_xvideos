package com.client.xvideos.l.ui.screens.explorer.tab.config.atom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.client.xvideos.l.ThemeL

@Composable
fun ConfigTextAndCheckBoxL(text: String, value: Boolean, onValueChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 0.dp)
            .padding(vertical = 0.dp)
            .height(30.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(value, onValueChange, colors = CheckboxDefaults.colors(
            checkmarkColor = ThemeL.primaryColor,
            checkedColor = ThemeL.grey3
            ,uncheckedColor = ThemeL.grey3

        ), modifier = Modifier.width(40.dp))
        Text(text, style = ThemeL.styleTextConfigL)
    }
}