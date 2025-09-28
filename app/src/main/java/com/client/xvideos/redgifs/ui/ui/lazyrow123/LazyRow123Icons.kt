package com.client.xvideos.redgifs.ui.ui.lazyrow123

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.client.xvideos.common.icons.IconCollection18
import com.client.xvideos.common.icons.IconFavorite18
import com.client.xvideos.common.icons.IconPerson18
import com.client.xvideos.common.icons.IconSave18
import com.client.xvideos.redgifs.common.saved.SavedRed
import com.client.xvideos.redgifs.model.GifsInfo

@Composable
fun LazyRow123Icons(
    modifier: Modifier = Modifier,
    savedRed: SavedRed,
    item: GifsInfo,
    downloadList: List<GifsInfo>
) {

    Row(
        modifier = Modifier.fillMaxWidth().then(modifier),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End
    ) {

        if (savedRed.collections.collectionList.any { it.items.any { it2 -> it2.id == item.id } }) {
            IconCollection18(Modifier.padding(bottom = 6.dp, end = 6.dp))
        }

        //
        if (savedRed.creators.list.any { it.username == item.userName }) {
            IconPerson18(Modifier.padding(bottom = 6.dp, end = 6.dp))
        }

        //✅ Лайк
        if (savedRed.likes.list.any { it.id == item.id }) {
            IconFavorite18(Modifier.padding(bottom = 6.dp, end = 6.dp))
        }

        //✅ Иконка того что видео скачано
        if (downloadList.any { it.id == item.id }) {
            IconSave18(Modifier.padding(bottom = 6.dp, end = 6.dp))
        }

    }

}

