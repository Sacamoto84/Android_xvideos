package com.client.xvideos.redgifs.ui.explorer.tab.saved.tab

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.common.coil.UrlImage
import com.client.xvideos.common.connectivityObserver.ConnectivityObserver
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.common.UsersRed
import com.client.xvideos.redgifs.common.di.HostDI
import com.client.xvideos.redgifs.common.saved.SelectedCreator
import com.client.xvideos.redgifs.model.UserInfo
import com.client.xvideos.redgifs.ui.profile.ScreenRedProfile
import com.client.xvideos.redgifs.ui.profile.atom.VerticalScrollbar
import com.client.xvideos.redgifs.ui.profile.rememberVisibleRangePercentIgnoringFirstNForGrid
import com.client.xvideos.redgifs.ui.ui.lazyrow123.LazyRow123
import com.client.xvideos.redgifs.ui.ui.lazyrow123.LazyRow123Host
import com.client.xvideos.redgifs.ui.ui.lazyrow123.model.TypePager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

object R_Screen_Saved_SubscriptionsTab : Screen {

    private fun readResolve(): Any = R_Screen_Saved_SubscriptionsTab

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: ScreenSavedSubscriptionsSM = getScreenModel()

        val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForGrid(
            gridState = vm.likedHost.state,
            itemsToIgnore = 0,
            numberOfColumns = 3
        )

        val pager = vm.likedHost.pager.collectAsLazyPagingItems()

        var selectCreatorName by remember { mutableStateOf<String?>(null) }

        var userToDelete by remember { mutableStateOf<SelectedCreator?>(null) }

        // Используем SnapshotStateList напрямую для реактивности UI
        val selectedListCreator = vm.hostDI.savedRed.subscriptions.selectedListCreator

        // Обработка нажатия: переключаем флаг и обновляем пейджер
        if (selectCreatorName != null) {
            val index = selectedListCreator.indexOfFirst { it.name == selectCreatorName }
            if (index != -1) {
                val item = selectedListCreator[index]
                // Обновляем элемент в SnapshotStateList для триггера Compose
                selectedListCreator[index] = item.copy(select = !item.select)
                // Сбрасываем имя, чтобы не зациклиться
                selectCreatorName = null
                // Обновляем данные из сети
                pager.refresh()
            }
        }

        SubscriptionsTabContent(
            host = vm.likedHost,
            scrollPercent = scrollPercent,
            listCreatorSelectedCreator = selectedListCreator,
            onOpenProfile = { navigator.push(ScreenRedProfile(it)) },
            onSelectCreator = { selectCreatorName = it },
            onLongClick = { name ->
                userToDelete = selectedListCreator.toList().firstOrNull { it.name == name }
            }
        )

        DialogSubscriptionDelete(
            user = { userToDelete },
            onDismiss = { userToDelete = null },
            onConfirm = {
                vm.hostDI.savedRed.subscriptions.remove(it)
                userToDelete = null
                pager.refresh()
            }
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SubscriptionsTabContent(
    host: LazyRow123Host?,
    scrollPercent: Pair<Float, Float>,
    listCreatorSelectedCreator: List<SelectedCreator>,
    onOpenProfile: (String) -> Unit,
    onSelectCreator: (String) -> Unit,
    onLongClick : (String) -> Unit = {}
) {

    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()
    val pager = host?.pager?.collectAsLazyPagingItems()


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF303030)
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {


            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    scope.launch {
                        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        isRefreshing = true
                        delay(500)
                        isRefreshing = false
                    }
                    pager?.refresh()
                },
                state = pullToRefreshState,
                indicator = {
                    Indicator(
                        modifier = Modifier.align(Alignment.TopCenter),
                        isRefreshing = isRefreshing,
                        containerColor = Color.White,
                        color = Color.Black,
                        state = pullToRefreshState
                    )
                },
            ) {


                if (host != null) {
                    LazyRow123(
                        host = host,
                        modifier = Modifier.fillMaxSize(),
                        onClickOpenProfile = onOpenProfile,
                        contentPadding = PaddingValues(0.dp),
                        contentBeforeList = {
                            CreatorsHeader(
                                listCreators = listCreatorSelectedCreator,
                                onCreatorClick = onSelectCreator,
                                onLongClick = onLongClick
                            )
                        },
                        isRunLike = true
                    )
                } else {
                    // Fallback for Preview
                    CreatorsHeader( listCreators = listCreatorSelectedCreator, onCreatorClick = onSelectCreator, onLongClick = onLongClick )
                }

                //---- Скролл ----
                Box( modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd).width(2.dp) ) { VerticalScrollbar(scrollPercent) }
            }
        }
    }
}

@Composable
fun CreatorsHeader(
    listCreators: List<SelectedCreator>,
    onCreatorClick: (String) -> Unit,
    onLongClick : (String) -> Unit = {}
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        listCreators.forEach { creator ->
            CreatorChip(
                creator = creator.name,
                url = creator.urlProfile,
                isSelected = creator.select,
                onClick = { onCreatorClick(creator.name) },
                onLongClick = { onLongClick(creator.name) }
            )
        }
    }
}

@Composable
fun CreatorChip(
    creator: String,
    url: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(50))
            .border(1.dp, Color.Gray, RoundedCornerShape(50))
            .background(
                if (isSelected) Color.Gray else Color.Transparent,
                RoundedCornerShape(50)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                indication = null,
                interactionSource = null,
            )
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {

            if (url != null) {
                UrlImage(url = url)
            } else {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(Modifier.width(8.dp))
        Text(
            text = creator,
            fontSize = 16.sp,
            color = Color.White,
            fontFamily = ThemeRed.fontFamilyPopinsRegular
        )
        Spacer(Modifier.width(4.dp))
    }
}

@Preview
@Composable
fun SubscriptionsTabPreview() {
    SubscriptionsTabContent(
        host = null,
        scrollPercent = 0f to 0.2f,
        listCreatorSelectedCreator = listOf(
            SelectedCreator("Creator 1", true, null),
            SelectedCreator("Another One", false, null),
            SelectedCreator("Superstar", true, null)
        ),
        onOpenProfile = {},
        onSelectCreator = {}
    )
}

class ScreenSavedSubscriptionsSM @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    val hostDI: HostDI
) : ScreenModel {

    val likedHost = LazyRow123Host(
        connectivityObserver = connectivityObserver,
        scope = screenModelScope,
        typePager = TypePager.SUBSCRIPTIONS,
        hostDI = hostDI
    )
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedSavedSubscriptions {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenSavedSubscriptionsSM::class)
    abstract fun bindScreenRedSavedSubscriptionsScreenModel(hiltListScreenModel: ScreenSavedSubscriptionsSM): ScreenModel
}
