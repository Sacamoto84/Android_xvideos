package com.client.xvideos.redgifs.screens.niche.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.redgifs.model.NichesInfo
import com.client.common.urlVideImage.UrlImage

@Composable
fun NicheProfile(niche: NichesInfo) {

    Box {

        UrlImage(
            "https://www.redgifs.com/static/DEFAULT_NICHE_BACKGROUND-BmUEhMGK.png",
            modifier = Modifier.height(110.dp),
            contentScale = ContentScale.FillHeight
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F0F0F), Color.Transparent),
                        startY = Float.POSITIVE_INFINITY, // снизу
                        endY = 0f // вверх
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(niche.name, color = Color.White)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {

                Text(
                    niche.subscribers.toString(),
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
                UrlImage(niche.thumbnail, modifier = Modifier.size(128.dp).clip(RoundedCornerShape(8.dp)))
                Text(niche.gifs.toString(), color = Color.White, modifier = Modifier.weight(1f))
            }

        }
    }
}