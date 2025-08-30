package com.client.xvideos.l.ui.screens.explorer.tab.saved.likes

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
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
import com.client.common.AppPath
import com.client.common.sharedPref.Settings
import com.client.xvideos.l.featured.saved.SavedL
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.ui.UrlImageLusciousGifs
import com.client.xvideos.l.ui.UrlImageLusciousGifsGlide
import com.example.ui.screens.explorer.tab.gifs.ColumnSelect
import com.redgifs.common.snackBar.SnackBarEvent
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import java.io.File
import javax.inject.Inject

object ScreenLSavedLikesTab : Screen {

    private fun readResolve(): Any = ScreenLSavedLikesTab

    override val key: ScreenKey = uniqueScreenKey
    @Transient
    val columnSelect  = ColumnSelect(Settings.current_count_likesTab)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: ScreenSavedLLikesSM = getScreenModel()

        val state = rememberLazyStaggeredGridState()

        var selectedImage by remember { mutableStateOf<PicsDetails?>(null) }
        var selectedBounds by remember { mutableStateOf<Rect?>(null) }

        Scaffold(modifier = Modifier.fillMaxSize()) {

            Box(modifier = Modifier.fillMaxSize()) {

                LazyVerticalStaggeredGrid (
                    state = state,
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(vm.listUrl) {index, it ->

                        var imageBounds by remember { mutableStateOf<Rect?>(null) }

                        Box(modifier = Modifier.padding(2.dp).fillMaxWidth()) {
                            UrlImageLusciousGifsGlide(
                                url = it.url_to_original!!,
                                modifier = Modifier.fillMaxSize()
                                    .aspectRatio(it.width.toFloat() / it.height).clipToBounds()
                                    .border(0.5.dp, Color.Gray)
                                    .onGloballyPositioned { coordinates ->
                                        val rect = coordinates.boundsInRoot()
                                        imageBounds = rect
                                    }
                                    .clickable {
                                        selectedImage = it
                                        selectedBounds = imageBounds
                                    },
                                contentScale = ContentScale.FillBounds,
                                loadIndicator = true,
                                isGrayscale = false,
                                onLoading = {},
                                onSuccess = { },
                                albumName = "likes"
                            )

                            Text(index.toString(), color = Color.White, fontSize = 14.sp)
                        }
                    }

                }

//            //---- Скролл ----
//            Box(
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .align(Alignment.CenterEnd)
//                    .width(2.dp)
//            ) {
//                //VerticalScrollbar(scrollPercent)
//            }
            }
        }

    }



}

class ScreenSavedLLikesSM @Inject constructor(
val snackBarEvent: SnackBarEvent,
val savedL: SavedL
) : ScreenModel {

    val listUrl = savedL.likes.listUrl

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLSavedLikes {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenSavedLLikesSM::class)
    abstract fun bindScreenLSavedLikesScreenModel(hiltListScreenModel: ScreenSavedLLikesSM): ScreenModel
}
