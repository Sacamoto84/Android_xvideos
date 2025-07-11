package com.client.xvideos.redgifs.screens.fullscreen.bottom_bar.line0

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.common.R
import com.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.screens.fullscreen.ScreenRedFullScreenSM
import com.client.xvideos.redgifs.screens.manager_block.ScreenRedManageBlock
import java.util.Locale

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FeedControls_Container_Line0(vm: ScreenRedFullScreenSM) {

    val border = Modifier.border(1.dp, ThemeRed.colorBorderGray)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(state = rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .height(48.dp)
                .width(48.dp)//.border(1.dp, Color.White)
                .border(1.dp, ThemeRed.colorBorderGray, RoundedCornerShape(8.dp))
                .clickable { vm.timeA = vm.currentPlayerTime }
        ) {
            Text(
                "A",
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = ThemeRed.fontFamilyPopinsRegular,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
            //Spacer(Modifier.width(4.dp)) // Небольшой отступ
            BasicText(
                vm.timeA.toTwoDecimalPlacesWithColon().toString(), // Отформатируйте это значение!
                style = TextStyle(
                    color = Color.White,
                    fontSize = 10.sp,
                    fontFamily = ThemeRed.fontFamilyPopinsRegular,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                //.background(Color.Green)
            )
        }

        Box(
            modifier = Modifier.height(48.dp).width(48.dp).border(1.dp, ThemeRed.colorBorderGray, RoundedCornerShape(8.dp))
                .clickable { vm.timeB = vm.currentPlayerTime }
        ) {
            Text(
                "B",
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = ThemeRed.fontFamilyPopinsRegular,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
            //Spacer(Modifier.width(4.dp)) // Небольшой отступ
            BasicText(
                vm.timeB.toTwoDecimalPlacesWithColon().toString(), // Отформатируйте это значение!
                style = TextStyle(
                    color = Color.White,
                    fontSize = 10.sp,
                    fontFamily = ThemeRed.fontFamilyPopinsRegular,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                //.background(Color.Green)
            )
        }



        IconButton(
            onClick = {
                vm.enableAB = vm.enableAB.not()
            }, modifier = Modifier.border(1.dp, ThemeRed.colorBorderGray, RoundedCornerShape(8.dp))
        ) {
            Box(contentAlignment = Alignment.Center) {

                Icon(
                    painter = painterResource(R.drawable.rg_button),
                    contentDescription = null,
                    tint = if (vm.enableAB == true) Color.Green else Color.LightGray
                )

                Text(
                    "AB",
                    color = if (vm.enableAB == true) Color.Green else Color.LightGray,
                    fontSize = 8.sp,
                    fontFamily = ThemeRed.fontFamilyPopinsRegular
                )
            }
        }

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
                contentDescription = null,
                tint = Color.White, modifier = Modifier.rotate(if (vm.play == true) 90f else 0f)
            )
        }

        IconButton(onClick = {
            vm.currentPlayerControls?.rewind(1f)
        }) {
            Icon(
                painter = painterResource(R.drawable.exo_icon_rewind),
                contentDescription = null,
                tint = Color.White
            )
        }

            IconButton(onClick = {
                vm.currentPlayerControls?.forward(1f)
            }) {
                Icon(
                    painter = painterResource(R.drawable.exo_icon_fastforward),
                    contentDescription = null,
                    tint = Color.White
                )
            }



        Box(
            modifier = Modifier
                .height(48.dp)
                .width(48.dp)
                .border(1.dp, ThemeRed.colorBorderGray, RoundedCornerShape(8.dp))
                .clickable(onClick = {
                    //vm.downloadCurrentItem()
                }), contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.VolumeOff,
                contentDescription = null,
                tint = if (vm.enableAB == true) Color.Green else Color.LightGray
            )
        }




        Box(
            modifier = Modifier
                .height(48.dp)
                .width(48.dp)
                .border(1.dp, ThemeRed.colorBorderGray, RoundedCornerShape(8.dp))
                .clickable(onClick = {
                    //vm.downloadCurrentItem()
                }), contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.FileDownload,
                contentDescription = null,
                tint = if (vm.enableAB == true) Color.Green else Color.LightGray
            )
        }









        Box(
            modifier = Modifier
                .height(48.dp)
                .width(48.dp)
                .border(1.dp, ThemeRed.colorBorderGray, RoundedCornerShape(8.dp))
                .clickable(onClick = {
                    //vm.downloadCurrentItem()
                }), contentAlignment = Alignment.Center
        ) {
            Icon(
              Icons.Filled.Fullscreen,
                contentDescription = null,
                tint = if (vm.enableAB == true) Color.Green else Color.LightGray
            )
        }

        val navigator = LocalNavigator.currentOrThrow


        Box(
            modifier = Modifier
                .height(48.dp)
                .width(48.dp)
                .border(1.dp, ThemeRed.colorBorderGray, RoundedCornerShape(8.dp))
                .clickable(onClick = {
                    navigator.push(ScreenRedManageBlock())
                }), contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Block,
                contentDescription = null,
                tint = Color.Gray
            )
            Icon(
                Icons.Filled.MoreVert,
                contentDescription = null,
                tint = Color.White
            )
        }




    }

}


/**
 * Форматирует Float до двух знаков после разделителя (который будет заменен на двоеточие),
 * всегда отображая два знака, даже если они нули.
 *
 * Примеры:
 * 2.984f.toTwoDecimalPlacesWithColon() // "2:98"
 * 2.0f.toTwoDecimalPlacesWithColon()   // "2:00"
 * 10f.toTwoDecimalPlacesWithColon()    // "10:00"
 * 123.456f.toTwoDecimalPlacesWithColon()// "123:46"
 */
fun Float.toTwoDecimalPlacesWithColon(): String {
    // Сначала форматируем с точкой как разделителем
    val formattedWithDot = String.format(Locale.US, "%.2f", this)
    // Затем заменяем точку на двоеточие
    return formattedWithDot.replace('.', ':')
}