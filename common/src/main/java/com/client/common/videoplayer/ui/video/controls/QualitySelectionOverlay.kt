package chaintech.videoplayer.ui.video.controls

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
import com.client.common.videoplayer.model.PlayerOption
import com.client.common.videoplayer.model.VideoPlayerConfig
import com.client.common.videoplayer.model.gradientBGColors
import com.client.common.videoplayer.model.selectedSpeedButtonColor
import com.client.common.videoplayer.model.selectedTextColor
import com.client.common.videoplayer.model.unselectedSpeedButtonColor
import com.client.common.videoplayer.model.unselectedTextColor
import com.client.common.videoplayer.ui.component.PlayerSpeedButton
import com.client.common.videoplayer.util.VideoQuality
import kotlinx.coroutines.delay

@Composable
internal fun QualitySelectionOverlay(
    paddingValues: PaddingValues,
    playerConfig: VideoPlayerConfig,
    qualityOptions: List<VideoQuality>,
    selectedQuality: VideoQuality?,
    selectedQualityCallback: (VideoQuality?) -> Unit,
    activeOption: PlayerOption,
    activeOptionCallBack: ((PlayerOption) -> Unit) = {},
) {
    LaunchedEffect(activeOption) {
        if (activeOption == PlayerOption.QUALITY) {
            delay(5000)
            activeOptionCallBack(PlayerOption.NONE)
        }
    }

    val qualityList = listOf(VideoQuality(bitrate = 0.0, resolution = "Auto", url = "")) + qualityOptions

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = activeOption == PlayerOption.QUALITY,
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
            visible = activeOption == PlayerOption.QUALITY,
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
                                items(qualityList) { quality ->
                                    QualitySelectionButton(
                                        quality = quality,
                                        selectedQuality = selectedQuality,
                                        playerConfig = playerConfig,
                                        onSelect = {
                                            selectedQualityCallback(if (quality.bitrate == 0.0) null else it)
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
internal fun QualitySelectionButton(
    quality: VideoQuality,
    selectedQuality: VideoQuality?,
    playerConfig: VideoPlayerConfig,
    onSelect: (VideoQuality) -> Unit
) {
    val isSelected = (selectedQuality == quality) || (quality.bitrate == 0.0 && selectedQuality == null)

    PlayerSpeedButton(
        title = quality.resolution,
        size = (playerConfig.topControlSize * 1.25f),
        backgroundColor = if (isSelected) selectedSpeedButtonColor else unselectedSpeedButtonColor,
        titleColor = if (isSelected) selectedTextColor else unselectedTextColor,
        onClick = { onSelect(quality) }
    )
}