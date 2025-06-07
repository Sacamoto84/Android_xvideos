package com.client.xvideos.screens_red.top_this_week


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.client.xvideos.screens_red.ThemeRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandMenuVideo(modifier: Modifier = Modifier) {

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.then(modifier)
    )
    {
        IconButton(
            modifier = Modifier
                .size(48.dp)
                .menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable),
            onClick = { expanded = true }) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(128.dp).border(1.dp, ThemeRed.colorBorderGray, RoundedCornerShape(4.dp)),
            containerColor = ThemeRed.colorCommonBackground
        ) {

            DropdownMenuItem(
                text = {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ThemeRed.colorBorderGray)
                            .padding(start = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {

                        Text(
                            "12345",
                            style = TextStyle(
                                color = Color.White,
                                fontFamily = ThemeRed.fontFamilyPopinsRegular,
                                fontSize = 18.sp,
                            ),
                            modifier = Modifier
                        )
                    }
                },
                onClick = {expanded = false},
                //contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
            )
        }
    }
}

@Preview
@Composable
fun ExpandMenuVideoPreview() {
    ExpandMenuVideo()
}
