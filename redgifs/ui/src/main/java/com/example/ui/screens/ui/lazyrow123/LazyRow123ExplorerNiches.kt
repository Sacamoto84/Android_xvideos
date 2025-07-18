package com.example.ui.screens.ui.lazyrow123

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
import com.redgifs.model.Niche
import com.redgifs.model.NichesInfo
import com.redgifs.common.ThemeRed
import com.redgifs.common.saved.SavedRed
import com.client.common.urlVideImage.UrlImage
import com.client.common.util.toPrettyCountInt
import com.example.ui.screens.niche.ScreenRedNiche

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
                    }, savedRed = host.hostDI.savedRed)
                }
            }
        }
    }

}

@Composable
fun NichePreview2(niches: Niche, savedRed: SavedRed, onClick: () -> Unit) {

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

                    val isFollowed = savedRed.niches.list.any { it.id == niches.id }

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
                                    savedRed.niches.remove(nichesInfo)
                                else
                                    savedRed.niches.add(nichesInfo)

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

        if (!niches.previews.isNullOrEmpty()) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                //Spacer(modifier = Modifier.width(4.dp))
                repeat(niches.previews!!.size) {
                    UrlImage(
                        niches.previews!![it].thumbnail,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f),
                        contentScale = ContentScale.Crop
                    )
                }
                repeat((3 - niches.previews!!.size).coerceIn(0,3)) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f),
                    )
                }
                //Spacer(modifier = Modifier.width(4.dp))

            }
        }
        Spacer(modifier = Modifier.height(4.dp))

    }
}


