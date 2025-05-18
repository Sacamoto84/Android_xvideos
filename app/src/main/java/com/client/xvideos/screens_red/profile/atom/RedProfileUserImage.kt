package com.client.xvideos.screens_red.profile.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.feature.redgifs.types.CreatorResponse
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.composeunstyled.Text

@Composable
fun RedProfileCreaterInfo(item: CreatorResponse) {

    Column(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {

        //Top info
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UrlImage(
                item.users[0].profileImageUrl.toString(),
                modifier = Modifier
                    .size(128.dp)
            )

            Text(item.users[0].username, color = Color.White)
        }

        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {

            Text(item.users[0].followers.toString(), color = Color.White)
            Text(item.users[0].views.toString(), color = Color.White)
            Text(item.users[0].publishedGifs.toString(), color = Color.White)

        }

        Text("About ${item.users[0].username}:", color = Color.White, fontSize = 14.sp)

        Text(item.users[0].description.toString().trimMargin(), color = Color.White, fontSize = 14.sp)

        val t = item.tags.sorted()

        FlowRow(modifier = Modifier.fillMaxWidth()){

            for (i in t) {
                Text(i, color = Color.White, fontSize = 14.sp , modifier = Modifier.padding(horizontal = 4.dp).background(Color.Gray))
            }


        }

    }


}