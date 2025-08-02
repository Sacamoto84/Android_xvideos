package com.client.xvideos.l.ui.screens.atom

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.client.common.util.capitalizeEachWord
import com.client.common.util.toPrettyCountInt
import com.client.xvideos.l.ui.screens.net.AlbumDetails
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

@Composable
fun AlbumInfoTags(parsed: AlbumDetails) {
    FlowRow(verticalArrangement = Arrangement.Center) {
        parsed.tags.reversed().forEach {
            Text("${it.text.capitalizeEachWord()} (${it.count.toPrettyCountInt()})" , modifier = Modifier.padding(horizontal = 2.dp).padding(vertical = 2.dp).border(1.dp, ThemeL.secondaryColor, RoundedCornerShape(4.dp)).padding(4.dp), color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyKarla)
        }
    }
}