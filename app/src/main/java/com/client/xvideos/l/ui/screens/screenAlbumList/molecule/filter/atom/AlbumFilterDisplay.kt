package com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.model.DataAlbumFilterDisplay
import com.client.xvideos.l.model.albumFilterDisplay
import com.redgifs.common.ThemeRed


@Preview(showSystemUi = false, showBackground = false)
@Composable
fun PreviewAlbumFilterDisplay() {

    val select by remember { mutableStateOf(albumFilterDisplay[0]) }
    AlbumFilterDisplay(albumFilterDisplay, select, {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumFilterDisplay(list: List<DataAlbumFilterDisplay>, selected: DataAlbumFilterDisplay, onRequest : (String)-> Unit) {

    val uniquePrimaryList = list.map { it.primary }.distinct()

    var expanded by remember { mutableStateOf(false) }
    var expanded2 by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- Первое меню (Primary) ---
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .weight(1f) // равная ширина
                .height(43.dp)
        ) {
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
                    selected.primary,
                    modifier = Modifier.padding(start = 4.dp),
                    maxLines = 1,
                    style = TextStyle(
                        color = ThemeL.textColor,
                        fontFamily = ThemeL.fontFamilyKarla,
                        fontSize = 16.sp
                    )
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = ThemeL.textColor)
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = ThemeRed.colorTabLevel3
            ) {
                uniquePrimaryList.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(item, color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyKarla, fontSize = 16.sp)
                        },
                        onClick = {
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.width(4.dp))

        // --- Второе меню (Secondary) ---
        ExposedDropdownMenuBox(
            expanded = expanded2,
            onExpandedChange = { expanded2 = it },
            modifier = Modifier
                .weight(1f) // равная ширина
                .height(43.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, ThemeL.grey5, RoundedCornerShape(4.dp))
                    .background(ThemeL.grey3)
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BasicText(
                    selected.secondary,
                    modifier = Modifier.padding(start = 4.dp),
                    maxLines = 1,
                    style = TextStyle(
                        color = ThemeL.textColor,
                        fontFamily = ThemeL.fontFamilyKarla,
                        fontSize = 16.sp
                    )
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = ThemeL.textColor)
            }

            ExposedDropdownMenu(
                expanded = expanded2,
                onDismissRequest = { expanded2 = false },
                containerColor = ThemeRed.colorTabLevel3
            ) {
                val items = list.filter { it.primary == selected.primary }
                items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(item.secondary, color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyKarla, fontSize = 16.sp, maxLines = 1)
                        },
                        onClick = {
                            expanded2 = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.width(4.dp))

        // --- Кнопка ---
        Box(
            modifier = Modifier
                .weight(1f) // равная ширина
                .height(43.dp)
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, ThemeL.grey5, RoundedCornerShape(4.dp))
                .background(ThemeL.red),
            contentAlignment = Alignment.Center
        ) {
            Text("Apply", color = Color.White, fontSize = 20.sp)
        }
    }

}