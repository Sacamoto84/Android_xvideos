package com.client.xvideos.l.ui.screens.screenAlbumList

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.node.ModifierNodeElement
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
import com.client.common.urlVideImage.UrlImage
import com.client.xvideos.l.Luscious
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.ui.screens.screenAlbum.ScreenLAlbum
import com.client.xvideos.l.ui.screens.screenAlbum.ScreenLAlbumSM
import com.client.xvideos.l.ui.screens.screenAlbum.net.Album
import com.client.xvideos.l.ui.screens.screenAlbumList.atom.AlbumListItem
import com.client.xvideos.l.ui.screens.screenAlbumList.atom.AlbumListPageSelector
import com.client.xvideos.l.ui.screens.screenAlbumList.net.AlbumListImpl
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

//https://members.luscious.net/graphql/nobatch/?operationName=AlbumList

class ScreenLAlbumList(val idAlbum: Long) : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalZoomableApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val vm = getScreenModel<ScreenLAlbumListSM, ScreenLAlbumListSM.Factory> { factory ->
            factory.create(idAlbum)
        }

        val items = vm.albumList.collectAsStateWithLifecycle().value?.items
        val info = vm.albumList.collectAsStateWithLifecycle().value?.info




        Scaffold(
            bottomBar = {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(ThemeL.grey7)
                    ) {
                    }
                }
            }, containerColor = ThemeL.greyBackground
        ) { padding ->


            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(bottom = padding.calculateBottomPadding())
            ) {

                item(
                    key = "dummy",
                    span = { GridItemSpan(maxLineSpan) })
                {
                    Spacer(Modifier.height(48.dp))
                }

                item(
                    key = "page_selector",
                    span = { GridItemSpan(maxLineSpan) }) {
                    if (info != null) {
                        AlbumListPageSelector( info.page, info.totalPages, { vm.loadAlbumList(it) } )
                    }
                }

                items(items?.size ?: 0) { index ->

                    val item = items?.get(index)
                    if (item != null) {
                        AlbumListItem(item){
                            navigator.push(ScreenLAlbum(item.id.toLong()))
                        }
                    }

                }


            }


        }

    }
}

class ScreenLAlbumListSM @AssistedInject constructor(
    @Assisted val idAlbum: Long,
    val luscious: Luscious
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(idAlbum: Long): ScreenLAlbumListSM
    }

    var albumList = MutableStateFlow<AlbumListImpl?>(null)

    init {
        screenModelScope.launch {

            if (!luscious.loggedIn) {
                luscious.login()
            }

            albumList.value = luscious.getAlbumList()
            albumList.value?.getAlbumList(1)
        }
    }

    fun loadAlbumList(page: Int) {
        screenModelScope.launch {
            albumList.value?.getAlbumList(page)
        }
    }

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