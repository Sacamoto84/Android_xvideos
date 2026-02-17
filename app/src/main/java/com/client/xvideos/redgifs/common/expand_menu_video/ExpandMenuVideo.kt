package com.client.xvideos.redgifs.common.expand_menu_video


import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.common.block.BlockRed
import com.client.xvideos.redgifs.common.downloader.DownloadRed
import com.client.xvideos.redgifs.common.saved.SavedRed
import com.client.xvideos.redgifs.model.GifsInfo
import com.client.xvideos.redgifs.network.api.RedApi


private val tintColor = Color(0xFF48454E)
private val style = TextStyle(color = tintColor, fontFamily = ThemeRed.fontFamilyPopinsRegular, fontSize = 20.sp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandMenuVideo(
    item: GifsInfo? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onRunLike: () -> Unit = {},
    onRefresh: () -> Unit = {},
    isCollection : Boolean = false,
    block: () -> BlockRed,
    redApi: () -> RedApi,
    savedRed: () -> SavedRed,
    downloadRed: () -> DownloadRed,
    haptic : ()->Unit = {}
) {

    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(expanded) {
        haptic.invoke()
    }

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
            Icon( Icons.Default.MoreVert, contentDescription = "", tint = Color.White, modifier = Modifier.size(24.dp))
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(IntrinsicSize.Min),
            containerColor = Color(0xFFF1EDF4)//ThemeRed.colorCommonBackground
        ) {
            DropdownMenuItem_Download(
                item,
                onClick = { downloadRed.invoke().downloadItem(it) }) { expanded = false }
            DropdownMenuItem_Share(
                item,
                onClick = { downloadRed.invoke().downloadItem(it) }) { expanded = false }
            DropdownMenuItem_Block(item = item, block = block) { expanded = false }
            DropdownMenuItem_Like(item, onRunLike, savedRed) { expanded = false }
            DropdownMenuItem_Follow(item, redApi, savedRed) { expanded = false }
            DropdownMenuItem_AddCollection(item, savedRed) { expanded = false }
            if(isCollection) DropdownMenuItem_RemoveFromCollection(item, onRefresh, savedRed) { expanded = false }
        }
    }
}
