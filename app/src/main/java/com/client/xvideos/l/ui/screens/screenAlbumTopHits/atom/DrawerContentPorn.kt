package com.client.xvideos.l.ui.screens.screenAlbumTopHits.atom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.net.graphQl.mediaCategories

@Composable
fun DrawerContentPorn() {
    if (mediaCategories != null) {
        val a =
            mediaCategories?.genres?.filter { it.onlyContent?.id == "6" || it.onlyContent == null }
                ?: emptyList()
        LazyColumn {
            items(a) {
                Text(
                    it.title,
                    fontSize = 16.sp,
                    color = ThemeL.textColor,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable(onClick = { })
                )
            }
        }
    }
}
