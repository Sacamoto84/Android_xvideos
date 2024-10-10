package com.client.xvideos.screens.dashboards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScreenDashBoardsBottomNavigationButtons(indexScreen: Int, vm: ScreenDashBoardsScreenModel) {

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Box(
            modifier = Modifier
                .padding(horizontal = (0.5).dp)
                .weight(1f)
                .height(48.dp)
                .background(
                    if (currentNumberScreen == 1)
                        Color(0xFF2c2c2c)
                    else
                        Color(0xFFFF9000)
                )
                .clickable {
                    vm.openNew(currentNumberScreen - 1)
                }, contentAlignment = Alignment.Center
        ) {
            Text(
                "<",
                color = if (currentNumberScreen == 1) Color.DarkGray else Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        repeat(10) {
            Box(
                modifier = Modifier
                    .padding(horizontal = (0.5).dp)
                    .weight(1f)
                    .height(48.dp)
                    .border(
                        2.dp,
                        Color(if (currentNumberScreen == it + 1) 0xFFFF9900 else 0x000000)
                    )

                    .background(
                        Color(0xFF252525)
                    )
                    .clickable { vm.openNew(it + 1) }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (it + 1).toString(),
                    color = Color(0xFFCCCCCC),
                    textAlign = TextAlign.Center
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(horizontal = (0.5).dp)
                .weight(1f)
                .height(48.dp)
                .background(
                    if (currentNumberScreen == 20000)
                        Color(0xFF2c2c2c)
                    else
                        Color(0xFFFF9000)
                )
                .clickable {
                    vm.openNew(currentNumberScreen + 1)
                }, contentAlignment = Alignment.Center
        ) {
            Text(
                ">",
                color = if (currentNumberScreen == 20000) Color.DarkGray else Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }


    }


}