package com.client.xvideos.l.ui.screens.screenAlbum.atom

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.model.AlbumDetails

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

            var s = item.title
            if (index != parsed.genres.lastIndex) { s += ", " }

            Text(
                text = s,
                color = ThemeL.primaryColor,
                fontFamily = ThemeL.fontFamilyKarla
            )

        }
    }
}



