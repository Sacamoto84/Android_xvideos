package com.client.xvideos.screens_red.profile.feedControl

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun RedProfileFeedGifTypesControl(vm: ScreenRedProfileSM) {
    Row {
        vm.typeGifsList.forEach {
            TextAndLine(Modifier.weight(1f), it.value, it == vm.typeGifs) {
                vm.typeGifs = it
                //vm.loadProfile()
            }
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

    Column(
        modifier = Modifier
            .then(modifier)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            str,
            fontSize = 20.sp,
            color = if (select) Color.White else ThemeRed.colorTextGray,
            fontFamily = ThemeRed.fontFamilyPopinsRegular
        )

        Box(
            Modifier
                .padding(top = 2.dp)
                .width(48.dp)
                .height(4.dp)
                .background(if (select) ThemeRed.colorRed else Color.Transparent)
        )

    }


}