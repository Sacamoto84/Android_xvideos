package com.client.xvideos.l.ui.screens.screenAlbum.atom

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.client.xvideos.l.ui.screens.screenAlbum.net.AlbumDetails
import com.client.xvideos.l.ThemeL

@Composable
fun AlbumInfoGreeting(parsed: AlbumDetails) {
    FlowRow {
        Text(
            "Genres: ",
            color = ThemeL.textColor,
            fontFamily = ThemeL.fontFamilyKarla,
            fontWeight = FontWeight.ExtraBold
        )
        parsed.genres.forEachIndexed { index, item ->
            Text(
                text = buildString {
                    append(item.title)
                    if (index != parsed.audiences.lastIndex) append(",")
                },
                color = ThemeL.primaryColor,
                fontFamily = ThemeL.fontFamilyKarla
            )
            if (index != parsed.audiences.lastIndex) {
                Text(" ", color = ThemeL.primaryColor)
            }
        }
    }
}



