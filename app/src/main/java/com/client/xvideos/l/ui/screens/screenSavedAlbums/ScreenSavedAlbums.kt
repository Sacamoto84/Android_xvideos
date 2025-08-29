package com.client.xvideos.l.ui.screens.screenSavedAlbums

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.client.xvideos.l.ui.screens.screenAlbum.ScreenLAlbum
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject

class ScreenLSavedAlbums() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val vm: ScreenLSavedAlbumsSM = getScreenModel()

        Scaffold()
        { padding ->
            Box(modifier = Modifier.padding(padding))
            LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier) {
                items(vm.albums) {
                    AlbumListItem(it) { navigator.push(ScreenLAlbum(it.id.toLong())) }
                }
            }
        }

    }

}







@Composable
fun AlbumListItem(item: AlbumDetails, onClick: () -> Unit = {}) {

    Column(modifier = Modifier.padding(vertical = 2.dp).width(137.dp).border(1.dp, ThemeL.grey3).clickable(onClick = onClick)) {
        UrlImage(item.cover.url, modifier = Modifier.width(137.dp).height(200.dp), contentScale = ContentScale.Crop)

        Text(item.title.removePrefix(" "), modifier = Modifier, color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyKarla, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 16.sp)

        Row {
            val str = StringBuilder()
            if (item.number_of_animated_pictures > 0){
                str.append(item.number_of_animated_pictures.toString() + " gifs")
                if (item.number_of_pictures > 0) str.append(" / ")
            }
            if(item.number_of_pictures > 0) {
                str.append(item.number_of_pictures.toString())
                if ( item.number_of_animated_pictures == 0 ) str.append(" pictures")
            }
            Text(str.toString(), modifier = Modifier, color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyKarla, fontSize = 14.sp)
        }

    }

}













class ScreenLSavedAlbumsSM @Inject constructor(
    val saved: SavedL
) : ScreenModel {

    val albums = saved.albums.list

    init{
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
