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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.model.ContentId

private val style = TextStyle(
    color = ThemeL.textColor,
    //fontWeight = FontWeight.Bold,
    fontFamily = ThemeL.fontFamilyKarla,
    fontSize = 14.sp
)

@Composable
fun AlbumListFilterContentType(onStart: ContentId, onChange: (ContentId) -> Unit) {

    var selectedIndex by remember { mutableIntStateOf(
        when (onStart) {
            ContentId.All -> 0
            ContentId.Hentai -> 1
            ContentId.NonErotic -> 2
            ContentId.RealPeople -> 3
        }
    ) }

    val options = listOf("All", "Hentai", "NErotic", "RPeople")

    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = ThemeL.grey4,
                    activeBorderColor = ThemeL.grey3,
                    inactiveBorderColor = ThemeL.grey3,
                ),

                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size,
                    baseShape = RoundedCornerShape(4.dp)
                ),
                onClick = {
                    selectedIndex = index
                    val a = when (index) {
                        0 -> ContentId.All
                        1 -> ContentId.Hentai
                        2 -> ContentId.NonErotic
                        3 -> ContentId.RealPeople
                        else -> ContentId.All
                    }
                    onChange(a)
                },
                selected = index == selectedIndex,
                label = { Text(label, color = ThemeL.textColor, style = style) }
            )
        }
    }

}
