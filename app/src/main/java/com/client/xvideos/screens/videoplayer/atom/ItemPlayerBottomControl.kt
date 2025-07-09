package com.client.xvideos.screens.videoplayer.atom

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries

import androidx.annotation.DrawableRes
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.common.R
import com.client.xvideos.screens.videoplayer.ScreenVideoPlayerSM


/**
 * ## Нижний рядяд кнопок упраления плеером
 */
@OptIn(UnstableApi::class)
@Composable
fun ItemPlayerBottomControl(
    vm: ScreenVideoPlayerSM,
    modifier: Modifier = Modifier,
    currentTime: () -> Long,
    bufferedPercentage: () -> Int,
    onSeekChanged: (timeMs: Float) -> Unit,
    onValueChangedFinished: (timeMs: Float) -> Unit,
    isPlaying: () -> Boolean,
    onPlayClick: () -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow

    val list = remember { mutableListOf<String>() }

    var isDragging by remember { mutableStateOf(false) }   // Флаг перетаскивания

    //val duration = remember(totalDuration()) { totalDuration() }

    var videoTimeBack by remember { mutableLongStateOf(0L) }

    val videoTime = remember(currentTime()) {
        currentTime()
    }

    val buffer = remember(bufferedPercentage()) { bufferedPercentage() }

    videoTimeBack = if (isDragging) videoTimeBack else videoTime

    //Timber.i("!!! videoTimeBack:$videoTimeBack videoTime:$videoTime  currentTime:${currentTime()} isDragging:${isDragging}")


    Column(modifier = modifier.padding(bottom = 0.dp)) {


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
                    valueRange = 0f..vm.totalDuration.toFloat(),
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
                text = vm.totalDuration.formatMinSec(),
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

            //Кнопка запуск/пауза
            IconButton(
                modifier = Modifier
                    .padding(horizontal = 0.dp, vertical = 4.dp)
                    .size(48.dp),
                onClick = { onPlayClick.invoke() },
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
            ) {

                Icon(
                    painter = painterResource(if (isPlaying.invoke()) R.drawable.exo_icon_pause else R.drawable.exo_icon_play),
                    contentDescription = "", tint = Color.White
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically){
                //Изменение качества
                VideoQualitySelector(vm.quality, list = vm.listFormat) {
                    vm.quality = it
                    val targetId = vm.listFormat.find { el -> el.height == it }?.id
                    if (targetId != null) {
                        vm.switchTrack(targetId)
                    }
                }
                Spacer(Modifier.width(8.dp))
                VideoSpeedSelector(vm.speed, onClick = { vm.changePlaybackSpeed(it) })
                Spacer(Modifier.width(8.dp))
                IconButtonLocal(R.drawable.exo_ic_fullscreen_enter){vm.isFullScreen = true}
            }

        }









    }
}


@Composable
fun IconButtonLocal(@DrawableRes id: Int, sizeIB : Dp = 48.dp, sizeI : Dp = 40.dp, onClick: () -> Unit){
    IconButton(
        modifier = Modifier
            .padding(horizontal = 0.dp)
            .size(sizeIB),
        onClick = {onClick.invoke()},
        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
    ) {
        Icon(
            painter = painterResource(id),
            contentDescription = "", tint = Color.White,
            modifier = Modifier.size(sizeI)
        )
    }
}
