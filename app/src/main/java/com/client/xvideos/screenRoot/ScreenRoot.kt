package com.client.xvideos.screenRoot

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.Navigator
import com.client.xvideos.MenuScreen
import com.client.xvideos.common.eventBus.Event
import com.client.xvideos.common.eventBus.EventBus
import com.client.xvideos.common.snackbar.show
import com.client.xvideos.common.traficStatistic.AppNetworkSpeedMonitorLite
import com.client.xvideos.common.collectionDB.ui.DaialogNewCollection
import com.client.xvideos.common.coil.UrlImage
import com.client.xvideos.l.featured.saved.SavedL
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.ui.screens.explorer.L_ScreenExplorer
import com.redgifs.common.downloader.ui.DownloadIndicator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.filterIsInstance
import net.engawapg.lib.zoomable.ExperimentalZoomableApi
import javax.inject.Inject

/**
 * Глобальный индикатор глубины навигации.
 *
 * Используется корневым экраном, чтобы понимать, нужно ли показывать кнопку
 * быстрого возврата домой. Значение меняют вложенные экраны, когда пользователь
 * уходит глубже стартового уровня.
 */
var depth by mutableIntStateOf(0)

/**
 * CompositionLocal для доступа к корневой ScreenModel из дочерних composable.
 *
 * Через неё экраны могут показать или скрыть общий overlay, не прокидывая
 * `ScreenRootSM` через длинную цепочку параметров.
 */
val LocalRootScreenModel = staticCompositionLocalOf<ScreenRootSM> { error("No ScreenRootSM provided") }

/**
 * CompositionLocal с главным Voyager-навигатором.
 *
 * Нужен дочерним экранам, которым требуется управлять корневым стеком навигации:
 * заменить текущий раздел, вернуться домой или проверить текущий экран.
 */
val LocalMainNavigator = staticCompositionLocalOf<Navigator?> { null }

/**
 * Корневой экран приложения.
 *
 * Собирает общий каркас UI: Voyager navigation stack, snackbar host,
 * индикатор загрузок L-раздела, кнопку перехода домой, overlay-слой
 * и мини-монитор скорости сети.
 */
object ScreenRoot : Screen {

    private fun readResolve(): Any = ScreenRoot

    override val key: ScreenKey = "ScreenRoot"

    /**
     * Строит корневой Compose UI и связывает глобальные обработчики событий.
     *
     * Здесь создаётся `Navigator`, подписка на `Event.ShowSnackBar`,
     * публикация `CompositionLocal` и вывод overlay-контента поверх текущего
     * экрана без разрушения навигационного стека.
     */
    @OptIn(ExperimentalZoomableApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val haptic = LocalHapticFeedback.current
        val vm: ScreenRootSM = getScreenModel()
        val lDownloadsVm: ScreenRootLDownloadsSM = getScreenModel()
        val savedL = lDownloadsVm.savedL
        val lPercentDownload = lDownloadsVm.savedL.likes.percentDownload.collectAsStateWithLifecycle().value
        val snackBarHostState = remember { SnackbarHostState() }

        var mainNavigator by remember { mutableStateOf<Navigator?>(null) }

        LaunchedEffect(Unit) {
            EventBus.events
                .filterIsInstance<Event.ShowSnackBar>()
                .collect { event ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    snackBarHostState.show(event.message)
                }
        }

        // Диалог создания новой коллекции
        if (savedL.collection.visibleDialogCreateNew) {
            DaialogNewCollection(
                visible = savedL.collection.visibleDialogCreateNew,
                onDismiss = { savedL.collection.visibleDialogCreateNew = false },
                onBlockConfirmed = { collection ->
                    if (collection.isNotEmpty()) {
                        savedL.collection.createCollection(collection)
                        savedL.collection.visibleDialogCreateNew = false
                    }
                }
            )
        }

        // Диалог добавления в коллекцию
        if (savedL.collection.visibleDialog) {
            L_DialogCollection(savedL = { savedL })
        }

        CompositionLocalProvider(
            LocalRootScreenModel provides vm,
            LocalMainNavigator provides mainNavigator
        ) {
            Scaffold(
                floatingActionButtonPosition = FabPosition.Start,
                floatingActionButton = {
                    HomeFloatingActionButton(mainNavigator)
                },
                containerColor = ThemeL.greyBackground,
                bottomBar = {
                    DownloadIndicator(lPercentDownload)
                },
                snackbarHost = {
                    RootSnackbarHost(snackBarHostState)
                }
            ) { paddingValues ->
                Navigator(screen = MenuScreen) { nav ->
                    mainNavigator = nav
                    nav.lastItem.Content()
                }

                vm.overlayContent.value?.let { content ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        content()
                    }
                }
            }
            AppNetworkSpeedMonitorLite()
        }
    }
}

/**
 * Плавающая кнопка возврата к L-разделу.
 *
 * Следит за размером основного navigation stack через `snapshotFlow` и показывает
 * badge с глубиной, если пользователь ушёл дальше первого экрана.
 */
@Composable
private fun HomeFloatingActionButton(mainNavigator: Navigator?) {
    var navigationDepth by remember { mutableIntStateOf(0) }

    LaunchedEffect(mainNavigator) {
        mainNavigator?.let { nav ->
            snapshotFlow { nav.items.size }
                .collect { stackSize ->
                    navigationDepth = if (stackSize > 1) stackSize - 1 else 0
                }
        }
    }

    if (depth > 0) {
        Box {
            SmallFloatingActionButton(
                onClick = {
                    mainNavigator?.let { nav ->
                        if (nav.lastItem !is L_ScreenExplorer) {
                            nav.replaceAll(L_ScreenExplorer())
                        }
                    }
                },
                content = {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "Home")
                }
            )

            if (navigationDepth > 0) {
                Badge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp),
                    containerColor = Color.Red,
                    contentColor = Color.White
                ) {
                    Text(
                        text = navigationDepth.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * ScreenModel корневого экрана.
 *
 * Хранит общий overlay как composable-лямбду. Это позволяет временно показать
 * поверх всего приложения диалог, полноэкранный слой или другой UI, не создавая
 * отдельный route в навигации.
 */
class ScreenRootSM @Inject constructor() : ScreenModel {
    private val _overlayContent = mutableStateOf<(@Composable () -> Unit)?>(null)
    val overlayContent: State<(@Composable () -> Unit)?> = _overlayContent

    /**
     * Показывает overlay поверх текущего экрана.
     *
     * Переданная composable-функция будет отрисована внутри полноэкранного `Box`
     * в `ScreenRoot.Content()`.
     */
    fun showOverlay(content: @Composable () -> Unit) {
        _overlayContent.value = content
    }

    /**
     * Убирает текущий overlay и возвращает пользователю обычный экран.
     */
    fun hideOverlay() {
        _overlayContent.value = null
    }
}

/**
 * ScreenModel для данных о загрузках L-раздела.
 *
 * Держит `SavedL`, чтобы корневой bottom bar мог наблюдать прогресс скачивания
 * лайков и показывать общий индикатор загрузки.
 */
class ScreenRootLDownloadsSM @Inject constructor(
    val savedL: SavedL
) : ScreenModel

/**
 * Hilt-модуль, регистрирующий корневые ScreenModel в multibinding Voyager.
 *
 * Благодаря этим биндингам `getScreenModel()` может создавать `ScreenRootSM`
 * и `ScreenRootLDownloadsSM` через Hilt.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenRootModule {
    /**
     * Привязывает `ScreenRootSM` к базовому типу Voyager `ScreenModel`.
     */
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRootSM::class)
    abstract fun bindScreenRootSM(sm: ScreenRootSM): ScreenModel

    /**
     * Привязывает `ScreenRootLDownloadsSM` к базовому типу Voyager `ScreenModel`.
     */
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRootLDownloadsSM::class)
    abstract fun bindScreenRootLDownloadsSM(sm: ScreenRootLDownloadsSM): ScreenModel
}

@Composable
private fun L_DialogCollection(savedL: () -> SavedL) {
    val haptic = LocalHapticFeedback.current

    Dialog(
        onDismissRequest = { savedL().collection.visibleDialog = false }
    ) {
        Column(
            modifier = Modifier
                .heightIn(min = 280.dp, max = 560.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF3F3F3F), RoundedCornerShape(12.dp))
                .background(Color(0xFF090909))
        ) {
            Text(
                text = "Добавить в коллекцию",
                style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color.White)
            )
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                LazyColumn(state = rememberLazyListState()) {
                    items(savedL().collection.collectionList.size) { index ->
                        val collectionName = savedL().collection.collectionList[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .padding(vertical = 4.dp)
                                .clickable(onClick = {
                                    val item = savedL().collection.collectionItemGifInfo as? PicsDetails
                                    if (item != null) {
                                        savedL().collection.add(item, collectionName)
                                        savedL().collection.visibleDialog = false
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                }),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(25))
                                    .size(72.dp)
                                    .background(Color.Gray)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                collectionName,
                                color = Color.White,
                                fontFamily = ThemeL.fontFamilyDMsanss
                            )
                        }
                    }
                }
            }

            com.composables.core.HorizontalSeparator(Color(0xFF363636))

            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFF232323))
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { savedL().collection.visibleDialog = false }) { Text("Отмена", color = Color.White) }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        savedL().collection.visibleDialog = false
                        savedL().collection.visibleDialogCreateNew = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = ThemeL.primaryColor)
                ) {
                    Text(text = "Создать", color = Color.Black)
                }
            }
        }
    }
}
