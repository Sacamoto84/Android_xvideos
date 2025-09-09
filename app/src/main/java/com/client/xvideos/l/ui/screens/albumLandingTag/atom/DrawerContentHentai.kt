package com.client.xvideos.l.ui.screens.albumLandingTag.atom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.net.graphQl.mediaCategories

@Composable
fun DrawerContentHentai() {
    val state = rememberLazyListState()

    if (mediaCategories != null) {

        val a =
            mediaCategories?.genres?.filter { it.onlyContent?.id == "2" || it.onlyContent == null }
                ?: emptyList()

        LazyColumn(state = state) {
            items(a) {
                Box(modifier = Modifier.fillMaxWidth().clickable(onClick = { }).padding(vertical = 3.dp))
                {
                    Text(
                        it.title,
                        fontSize = 18.sp,
                        color = ThemeL.textColor,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}