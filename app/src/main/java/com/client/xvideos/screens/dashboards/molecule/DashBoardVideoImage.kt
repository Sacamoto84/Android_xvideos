package com.client.xvideos.screens.dashboards.molecule

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.model.GalleryItem
import com.client.xvideos.screens.dashboards.atom.DashBoardImage
import com.client.xvideos.screens.dashboards.atom.DashBoardVideoLite
import com.client.xvideos.vibrateWithPatternAndAmplitude


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashBoardVideoImage(item: GalleryItem, onLongClick : () -> Unit ) {

    val haptic = LocalHapticFeedback.current

    val context = LocalContext.current

    var isVideo by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxSize()
        .combinedClickable (
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

            DashBoardVideoLite(item.previewVideo)

//            VideoPlayerView(
//                modifier = Modifier.fillMaxSize(),
//                url = item.previewVideo,
//                playerConfig = PlayerConfig(
//                    isMute = true,
//                    loop = true,
//                    isSeekBarVisible = false,
//                    showDesktopControls = false,
//                    isPauseResumeEnabled = false,
//                    isAutoHideControlEnabled = true,
//                    controlHideIntervalSeconds = 0
//                )
//            )

        } else {
            DashBoardImage(item.previewImage, context)

            val offsetY = (-3).dp

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

        }

    }

}