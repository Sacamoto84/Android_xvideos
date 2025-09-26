package com.client.xvideos.l.ui.screens.explorer.tab.saved.likes

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.l.featured.saved.SavedL
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.ui.element.expandMenu.ExpandMenuType
import com.client.xvideos.l.ui.element.lazyRowPictureDetails.LazyRowPictureDetails
import com.client.xvideos.l.ui.element.lazyRowPictureDetails.LazyRowPictureDetailsHost
import com.client.xvideos.common.snackBar.SnackBarEvent
import com.client.xvideos.redgifs.ui.explorer.tab.gifs.ColumnSelect
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
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

        Scaffold(modifier = Modifier.fillMaxSize()) {
            LazyRowPictureDetails(
                vm.host,
                expandMenu =  ExpandMenuType.LIKES ,
            )
        }

    }

}

class ScreenSavedLLikesSM @Inject constructor(
val snackBarEvent: SnackBarEvent,
val savedL: SavedL
) : ScreenModel {

    val host =  LazyRowPictureDetailsHost("likes")

    init {
        host.filteredPic = savedL.likes.listUrl
    }

    fun delete(item: PicsDetails){
        savedL.likes.remove(item.url_to_original!!)
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
