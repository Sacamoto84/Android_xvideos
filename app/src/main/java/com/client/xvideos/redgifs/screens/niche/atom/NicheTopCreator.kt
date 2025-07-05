package com.client.xvideos.redgifs.screens.niche.atom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.client.xvideos.redgifs.network.types.TopCreator
import com.client.xvideos.screens.common.urlVideImage.UrlImage

@Composable
fun NicheTopCreator(creator : TopCreator, onClick: () -> Unit) {

    UrlImage(creator.profileImageUrl, modifier = Modifier.padding(end =4.dp).size(96.dp).clip(RoundedCornerShape(8.dp)).clickable{onClick.invoke()})

}