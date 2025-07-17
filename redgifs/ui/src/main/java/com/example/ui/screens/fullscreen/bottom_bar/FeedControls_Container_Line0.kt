package com.example.ui.screens.fullscreen.bottom_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.common.R
import com.client.common.util.toTwoDecimalPlacesWithColon
import com.example.ui.screens.fullscreen.ScreenRedFullScreenSM
import com.redgifs.common.ThemeRed

@Composable
private fun Divider(){
    Spacer(modifier = Modifier.height(8.dp).width(2.dp).background(Color.DarkGray))
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FeedControls_Container_Line0(vm: ScreenRedFullScreenSM) {

    val border = Modifier.border(1.dp, ThemeRed.colorBorderGray)

    Row(
        modifier = Modifier
            .fillMaxWidth().height(48.dp)
            .horizontalScroll(state = rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .height(46.dp)
                .width(46.dp)//.border(1.dp, Color.White)
                //.border(1.dp, ThemeRed.colorBorderGray, RoundedCornerShape(8.dp))
                .clickable { vm.timeA = vm.currentPlayerTime }, verticalArrangement = Arrangement.Center,horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicText(
                vm.timeA.toTwoDecimalPlacesWithColon(),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 10.sp,
                    fontFamily = ThemeRed.fontFamilyPopinsRegular,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "A",
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = ThemeRed.fontFamilyPopinsRegular,
                textAlign = TextAlign.Center,
                modifier = Modifier
            )
        }

        Divider()

        Column(
            modifier = Modifier.height(46.dp).width(46.dp)
                //.border(1.dp, ThemeRed.colorBorderGray, RoundedCornerShape(8.dp))
                .clickable { vm.timeB = vm.currentPlayerTime },verticalArrangement = Arrangement.Center,horizontalAlignment = Alignment.CenterHorizontally
        ) {

            BasicText(
                vm.timeB.toTwoDecimalPlacesWithColon(), // Отформатируйте это значение!
                style = TextStyle(
                    color = Color.White,
                    fontSize = 10.sp,
                    fontFamily = ThemeRed.fontFamilyPopinsRegular,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Text("B", color = Color.White, fontSize = 20.sp, fontFamily = ThemeRed.fontFamilyPopinsRegular, textAlign = TextAlign.Center)

        }

        Divider()

        IconButton( onClick = { vm.enableAB = vm.enableAB.not() }, modifier = Modifier.size(46.dp) ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(R.drawable.rg_button), contentDescription = null, tint = if (vm.enableAB) Color.Green else Color.LightGray)
                Text("AB", color = if (vm.enableAB) Color.Green else Color.LightGray, fontSize = 8.sp, fontFamily = ThemeRed.fontFamilyPopinsRegular)
            }
        }

        Divider()

        IconButton(onClick = {
            if (vm.play) {
                vm.play = false
                vm.currentPlayerControls?.pause()
            } else {
                vm.play = true
                vm.currentPlayerControls?.play()
            }
        }) {
            Icon(
                painter = painterResource(if (vm.play) R.drawable.select_1 else R.drawable.rg_button),
                contentDescription = null, tint = Color.White, modifier = Modifier.rotate(if (vm.play) 90f else 0f)
            )
        }

        Divider()
        IconButton(onClick = { vm.currentPlayerControls?.rewind(1f)}) { Icon(painter = painterResource(R.drawable.exo_icon_rewind), contentDescription = null, tint = Color.White) }
        Divider()
        IconButton(onClick = { vm.currentPlayerControls?.forward(1f) }) { Icon(painter = painterResource(R.drawable.exo_icon_fastforward), contentDescription = null, tint = Color.White) }
        Divider()

        Box(
            modifier = Modifier
                .height(46.dp) .width(46.dp)
                //.border(1.dp, ThemeRed.colorBorderGray, RoundedCornerShape(8.dp))
                .clickable(onClick = {
                    //vm.downloadCurrentItem()
                }), contentAlignment = Alignment.Center
        ) { Icon(Icons.Filled.VolumeOff, contentDescription = null, tint = if (vm.enableAB) Color.Green else Color.LightGray) }

    }

}


