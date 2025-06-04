package com.client.xvideos.screens_red.top_this_week.row1

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.UserInfo
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.client.xvideos.screens_red.ThemeRed

@Composable
fun LazyRow1(listGifs: List<GifsInfo>, listUsers: List<UserInfo>, modifier: Modifier = Modifier) {

    val state = rememberLazyListState()

    LazyColumn(state = state, modifier = Modifier.then(modifier)) {


        items(listGifs, key = {it.id}){


            UrlImage(it.urls.thumbnail, modifier = Modifier.aspectRatio(1080f/1920), contentScale = ContentScale.Fit)


            Row {

                val a = listUsers.firstOrNull { it1 -> it1.username == it.userName }
                if ((a != null) && (a.profileImageUrl != null)) {
                    Box(
                        modifier = Modifier.clip(CircleShape).size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        UrlImage(
                            a.profileImageUrl,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }else{
                    Icon(Icons.Default.AssignmentInd, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.Green)
                }

                ////////////
                Text(it.userName, color = Color.White, fontFamily = ThemeRed.fontFamilyPopinsRegular)

            }

        }

    }


}