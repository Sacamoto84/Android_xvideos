package com.client.xvideos.red.screens.explorer.top

import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.red.ThemeRed

@Composable
fun TabRow(onChangeState: (Int) -> Unit) {
    var state by remember { mutableIntStateOf(0) }
    val titles = listOf("Gifs", "Images", "Creators", "Niches")
    SecondaryTabRow(
        modifier = Modifier.height(48.dp),
        selectedTabIndex = state,
        containerColor = ThemeRed.colorCommonBackground2,
        contentColor = Color.White,
        indicator = {
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(state, matchContentSize = false),
                height = 4.dp,
                color = ThemeRed.colorRed
            )
        }
    ) {
        titles.forEachIndexed { index, item ->
            Tab(
                selected = index == state,
                onClick = {
                    state = index
                    onChangeState.invoke(index)
                },
                text = {
                    Text(
                        item,
                        fontFamily = ThemeRed.fontFamilyPopinsRegular,
                        fontSize = 14.sp
                    )
                })
        }
    }
}

@Preview
@Composable
fun TabRowPreview() {
    TabRow(onChangeState = {})
}

