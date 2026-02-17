package com.client.xvideos.redgifs.common.expand_menu_video

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.redgifs.common.block.BlockRed
import com.client.xvideos.redgifs.model.GifsInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuItem_Block(item: GifsInfo? = null, block:()-> BlockRed, onDismiss: () -> Unit){
    DropdownMenuItem(
        leadingIcon = {Icon(Icons.Default.Block, contentDescription = "", tint = ThemeL.ExpandMenu.tintColor)},
        text = { Text("Блокировать", style = ThemeL.ExpandMenu.style) },
        onClick = {
            if (item == null) return@DropdownMenuItem; block.invoke().blockVisibleDialog = true
            onDismiss.invoke()
        }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
    )
}