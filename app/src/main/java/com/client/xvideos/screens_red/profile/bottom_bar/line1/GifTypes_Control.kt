package com.client.xvideos.screens_red.profile.bottom_bar.line1

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.screens_red.ThemeRed
import com.client.xvideos.screens_red.profile.ScreenRedProfileSM
import com.composeunstyled.Text

@Composable
fun GifTypes_Control(vm: ScreenRedProfileSM) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {

        TextAndLine(Modifier.weight(1f), vm.typeGifsList[0].value, vm.typeGifsList[0] == vm.typeGifs) {
            vm.typeGifs = vm.typeGifsList[0]
            vm.clear()
            //vm.loadNextPage()
        }

        Box(Modifier.width(1.dp).height(48.dp).background(ThemeRed.colorBorderGray))

        TextAndLine(Modifier.weight(1f), vm.typeGifsList[1].value, vm.typeGifsList[1] == vm.typeGifs) {
            vm.typeGifs = vm.typeGifsList[1]
            vm.clear()
            //vm.loadNextPage()
        }

    }
}

@Composable
private fun TextAndLine(
    modifier: Modifier = Modifier,
    str: String,
    select: Boolean,
    onClick: () -> Unit,
) {

    Box(
        modifier = Modifier
            .then(modifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {

        Text(
            str,
            fontSize = 18.sp,
            color = if (select) Color.White else ThemeRed.colorTextGray,
            fontFamily = ThemeRed.fontFamilyPopinsRegular
        )

        Box(
            Modifier
                //.align(Alignment.BottomCenter)
                .offset(0.dp, 16.dp)
                .width(48.dp)
                .height(4.dp)
                .background(if (select) ThemeRed.colorRed else Color.Transparent)
        )

    }


}