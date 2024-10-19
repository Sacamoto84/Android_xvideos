package com.client.xvideos.screens.item.atom

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.client.xvideos.R
import com.client.xvideos.screens.item.util.formatMinSec
import timber.log.Timber


@Composable
fun BottomControls(
    modifier: Modifier = Modifier,
    totalDuration: () -> Long,
    currentTime: () -> Long,
    bufferedPercentage: () -> Int,
    onSeekChanged: (timeMs: Float) -> Unit,
    onValueChangedFinished: (timeMs: Float) -> Unit,

    isPlaying : () -> Boolean,

    onPlayClick : () -> Unit


) {

    var isDragging by remember { mutableStateOf(false) }   // Флаг перетаскивания

    val duration = remember(totalDuration()) { totalDuration() }

    var videoTimeBack by remember { mutableLongStateOf(0L) }

    val videoTime = remember(currentTime()) {
        currentTime()
    }

    val buffer = remember(bufferedPercentage()) { bufferedPercentage() }

    videoTimeBack = if (isDragging) videoTimeBack else videoTime

    Timber.i("!!! videoTimeBack:$videoTimeBack videoTime:$videoTime  currentTime:${currentTime()} isDragging:${isDragging}")

    Column(modifier = modifier.padding(bottom = 32.dp)) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1C1C1C)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {


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

            Text(
                modifier = Modifier.padding(end = 4.dp),
                text = duration.formatMinSec(),
                color = Color.Cyan
            )

        }

        //...
        Row(
            modifier = Modifier
                .fillMaxWidth()
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

