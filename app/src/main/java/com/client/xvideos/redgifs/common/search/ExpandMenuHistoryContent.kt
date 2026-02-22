package com.client.xvideos.redgifs.common.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.ui.theme.XvideosTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandMenuHistoryContent(
    items: ()->List<String>,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {}
) {

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.then(modifier)
    )
    {
        IconButton(
            modifier = Modifier.padding(end = 4.dp).height(46.dp).width(24.dp)
                .menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable),
            onClick = {}) {
            Icon(
                if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = "", tint = Color(0xFF757575),  modifier = Modifier.size(24.dp)
            )
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(IntrinsicSize.Min),
            containerColor = ThemeRed.colorBottomBarDivider
        ) {
            //DropdownMenuItem_Download(item){ expanded = false }

            items().reversed().forEach {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .clickable(onClick = {
                            onClick(it)
                        }),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        it,
                        color = Color.White,
                        fontSize = 22.sp,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .padding(start = 16.dp),
                        fontFamily = ThemeRed.fontFamilyDMsanss
                    )

                    IconButton(onClick = { onDeleteClick(it) }) {
                        Icon( Icons.Default.Clear, contentDescription = null, tint = Color.LightGray )
                    }
                }

            }

        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF303030)
@Composable
fun PreviewExpandMenuHistory() {
    XvideosTheme {
        Surface(color = ThemeRed.colorCommonBackground) {
            ExpandMenuHistoryContent(
                items = {listOf("Search Query 1", "Search Query 2", "Search Query 3")}
            )
        }
    }
}
