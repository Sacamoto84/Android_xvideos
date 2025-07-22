package com.redgifs.common.video.player_row_mini

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import com.client.common.AppPath
import com.client.common.urlVideImage.UrlImage
import com.client.common.vibrate.vibrateWithPatternAndAmplitude
import com.composeunstyled.theme.ThemeOverride
import com.redgifs.common.BuildConfig
import com.redgifs.common.ThemeRed
import com.redgifs.common.downloader.DownloadRed
import com.redgifs.common.video.player_row_mini.atom.Red_Video_Lite_Row2
import com.redgifs.model.GifsInfo
import timber.log.Timber
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RedUrlVideoImageAndLongClick(
    item: GifsInfo,                      //Текущий элемент
    index: Int,                          //Индекс элемента, отображается в режиме картинка
    modifier: Modifier = Modifier,

    //--- Свойства ---
    isNetConnected: Boolean,             // Состояние сети
    isVisibleView: Boolean = true,       // Показать количество просмотров
    isVisibleDuration: Boolean = true,   // Показать продолжительность видео

    play: Boolean = false,                //Запуск видео или картинка, управление из вне

    //-= Колбеки =-

    //--- Нажатия на кнопки ---
    onFullScreen: () -> Unit = {},         //Нажатие на кнопку FullScreen
    onLongClick: () -> Unit = {},
    onDoubleClick: () -> Unit = {},

    onVideo: (Boolean) -> Unit = {},       //true - видео, false - картинка

    downloadRed: DownloadRed

) {

    if (BuildConfig.DEBUG) {
        SideEffect {
            Timber.i("@@@ RedUrlVideoImageAndLongClick() play:$play")
        }
    }

    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var isVideo by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(isVideo) { onVideo(isVideo) }

    LaunchedEffect(play) { isVideo = play }

    var poster by remember { mutableStateOf(true) }

    val videoUri: String = remember(item.id, item.userName) {
        Timber.tag("???").i("Перерачсет videoItem.id = ${item.id}")
        //Определяем адрес откуда брать видео, из кеша или из сети
        if (downloadRed.downloader.findVideoInDownload(item.id, item.userName))
            "${AppPath.cache_download_red}/${item.userName}/${item.id}.mp4"
        else {
            if (isNetConnected)
                "https://api.redgifs.com/v2/gifs/${item.id.lowercase()}/hd.m3u8"
            else
                "android.resource://${context.packageName}/raw/q"
        }
    }

    val imageUrl by remember {
        mutableStateOf(
            run {
                val imagePath = "${AppPath.cache_download_red}/${item.userName}/${item.id}.jpg"
                if (File(imagePath).exists()) {
                    imagePath
                } else {
                    item.urls.poster ?: item.urls.thumbnail
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1080f / 1920)
            .combinedClickable(
                indication = null, // 👈 отключает ripple
                interactionSource = interactionSource, // 👈 обязательно для отключения ripple

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

        AnimatedVisibility(
            isVideo,
            enter = fadeIn(animationSpec = tween(100)),
            exit = fadeOut(animationSpec = tween(200))
        ) {

            //Timber.i("@@@ RedUrlVideoImageAndLongClick() >> videoUri: $videoUri")
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Red_Video_Lite_Row2(
                    videoUri,
                    play = true,
                    onClick = { isVideo = isVideo.not() },
                    onLongClick = { onFullScreen.invoke() },
                    poster = { poster = it }
                )
            }
        }

        AnimatedVisibility(
            poster || !isVideo,
            enter = fadeIn(animationSpec = tween(100)),
            exit = fadeOut(animationSpec = tween(100))
        ) {
            Box{
                UrlImage(
                    url = imageUrl,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (isVideo) 0.8f else 1.0f),
                    //isGrayscale = isVideo
                )

                if (isVideo) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.LightGray
                    )
                }
            }

        }

    }

}


