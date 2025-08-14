package com.client.xvideos.l.ui.screenAlbum.atom

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.client.common.util.capitalizeEachWord
import com.client.common.util.toPrettyCountInt
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.ui.screenAlbum.net.AlbumDetails

@Composable
fun AlbumInfoTags(parsed: AlbumDetails) {
    FlowRow(verticalArrangement = Arrangement.Center) {
        parsed.tags.reversed().forEach {
            Text("${it.text.capitalizeEachWord()} (${it.count.toPrettyCountInt()})" , modifier = Modifier.padding(horizontal = 2.dp).padding(vertical = 2.dp).border(1.dp, ThemeL.secondaryColor, RoundedCornerShape(4.dp)).padding(4.dp), color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyKarla)
        }
    }
}