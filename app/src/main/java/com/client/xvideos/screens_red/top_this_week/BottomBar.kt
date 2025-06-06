package com.client.xvideos.screens_red.top_this_week

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BottomBar(onClickWeek: () -> Unit, onClickMonth: () -> Unit) {
    Row {

        Button(onClick = onClickWeek) {
            Text("Week")
        }

        Button(onClick = onClickMonth) {
            Text("Month")
        }

    }
}