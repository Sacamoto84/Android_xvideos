package com.client.xvideos.l.ui.screens

//val LocalRootLScreenModel = staticCompositionLocalOf<ScreenLRootSM> { error("No ScreenLRootSM provided") }
//
//// Глобальная ссылка на основной навигатор для доступа из любого места
//val LocalMainNavigator = staticCompositionLocalOf<Navigator?> { null }
//
//class ScreenLRoot() : Screen {
//
//    override val key: ScreenKey = uniqueScreenKey
//
//    @OptIn(ExperimentalZoomableApi::class)
//    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//    @Composable
//    override fun Content() {
//
//        val haptic = LocalHapticFeedback.current
//
//        val vm: ScreenLRootSM = getScreenModel()
//
//        val snackbarHostState = remember { SnackbarHostState() }
//
//        val snackBarEvent = vm.hostDI.snackBarEvent
//
//        val navigator = LocalNavigator.currentOrThrow
//
//        // Создаем отдельный навигатор для внутренней навигации
//        var mainNavigator: Navigator? = null
//
//        LaunchedEffect(Unit) {
//            vm.snackbarEvents.collect { message ->
//                snackbarHostState.showSnackbar(
//                    message
//                )
//            }
//        }
//
//        LaunchedEffect(Unit) {
//            snackBarEvent.messages.receiveAsFlow().collect { message ->
//                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
//                snackbarHostState.show(message)
//            }
//        }
//
//        val queueState by DownloadQueueManager.queueState.collectAsState()
//
//        CompositionLocalProvider(
//            LocalRootLScreenModel provides vm,
//            LocalMainNavigator provides mainNavigator
//        ) {
//            Scaffold(
//                floatingActionButtonPosition = FabPosition.Start,
//
//                floatingActionButton = {
//
//                    // Отслеживаем изменения в навигаторе для обновления Badge
//                    var navigationDepth by remember { mutableIntStateOf(0) }
//
//                    LaunchedEffect(mainNavigator) {
//                        mainNavigator?.let { nav ->
//                            // Подписываемся на изменения в стеке навигации
//                            snapshotFlow { nav.items.size }
//                                .collect { stackSize ->
//                                    // Глубина = размер стека - 1 (так как ScreenLExplorer = 0)
//                                    navigationDepth = if (stackSize > 1) stackSize - 1 else 0
//                                }
//                        }
//                    }
//
//
//
//                    if (depth > 0) {
//                        Box {
//                            SmallFloatingActionButton(
//                                onClick = {
//                                    // Возврат к домашнему экрану через основной навигатор
//                                    mainNavigator?.let { nav ->
//                                        // Проверяем, не находимся ли мы уже на домашнем экране
//                                        if (nav.lastItem !is ScreenLExplorer) {
//                                            // Очищаем весь стек и переходим к домашнему экрану
//                                            nav.replaceAll(ScreenLExplorer())
//                                        }
//                                    }
//                                },
//                                content = {
//                                    Icon(
//                                        imageVector = Icons.Default.Home,
//                                        contentDescription = "Home"
//                                    )
//                                }
//                            )
//
//                            // Badge показываем только если есть глубина навигации
//                            if (navigationDepth > 0) {
//                                Badge(
//                                    modifier = Modifier
//                                        .align(Alignment.TopEnd)
//                                        .offset(x = 4.dp, y = (-4).dp),
//                                    containerColor = Color.Red,
//                                    contentColor = Color.White
//                                ) {
//                                    Text(
//                                        text = navigationDepth.toString(),
//                                        fontSize = 10.sp,
//                                        fontWeight = FontWeight.Bold
//                                    )
//                                }
//                            }
//                        }
//                    }
//                },
//                containerColor = ThemeL.greyBackground,
//                snackbarHost = {
//                    Box(modifier = Modifier.zIndex(Float.MAX_VALUE)) {
//                        SnackbarHost(snackbarHostState) { data ->
//                            val uiMsg = (data.visuals as? UiSnackbarVisuals)?.ui ?: UiMessage.Info(
//                                data.visuals.message
//                            )
//                            val (bg, fg, icon) = when (uiMsg) {
//                                is UiMessage.Success -> Triple(
//                                    Color(0xFF0F9960),
//                                    Color.White,
//                                    Icons.Default.Check
//                                )
//
//                                is UiMessage.Error -> Triple(
//                                    Color(0xFFD13913),
//                                    Color.White,
//                                    Icons.Default.ErrorOutline
//                                )
//
//                                is UiMessage.Info -> Triple(
//                                    Color(0xFF137CBD),
//                                    Color.White,
//                                    Icons.Default.Info
//                                )
//                            }
//                            LaunchedEffect(data) {
//                                when (uiMsg) {
//                                    is UiMessage.Success -> {
//                                        delay(2000); data.dismiss()
//                                    }
//
//                                    is UiMessage.Error -> {
//                                        delay(5000); data.dismiss()
//                                    }
//
//                                    is UiMessage.Info -> {
//                                        delay(2000); data.dismiss()
//                                    }
//                                }
//                            }
//                            Surface(
//                                modifier = Modifier
//                                    .zIndex(Float.MAX_VALUE)
//                                    .wrapContentWidth()
//                                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
//                                    .zIndex(Float.MAX_VALUE),
//                                color = bg,
//                                contentColor = fg,
//                                shape = RoundedCornerShape(12.dp),
//                                tonalElevation = 6.dp,
//                                shadowElevation = 6.dp,
//                            ) {
//                                Row(
//                                    Modifier
//                                        .zIndex(Float.MAX_VALUE)
//                                        .padding(horizontal = 12.dp, vertical = 12.dp)
//                                        .zIndex(Float.MAX_VALUE),
//                                    verticalAlignment = Alignment.CenterVertically
//                                )
//                                {
//                                    Icon(icon, contentDescription = null)
//                                    Spacer(Modifier.width(8.dp))
//                                    Text(
//                                        data.visuals.message,
//                                        Modifier,
//                                        fontFamily = ThemeRed.fontFamilyDMsanss
//                                    )
//                                    data.visuals.actionLabel?.let { label ->
//                                        TextButton(onClick = { data.performAction() }) { Text(label) }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            ) { paddingValues ->
//
//                // Основной навигатор приложения
//                Navigator(screen = ScreenLExplorer()) { nav ->
//                    mainNavigator = nav // Сохраняем ссылку на навигатор
//                    nav.lastItem.Content()
//                }
//
//                QueueStatisticsCardLite(queueState = queueState)
//
//                // Оверлей рисуется поверх Scaffold
//                vm.overlayContent.value?.let { content ->
//                    Box(
//                        modifier = Modifier
//                            //.zIndex(10f)
//                            .fillMaxSize()
//                            .background(Color.Black.copy(alpha = 0.95f))
//                    ) { content() }
//                }
//
//
//            }
//
//            AppNetworkSpeedMonitorLite()
//        }
//    }
//
//}
//
////Глубина погружения навигации
//var depth by mutableIntStateOf(0)
//
//class ScreenLRootSM @Inject constructor(
//    val hostDI: HostDI
//) : ScreenModel {
//    private val _snackbarEvents = Channel<String>(64)
//    val snackbarEvents = _snackbarEvents.receiveAsFlow()
//
//    @OptIn(DelicateCoroutinesApi::class)
//    fun showSnackbar(message: String) {
//        GlobalScope.launch { _snackbarEvents.send(message) }
//    }
//
//
//    // состояние для фуллскрин-оверлея
//    private val _overlayContent = mutableStateOf<(@Composable () -> Unit)?>(null)
//    val overlayContent: State<(@Composable () -> Unit)?> = _overlayContent
//    fun showOverlay(content: @Composable () -> Unit) {
//        _overlayContent.value = content
//    }
//
//    fun hideOverlay() {
//        _overlayContent.value = null
//    }
//
//
//}
//
//// Расширение для удобного доступа к домашней навигации из любого экрана
//@Composable
//fun navigateToHome() {
//    val mainNavigator = LocalMainNavigator.current
//    mainNavigator?.let { nav ->
//        if (nav.lastItem !is ScreenLExplorer) {
//            nav.replaceAll(ScreenLExplorer())
//        }
//    }
//}
//
//@Module
//@InstallIn(SingletonComponent::class)
//abstract class ScreenModuleLRootBlock {
//    @Binds
//    @IntoMap
//    @ScreenModelKey(ScreenLRootSM::class)
//    abstract fun bindScreenLRootScreenModel(hiltListScreenModel: ScreenLRootSM): ScreenModel
//}


enum class SelectIndex(val value: Int) {
    Unselect(-1),
    Default(0),
    Manga(1),
    Hentai(2),
    Porn(3)
}

