package com.client.xvideos.screens_red.profile.atom

import android.graphics.drawable.Icon
import androidx.annotation.OptIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import com.client.xvideos.AppPath
import com.client.xvideos.feature.findVideoOnRedCacheDownload
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.vibrate.vibrateWithPatternAndAmplitude
import timber.log.Timber

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RedUrlVideoImageAndLongClick(
    item: GifsInfo,
    index: Int,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit,
    onDoubleClick: () -> Unit,
    overlay: @Composable () -> Unit = {},
    onFullScreen: () -> Unit = {}, //Запуск фуллскрин
    onVideo: (Boolean) -> Unit = {},


    isVisibleView : Boolean = true,
    isVisibleDuration : Boolean = true,

    play: Boolean = false

) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var isVideo by remember { mutableStateOf(false) }

    LaunchedEffect(isVideo) { onVideo(isVideo) }

    LaunchedEffect(play) { isVideo = play }

    val videoUri: String = remember(item.id, item.userName) {
        Timber.tag("???").i("Перерачсет videoItem.id = ${item.id}")
        //Определяем адрес откуда брать видео, из кеша или из сети
        if (findVideoOnRedCacheDownload(item.id, item.userName))
            "${AppPath.cache_download_red}/${item.userName}/${item.id}.mp4"
        else
            "https://api.redgifs.com/v2/gifs/${item.id.lowercase()}/hd.m3u8"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1080f / 1920)
            .combinedClickable(
                onDoubleClick = {
                    vibrateWithPatternAndAmplitude(context = context)
                    onDoubleClick.invoke()
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
            .then(modifier),
        contentAlignment = Alignment.Center

    ) {
        if (isVideo) {

            Red_Video_Lite_Row2(
                videoUri,//"https://api.redgifs.com/v2/gifs/${item.id.lowercase()}/hd.m3u8",
                play = true,
                Modifier,
                onClick = {isVideo = isVideo.not()},
                onLongClick = {},
                overlayBottomEnd ={
                    IconButton (
                        modifier = Modifier.height(48.dp).width(48.dp), onClick = {
                            onFullScreen.invoke()
                        }
                    ) { Icon(Icons.Filled.Fullscreen,  contentDescription = null, tint =  Color.White) }
                }
            )
        } else {
            //Показ картинки
            RedProfileTile(item, index, isVisibleView = isVisibleView, isVisibleDuration = isVisibleDuration)
            Row(modifier = Modifier.fillMaxSize().align(Alignment.BottomCenter), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.End) {
                if (!videoUri.contains("https"))
                    Icon( Icons.Default.Save, contentDescription = null, tint = Color.LightGray,  modifier = Modifier.padding(bottom = 9.dp, end = 9.dp).size(18.dp))
            }
            overlay.invoke()
        }

    }

}