package com.redgifs.common.expand_menu_video


import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.outlined.Tag
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.redgifs.network.api.RedApi
import com.redgifs.model.GifsInfo
import com.redgifs.common.ThemeRed
import com.redgifs.common.block.BlockRed
import com.redgifs.common.downloader.DownloadRed
import com.redgifs.common.saved.SavedRed
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private val tintColor = Color(0xFF48454E)
private val style =
    TextStyle(color = tintColor, fontFamily = ThemeRed.fontFamilyPopinsRegular, fontSize = 20.sp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandMenuVideoTags(
    item: GifsInfo? = null,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {},
) {

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
            onClick = {}) {
            Icon(
                Icons.Outlined.Tag,
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(IntrinsicSize.Min),
            containerColor = ThemeRed.colorTabLevel3
        ) {

            FlowRow {
                item?.tags?.forEach {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .padding(vertical = 4.dp)
                            .border(1.dp, ThemeRed.colorYellow, RoundedCornerShape(4.dp))
                            .clickable(onClick = {
                                onClick(it)
                                expanded = false
                            }),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            it,
                            color = Color.White,
                            fontFamily = ThemeRed.fontFamilyDMsanss,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}













