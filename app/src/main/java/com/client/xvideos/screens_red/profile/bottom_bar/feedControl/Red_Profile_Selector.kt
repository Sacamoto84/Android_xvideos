package com.client.xvideos.screens_red.profile.bottom_bar.feedControl

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.client.xvideos.R
import com.client.xvideos.screens_red.ThemeRed

@Preview
@Composable
fun DefaultPreview() {
    Red_Profile_Selector(1, onSelect = {})
}

@Composable
fun Red_Profile_Selector(selectedIndex: Int, onSelect: (Int) -> Unit) {

    val colorSelect = Color(0xFF1B1A33)

    Row(

        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, ThemeRed.colorBorderGray, RoundedCornerShape(8.dp))
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(if (selectedIndex == 2) colorSelect else ThemeRed.colorCommonBackground)
                .clickable { onSelect(2) },

            contentAlignment = Alignment.Center
        )
        {
            Icon(
                painter = painterResource(R.drawable.select_2),
                contentDescription = null,
                tint = Color.White, modifier = Modifier.size(20.dp)
            )
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(48.dp)
                .background(ThemeRed.colorBorderGray)
        )
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(if (selectedIndex == 1) colorSelect else ThemeRed.colorCommonBackground)
                .clickable { onSelect(1) }, contentAlignment = Alignment.Center
        )
        {
            Icon(
                painter = painterResource(R.drawable.select_1),
                contentDescription = null,
                tint = Color.White, modifier = Modifier.size(20.dp)
            )
        }


    }


}




