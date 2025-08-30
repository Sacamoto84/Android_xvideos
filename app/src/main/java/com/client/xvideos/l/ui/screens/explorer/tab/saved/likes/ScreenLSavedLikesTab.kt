package com.client.xvideos.l.ui.screens.explorer.tab.saved.likes

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
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

        Box(modifier = Modifier.fillMaxSize()) {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
            ) {
                items(vm.listUrl){

                    UrlImageLusciousGifsGlide(
                        url = it,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loadIndicator = true,
                        isGrayscale = false,
                        onLoading = {},
                        onSuccess = { } ,
                        albumName = "likes"
                    )

                }

            }

            //---- Скролл ----
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .width(2.dp)
            ) {
                //VerticalScrollbar(scrollPercent)
            }
        }


    }



}

class ScreenSavedLLikesSM @Inject constructor(
val snackBarEvent: SnackBarEvent

) : ScreenModel {

    val listUrl = mutableStateListOf<String>()

    init{
        try {
            val files = File(AppPath.likes_l).list()
            listUrl.clear()
            if (files != null) {
                listUrl.addAll(files)
            }
        }catch (e:Exception){
            e.printStackTrace()
            snackBarEvent.error("Ошибка получения списка likes")
        }

    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLSavedLikes {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenSavedLLikesSM::class)
    abstract fun bindScreenLSavedLikesScreenModel(hiltListScreenModel: ScreenSavedLLikesSM): ScreenModel
}
