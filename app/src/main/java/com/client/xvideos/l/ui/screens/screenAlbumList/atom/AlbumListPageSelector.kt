package com.client.xvideos.l.ui.screens.screenAlbumList.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.client.xvideos.l.ThemeL

@Preview
@Composable
private fun preview() {

    AlbumListPageSelector(1, 199)

}

@Composable
fun AlbumListPageSelector(page: Int, pageMax: Int, onChange: (Int) -> Unit = {}) {

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            "Page $page of $pageMax",
            color = ThemeL.textColor,
            fontFamily = ThemeL.fontFamilyKarla
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {


            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(128.dp)
                    .background(ThemeL.red)
                    .clickable(
                        onClick = { onChange((page - 1).coerceAtLeast(1)) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.KeyboardArrowLeft, tint = Color.White, contentDescription = null)
            }


            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(128.dp)
                    .background(ThemeL.red)
                    .clickable(
                        onClick = { onChange((page + 1).coerceAtMost(pageMax)) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    tint = Color.White,
                    contentDescription = null
                )
            }


        }

    }

}