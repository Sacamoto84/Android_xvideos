package com.client.xvideos.screens_red.top_this_week

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BottomBar(onClickWeek: () -> Unit, onClickMonth: () -> Unit, onClickLazy:()-> Unit, onClickTiktok:()-> Unit, onClickLazy2:()-> Unit,) {
    Row {

        Button(onClick = onClickWeek) {
            Text("Week")
        }

        Button(onClick = onClickMonth) {
            Text("Month")
        }

        Button(onClick = onClickLazy) {
            Text("Lazy")
        }

        Button(onClick = onClickTiktok) {
            Text("Tiktok")
        }

        Button(onClick = onClickLazy2) {
            Text("2")
        }
    }
}