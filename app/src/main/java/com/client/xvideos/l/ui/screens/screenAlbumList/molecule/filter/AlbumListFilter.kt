package com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom.AlbumListFilterTags

private val style = TextStyle(
    color = ThemeL.textColor,
    fontWeight = FontWeight.Bold,
    fontFamily = ThemeL.fontFamilyKarla,
    fontSize = 18.sp
)


@Composable
fun AlbumListFilter(filter: AlbumListFilter,
                    filterGCount: List<AlbumListFilterGenreCountResponse>?,
                    filterTagsCount: List<AlbumListFilterGenreCountResponse>?,
                    onClose : () -> Unit, onFilterApply: (AlbumListFilter) -> Unit ) {
    Column(modifier = Modifier.alpha(0.95f).background(ThemeL.grey4)) {



        Box(Modifier.fillMaxWidth().height(48.dp)) {

            Row( modifier = Modifier.padding(end = 0.dp).fillMaxWidth().height(46.dp).align(Alignment.CenterEnd).clickable(onClick = {onClose()}), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start ){
                //Icon(Icons.Filled.Close, contentDescription = null, tint = ThemeL.textColor)
                Spacer(modifier = Modifier.fillMaxHeight().fillMaxWidth().weight(1f))
                Spacer(modifier = Modifier.fillMaxHeight().width(4.dp))
                Spacer(modifier = Modifier.fillMaxHeight().fillMaxWidth().weight(1f))
                Spacer(modifier = Modifier.fillMaxHeight().width(4.dp))
                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth().weight(1f).border(0.5.dp, ThemeL.grey2, RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center){
                    Text("Close", textAlign = TextAlign.Center, color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyKarla, fontSize = 20.sp, modifier = Modifier)
                }

            }
        }

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

        HorizontalDivider()

        AlbumListFilterTags(filter, filterTagsCount) {
            onFilterApply(it)
        }

    }
}