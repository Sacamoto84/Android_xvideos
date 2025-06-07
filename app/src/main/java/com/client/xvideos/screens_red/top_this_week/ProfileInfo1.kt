package com.client.xvideos.screens_red.top_this_week

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.UserInfo
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.client.xvideos.screens_red.ThemeRed

@Composable
fun ProfileInfo1(modifier: Modifier = Modifier, onClick: ()->Unit, videoItem: GifsInfo, listUsers: List<UserInfo>, visibleUserName:Boolean = true, visibleIcon:Boolean = true){
    Row(
        modifier = Modifier.then(modifier).background(Color(0x0CFFFFFF)).clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically) {

        if (visibleIcon) {
            val a = listUsers.firstOrNull { it1 -> it1.username == videoItem.userName }
            if ((a != null) && (a.profileImageUrl != null)) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    UrlImage(
                        a.profileImageUrl,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).size(48.dp)
                        .background(Color.DarkGray), contentAlignment = Alignment.Center
                )
                {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
            }
        }
        ////////////
        AnimatedVisibility(visibleUserName) {
            Text(videoItem.userName, color = Color.White, fontFamily = ThemeRed.fontFamilyPopinsRegular, fontSize = 22.sp, modifier = Modifier.height(48.dp).padding(start = 8.dp))
        }



    }
}