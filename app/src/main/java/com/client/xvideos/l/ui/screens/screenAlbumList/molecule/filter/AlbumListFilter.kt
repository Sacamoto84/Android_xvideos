package com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.model.AlbumListFilter
import com.client.xvideos.l.model.enum.AlbumType
import com.client.xvideos.l.net.AlbumListFilterGenreCountResponse
import com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom.AlbumFilterDisplay
import com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom.AlbumListFilterAlbumType
import com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom.AlbumListFilterContentType
import com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom.AlbumListFilterGenres
import com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom.AlbumListFilterSize

private val style = TextStyle(
    color = ThemeL.textColor,
    fontWeight = FontWeight.Bold,
    fontFamily = ThemeL.fontFamilyKarla,
    fontSize = 18.sp
)


@Composable
fun AlbumListFilter(filter: AlbumListFilter, filterGCount: List<AlbumListFilterGenreCountResponse>?, onFilterApply: (AlbumListFilter) -> Unit) {
    Column(modifier = Modifier.alpha(0.95f).background(ThemeL.grey4)) {

        Spacer(Modifier.height(48.dp))

        AlbumFilterDisplay(
            filter.display,
            onRequestApply = { onFilterApply(filter.copy(display = it)) })

        AlbumListFilterAlbumType(
            when (filter.album_type) {
                AlbumType.All -> 0
                AlbumType.Manga -> 1
                AlbumType.Pictures -> 2
            }
        ) {
            val type = when (it) {
                0 -> AlbumType.All
                1 -> AlbumType.Manga
                2 -> AlbumType.Pictures
                else -> AlbumType.All
            }
            onFilterApply(filter.copy(album_type = type))
        }

        AlbumListFilterContentType(filter.content_id) {
            onFilterApply(filter.copy(content_id = it))
        }

        HorizontalDivider()

        AlbumListFilterSize(filter.picture_count_rank) {
            onFilterApply(filter.copy(picture_count_rank = it))
        }

        AlbumListFilterGenres(filter, filterGCount) {
            onFilterApply(it)
        }

    }
}