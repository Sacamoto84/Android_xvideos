package com.client.xvideos.redgifs.common.expand_menu_video

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Unsubscribe
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.redgifs.common.saved.SavedRed
import com.client.xvideos.redgifs.model.GifsInfo
import com.client.xvideos.ui.theme.XvideosTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun DropdownMenuItem_Subscribtion(item: GifsInfo? = null, savedRed: ()->SavedRed, onDismiss: () -> Unit){

    val isSubscribed = savedRed.invoke().subscriptions.listCreators.any { it == item?.userName }

    DropdownMenuItem_SubscriptionContent(

        isSubscribted = isSubscribed,

        onClick = {
            if (item == null) return@DropdownMenuItem_SubscriptionContent

            GlobalScope.launch {
                delay(200)
                if (!isSubscribed) {
                    try {
                        savedRed.invoke().subscriptions.add(item.userName)
                    } catch (e: Exception) { e.printStackTrace() }
                }
                else {
                    savedRed.invoke().subscriptions.remove(item.userName)
                }
            }
            onDismiss.invoke()
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownMenuItem_SubscriptionContent(
    isSubscribted: Boolean,
    onClick: () -> Unit
) {
    val textFollowed = if (isSubscribted) "Unsubscribe" else "Subscribe"
    val textFollowedIcon = if (isSubscribted) Icons.Default.Unsubscribe else Icons.Default.Subscriptions
    DropdownMenuItem(
        leadingIcon = {Icon(textFollowedIcon, contentDescription = "", tint = ThemeL.ExpandMenu.tintColor)},
        text = { Text(textFollowed, style = ThemeL.ExpandMenu.style) },
        onClick = onClick,
        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
    )
}

@Preview(showBackground = true)
@Composable
private fun DropdownMenuItem_FollowPreview() {
    XvideosTheme {
        DropdownMenuItem_SubscriptionContent(
            isSubscribted = true,
            onClick = {}
        )
    }
}
