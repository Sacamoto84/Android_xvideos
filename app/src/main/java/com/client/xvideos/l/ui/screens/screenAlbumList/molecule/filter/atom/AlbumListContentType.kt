package com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material3.Icon
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.l.ThemeL

private val style = TextStyle(
    color = ThemeL.textColor,
    //fontWeight = FontWeight.Bold,
    fontFamily = ThemeL.fontFamilyKarla,
    fontSize = 14.sp
)

@Composable
fun AlbumListContentType() {

    val selectedOptions = remember {
        mutableStateListOf(false, false, false, false)
    }
    val options = listOf("Walk", "Ride", "Drive", "RealPeople")

    MultiChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                colors = SegmentedButtonDefaults.colors(
                activeContainerColor = ThemeL.grey3,
                activeBorderColor = ThemeL.grey3,
                inactiveBorderColor = ThemeL.grey3,
            ),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size,
                    baseShape = RoundedCornerShape(4.dp)
                ),
                checked = selectedOptions[index],
                onCheckedChange = {
                    selectedOptions[index] = !selectedOptions[index]
                },
                icon = {},// { SegmentedButtonDefaults.Icon(selectedOptions[index]) },
                label = {
                    when (label) {
                        "Walk" -> Text("All", style = style)
                        "Ride" -> Text("Hentai", style = style)
                        "Drive" -> Text("NErotic", style = style)
                        "RealPeople" -> Text("RPeople", style = style)
                    }
                }
            )
        }
    }


}