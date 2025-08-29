package com.client.xvideos.l.ui.screens.screenSavedAlbums

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.common.urlVideImage.UrlImage
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.featured.saved.SavedL
import com.client.xvideos.l.model.AlbumDetails
import com.client.xvideos.l.ui.element.AlbumListItem
import com.client.xvideos.l.ui.screens.screenAlbum.ScreenLAlbum
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import my.nanihadesuka.compose.LazyVerticalGridScrollbar
import my.nanihadesuka.compose.ScrollbarSettings
import javax.inject.Inject

class ScreenLSavedAlbums() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val vm: ScreenLSavedAlbumsSM = getScreenModel()

        val state = rememberLazyGridState()

        Scaffold()
        { padding ->
            Box(modifier = Modifier.padding(padding)) {

                LazyVerticalGridScrollbar(
                    state = state,
                    settings = ScrollbarSettings.Default
                ) {

                    LazyVerticalGrid(
                        state = state,
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        items(vm.albums) {
                            AlbumListItem(
                                title = it.title,
                                coverUrl = it.cover.url,
                                numberOfAnimatedPictures = it.number_of_animated_pictures,
                                numberOfPictures = it.number_of_pictures,
                                modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
                            ) { navigator.push(ScreenLAlbum(it.id.toLong())) }
                        }
                    }

                }
            }
        }

    }

}


class ScreenLSavedAlbumsSM @Inject constructor(
    val saved: SavedL
) : ScreenModel {

    val albums = saved.albums.list

    init {
        if (albums.isEmpty()) saved.albums.refresh()
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLSavedAlbums {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenLSavedAlbumsSM::class)
    abstract fun bindScreenRedFulScreenSreenModel(hiltListScreenModel: ScreenLSavedAlbumsSM): ScreenModel
}
