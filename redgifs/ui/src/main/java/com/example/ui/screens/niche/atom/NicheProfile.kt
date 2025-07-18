package com.example.ui.screens.niche.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.client.common.urlVideImage.UrlImage
import com.client.common.util.toPrettyCount
import com.redgifs.common.ThemeRed
import com.redgifs.common.saved.SavedRed
import com.redgifs.common.snackBar.SnackBarEvent
import com.redgifs.db.dao.CacheMediaResponseDao
import com.redgifs.model.NichesInfo
import com.redgifs.network.api.RedApi

@Composable
fun NicheProfile(savedRed: SavedRed, niche: NichesInfo) {

    Box {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            UrlImage(
                niche.thumbnail,
                modifier = Modifier
                    .size(128.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Column(
                modifier = Modifier
                    .padding(start = 8.dp).height(128.dp)
                    .weight(1f), verticalArrangement = Arrangement.SpaceBetween
            ) {

                val color = if (niche.id == "id") Color.Transparent else Color.Gray

                if (niche.id != "id") {
                    Text(niche.name, color = Color.White, fontFamily = ThemeRed.fontFamilyDMsanss)
                }

                Text(
                    niche.subscribers.toPrettyCount(),
                    color = color,
                    modifier = Modifier,
                    fontFamily = ThemeRed.fontFamilyDMsanss
                    //textAlign = TextAlign.End
                )

                Text(
                    niche.gifs.toPrettyCount(),
                    color = color,
                    fontFamily = ThemeRed.fontFamilyDMsanss
                )

                if (niche.id != "id") {
                    ButtonFollow(savedRed = savedRed, niche = niche)
                }

            }
        }

    }
}

@Composable
private fun ButtonFollow(savedRed: SavedRed, niche: NichesInfo) {
    val isFollowed = savedRed.niches.list.any { it.id == niche.id }

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
                    id = niche.id,
                    name = niche.name,
                    subscribers = niche.subscribers,
                    gifs = niche.gifs,
                    thumbnail = niche.thumbnail,
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

//@Preview
//@Composable
//fun ButtonFollowPreview() {
//    val cacheMediaResponseDao: CacheMediaResponseDao? = null
//    val redApi = RedApi(dao = cacheMediaResponseDao!!)
//    val snackBarEvent = SnackBarEvent()
//    val savedRed = SavedRed(redApi = redApi, snackBarEvent = snackBarEvent)
//    val niche = NichesInfo(
//        cover = "cover",
//        description = "description",
//        gifs = 0,
//        id = "id",
//        name = "name",
//        owner = "owner",
//        subscribers = 0,
//        thumbnail = "thumbnail",
//        rules = "rules"
//    )
//    ButtonFollow(savedRed, niche)
//}
