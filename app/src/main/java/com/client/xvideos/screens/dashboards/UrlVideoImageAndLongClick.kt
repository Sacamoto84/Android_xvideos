package com.client.xvideos.screens.dashboards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.model.GalleryItem
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.client.xvideos.screens.common.urlVideImage.UrlVideoLite
import com.client.xvideos.feature.vibrate.vibrateWithPatternAndAmplitude


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UrlVideoImageAndLongClick(item: GalleryItem, onLongClick: () -> Unit) {

    val haptic = LocalHapticFeedback.current

    val context = LocalContext.current

    var isVideo by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .combinedClickable(
                onDoubleClick = {
                    vibrateWithPatternAndAmplitude(context = context)
                    onLongClick.invoke()
                },

                onLongClick = {
                    //haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    vibrateWithPatternAndAmplitude(context = context)
                    onLongClick.invoke()
                },
                onClick = {
                    isVideo = isVideo.not()
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            )

    ) {

        if (isVideo) {
            //Показ видео
            UrlVideoLite(item.previewVideo)
        } else {

            //Показ картинки
            UrlImage(item.previewImage, Modifier.fillMaxWidth())

            val offsetY = (-3).dp

            //Продолжительность видео
            Text(
                text = item.duration.dropLast(1),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(1.dp, offsetY + 1.dp),
                textAlign = TextAlign.Right,
                fontSize = 14.sp,
                color = Color.Black
            )

            Text(
                text = item.duration.dropLast(1),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(0.dp, offsetY),
                textAlign = TextAlign.Right,
                fontSize = 14.sp,
                color = Color.White
            )


//            //Название канала
//            Box(
//                modifier = Modifier.align(Alignment.TopStart).background(Color(0x60000000)),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = item.channel,
//                    modifier = Modifier
//                        .align(Alignment.Center), fontSize = 14.sp,
//                    color = Color.White
//                )
//            }

            //Индикатор что видео в фаворитах
//            Box(
//                modifier = Modifier.align(Alignment.BottomStart),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = Icons.Filled.FavoriteBorder,
//                    contentDescription = "Like",
//                    tint = Color.Red,
//                    modifier = Modifier.padding(0.dp).size(32.dp)
//                )
//            }


        }

    }

}