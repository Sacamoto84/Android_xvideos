package com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.client.xvideos.l.ThemeL

@Composable
fun AlbumListAlbumType(onChange: (Int) -> Unit) {

    var selectedIndex by remember { mutableIntStateOf(0) }

    val options = listOf("All", "Manga", "Pictures")

    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth(2/3f)) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = ThemeL.grey4
                ),

                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size,
                    baseShape = RoundedCornerShape(4.dp)
                ),
                onClick = {
                    selectedIndex = index
                    onChange(index)
                },
                selected = index == selectedIndex,
                label = { Text(label, color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyKarla) }
            )
        }
    }


}