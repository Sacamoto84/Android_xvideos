package com.client.xvideos.screens.item.atom

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.client.xvideos.R
import com.google.common.collect.ImmutableList
import timber.log.Timber


/**
 * ## Нижний рядяд кнопок упраления плеером
 */
@OptIn(UnstableApi::class)
@Composable
fun ItemPlayerBottomControl(
    modifier: Modifier = Modifier,
    totalDuration: () -> Long,
    currentTime: () -> Long,
    bufferedPercentage: () -> Int,
    onSeekChanged: (timeMs: Float) -> Unit,
    onValueChangedFinished: (timeMs: Float) -> Unit,
    isPlaying: () -> Boolean,
    onPlayClick: () -> Unit,
    player: Player?,
) {

    val list = remember { mutableListOf<String>() }


    var isDragging by remember { mutableStateOf(false) }   // Флаг перетаскивания

    val duration = remember(totalDuration()) { totalDuration() }

    var videoTimeBack by remember { mutableLongStateOf(0L) }

    val videoTime = remember(currentTime()) {
        currentTime()
    }

    val buffer = remember(bufferedPercentage()) { bufferedPercentage() }

    videoTimeBack = if (isDragging) videoTimeBack else videoTime

    //Timber.i("!!! videoTimeBack:$videoTimeBack videoTime:$videoTime  currentTime:${currentTime()} isDragging:${isDragging}")

    val context = LocalContext.current




//
//    val trackGroups = player?.currentTracks?.groups
//    Timber.d("!!! --------------")
//    if (trackGroups != null) {
//
//        if (trackGroups.size > 0) {
//            val group = trackGroups[0]
//            for (j in 0 until group.length) {
//                val format = group.getTrackFormat(j)
//                Timber.d("!!! Group: 0, Format: $j, Resolution: ${format.width}x${format.height}, Bitrate: ${format.bitrate}")
//            }
//        }
//    }

    // Пытаемся получить TrackSelector из плеера


   // Timber.d("!!! --------------")


    Column(modifier = modifier.padding(bottom = 32.dp)) {




        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color(0xFF1C1C1C)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {


            //Текущее время
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = currentTime.invoke().formatMinSec(),
                color = Color.Cyan
            )


            ///...
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF1C1C1C))
            ) {
                Slider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                    value = buffer.toFloat(),
                    enabled = false,
                    onValueChange = {},
                    valueRange = 0f..100f,
                    colors =
                    SliderDefaults.colors(
                        disabledThumbColor = Color.Transparent,
                        disabledActiveTrackColor = Color.LightGray
                    )
                )

                Slider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                    value = videoTimeBack.toFloat(),
                    onValueChange = {
                        videoTimeBack = it.toLong()
                        isDragging = true
                    },
                    onValueChangeFinished = {
                        onValueChangedFinished.invoke(videoTimeBack.toFloat())
                        isDragging = false
                    },
                    valueRange = 0f..duration.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        activeTickColor = Color.DarkGray
                    )
                )
            }

            //Общее время
            Text(
                modifier = Modifier.padding(end = 4.dp),
                text = duration.formatMinSec(),
                color = Color.Cyan
            )

        }

        //Строка с кнопками
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color.DarkGray),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.padding(horizontal = 0.dp),
                onClick = {},
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
            ) {
                Icon(
                    painter = painterResource(R.drawable.fit_to_page_outline),
                    contentDescription = "", tint = Color.White
                )
            }

            //Кнопка запуск/пауза
            IconButton(
                modifier = Modifier
                    .padding(horizontal = 0.dp, vertical = 4.dp)
                    .size(48.dp),
                onClick = {onPlayClick.invoke()},
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
            ) {

                Icon(
                    painter = painterResource(if (isPlaying.invoke()) R.drawable.pause_circle else R.drawable.play_circle),
                    contentDescription = "", tint = Color.White
                )
            }

            IconButton(
                modifier = Modifier.padding(horizontal = 0.dp).size(32.dp),
                onClick = {},
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
            ) {
                Icon(
                    painter = painterResource(R.drawable.fit_to_page_outline),
                    contentDescription = "", tint = Color.White
                )
            }

        }


    }
}

