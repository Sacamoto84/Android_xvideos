package com.client.xvideos.redgifs.common.expand_menu_video

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.client.xvideos.redgifs.common.saved.SavedRed
import com.client.xvideos.redgifs.model.GifsInfo
import com.client.xvideos.redgifs.network.api.RedApi
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun DropdownMenuItem_Follow(item: GifsInfo? = null, redApi:()-> RedApi, savedRed: ()->SavedRed, onDismiss: () -> Unit){
    val isFollowed = savedRed.invoke().creators.list.any { it.username == item?.userName }
    val textFollowed = if (isFollowed) "Unfollow" else "Follow"
    val textFollowedIcon = if (isFollowed) Icons.Default.Person else Icons.Default.PermIdentity
    DropdownMenuItem(
        leadingIcon = {Icon(textFollowedIcon, contentDescription = "", tint = tintColor)},
        text = { Text(textFollowed, style = style) },
        onClick = {
            if (item == null) return@DropdownMenuItem
            GlobalScope.launch {
                delay(200)
                if (!isFollowed) {
                    try {
                        val a = redApi.invoke().readCreator(item.userName).getOrNull()
                        savedRed.invoke().creators.add(a!!)
                    } catch (e: Exception) { e.printStackTrace() }
                }
                else {
                    savedRed.invoke().creators.remove(item.userName)
                }
            }
            onDismiss.invoke()
        }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
    )
}