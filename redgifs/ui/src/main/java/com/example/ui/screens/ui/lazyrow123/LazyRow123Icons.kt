package com.example.ui.screens.ui.lazyrow123

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.client.common.R
import com.redgifs.common.di.HostDI
import com.redgifs.model.GifsInfo

@Composable
fun LazyRow123Icons(modifier : Modifier = Modifier,hostDI : HostDI, item : GifsInfo, isVideo : Boolean, downloadList: List<GifsInfo>) {

    AnimatedVisibility(
        !isVideo, modifier = Modifier.fillMaxSize().then(modifier),
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight }, // снизу вверх
            animationSpec = tween(durationMillis = 200)
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight }, // сверху вниз
            animationSpec = tween(durationMillis = 200)
        )
    )
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End
        ) {


            if (hostDI.savedRed.collections.collectionList.any { it.list.any { it2 -> it2.id == item.id } }) {
                Icon(
                    painter = painterResource(R.drawable.collection_multi_input_svgrepo_com),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(bottom = 6.dp, end = 6.dp)
                        .size(18.dp)
                )
            }

            //
            if (hostDI.savedRed.creators.list.any { it.username == item.userName }) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(bottom = 6.dp, end = 6.dp)
                        .size(18.dp)
                )
            }

            //✅ Лайк
            if (hostDI.savedRed.likes.list.any { it.id == item.id }) {
                Icon(
                    Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(bottom = 6.dp, end = 6.dp)
                        .size(18.dp)
                )
            }

            //✅ Иконка того что видео скачано
            if (
                downloadList.any { it.id == item.id }
            ) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(bottom = 6.dp, end = 6.dp)
                        .size(18.dp)
                )
            }

        }

    }

}


