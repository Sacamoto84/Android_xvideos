package com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.model.PictureCountRank
import com.redgifs.common.ThemeRed

private val style = TextStyle(
    color = ThemeL.textColor,
    fontWeight = FontWeight.Bold,
    fontFamily = ThemeL.fontFamilyKarla,
    fontSize = 18.sp
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumListFilterSize(value: PictureCountRank, onChanged: (PictureCountRank) -> Unit) {

    var expanded by remember { mutableStateOf(false) }

    val itemS = listOf("Any", "0..25", "25..50", "50..100", "200..800", "800..3200", "3200..12800")

    val a = when (value) {
        PictureCountRank.all -> "Any"
        PictureCountRank.c0_25 -> "0..25"
        PictureCountRank.c25_50 -> "25..50"
        PictureCountRank.c50_100 -> "50..100"
        PictureCountRank.c100_200 -> "100..200"
        PictureCountRank.c200_800 -> "200..800"
        PictureCountRank.c800_3200 -> "800..3200"
        PictureCountRank.c3200_12800 -> "3200..12800"
    }

    // --- Первое меню (Primary) ---

    Row(modifier = Modifier. fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

        Text("Album Size", style = style)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .height(43.dp)
        )
        {
            Row(
                modifier = Modifier
                    .fillMaxSize() // заполняет всю выделенную ширину
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, ThemeL.grey5, RoundedCornerShape(4.dp))
                    .background(ThemeL.grey3)
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                BasicText(
                    a,
                    modifier = Modifier.padding(start = 4.dp),
                    maxLines = 1,
                    style = TextStyle(
                        color = ThemeL.textColor,
                        fontFamily = ThemeL.fontFamilyKarla,
                        fontSize = 16.sp
                    )
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = ThemeL.textColor
                )

            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = ThemeRed.colorTabLevel3
            ) {
                itemS.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                item,
                                color = ThemeL.textColor,
                                fontFamily = ThemeL.fontFamilyKarla,
                                fontSize = 16.sp
                            )
                        },
                        onClick = {

                            val selected = when (item) {
                                "Any" -> PictureCountRank.all
                                "0..25" -> PictureCountRank.c0_25
                                "25..50" -> PictureCountRank.c25_50
                                "50..100" -> PictureCountRank.c50_100
                                "100..200" -> PictureCountRank.c100_200
                                "200..800" -> PictureCountRank.c200_800
                                "800..3200" -> PictureCountRank.c800_3200
                                "3200..12800" -> PictureCountRank.c3200_12800
                                else -> PictureCountRank.all
                            }

                            onChanged(selected)
                            expanded = false
                        }
                    )
                }
            }
        }

    }

}