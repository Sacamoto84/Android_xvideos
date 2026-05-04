package com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.client.xvideos.l.theme.ThemeL
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

    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFF3F3F3F)))
    {

        //HorizontalDivider()

        LazyColumn {
            items(tagsPlus) {
                Text(
                    it,
                    color = StyleGenresTags.colorSelectTextItem,
                    modifier = Modifier
                        .then(StyleGenresTags.modifierSelectTextItem)
                        .clickable(onClick = {
                            val plus = mutableListOf<String>()
                            plus.addAll(tagsPlus)
                            plus.remove(it)
                            val filter1 = filter.copy(tagPlus = plus)
                            onChange(filter1)
                        }),
                    style = ThemeL.Type.bodyLarge.copy(color = StyleGenresTags.colorSelectTextItem, fontWeight = FontWeight.Bold)
                )
            }

            items(tagsMinus) {

                val s = buildAnnotatedString {
                    withStyle(SpanStyle( color = Color(0xb3ceeefc), textDecoration = TextDecoration.Underline)) { append("NOT") }
                    append(" $it")
                }

                Text(
                    s,
                    color = StyleGenresTags.colorSelectTextItem,
                    modifier = Modifier
                        .then(StyleGenresTags.modifierSelectTextItem)
                        .clickable(onClick = {
                            val minus = mutableListOf<String>()
                            minus.addAll(tagsMinus)
                            minus.remove(it)
                            val filter1 = filter.copy(tagMinus = minus)
                            onChange(filter1)
                        }),
                    style = ThemeL.Type.bodyLarge.copy(color = StyleGenresTags.colorSelectTextItem, fontWeight = FontWeight.Bold)
                )
            }
        }

        DisclosureLayout("Tags") {
            Box(
                modifier = Modifier.padding(4.dp)
            ) {
                LazyColumn(modifier = Modifier.fillMaxWidth()
                    .border( 2.dp, Color(0xFF303030), RoundedCornerShape(4.dp))
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF252525))
                ) {
                    item{ Spacer(Modifier.height(0.dp)) }
                    items(tagsCorrect?.size ?: 0) {
                        val item = tagsCorrect?.get(it)

                        if (item != null) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 2.dp, top = 4.dp,end = 6.dp),
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
                                            .border(1.dp, ThemeL.grey2, RoundedCornerShape(2.dp))
                                            .clickable(onClick = {
                                                val plus = mutableListOf<String>()
                                                plus.addAll(tagsPlus)
                                                plus.add(item)
                                                val filter1 = filter.copy(tagPlus = plus)
                                                onChange(filter1)
                                            })
                                    )



                                    Text(item, color = ThemeL.textColor, style = ThemeL.Type.rowTitle.copy(fontWeight = FontWeight.Bold))

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

                                val count = filterTagStateCount.find { it1 -> it1.term == item }?.count ?: -1
                                Text(count.toString(), color = ThemeL.textColor, style = ThemeL.Type.rowTitle.copy(fontWeight = FontWeight.Bold))

                            }

                        }

                    }
                    item{ Spacer(Modifier.height(4.dp)) }
                }
            }
        }

    }
}
