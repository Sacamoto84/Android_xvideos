package com.client.redgifs.screens.profile.atom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.redgifs.ThemeRed
import com.client.redgifs.common.saved.SavedRed
import com.client.redgifs.network.types.UserInfo
import com.composeunstyled.Text

@Composable
fun RedProfileCreaterInfo(item: UserInfo) {

    Column(
        modifier = Modifier
            .padding(horizontal = 0.dp)
            .fillMaxWidth()
    ) {


        //Top info
        Row(
            modifier = Modifier
                .padding(top = 2.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if(item.profileImageUrl != null) {
                UrlImage(item.profileImageUrl, modifier = Modifier.size(128.dp))
            }else {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(0.dp)).size(128.dp)
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

            Row(
                modifier = Modifier.wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(8.dp))
                Text(
                    item.username,
                    color = Color.White,
                    fontFamily = ThemeRed.fontFamilyPopinsMedium,
                    fontSize = 28.sp,
                    modifier = Modifier
                )
                Spacer(Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.verificed),
                    contentDescription = stringResource(id = R.string.dog_content_description),
                    modifier = Modifier
                        .size(26.dp)
                )
            }


        }

        val isFollow = SavedRed.creators.list.any { it.username == item.username }
        Box(
            modifier = Modifier
                .width(96.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isFollow) Color.Black else ThemeRed.colorYellow)
                .border( 1.dp, if (isFollow)Color.White else Color.Transparent, RoundedCornerShape(8.dp))
                .clickable { if (isFollow) SavedRed.creators.remove(item.username) else SavedRed.creators.add(item)
                           }, contentAlignment = Alignment.Center) {
            Text(if (isFollow) "Отписаться" else "Подписаться", color = if (isFollow) Color.White else Color.Black)
        }

        Row(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    item.followers.toPrettyCount().toString(),
                    color = Color.White,
                    fontFamily = ThemeRed.fontFamilyPopinsMedium
                )

                Text(
                    "Подписчиков",
                    color = Color(0xFF9E9DA9),
                    fontFamily = ThemeRed.fontFamilyPopinsRegular
                )

            }

            Box(
                Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(Color(0xFF3D3C53))
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    item.views.toPrettyCount(),
                    color = Color.White,
                    fontFamily = ThemeRed.fontFamilyPopinsMedium
                )

                Text(
                    "Просмотров",
                    color = Color(0xFF9E9DA9),
                    fontFamily = ThemeRed.fontFamilyPopinsRegular
                )
            }

            Box(
                Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(Color(0xFF3D3C53))
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                Text(
                    item.publishedGifs.toPrettyCount(),
                    color = Color.White,
                    fontFamily = ThemeRed.fontFamilyPopinsMedium
                )

                Text(
                    "Постов",
                    color = Color(0xFF9E9DA9),
                    fontFamily = ThemeRed.fontFamilyPopinsRegular
                )

            }
        }

        Text(
            "About ${item.username}:",
            color = ThemeRed.colorTextGray,
            fontSize = 14.sp,
            fontFamily = ThemeRed.fontFamilyPopinsRegular
        )

        Spacer(Modifier.height(4.dp))

        Text(
            item.description.toString().trimMargin(),
            color = Color.White,
            fontSize = 14.sp, fontFamily = ThemeRed.fontFamilyPopinsRegular
        )

        Spacer(Modifier.width(8.dp))

    }

}
