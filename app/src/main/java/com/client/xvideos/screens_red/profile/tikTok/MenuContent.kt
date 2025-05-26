package com.client.xvideos.screens_red.profile.tikTok

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.client.xvideos.screens_red.profile.ScreenRedProfileSM

@Composable
fun MenuContent(vm: ScreenRedProfileSM) {

    val context = LocalContext.current

    Row(
        modifier = Modifier,
        //horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    )
    {

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .height(48.dp)
                .width(48.dp)
                .border(2.dp, Color.White, RoundedCornerShape(8.dp))
                .clickable(onClick = {

                    if (vm.currentTikTokGifInfo != null) {
                        vm.shareGifs(context = context, vm.currentTikTokGifInfo!!)
                    }

                }), contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Share,//Icons.Filled.FileDownload,
                contentDescription = null,
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(
            modifier = Modifier
                .height(48.dp)
                .width(48.dp)
                .border(2.dp, Color.White, RoundedCornerShape(8.dp))
                .clickable(onClick = {

                }), contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.VolumeOff,
                contentDescription = null,
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(
            modifier = Modifier
                .height(48.dp)
                .width(48.dp)
                .border(2.dp, Color.White, RoundedCornerShape(8.dp))
                .clickable(onClick = {
                    vm.blockVisibleDialog = true
                }), contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Block,
                contentDescription = null,
                tint = Color.White
            )
        }
        //Spacer(modifier = Modifier.width(8.dp))
        Spacer(modifier = Modifier.width(48.dp))
    }
}