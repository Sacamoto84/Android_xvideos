package com.client.xvideos.redgifs.common.expand_menu_video

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.redgifs.common.saved.SavedRed
import com.client.xvideos.redgifs.model.GifsInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuItem_RemoveFromCollection(item: GifsInfo? = null, onRefresh: () -> Unit, savedRed: ()->SavedRed, onDismiss: () -> Unit){

    val selectedCollection = savedRed.invoke().collections.selectedCollection.collectAsStateWithLifecycle().value

    DropdownMenuItem(
        leadingIcon = {
            Icon(
                Icons.Default.RemoveCircleOutline,
                contentDescription = "",
                tint = ThemeL.ExpandMenu.tintColor
            )
        },
        text = { Text("Remove from Collection", style = ThemeL.ExpandMenu.style) },
        onClick = {
            if (item == null) return@DropdownMenuItem
            if (selectedCollection == null) {
                onDismiss.invoke()
                return@DropdownMenuItem
            }
            savedRed.invoke().collections.deleteItemFromCollection(item.id, selectedCollection)
            onRefresh.invoke()

            onDismiss.invoke()
        }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
    )
}