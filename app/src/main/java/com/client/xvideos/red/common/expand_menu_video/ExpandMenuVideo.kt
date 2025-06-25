package com.client.xvideos.red.common.expand_menu_video


import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.red.ThemeRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandMenuVideo(
    modifier: Modifier = Modifier,
    option: List<ExpandMenuVideoModel> = emptyList(),
    item: GifsInfo? = null,
    onClick: () -> Unit = {},
    onRun0: () -> Unit = {},
    onRun1: () -> Unit = {},
    onRun2: () -> Unit = {},
    onRun3: () -> Unit = {},
    onRun4: () -> Unit = {},
    onRun5: () -> Unit = {},
) {

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (it) onClick.invoke(); expanded = it },
        modifier = Modifier.then(modifier)
    )
    {
        IconButton(
            modifier = Modifier
                .size(48.dp)
                .menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable),
            onClick = {}) {
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
            modifier = Modifier.width(IntrinsicSize.Min),
            containerColor = Color(0xFFF1EDF4)//ThemeRed.colorCommonBackground
        ) {

            option.forEachIndexed { index, it ->

                DropdownMenuItem(
                    leadingIcon = {
                        if (it.leadingIconVector != null) {
                            Icon(
                                it.leadingIconVector,
                                contentDescription = "",
                                tint = Color(0xFF48454E)
                            )
                        }
                    },

                    text = {
                        Text(
                            it.text,
                            style = TextStyle(
                                color = Color(0xFF48454E),
                                fontFamily = ThemeRed.fontFamilyPopinsRegular,
                                fontSize = 20.sp
                            ),
                            modifier = Modifier
                        )
                    },
                    onClick = {
                        it.onClick.invoke(item)
                        when(index){
                            0 -> onRun0.invoke()
                            1 -> onRun1.invoke()
                            2 -> onRun2.invoke()
                            3 -> onRun3.invoke()
                            4 -> onRun4.invoke()
                            5 -> onRun5.invoke()
                        }
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }

        }
    }
}

@Preview
@Composable
fun ExpandMenuVideoPreview() {
    ExpandMenuVideo()
}
