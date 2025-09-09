package com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.model.AlbumListFilter
import com.client.xvideos.l.net.AlbumListFilterGenreCountResponse
import com.composeunstyled.Icon
import com.composeunstyled.Text

@Composable
fun AlbumListFilterTags(
    filter: AlbumListFilter,
    filterTagStateCount: List<AlbumListFilterGenreCountResponse>?,
    onChange: (AlbumListFilter) -> Unit
) {

    val filterTerms = filterTagStateCount?.map { it.term }?.toSet()

    val tagsPlus = filter.tagPlus
    val tagsMinus = filter.tagMinus

    val tagsCorrect = filterTerms?.minus(tagsPlus.map{it})?.minus(tagsMinus.map{it})?.toList()



    Column()
    {

        HorizontalDivider()

        LazyColumn {
            items(tagsPlus) {
                Text(
                    it,
                    color = ThemeL.lavender,
                    modifier = Modifier
                        .padding(start = 4.dp, top = 2.dp, bottom = 2.dp)
                        .fillMaxWidth()
                        .clickable(onClick = {
                            val plus = mutableListOf<String>()
                            plus.addAll(tagsPlus)
                            plus.remove(it)
                            val filter1 = filter.copy(tagPlus = plus)
                            onChange(filter1)
                        }),
                    fontFamily = ThemeL.fontFamilyKarla,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(tagsMinus) {
                Text(
                    "NOT $it",
                    color = ThemeL.lavender,
                    modifier = Modifier
                        .padding(start = 4.dp, top = 2.dp, bottom = 2.dp)
                        .fillMaxWidth()
                        .clickable(onClick = {
                            val minus = mutableListOf<String>()
                            minus.addAll(tagsMinus)
                            minus.remove(it)
                            val filter1 = filter.copy(tagMinus = minus)
                            onChange(filter1)
                        }),
                    fontFamily = ThemeL.fontFamilyKarla,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        DisclosureLayout("Tags") {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                LazyColumn(modifier = Modifier.weight(1f)) {

                    items(tagsCorrect?.size ?: 0) {
                        val item = tagsCorrect?.get(it)

                        if (item != null) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
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
                                                val plus = mutableListOf<String>()
                                                plus.addAll(tagsPlus)
                                                plus.add(item)
                                                val filter1 = filter.copy(tagPlus = plus)
                                                onChange(filter1)
                                            })
                                    )



                                    Text(
                                        item,
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
                                                val minus = mutableListOf<String>()
                                                minus.addAll(tagsMinus)
                                                minus.add(item)
                                                val filter1 = filter.copy(tagMinus = minus)
                                                onChange(filter1)
                                            })
                                    )

                                }



                                val count = filterTagStateCount.find { it1 -> it1.term == item }?.count
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