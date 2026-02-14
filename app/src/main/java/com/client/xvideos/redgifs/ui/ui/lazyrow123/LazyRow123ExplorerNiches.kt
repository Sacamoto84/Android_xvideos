package com.client.xvideos.redgifs.ui.ui.lazyrow123

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
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.client.xvideos.common.coil.UrlImage
import com.client.xvideos.common.util.toPrettyCountInt
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.model.Niche
import com.client.xvideos.redgifs.model.NichesInfo
import com.client.xvideos.redgifs.ui.niche.R_ScreenNiche
import com.client.xvideos.redgifs.common.saved.SavedRed

@Composable
fun NichePreview2(niches: Niche, savedRed: SavedRed, onClick: () -> Unit) {

    Row(
        modifier = Modifier.padding(horizontal = 8.dp) .fillMaxWidth().height(78.dp)
            .clip(RoundedCornerShape(16.dp)).background(Color(0xFF323232)).clickable { onClick() }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween )
    {

            UrlImage( niches.thumbnail, modifier = Modifier.padding(start = 4.dp).size(70.dp).clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)) )

            Column( modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp).fillMaxWidth().fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween)
            {

                Text(
                    text = niches.name,
                    modifier = Modifier.fillMaxWidth().height((70/3).dp),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = ThemeRed.fontFamilyDMsanss
                )

                Row(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {

                    Column {
                        Row(modifier = Modifier.height((70/3).dp),verticalAlignment = Alignment.CenterVertically) {
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
                        Row(modifier = Modifier.height((70/3).dp), verticalAlignment = Alignment.CenterVertically) {
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
                            .padding(end = 6.dp)
                            .width(128.dp).height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border( 1.dp, if (isFollowed) Color.White else Color.Transparent, RoundedCornerShape(10.dp))
                            .background(if (isFollowed) Color(0xFF111111) else ThemeRed.colorYellow)
                            .clickable(onClick = {

                                val nichesInfo = NichesInfo(
                                    id = niches.id,
                                    name = niches.name,
                                    subscribers = niches.subscribers,
                                    gifs = niches.gifs,
                                    thumbnail = niches.thumbnail,
                                )

                                if (isFollowed) savedRed.niches.remove(nichesInfo) else savedRed.niches.add(nichesInfo)

                            }), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (isFollowed) "Выйти" else "Подписаться",
                            color = if (isFollowed) Color.White else Color.Black
                        )
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
                repeat(niches.previews.size) {
                    UrlImage(
                        niches.previews[it].thumbnail,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f),
                        contentScale = ContentScale.Crop
                    )
                }
                repeat((3 - niches.previews.size).coerceIn(0, 3)) {
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


