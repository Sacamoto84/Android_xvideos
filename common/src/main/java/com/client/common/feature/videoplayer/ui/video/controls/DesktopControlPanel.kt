package com.client.common.feature.videoplayer.ui.video.controls

//import com.client.xvideos.feature.videoplayer.reelsdemo.composemultiplatformmediaplayer.generated.resources.Res
//import com.client.xvideos.feature.videoplayer.reelsdemo.composemultiplatformmediaplayer.generated.resources.resize_fill
//import com.client.xvideos.feature.videoplayer.reelsdemo.composemultiplatformmediaplayer.generated.resources.resize_fit

//@Composable
//internal fun DesktopControlPanel(
//    playerHost: MediaPlayerHost,
//    playerConfig: VideoPlayerConfig,
//    volume: Float,
//    volumeCallback: (Float) -> Unit,
//    saveVolumeCallback: (Float) -> Unit,
//    activeOption: PlayerOption,
//    activeOptionCallBack: ((PlayerOption) -> Unit)
//) {
//    var changeV by remember { mutableStateOf(1f) }
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 10.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(10.dp)
//    ) {
//        // Fast Backward Icon
//        if (playerConfig.isFastForwardBackwardEnabled && !playerConfig.isLiveStream) {
//            AnimatedClickableIcon(
//                painterRes = playerConfig.fastBackwardIconResource,
//                imageVector = Icons.Filled.FastRewind,
//                contentDescription = "Fast Backward",
//                tint = playerConfig.iconsTintColor,
//                iconSize = playerConfig.fastForwardBackwardIconSize,
//                animationDuration = playerConfig.controlClickAnimationDuration,
//                onClick = {
//                    playerHost.isSliding = true
//                    val newTime =
//                        maxOf(0f, playerHost.currentTime - playerConfig.fastForwardBackwardIntervalSeconds)
//                    playerHost.seekToTime = newTime.toFloat()
//                    playerHost.isSliding = false
//                }
//            )
//        }
//
//        // Play/Pause Icon
//        if (playerConfig.isPauseResumeEnabled) {
//            AnimatedClickableIcon(
//                painterRes = if (playerHost.isPaused) playerConfig.playIconResource else playerConfig.pauseIconResource,
//                imageVector = if (playerHost.isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
//                contentDescription = "Play/Pause",
//                tint = playerConfig.iconsTintColor,
//                iconSize = playerConfig.pauseResumeIconSize,
//                animationDuration = playerConfig.controlClickAnimationDuration,
//                onClick = {
//                    playerHost.togglePlayPause()
//                }
//            )
//        }
//
//        // Fast Forward Icon
//        if (playerConfig.isFastForwardBackwardEnabled && !playerConfig.isLiveStream) {
//            AnimatedClickableIcon(
//                painterRes = playerConfig.fastForwardIconResource,
//                imageVector = Icons.Filled.FastForward,
//                contentDescription = "Fast Forward",
//                tint = playerConfig.iconsTintColor,
//                iconSize = playerConfig.fastForwardBackwardIconSize,
//                animationDuration = playerConfig.controlClickAnimationDuration,
//                onClick = {
//                    playerHost.isSliding = true
//                    val newTime = minOf(  playerHost.totalTime.toFloat(),
//                        playerHost.currentTime + playerConfig.fastForwardBackwardIntervalSeconds
//                    )
//                    playerHost.seekToTime = newTime.toFloat()
//                    playerHost.isSliding = false
//                }
//            )
//        }
//
//        Spacer(modifier = Modifier.weight(1f))
//        if (playerHost.audioTrackOptions.isNotEmpty() && playerConfig.showAudioTracksOptions) {
//            AnimatedClickableIcon(
//                imageVector = Icons.Default.Audiotrack,
//                contentDescription = "Audio",
//                tint = playerConfig.iconsTintColor,
//                iconSize = playerConfig.topControlSize,
//                animationDuration = playerConfig.controlClickAnimationDuration,
//                onClick = {
//                    activeOptionCallBack(if (activeOption == PlayerOption.NONE) PlayerOption.AUDIO_TRACK else PlayerOption.NONE)
//                } // Toggle Lock on click
//            )
//        }
//        if (playerHost.qualityOptions.isNotEmpty() && playerConfig.showVideoQualityOptions) {
//            AnimatedClickableIcon(
//                imageVector = Icons.Default.HighQuality,
//                contentDescription = "Quality",
//                tint = playerConfig.iconsTintColor,
//                iconSize = playerConfig.topControlSize,
//                animationDuration = playerConfig.controlClickAnimationDuration,
//                onClick = {
//                    activeOptionCallBack(if (activeOption == PlayerOption.NONE) PlayerOption.QUALITY else PlayerOption.NONE)
//                } // Toggle Lock on click
//            )
//        }
//
//        // Speed Control Icon
//        if (playerConfig.isSpeedControlEnabled && !playerConfig.isLiveStream) {
//            AnimatedClickableIcon(
//                painterRes = playerConfig.speedIconResource,
//                imageVector = Icons.Default.Speed,
//                contentDescription = "Speed",
//                tint = playerConfig.iconsTintColor,
//                iconSize = playerConfig.topControlSize,
//                animationDuration = playerConfig.controlClickAnimationDuration,
//                onClick = {
//                    activeOptionCallBack(if (activeOption == PlayerOption.NONE) PlayerOption.SPEED else PlayerOption.NONE)
//                } // Toggle Speed on click
//            )
//        }
//
//        // Mute/Unmute Icon and Volume Slider
//        if (playerConfig.isMuteControlEnabled) {
//            AnimatedClickableIcon(
//                painterRes = if (volume == 0f) playerConfig.unMuteIconResource else playerConfig.muteIconResource,
//                imageVector = if (volume == 0f) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
//                contentDescription = "Mute/Unmute",
//                tint = playerConfig.iconsTintColor,
//                iconSize = playerConfig.topControlSize,
//                animationDuration = playerConfig.controlClickAnimationDuration,
//                onClick = {
//                    if (volume > 0) playerHost.mute() else playerHost.unmute()
//                }
//            )
//            Slider(
//                modifier = Modifier.width(100.dp).height(25.dp),
//                value = volume, // Current value of the slider
//                onValueChange = {
//                    volumeCallback(it)
//                    changeV = it
//                },
//                valueRange = 0f..1f, // Range of the slider
//                onValueChangeFinished = { saveVolumeCallback(changeV) },
//                colors = SliderDefaults.colors(
//                    thumbColor = Color(0xFFFFA500),
//                    inactiveTrackColor = Color(0xFFD3D3D3),
//                    activeTrackColor = Color(0xFF008080)
//                )
//            )
//        }
//
//        if (playerConfig.isScreenResizeEnabled) {
//            AnimatedClickableIcon(
//                painterRes = when (playerHost.videoFitMode) {
//                    ScreenResize.FIT -> ComposeResourceDrawable(Res.drawable.resize_fit)
//                    ScreenResize.FILL -> ComposeResourceDrawable(Res.drawable.resize_fill)
//                },
//                contentDescription = "Resize",
//                tint = playerConfig.iconsTintColor,
//                iconSize = playerConfig.topControlSize,
//                colorFilter = ColorFilter.tint(playerConfig.iconsTintColor),
//                animationDuration = playerConfig.controlClickAnimationDuration,
//                onClick = {
//                    playerHost.setVideoFitMode(
//                        when (playerHost.videoFitMode) {
//                            ScreenResize.FIT -> ScreenResize.FILL
//                            ScreenResize.FILL -> ScreenResize.FIT
//                        })
//                }
//            )
//        }
//
//        if (playerConfig.isFullScreenEnabled) {
//            AnimatedClickableIcon(
//                imageVector = if (playerHost.isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
//                contentDescription = "FullScreen",
//                tint = playerConfig.iconsTintColor,
//                iconSize = playerConfig.topControlSize,
//                animationDuration = playerConfig.controlClickAnimationDuration,
//                onClick = { playerHost.toggleFullScreen() } // Toggle mute/unMute on click
//            )
//        }
//    }
//}