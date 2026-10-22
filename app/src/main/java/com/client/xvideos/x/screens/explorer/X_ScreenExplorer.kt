package com.client.xvideos.x.screens.explorer

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.client.xvideos.r.common.ThemeRed
import com.client.xvideos.r.ui.explorer.top.TabRow
import com.client.xvideos.screens.dashboards.ScreenXDashBoards
import com.client.xvideos.x.screens.favorites.FavoritesListContent
import com.client.xvideos.x.screens.favorites.ScreenFavoritesSM
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.collections.immutable.persistentListOf
import javax.inject.Inject

/**
 * Корневой экран раздела X: нижнее меню с вкладками «Дашборды» и «Избранное»,
 * по аналогии с разделами R/L.
 */
class X_ScreenExplorer : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val vm = getScreenModel<X_ScreenExplorerSM>()
        val favoritesVm = getScreenModel<ScreenFavoritesSM>()

        // Один экземпляр на весь X_ScreenExplorer, чтобы ScreenModel дашбордов (кэш страниц,
        // позиции скролла) переживал переключение вкладок.
        val dashboards = remember { ScreenXDashBoards() }

        val icons = remember { persistentListOf(Icons.Outlined.Dashboard, Icons.Outlined.FavoriteBorder) }
        val tags = remember { persistentListOf("", "") }

        Scaffold(
            bottomBar = {
                TabRow(
                    containerColor = ThemeRed.colorTabLevel0,
                    titlesIcon = icons,
                    value = vm.screenType,
                    onChangeState = { vm.screenType = it },
                    tags = tags
                )
            },
            containerColor = Color.Black
        ) { paddingValues ->
            Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
                when (vm.screenType) {
                    TAB_FAVORITES -> FavoritesListContent(favoritesVm)
                    else -> dashboards.Content()
                }
            }
        }
    }

    private companion object {
        const val TAB_FAVORITES = 1
    }
}

class X_ScreenExplorerSM @Inject constructor() : ScreenModel {
    /** Текущая вкладка: 0 — дашборды, 1 — избранное. */
    var screenType by mutableIntStateOf(0)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class X_ScreenExplorerModule {
    @Binds
    @IntoMap
    @ScreenModelKey(X_ScreenExplorerSM::class)
    abstract fun bindX_ScreenExplorerSM(sm: X_ScreenExplorerSM): ScreenModel
}
