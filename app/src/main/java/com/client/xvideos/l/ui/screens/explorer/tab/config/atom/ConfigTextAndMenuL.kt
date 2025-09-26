package com.client.xvideos.l.ui.screens.explorer.tab.config.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.common.settings.ui.styleTextConfig
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.theme.ThemeL.ExpandMenu.backgroundColor
import com.client.xvideos.l.theme.ThemeL.ExpandMenu.style


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigTextAndMenuL(
    text: String,
    value: String,
    items: List<String>,
    onClick: (String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(start = 8.dp, end = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(text, style = ThemeL.styleTextConfigL)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
        )
        {
            IconButton(
                modifier = Modifier
                    .height(48.dp)
                    .width(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF333333), RoundedCornerShape(8.dp))
                    .background(ThemeL.grey4)
                    .menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable),
                onClick = { expanded = true }) {
                Box(contentAlignment = Alignment.Center) {
                    Text(value, style = styleTextConfig.copy(fontSize = 18.sp))
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(IntrinsicSize.Min),
                containerColor = backgroundColor//ThemeRed.colorCommonBackground
            ) {
                items.forEach {

                    DropdownMenuItem(
//                        leadingIcon = {
//                            Icon(
//                                Icons.Filled.FileDownload,
//                                contentDescription = "",
//                                tint = tintColor
//                            )
//                        },
                        text = { Text(it, style = style) },
                        onClick = {
                            onClick(it)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )

                }

            }

        }


    }

//        Row {
//            Box(
//                modifier = Modifier.padding(end = 8.dp).size(48.dp), contentAlignment = Alignment.Center
//            ) {
//                composableIcon()
//            }
//
//            Box(
//                modifier = Modifier
//                    .height(48.dp)
//                    .width(100.dp)
//                    .clip(RoundedCornerShape(8.dp))
//                    .border(1.dp, Color(0xFF333333), RoundedCornerShape(8.dp))
//                    .background(ThemeL.grey4)
//                    .clickable(onClick = { visible = true }), contentAlignment = Alignment.Center
//            ) {
//                Text(value, style = styleTextConfig.copy(fontSize = 18.sp))
//            }
//        }
}
