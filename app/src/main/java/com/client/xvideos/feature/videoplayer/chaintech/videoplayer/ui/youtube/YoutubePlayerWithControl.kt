package chaintech.videoplayer.ui.youtube

//@Composable
//internal fun YoutubePlayerWithControl(
//    modifier: Modifier,
//    playerHost: MediaPlayerHost,
//    playerConfig: VideoPlayerConfig
//) {
//    val coroutineScope = rememberCoroutineScope()
//    val hostState = remember { VideoPlayerHost() }
//
//    var isScreenLocked by remember { mutableStateOf(false) }
//    var isInitializing by remember { mutableStateOf(true) }
//    var pause by remember { mutableStateOf(playerHost.isPaused) }
//    var isVideoStarted by remember { mutableStateOf(false) }
//    var isCooldownActive by remember { mutableStateOf(false) }
//    var showControls by remember { mutableStateOf(playerConfig.showControls) } // State for showing/hiding controls
//    var showVolumeControl by remember { mutableStateOf(false) }
//    var volumeDragAmount by remember { mutableStateOf(0f) }
//    var activeOption by remember { mutableStateOf(PlayerOption.NONE) }
//
//    val timeSource = remember { TimeSource.Monotonic }
//    var lastInteractionMark by remember { mutableStateOf(timeSource.markNow()) }
//    val handleControlInteraction = {
//        if (playerConfig.showControls) {
//            lastInteractionMark = timeSource.markNow()  // Reset the interaction timer
//            showControls = true
//        }
//    }
//
//    // Auto-hide controls if enabled
//    if (playerConfig.showControls && playerConfig.isAutoHideControlEnabled) {
//        LaunchedEffect(showControls, lastInteractionMark) {
//            if (showControls) {
//                val timeoutDuration = playerConfig.controlHideIntervalSeconds.seconds
//                delay(timeoutDuration.inWholeMilliseconds) // Delay hiding controls
//                if (!playerHost.isSliding && lastInteractionMark.elapsedNow() >= timeoutDuration) {
//                    showControls = false // Hide controls if seek bar is not being slid
//                }
//            }
//        }
//    }
//
//    pause = playerHost.isPaused
//    playerConfig.isScreenResizeEnabled = false
//
//    // Observe app background state
//    val appBackgroundObserver = rememberAppBackgroundObserver()
//    LaunchedEffect(Unit) {
//        appBackgroundObserver.observe { enterBackground ->
//            if (enterBackground && !pause) {
//                playerHost.togglePlayPause()
//            }
//        }
//    }
//    DisposableEffect(Unit) {
//        onDispose { appBackgroundObserver.removeObserver() }
//    }
//
//    // React to URL changes
//    LaunchedEffect(playerHost.url) {
//        isVideoStarted = false
//        val videoId = extractYouTubeVideoId(playerHost.url) ?: playerHost.url
//        coroutineScope.launch { hostState.load(videoId) }
//    }
//// Update player callbacks
//    LaunchedEffect(hostState.isBuffering) {
//        playerHost.setBufferingStatus(hostState.isBuffering)
//    }
//
//    LaunchedEffect(playerHost.seekToTime) {
//        playerHost.seekToTime?.let {
//            coroutineScope.launch {
//                hostState.seekTo(it.toInt().seconds)
//                playerHost.seekToTime = null
//            }
//        }
//    }
//    fun handleEndVideo(status: PlayerEvent.State.VideoState) {
//        if (status == PlayerEvent.State.VideoState.ENDED) {
//            if (isCooldownActive) return
//
//            isCooldownActive = true
//            playerHost.triggerMediaEnd()
//
//            coroutineScope.launch {
//                if (playerHost.isLooping) {
//                    hostState.seekTo(0.seconds)
//                    hostState.play()
//                } else {
//                    hostState.seekTo(0.seconds)
//                    if (!playerHost.isPaused) {
//                        playerHost.togglePlayPause()
//                    }
//                }
//
//                delay(1000)
//                isCooldownActive = false
//            }
//        }
//    }
//
//    fun handleStartTime() {
//        getSeekTime(playerHost, playerConfig)?.let {
//            coroutineScope.launch {
//                hostState.seekTo(it.toInt().seconds)
//            }
//        }
//    }
//
//    var previousUrl by remember { mutableStateOf(playerHost.url) }
//    DisposableEffect(playerHost.url) {
//        onDispose {
//            if(playerConfig.enableResumePlayback) {
//                saveCurrentPosition(playerHost, previousUrl)
//                previousUrl = playerHost.url
//            }
//        }
//    }
//
//    val volumeDragModifier = Modifier.pointerInput(Unit) {
//        detectVerticalDragGestures(
//            onDragStart = {
//                showVolumeControl = true
//                volumeDragAmount = 0f
//            },
//            onVerticalDrag = { _, dragAmount ->
//                volumeDragAmount += dragAmount
//                val volumeChange = volumeDragAmount / 4000f // Adjust sensitivity
//                playerHost.setVolume((playerHost.volumeLevel - volumeChange).coerceIn(0f, 1f))
//                if (playerHost.volumeLevel > 0) playerHost.unmute() else playerHost.mute()
//            },
//            onDragEnd = {
//                showVolumeControl = false // Hide immediately when finger is lifted
//            }
//        )
//    }
//
//    fun setSpeed() {
//        coroutineScope.launch {
//            if (hostState.playerState is VideoState.Playing) {
//                hostState.setSpeed(
//                    when (playerHost.speed) {
//                        PlayerSpeed.X0_5 -> 0.5f
//                        PlayerSpeed.X1 -> 1f
//                        PlayerSpeed.X1_5 -> 1.5f
//                        PlayerSpeed.X2 -> 2f
//                    }
//                )
//            }
//        }
//    }
//
//    when (val state = hostState.playerState) {
//        is VideoState.Failed -> isInitializing = false
//
//        VideoState.Idle -> isInitializing = true
//
//        is VideoState.Playing -> {
//            playerHost.updateTotalTime(state.totalDuration.inWholeSeconds.toInt())
//            if (!playerHost.isSliding) {
//                playerHost.updateCurrentTime(state.currentTime.inWholeSeconds.toInt())
//            }
//            handleEndVideo(state.playbackStatus)
//            handleStartTime()
//            if (!isVideoStarted) {
//                isVideoStarted = true
//                coroutineScope.launch {
//                    if (playerHost.isMuted) { hostState.mute() } else { hostState.unmute() }
//                    if (playerHost.isPaused) { hostState.pause()  } else { hostState.play() }
//                }
//                setSpeed()
//            }
//            isInitializing = false
//        }
//
//        VideoState.Initialized -> coroutineScope.launch {
//            val videoId = extractYouTubeVideoId(playerHost.url) ?: playerHost.url
//            hostState.load(videoId)
//        }
//    }
//
//    // React to state changes
//    LaunchedEffect(playerHost.isPaused) {
//        coroutineScope.launch {
//            if (hostState.playerState is VideoState.Playing) {
//                if (playerHost.isPaused) {
//                    hostState.pause()
//                } else {
//                    hostState.play()
//                }
//            }
//        }
//    }
//    LaunchedEffect(playerHost.isMuted) {
//        coroutineScope.launch {
//            if (hostState.playerState is VideoState.Playing) {
//                if (playerHost.isMuted) {
//                    hostState.mute()
//                } else {
//                    hostState.unmute()
//                }
//            }
//        }
//    }
//    LaunchedEffect(playerHost.speed) {
//        setSpeed()
//    }
//
//    val zoomState = rememberZoomState(maxScale = 3f)
//    LaunchedEffect(playerHost.videoFitMode) {
//        zoomState.reset()
//    }
//
//    // Container for the video player and control components
//    Box(
//        modifier = modifier
//            .clipToBounds() // Ensures the zoomed content stays within the bo
//    ) {
//        Box(
//            modifier = modifier
//                .zoomable(
//                    zoomState = zoomState,
//                    zoomEnabled = (!isScreenLocked && playerConfig.isZoomEnabled),
//                    enableOneFingerZoom = false,
//                    onTap = {
//                        if(playerConfig.showControls) {
//                            showControls = !showControls // Toggle show/hide controls on tap
//                            activeOption = PlayerOption.NONE
//                        }
//                    }
//                )
//        ){
//            // Youtube player component
//            EmbeddedPlayer(
//                modifier = modifier,
//                host = hostState,
//            )
//
//            playerConfig.watermarkConfig?.let {
//                MovingWatermark(
//                    config = it,
//                    modifier = Modifier.matchParentSize()
//                )
//            }
//
//            Box(
//                modifier = Modifier
//                    .matchParentSize() // Covers the WebView entirely
//                    .background(Color.Transparent) // Invisible but blocks touch
//                    .pointerInput(Unit) {} // Consumes all touch events, blocking WebView
//            )
//
//            if (!isScreenLocked && playerConfig.isGestureVolumeControlEnabled) {
//                // Detect right-side drag gestures
//                Box(
//                    modifier = Modifier
//                        .fillMaxHeight()
//                        .fillMaxWidth(0.3f) // Occupy 30% of the right side dynamically
//                        .align(Alignment.CenterEnd)
//                        .then(volumeDragModifier) // Apply drag gesture detection only on the right side
//                )
//            }
//
//            if (isInitializing) {
//                Box(
//                    modifier = Modifier
//                        .matchParentSize()
//                        .background(Color.Black)
//                        .wrapContentSize(align = Alignment.Center)
//                ) {
//                    CircularProgressIndicator(
//                        modifier = Modifier
//                            .size(80.dp)
//                            .scale(1.5f)
//                            .wrapContentSize(align = Alignment.Center), // Center the progress indicator
//                        color = youtubeProgressColor
//                    )
//                }
//            }
//        }
//
//        FullControlComposable(
//            playerHost = playerHost,
//            playerConfig = playerConfig,
//            showControls = showControls,
//            isScreenLocked = isScreenLocked,
//            showVolumeControl = showVolumeControl,
//            activeOption = activeOption,
//            activeOptionCallBack = { activeOption = it },
//            onBackwardToggle = {
//                coroutineScope.launch {
//                    hostState.seekTo(
//                        maxOf(
//                            0,
//                            playerHost.currentTime - playerConfig.fastForwardBackwardIntervalSeconds
//                        ).seconds
//                    )
//                }
//            },
//            onForwardToggle = {
//                coroutineScope.launch {
//                    hostState.seekTo(
//                        minOf(
//                            playerHost.totalTime,
//                            playerHost.currentTime + playerConfig.fastForwardBackwardIntervalSeconds
//                        ).seconds
//                    )
//                }
//            },
//            onChangeSliderTime = {
//                it?.let {
//                    coroutineScope.launch {
//                        hostState.seekTo(it.seconds)
//                    }
//                }
//            },
//            onLockScreenToggle = { isScreenLocked = it },
//            userInteractionCallback = { handleControlInteraction() }
//        )
//    }
//}