package com.example.ui.screens.explorer.tab.setting

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

//@Preview(showBackground = false)
//@Composable
//private fun Preview(){
//    ConfigText("Test", "Test")
//}

@Composable
fun ConfigText(text: String, value: String) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .padding(vertical = 2.dp)
            .height(48.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, style = styleTest)
        Text(value, style = styleTest)
    }
}
