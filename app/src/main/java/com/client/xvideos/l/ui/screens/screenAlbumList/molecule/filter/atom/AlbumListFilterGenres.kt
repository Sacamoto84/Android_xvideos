package com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.model.AlbumListFilter
import com.client.xvideos.l.net.AlbumListFilterGenreCountResponse
import com.client.xvideos.l.net.graphQl.Genre
import com.client.xvideos.l.net.graphQl.mediaCategories
import com.composeunstyled.Icon
import com.composeunstyled.Text

@Composable
fun AlbumListFilterGenres(
    filter: AlbumListFilter,
    filterGenreStateCount: List<AlbumListFilterGenreCountResponse>?,
    onChange: (AlbumListFilter) -> Unit
) {

    val filterTerms = filterGenreStateCount?.map { it.term }?.toSet()

    val genresPlus = filter.genresPlus
    val genresMinus = filter.genresMinus

    val allGenres = mediaCategories?.genres

    val genresPlusCorrect = allGenres?.minus(genresPlus)?.minus(genresMinus)
        ?.filter { filterTerms?.contains(it.title) == true }

    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFF383838)))
    {

        HorizontalDivider()

        LazyColumn(modifier = Modifier
            //contentPadding= PaddingValues(4.dp)
        ) {
            items(genresPlus) {
                Text(
                    it.title,
                    color = ThemeL.lavender,
                    modifier = Modifier
                        .padding(start = 4.dp, top = 2.dp, bottom = 2.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, Color(0xFF585858), RoundedCornerShape(4.dp))
                        .background(Color(0xFF303030))
                        .padding(horizontal = 4.dp)
                        .clickable(onClick = {
                            val plus = mutableListOf<Genre>()
                            plus.addAll(genresPlus)
                            plus.remove(it)
                            val filter1 = filter.copy(genresPlus = plus)
                            onChange(filter1)
                        }),
                    fontFamily = ThemeL.fontFamilyKarla,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(genresMinus) {
                Text(
                    "NOT ${it.title}",
                    color = ThemeL.lavender,
                    modifier = Modifier
                        .padding(start = 4.dp, top = 2.dp, bottom = 2.dp)
                        .fillMaxWidth()
                        .clickable(onClick = {
                            val minus = mutableListOf<Genre>()
                            minus.addAll(genresMinus)
                            minus.remove(it)
                            val filter1 = filter.copy(genresMinus = minus)
                            onChange(filter1)
                        }),
                    fontFamily = ThemeL.fontFamilyKarla,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        DisclosureLayout("Genres") {
            Column(
                modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF252525))
            ) {
                HorizontalDivider(modifier = Modifier.padding(bottom = 6.dp), thickness = 2.dp, color = Color(0xFF585858))
                LazyColumn(modifier = Modifier.fillMaxWidth().offset(y = (-6).dp)) {


                    items(genresPlusCorrect?.size ?: 0) {
                        val item = genresPlusCorrect?.get(it)
                        if (item != null) {

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(end = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        tint = ThemeL.textColor,
                                        modifier = Modifier
                                            .padding(vertical = 2.dp)
                                            .padding(horizontal = 4.dp)
                                            .size(40.dp)
                                            .border(1.dp, ThemeL.grey2, RoundedCornerShape(4.dp))
                                            .clickable(onClick = {
                                                val plus = mutableListOf<Genre>()
                                                plus.addAll(genresPlus)
                                                plus.add(item)
                                                val filter1 = filter.copy(genresPlus = plus)
                                                onChange(filter1)
                                            })
                                    )



                                    Text(
                                        item.title,
                                        color = ThemeL.textColor,
                                        fontFamily = ThemeL.fontFamilyKarla,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Icon(
                                        Icons.Default.Remove,
                                        contentDescription = null,
                                        tint = ThemeL.textColor,
                                        modifier = Modifier
                                            .padding(vertical = 2.dp)
                                            .padding(horizontal = 4.dp)
                                            .size(40.dp)
                                            .border(1.dp, ThemeL.grey2, RoundedCornerShape(4.dp))
                                            .clickable(onClick = {
                                                val minus = mutableListOf<Genre>()
                                                minus.addAll(genresMinus)
                                                minus.add(item)
                                                val filter1 = filter.copy(genresMinus = minus)
                                                onChange(filter1)
                                            })
                                    )

                                }


                                val count =
                                    filterGenreStateCount?.find { it1 -> it1.term == item.title }?.count
                                Text(
                                    count.toString(),
                                    color = ThemeL.textColor,
                                    fontFamily = ThemeL.fontFamilyKarla,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                        }

                    }
                }
            }
        }

    }
}