package com.client.xvideos.screens.dashboards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import com.client.xvideos.feature.vibrate.vibrateWithPatternAndAmplitude
import com.client.xvideos.model.GalleryItem
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.client.xvideos.screens.common.urlVideImage.UrlVideoLite

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UrlVideoImageAndLongClick(
    item: GalleryItem,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit,
    onDoubleClick: () -> Unit,
    overlay: @Composable () -> Unit= {}) {

    val haptic = LocalHapticFeedback.current

    val context = LocalContext.current

    var isVideo by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
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
            .then(modifier)

    ) {

        if (isVideo) {
            //Показ видео
            UrlVideoLite(item.previewVideo)
        } else {

            //Показ картинки
            UrlImage(item.previewImage, Modifier.fillMaxWidth())

            overlay.invoke()

        }

    }

}