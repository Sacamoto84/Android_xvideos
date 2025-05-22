package com.client.xvideos.screens_red.profile.bottom_bar.line0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.ArrowCircleDown
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
import androidx.compose.ui.unit.sp
import com.client.xvideos.R
import com.client.xvideos.screens_red.ThemeRed
import com.client.xvideos.screens_red.profile.ScreenRedProfileSM

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Red_Profile_FeedControls_Container_Line0(vm: ScreenRedProfileSM) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = {
            vm.downloadItem(vm.creator?.gifs?.get(0)!!)
        }) {
            Row {


                Icon(
                    painter = painterResource(R.drawable.exo_ic_subtitle_off),
                    contentDescription = null,
                    tint = if (vm.enableAB == true) Color.Green else Color.LightGray
                )

            }

        }



        IconButton(onClick = {
            vm.timeA = vm.currentPlayerTime
        }) {
            Row {

                Text(
                    "A",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontFamily = ThemeRed.fontFamilyPopinsRegular
                )
                Text(
                    vm.timeA.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontFamily = ThemeRed.fontFamilyPopinsRegular
                )
            }

        }

        IconButton(onClick = {
            vm.timeB = vm.currentPlayerTime
        }) {
            Row {
                Text(
                    "B",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontFamily = ThemeRed.fontFamilyPopinsRegular
                )
                Text(
                    vm.timeB.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontFamily = ThemeRed.fontFamilyPopinsRegular
                )
            }

        }



        IconButton(onClick = {
            vm.enableAB = vm.enableAB.not()
        }) {
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
            }
            else{
            vm.play = true
            vm.currentPlayerControls?.play()}
        }) {
            Icon(
                painter = painterResource( if (vm.play) R.drawable.select_1  else R.drawable.rg_button),
                contentDescription = null,
                tint = Color.White, modifier = Modifier.rotate(if(vm.play == true) 90f else 0f)
            )
        }



//
//        IconButton(onClick = {
//            vm.play = true
//            vm.currentPlayerControls?.play()
//        }) {
//            Icon(
//                painter = painterResource(R.drawable.rg_button),
//                contentDescription = null,
//                tint = if( vm.play == true) Color.Green else Color.White, modifier = Modifier
//            )
//        }
//
//        IconButton(onClick = {
//            vm.play = false
//            vm.currentPlayerControls?.pause()
//        }) {
//            Icon(
//                painter = painterResource(R.drawable.select_1),
//                contentDescription = null,
//                tint = Color.White, modifier = Modifier.rotate(90f)
//            )
//        }



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


    }

}