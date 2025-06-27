package com.client.xvideos.red.common.ui.lazyrow123

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.feature.redgifs.types.Niche
import com.client.xvideos.feature.redgifs.types.NichesInfo
import com.client.xvideos.red.ThemeRed
import com.client.xvideos.red.common.saved.SavedRed
import com.client.xvideos.red.screens.niche.ScreenRedNiche
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.client.xvideos.util.toPrettyCountInt

@Composable
fun LazyRow123ExplorerNiches(
    host: LazyRow123Host,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {

    val navigator = LocalNavigator.currentOrThrow


    val listNiche = host.pager.collectAsLazyPagingItems() as LazyPagingItems<Niche>

    val state = host.state//rememberLazyGridState()


    if (listNiche.itemCount == 0) return

    LazyVerticalGrid(
        state = state,
        columns = GridCells.Fixed(host.columns.coerceIn(1..3)),
        modifier = Modifier.then(modifier),
        contentPadding = contentPadding,
    ) {
        items(
            count = listNiche.itemCount,
        ) { index ->
            val item = listNiche[index]
            if (item != null) {
                Box(modifier = Modifier.padding(vertical = 4.dp)) {
                    NichePreview2(niches = item, onClick = {
                        navigator.push(ScreenRedNiche(item.id))
                    })
                }
            }
        }
    }

}

@Composable
fun NichePreview2(niches: Niche, onClick: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF323232))
            .clickable { onClick() })
    {

        ////////////////////////////////////////////
        Row(modifier = Modifier.fillMaxWidth()) {
            UrlImage(
                niches.thumbnail,
                modifier = Modifier
                    .padding(top = 4.dp, start = 4.dp, bottom = 4.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp))
                    .size(64.dp)
            )

            Column(modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth())
            {

                Text(
                    text = niches.name,
                    modifier = Modifier,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = ThemeRed.fontFamilyDMsanss
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.Group,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color(0XFF959595)
                            )
                            Text(
                                text = niches.subscribers.toPrettyCountInt(),
                                modifier = Modifier.padding(start = 4.dp),
                                color = Color(0XFF959595),
                                fontSize = 16.sp,
                                fontFamily = ThemeRed.fontFamilyDMsanss
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.Photo,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color(0XFF959595)
                            )
                            Text(
                                text = niches.gifs.toPrettyCountInt(),
                                modifier = Modifier.padding(start = 4.dp),
                                color = Color(0XFF959595),
                                fontSize = 16.sp,
                                fontFamily = ThemeRed.fontFamilyDMsanss
                            )
                        }
                    }

                    val isFollowed = SavedRed.nichesList.any { it.id == niches.id }

                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .width(128.dp)
                            .height(44.dp)
                            .border(
                                1.dp,
                                if (isFollowed) Color.White else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .background(if (isFollowed) Color.Black else ThemeRed.colorYellow)
                            .clickable(onClick = {

                                val nichesInfo = NichesInfo(
                                    id = niches.id,
                                    name = niches.name,
                                    subscribers = niches.subscribers,
                                    gifs = niches.gifs,
                                    thumbnail = niches.thumbnail,
                                )

                                if (isFollowed)
                                    SavedRed.removeNiches(nichesInfo)
                                else
                                    SavedRed.addNiches(nichesInfo)

                            }), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (isFollowed) "Выйти" else "Подписаться",
                            color = if (isFollowed) Color.White else Color.Black
                        )
                    }
                }
            }
        }
        ////////////////////////////////////////////
        Row(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            UrlImage(
                niches.previews[0].thumbnail,
                modifier = Modifier
                    .aspectRatio(1f)
                    .weight(1f),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(4.dp))
            UrlImage(
                niches.previews[1].thumbnail,
                modifier = Modifier
                    .aspectRatio(1f)
                    .weight(1f),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(4.dp))
            UrlImage(
                niches.previews[2].thumbnail,
                modifier = Modifier
                    .aspectRatio(1f)
                    .weight(1f),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

    }
}


