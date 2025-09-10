package com.client.xvideos.l.ui.screens.screenAlbumList

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.model.AlbumListFilter
import com.client.xvideos.l.net.AlbumListImpl
import com.client.xvideos.l.net.Luscious
import com.client.xvideos.l.ui.element.AlbumListItem
import com.client.xvideos.l.ui.screens.screenAlbum.ScreenLAlbum
import com.client.xvideos.l.ui.screens.screenAlbumList.atom.AlbumListPageSelector
import com.client.xvideos.l.ui.screens.screenAlbumList.bottomBar.AlbumListBottomBar
import com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.AlbumListFilter
import com.client.xvideos.redgifs.common.ThemeRed
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ExperimentalZoomableApi
import timber.log.Timber

//https://members.luscious.net/graphql/nobatch/?operationName=AlbumList

object ScreenLAlbumList {

    // Сохраняем первый экземпляр навсегда
    private val firstInstance: Screen by lazy {
        ScreenLAlbumListImpl(filter = null, isFirst = true)
    }

    private var instanceCounter = 0

    // Получить первый экземпляр (всегда живой)
    fun getFirst(): Screen = firstInstance

    // Создать новый экземпляр
    fun create(filter: AlbumListFilter? = null): Screen {
        instanceCounter++
        return ScreenLAlbumListImpl(filter = filter, isFirst = false, instanceId = instanceCounter)
    }
    // Создать экземпляр или вернуть первый
    fun getInstance(filter: AlbumListFilter? = null, useFirst: Boolean = false): Screen {
        return if (useFirst && filter == null) {
            firstInstance
        } else {
            create(filter)
        }
    }


    class ScreenLAlbumListImpl(
        val filter: AlbumListFilter?,
        private val isFirst: Boolean = false,
        private val instanceId: Int = 0
    ) : Screen {

        //override val key: ScreenKey = uniqueScreenKey

        override val key: ScreenKey = if (isFirst) {
            "ScreenLAlbumList_FIRST" // Уникальный ключ для первого экземпляра
        } else {
            "ScreenLAlbumList_$instanceId" // Уникальные ключи для остальных
        }

        @OptIn(ExperimentalZoomableApi::class)
        @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
        @Composable
        override fun Content() {

            val navigator = LocalNavigator.currentOrThrow
            val vm = getScreenModel<ScreenLAlbumListSM, ScreenLAlbumListSM.Factory> { factory ->
                factory.create(filter)
            }
            val items = vm.albumList.collectAsStateWithLifecycle().value?.items
            val info = vm.albumList.collectAsStateWithLifecycle().value?.info
            val filter = vm.albumList.collectAsStateWithLifecycle().value?.filter

            val filterGCount =
                vm.albumList.collectAsStateWithLifecycle().value?.filterGenreStateCount

            val filterTagsCount =
                vm.albumList.collectAsStateWithLifecycle().value?.filterTaggedStateCount

            var visibleFilter by remember { mutableStateOf(false) }

            val haptic = LocalHapticFeedback.current

            //val state = rememberLazyGridState()

            val scope = rememberCoroutineScope()

            Scaffold(
                bottomBar = {
                    AlbumListBottomBar(
                        onClickVisibleFilter = { visibleFilter = !visibleFilter },
                        onClickPrev = { vm.loadPrevList() },
                        onClickNext = { vm.loadNextList() })
                }, containerColor = ThemeL.greyBackground
            ) { padding ->


                LazyVerticalGrid(
                    state = vm.state,
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(bottom = padding.calculateBottomPadding())
                ) {
                    item(
                        key = "dummy",
                        span = { GridItemSpan(maxLineSpan) }) { Spacer(Modifier.height(48.dp)) }

                    item(
                        key = "page_selector",
                        span = { GridItemSpan(maxLineSpan) }) {
                        if (info != null) {
                            AlbumListPageSelector(info.page, info.totalPages, {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                vm.loadAlbumList(it)
                            })
                        }
                    }

                    items(items?.size ?: 0) { index ->
                        val item = items?.get(index)
                        if (item != null) {
                            AlbumListItem(
                                title = item.title,
                                coverUrl = item.cover.url,
                                numberOfAnimatedPictures = item.numberOfAnimatedPictures,
                                numberOfPictures = item.numberOfPictures,
                            ) {
                                navigator.push(ScreenLAlbum(item.id.toLong()))
                            }
                        }
                    }

                    item(
                        key = "page_selector2",
                        span = { GridItemSpan(maxLineSpan) }) {

                        if (items?.isNotEmpty() ?: false) {
                            if (info != null) {
                                AlbumListPageSelector(
                                    info.page, info.totalPages,
                                    {
                                        scope.launch { vm.state.scrollToItem(0) }
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        vm.loadAlbumList(it)
                                    })
                            }
                        }

                    }

                }

                if (filter != null) {
                    if (visibleFilter) AlbumListFilter(
                        filter,
                        filterGCount,
                        filterTagsCount,
                        onClose = { visibleFilter = false }) {
                        //vm.albumList.value?.filter = it
                        vm.screenModelScope.launch {
                            vm.albumList.value?.getAlbumList(1, it)
                            vm.albumList.value?.getAlbumListAggregations(1)
                        }
                    }
                }
            }
        }
    }

}

class ScreenLAlbumListSM @AssistedInject constructor(
    @Assisted val filter: AlbumListFilter?,
    val luscious: Luscious
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(filter: AlbumListFilter?): ScreenLAlbumListSM
    }

    var albumList = MutableStateFlow<AlbumListImpl?>(null)

    init {
        Timber.i("iii ScreenLAlbumListSM init")
        screenModelScope.launch {
            albumList.value = luscious.getAlbumList()
            albumList.value?.getAlbumList(1, filter)
            albumList.value?.getAlbumListAggregations(1)
        }
    }

    override fun onDispose() {
        super.onDispose()
        Timber.i("iii ScreenLAlbumListSM onDispose")
    }

    fun loadAlbumList(page: Int) {
        screenModelScope.launch {
            albumList.value?.getAlbumList(page, albumList.value!!.filter)
            //albumList.value?.getAlbumListAggregations(page)
        }
    }


    fun loadNextList(){
          if (albumList.value != null){
              val page = (albumList.value!!.info.page + 1)
              loadAlbumList(page)
          }
    }

    fun loadPrevList(){
        if (albumList.value != null){
            val page = (albumList.value!!.info.page - 1).coerceAtLeast(1)
            loadAlbumList(page)
        }
    }

    val state = LazyGridState()

}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLAlbumList {

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(ScreenLAlbumListSM.Factory::class)
    abstract fun bindHiltProfilesScreenModelFactory(
        hiltDetailsScreenModelFactory: ScreenLAlbumListSM.Factory
    ): ScreenModelFactory

}