package com.client.xvideos.feature.videoplayer.chaintech.videoplayer.ui.video.controls

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.PlayerOption
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.VideoPlayerConfig
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.gradientBGColors
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.selectedSpeedButtonColor
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.selectedTextColor
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.unselectedSpeedButtonColor
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.unselectedTextColor
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.ui.component.PlayerSpeedButton
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.util.SubtitleTrack
import kotlinx.coroutines.delay


@Composable
internal fun SubTitlesSelectionOverlay(
    paddingValues: PaddingValues,
    playerConfig: VideoPlayerConfig,
    subTitlesOptions: List<SubtitleTrack>,
    selectedSubTitle: SubtitleTrack?,
    selectedSubTitleCallback: (SubtitleTrack?) -> Unit,
    activeOption: PlayerOption,
    activeOptionCallBack: ((PlayerOption) -> Unit) = {},
) {
    LaunchedEffect(activeOption) {
        if (activeOption == PlayerOption.SUBTITLES) {
            delay(5000)
            activeOptionCallBack(PlayerOption.NONE)
        }
    }

    val subTitleList = listOf(SubtitleTrack(language = "", name = "Off", groupId = "", url = "", isDefault = false)) + subTitlesOptions
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = activeOption == PlayerOption.SUBTITLES,
            enter = fadeIn(),
            exit = fadeOut(animationSpec = tween(durationMillis = 700))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = Brush.horizontalGradient(gradientBGColors))
                    .pointerInput(Unit) {
                        detectTapGestures { activeOptionCallBack(PlayerOption.NONE) }
                    }
            )
        }

        AnimatedVisibility(
            visible = activeOption == PlayerOption.SUBTITLES,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 500)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 500)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 35.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Center, // Ensures centering
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f) // Takes full height, centering LazyColumn
                        ) {
                            LazyColumn(
                                modifier = Modifier.align(Alignment.Center).padding(paddingValues),
                                verticalArrangement = Arrangement.spacedBy(15.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                contentPadding = PaddingValues(vertical = 20.dp)
                            ) {
                                items(subTitleList) { track ->
                                    SubTitlesSelectionButton(
                                        subTitle = track,
                                        selectedSubTitle = selectedSubTitle,
                                        playerConfig = playerConfig,
                                        onSelect = {
                                            selectedSubTitleCallback(if (it.name == "Off") null else it)
                                            activeOptionCallBack(PlayerOption.NONE)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun SubTitlesSelectionButton(
    subTitle: SubtitleTrack,
    selectedSubTitle: SubtitleTrack?,
    playerConfig: VideoPlayerConfig,
    onSelect: (SubtitleTrack) -> Unit
) {
    val isSelected = selectedSubTitle == subTitle || (subTitle.name == "Off" && selectedSubTitle == null)

    PlayerSpeedButton(
        title = subTitle.name,
        size = (playerConfig.topControlSize * 1.25f),
        backgroundColor = if (isSelected) selectedSpeedButtonColor else unselectedSpeedButtonColor,
        titleColor = if (isSelected) selectedTextColor else unselectedTextColor,
        onClick = { onSelect(subTitle) }
    )
}