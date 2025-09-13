package com.client.xvideos.l.ui.screens.screenAlbum.atom

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.model.AlbumDetails
import com.client.xvideos.l.model.Audience
import com.client.xvideos.l.model.Content
import com.client.xvideos.l.model.Cover
import com.client.xvideos.l.model.Genre
import com.client.xvideos.l.model.Tag

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

@Preview
@Composable
fun AlbumInfoGreetingPreview() {
    val parsed = AlbumDetails(
        created = 1678886400L,
        id = "album123",
        title = "Sample Album",
        tags = listOf(
            Tag(id = "tag1", category = "Nature", text = "Mountains", url = "url/mountains", count = 100),
            Tag(id = "tag2", category = "Nature", text = "Rivers", url = "url/rivers", count = 50)
        ),
        is_manga = false,
        content = Content(id = "content1", title = "Album Content", url = "url/content"),
        genres = listOf(
            Genre(id = "genre1", title = "Adventure", actsAsWarning = false, url = "url/adventure"),
            Genre(id = "genre2", title = "Sci-Fi", actsAsWarning = false, url = "url/scifi")
        ),
        cover = Cover(width = 800, height = 600, size = "large", url = "url/cover.jpg"),
        description = "This is a sample album description.",
        audiences = listOf(
            Audience(id = "audience1", title = "General", url = "url/general"),
            Audience(id = "audience2", title = "Teens", url = "url/teens")
        ),
        number_of_pictures = 10,
        number_of_animated_pictures = 2,
        url = "url/album123",
        download_url = "url/download/album123"
    )
    AlbumInfoGreeting(parsed = parsed)
}






