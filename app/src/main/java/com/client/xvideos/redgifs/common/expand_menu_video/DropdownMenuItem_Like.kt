package com.client.xvideos.redgifs.common.expand_menu_video

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.client.xvideos.redgifs.common.saved.SavedRed
import com.client.xvideos.redgifs.model.GifsInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun DropdownMenuItem_Like(item: GifsInfo? = null, onRunLike: () -> Unit, savedRed: ()-> SavedRed, onDismiss: () -> Unit){
    val isLiked = savedRed.invoke().likes.list.any { it.id == item?.id }
    val textLiked = if (isLiked) "Unlike" else "Like"
    val textLikedIcon = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder
    DropdownMenuItem(
        leadingIcon = {Icon(textLikedIcon, contentDescription = "", tint = tintColor)},
        text = { Text(textLiked, style = style) },
        onClick = {
            if (item == null) return@DropdownMenuItem
            GlobalScope.launch {
                delay(200)
                if (!isLiked) savedRed.invoke().likes.add(item) else savedRed.invoke().likes.remove(item)
                onRunLike.invoke()
                onDismiss.invoke()
            }
        }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
    )
}