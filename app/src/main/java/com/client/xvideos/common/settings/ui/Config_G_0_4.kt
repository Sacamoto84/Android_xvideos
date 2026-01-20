package com.client.xvideos.common.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.redgifs.common.ThemeRed
import com.composeunstyled.Text
import com.skydoves.compose.stability.runtime.TraceRecomposition


@Composable
fun Config_G_0_4(text: String = "123453232") {
//    Row(
//        modifier = Modifier.padding(horizontal = 8.dp).padding(vertical = 2.dp).height(48.dp).fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(text, style = styleTextConfig)
//        Text(value, style = styleTextConfig)
//    }
    val selectedOptions = remember { mutableStateListOf(false, false, true, true, false) }

    Row(
        modifier = Modifier.padding(horizontal = 8.dp).padding(vertical = 2.dp).height(48.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, modifier = Modifier.width(64.dp), style = styleTextConfig)

        MultiChoiceSegmentedButtonRow(
            modifier = Modifier.padding(start = 16.dp).fillMaxWidth()
        ) {
            selectedOptions.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = selectedOptions.size
                    ),
                    checked = selectedOptions[index],
                    onCheckedChange = {
                        selectedOptions[index] = !selectedOptions[index]
                    },

                    //icon = { SegmentedButtonDefaults.Icon(selectedOptions[index]) },

                    label = {
                        when (index) {
                            0 -> TabBarPoints(0, selectedOptions[index])
                            1 -> TabBarPoints(1, selectedOptions[index])
                            2 -> TabBarPoints(2, selectedOptions[index])
                            3 -> TabBarPoints(3, selectedOptions[index])
                            4 -> TabBarPoints(4, selectedOptions[index])
                        }
                    }
                )
            }
        }

    }


}


@Composable
private fun TabBarPoints(count: Int, screenType: Boolean) {
    Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row {
            if (count == 0) {
                Text(
                    "T",
                    color = if (screenType) Color.White else Color.Gray,
                    fontFamily = ThemeRed.fontFamilyPopinsSemiBold,
                    fontSize = 12.sp
                )
            } else {
                repeat(count) {
                    Box(
                        modifier = Modifier
                            .padding(end = 2.dp)
                            .clip(CircleShape)
                            .size(4.dp)
                            .background(if (screenType) Color.White else Color.Gray)
                    )
                }
            }
        }
    }
}


@TraceRecomposition
@Preview(showBackground = false)
@Composable
fun PreviewConfig_G_0_4() {
    Config_G_0_4()
}
