package com.client.xvideos.screens_red.profile.atom

import androidx.annotation.OptIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.vibrate.vibrateWithPatternAndAmplitude

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RedUrlVideoImageAndLongClick(
    item: GifsInfo,
    index: Int,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit,
    onDoubleClick: () -> Unit,
    overlay: @Composable () -> Unit = {},
) {

    val haptic = LocalHapticFeedback.current

    val context = LocalContext.current

    var isVideo by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1080f / 1920)
            //.aspectRatio(1f)
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
        //RedUrlVideoLite("https://api.redgifs.com/v2/gifs/${item.id.lowercase()}/hd.m3u8", item.urls.thumbnail)
        if (isVideo) {
            //val url = "https://api.redgifs.com/v2/gifs/${item.id.lowercase()}/hd.m3u8"
            //https://api.redgifs.com/v2/gifs/easytightibisbill/hd.m3u8
            //Timber.i("!!! url = $url")
            //RedUrlVideoLiteAndroid("https://api.redgifs.com/v2/gifs/${item.id.lowercase()}/hd.m3u8")
            //RedUrlVideoLite("https://api.redgifs.com/v2/gifs/${item.id.lowercase()}/hd.m3u8")

            Red_Video_Lite_2Rrow(
                "https://api.redgifs.com/v2/gifs/${item.id.lowercase()}/hd.m3u8",
                item.urls.thumbnail,
                true,
                onChangeTime = {},
                Modifier,
                onClick = {isVideo = isVideo.not()},
                onLongClick = {}
            )


        } else {
            //Показ картинки
            RedProfileTile(item, index)
            overlay.invoke()
        }

    }

}