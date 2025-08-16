package com.client.xvideos.l.ui.screens.screenAlbum.atom

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.net.AlbumDetails

@Composable
fun AlbumInfoAudiences(parsed: AlbumDetails) {
    FlowRow {
        Text("Audiences: ", color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyKarla)
        parsed.audiences.forEachIndexed { index, item ->
            Text(
                text = buildString {
                    append(item.title)
                    if (index != parsed.audiences.lastIndex) append(",")
                },
                color = ThemeL.primaryColor,
                fontFamily = ThemeL.fontFamilyKarla,
            )
            if (index != parsed.audiences.lastIndex) {
                Text(" ", color = ThemeL.primaryColor)
            }
        }
    }
}