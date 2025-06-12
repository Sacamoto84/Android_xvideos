package com.client.xvideos.screens_red.niche.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.client.xvideos.feature.redgifs.types.Niche
import com.client.xvideos.feature.redgifs.types.Preview as NichePreviewType
import com.client.xvideos.screens.common.urlVideImage.UrlImage

@Composable
fun NichePreview(niches: Niche) {

    Column(modifier = Modifier.fillMaxWidth().padding(end = 8.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFF323232))) {

        Row(modifier = Modifier) {
            UrlImage(niches.thumbnail, modifier = Modifier.clip(CircleShape).size(64.dp))
            Text(text = niches.name, modifier = Modifier.padding(start = 8.dp), color = Color.White)
        }

        Row(modifier = Modifier) {
            repeat(3) {
                UrlImage(
                    niches.previews[it].thumbnail,
                    modifier = Modifier.padding(horizontal = 4.dp).size(96.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }


    }


}
