package com.client.xvideos.l.ui.screens.screenAlbum.atom

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.model.AlbumDetails
import com.client.xvideos.l.model.Audience

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

@Preview
@Composable
fun AlbumInfoAudiencesPreview() {
    val parsed = AlbumDetails(
        created = 1678886400L,
        id = "123",
        title = "Sample Album",
        tags = emptyList(),
        is_manga = false,
        content = com.client.xvideos.l.model.Content(id = "c1", title = "Content 1", url = "url_content"),
        genres = emptyList(),
        cover = com.client.xvideos.l.model.Cover(width = 100, height = 100, size = "small", url = "url_cover"),
        description = "This is a sample album description.",
        audiences = listOf(
            Audience(id = "a1", title = "Audience 1", url = "url1"),
            Audience(id = "a2", title = "Audience 2", url = "url2"),
            Audience(id = "a3", title = "Audience 3", url = "url3")
        ),
        number_of_pictures = 10,
        number_of_animated_pictures = 2,
        url = "album_url",
        download_url = "download_album_url"
    )
    AlbumInfoAudiences(parsed = parsed)
}
