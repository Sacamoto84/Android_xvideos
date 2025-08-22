package com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.model.AlbumListFilter
import com.client.xvideos.l.net.graphQl.mediaCategories
import com.composeunstyled.Icon
import com.composeunstyled.Text

@Composable
fun AlbumListFilterGenres(filter : AlbumListFilter, onChange: (AlbumListFilter) -> Unit ) {

    val genresPlus = filter.genresPlus
    val genresMinus = filter.genresMinus

    val allGenres = mediaCategories?.genres

    Column()
    {

        LazyColumn {
            items(genresPlus) {
                Text(it, color = ThemeL.textColor, modifier = Modifier)
            }

            items(genresMinus) {
                Text("NOT $it", color = ThemeL.textColor, modifier = Modifier)
            }
        }

        DisclosureLayout("Genres") {

            LazyColumn(modifier = Modifier.alpha(0.8f)) {

                items(allGenres?.size ?: 0) {
                    val item = allGenres?.get(it)
                    if (item != null) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = {
                                val plus = mutableListOf<String>()
                                plus.addAll(genresPlus)
                                plus.add(item.title)
                                val filter1 = filter.copy(genresPlus = plus)
                                onChange(filter1)
                            })
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = ThemeL.textColor
                            )
                            Text(item.title, color = ThemeL.textColor)
                        }

                    }
                }
            }
        }
    }
}