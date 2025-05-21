package com.client.xvideos.screens_red.profile.bottom_bar.feedControl

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.R
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.screens_red.ThemeRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Red_Profile_SortBy(list: List<Order>, selected: Order, onSelect: (Order) -> Unit) {

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier
    )
    {

        Row(
            modifier = Modifier
                .width(120.dp)
                .height(48.dp)
                .menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable)
                .border(
                    1.dp, ThemeRed.colorBorderGray,
                    RoundedCornerShape(8.dp)
                )
                .clickable(
                    onClick = {
                        expanded = true
                    }
                ), horizontalArrangement = Arrangement.SpaceAround
        ) {

            BasicText(
                selected.value,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically),
                style = TextStyle(
                    color = Color.White,
                    fontFamily = ThemeRed.fontFamilyPopinsRegular,
                    fontSize = 18.sp
                )
            )

            Icon(
                painter = painterResource(R.drawable.arrow_down),
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .align(Alignment.CenterVertically)
                    .size(12.dp)
                    .rotate(if (expanded) 180f else 0f)
            )
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                //.width(120.dp)
                .border(
                    1.dp, ThemeRed.colorBorderGray,
                    RoundedCornerShape(4.dp)
                ), containerColor = ThemeRed.colorCommonBackground
        ) {
            list.forEach { option ->

                DropdownMenuItem(
                    text = {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (option.value == selected.value) ThemeRed.colorBorderGray else Color.Transparent)
                                .padding(start = 8.dp), contentAlignment = Alignment.CenterStart
                        ) {

                            Text(
                                option.value,
                                style = TextStyle(
                                    color = Color.White,
                                    fontFamily = ThemeRed.fontFamilyPopinsRegular,
                                    fontSize = 18.sp,
                                ),
                                modifier = Modifier
                            )
                        }
                    },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                    //contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }

    }

}