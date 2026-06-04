package com.client.xvideos.l.ui.screens.explorer

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Topic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.l.featured.saved.SavedL
import com.client.xvideos.screenRoot.LocalRootScreenModel
import com.client.xvideos.l.ui.screens.LLoginContent
import com.client.xvideos.l.ui.screens.explorer.tab.albumSearch.L_ScreenAlbumSearch
import com.client.xvideos.l.ui.screens.explorer.tab.albumTopHits.L_ScreenAlbumTopHits
import com.client.xvideos.l.ui.screens.explorer.tab.saved.L_SavedTab
import com.client.xvideos.l.ui.screens.screenAlbumList.L_ScreenAlbumList
import com.client.xvideos.r.common.ThemeRed
import com.client.xvideos.r.ui.explorer.tab.gifs.ColumnSelect_AddColumn
import com.client.xvideos.r.ui.explorer.top.TabRow
import com.client.xvideos.r.ui.ui.atom.TabBarPoints
import com.redgifs.common.downloader.ui.DownloadIndicator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import javax.inject.Inject

class L_ScreenExplorer : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val vm = getScreenModel<L_ScreenExplorerSM>()
        val savedL = vm.savedL
        val rootVm = LocalRootScreenModel.current

        LaunchedEffect(Unit) { rootVm.depthState.depth = 0 }

        val savedLogin = Settings.l_login.field.collectAsStateWithLifecycle().value
        val savedPassword = Settings.l_pass.field.collectAsStateWithLifecycle().value

        if (savedLogin.isBlank() || savedPassword.isBlank()) {
            LLoginContent(
                initialLogin = savedLogin,
                initialPassword = savedPassword,
                onSaved = {},
                onBack = { navigator.pop() }
            )
            return
        }

        val percentDownload = savedL.likes.percentDownload.collectAsStateWithLifecycle().value

        // ПЕРЕНЕСЕНО СЮДА: Теперь эти списки создаются внутри Composable
        val l = remember {
            persistentListOf(
                Icons.AutoMirrored.Outlined.FormatListBulleted,
                Icons.Outlined.BookmarkBorder,
                Icons.Outlined.Topic,
                Icons.Outlined.Search,
            )
        }

        val tags = remember {
            persistentListOf(
                "",
                "",
                "bBookMark",
                ""
            )
        }

        val columnR_ScreenGifsTab = Settings.l_gifsTab_column_current_count.field.collectAsStateWithLifecycle().value

        L_ScreenExplorerContent(
            percentDownload = percentDownload,
            titlesIcon = l,
            tags = tags,
            screenType = vm.screenType,
            columnCount = columnR_ScreenGifsTab,
            onChangeState = {
                if (it == vm.screenType) {
                    when (it) {
                        0 -> { ColumnSelect_AddColumn(Settings.l_gifsTab_column_current_count, Settings.l_gifsTab_G_0_4) }
                    }
                }
                vm.screenType = it
            },
        ) {
            //Navigator(
            when (vm.screenType) {
                0 -> L_ScreenAlbumList.Content()
                1 -> L_SavedTab.Content()
                2 -> L_ScreenAlbumTopHits.Content()
                3 -> L_ScreenAlbumSearch.Content()
                else -> L_SavedTab.Content()
            }
            //)
        }

    }
}

/**
 * Stateless-каркас экрана [L_ScreenExplorer]: нижняя панель (индикатор загрузки +
 * [TabRow]) и слот [content] под текущую вкладку. Вынесен отдельно, чтобы не зависеть
 * от Hilt / Voyager / [Settings] и быть пригодным для [Preview].
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun L_ScreenExplorerContent(
    percentDownload: Float,
    titlesIcon: ImmutableList<ImageVector>,
    tags: ImmutableList<String>,
    screenType: Int,
    columnCount: Int,
    onChangeState: (Int) -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(bottomBar = {
        Column {
            DownloadIndicator(percentDownload)
            TabRow(
                containerColor = ThemeRed.colorTabLevel0,
                titlesIcon = titlesIcon,
                value = screenType,
                onChangeState = onChangeState,
                overlay0 = { TabBarPoints(columnCount, screenType == 0) },
                tags = tags
            )
        }
    }, containerColor = ThemeRed.colorCommonBackground2) { paddingValues ->
        Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
            content()
        }
    }
}

@Preview
@Composable
private fun L_ScreenExplorerContentPreview() {
    L_ScreenExplorerContent(
        percentDownload = -2f,
        titlesIcon = persistentListOf(
            Icons.AutoMirrored.Outlined.FormatListBulleted,
            Icons.Outlined.BookmarkBorder,
            Icons.Outlined.Topic,
            Icons.Outlined.Search,
        ),
        tags = persistentListOf("", "", "bBookMark", ""),
        screenType = 0,
        columnCount = 2,
        onChangeState = {},
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ThemeRed.colorCommonBackground2),
            contentAlignment = Alignment.Center
        ) {
            Text("L_ScreenAlbumList", color = Color.White)
        }
    }
}

/**
 * ScreenModel L-раздела. Держит ссылку на singleton [SavedL], чтобы
 * корневой L-экран мог показывать общие диалоги (создание/добавление коллекции)
 * и индикатор загрузок без обращения к глобальному состоянию.
 */
class L_ScreenExplorerSM @Inject constructor(
    val savedL: SavedL,
    private val navigationState: LNavigationState
) : ScreenModel {
    /** Текущая вкладка верхнего уровня L-раздела (раньше — статика в Companion). */
    var screenType: Int
        get() = navigationState.rootTab
        set(value) {
            navigationState.rootTab = value
        }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class L_ScreenExplorerModule {
    @Binds
    @IntoMap
    @ScreenModelKey(L_ScreenExplorerSM::class)
    abstract fun bindL_ScreenExplorerSM(sm: L_ScreenExplorerSM): ScreenModel
}

