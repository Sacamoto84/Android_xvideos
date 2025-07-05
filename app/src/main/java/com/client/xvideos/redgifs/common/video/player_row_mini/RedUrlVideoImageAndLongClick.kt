package com.client.xvideos.redgifs.common.video.player_row_mini

import androidx.annotation.OptIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import com.client.xvideos.BuildConfig
import com.client.xvideos.redgifs.network.types.GifsInfo
import com.client.xvideos.feature.vibrate.vibrateWithPatternAndAmplitude
import com.client.xvideos.redgifs.common.downloader.DownloadRed
import com.client.xvideos.redgifs.common.video.player_row_mini.atom.RedProfileTile
import com.client.xvideos.redgifs.common.video.player_row_mini.atom.Red_Video_Lite_Row2
import timber.log.Timber

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RedUrlVideoImageAndLongClick(
    item: GifsInfo,                      //Текущий элемент
    index: Int,                          //Индекс элемента, отображается в режиме картинка
    modifier: Modifier = Modifier,

    overlay: @Composable () -> Unit = {},

    //--- Свойства ---
    isNetConnected : Boolean,             // Состояние сети
    isVisibleView : Boolean = true,       // Показать количество просмотров
    isVisibleDuration : Boolean = true,   // Показать продолжительность видео

    play: Boolean = false,                //Запуск видео или картинка, управление из вне

    //-= Колбеки =-

    //--- Нажатия на кнопки ---
    onFullScreen: () -> Unit = {},         //Нажатие на кнопку FullScreen
    onLongClick: () -> Unit= {},
    onDoubleClick: () -> Unit= {},

    onVideo: (Boolean) -> Unit = {},       //true - видео, false - картинка

    onVideoUri : (String)-> Unit = {}

) {

    if (BuildConfig.DEBUG) { SideEffect {
        Timber.i("@@@ RedUrlVideoImageAndLongClick() play:$play") } }

    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var isVideo by remember { mutableStateOf(false) }

    LaunchedEffect(isVideo) { onVideo(isVideo) }

    LaunchedEffect(play) { isVideo = play }

    val videoUri: String = remember(item.id, item.userName) {
        Timber.tag("???").i("Перерачсет videoItem.id = ${item.id}")
        //Определяем адрес откуда брать видео, из кеша или из сети
        if (DownloadRed.findVideoInDownload(item.id, item.userName))
            "${AppPath.cache_download_red}/${item.userName}/${item.id}.mp4"
        else {
            if (isNetConnected)
                "https://api.redgifs.com/v2/gifs/${item.id.lowercase()}/hd.m3u8"
            else
                "android.resource://${context.packageName}/raw/q"
        }
    }

    SideEffect {
        onVideoUri(videoUri)
        Timber.i("@@@ RedUrlVideoImageAndLongClick() >> videoUri: $videoUri")
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
                onClick = { isVideo = isVideo.not() },
                onLongClick = {},
                overlayBottomEnd = {
                    IconButton(
                        modifier = Modifier.size(48.dp), onClick = { onFullScreen.invoke() }
                    ) {Icon(Icons.Filled.Fullscreen, contentDescription = null, tint = Color.White)}
                }
            )

        } else {
            //Показ картинки
            RedProfileTile(
                item, index, isVisibleView = isVisibleView, isVisibleDuration = isVisibleDuration
            )

            overlay.invoke()
        }

    }

}