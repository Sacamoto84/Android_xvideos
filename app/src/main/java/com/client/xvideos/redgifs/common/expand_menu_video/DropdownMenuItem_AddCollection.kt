package com.client.xvideos.redgifs.common.expand_menu_video

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.client.xvideos.redgifs.common.saved.SavedRed
import com.client.xvideos.redgifs.model.GifsInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuItem_AddCollection(item: GifsInfo? = null, savedRed: ()->SavedRed, onDismiss: () -> Unit){
    DropdownMenuItem(
        leadingIcon = { Icon( Icons.Default.AddCircleOutline, contentDescription = "", tint = tintColor ) },
        text = { Text("Add to Collection", style = style) },
        onClick = {
            if (item == null) return@DropdownMenuItem
            savedRed.invoke().collections.collectionItemGifInfo = item
            savedRed.invoke().collections.collectionVisibleDialog = true
            onDismiss.invoke()
        }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
    )
}