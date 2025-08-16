package com.client.xvideos.l.ui.screens.screenAlbumTopHits

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
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
import com.client.xvideos.l.net.AlbumListImpl
import com.client.xvideos.l.net.AlbumTopHitsImpl
import com.client.xvideos.l.net.Luscious
import com.client.xvideos.l.ui.screens.screenAlbumList.ScreenLAlbumListSM
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



class ScreenLAlbumTopHits() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalZoomableApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val vm = getScreenModel<ScreenLAlbumTopHitsSM, ScreenLAlbumTopHitsSM.Factory> { factory ->  factory.create(0) }







    }

}

class ScreenLAlbumTopHitsSM @AssistedInject constructor(
    @Assisted val idAlbum: Long,
    val luscious: Luscious
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(idAlbum: Long): ScreenLAlbumTopHitsSM
    }

    var albumTopHits = MutableStateFlow<AlbumTopHitsImpl?>(null)

    init {
        screenModelScope.launch {
            if (!luscious.loggedIn) { luscious.login() }
            albumTopHits.value = luscious.getAlbumTopHits()
            albumTopHits.value?.getAlbumTopHits()
            //albumList.value?.getAlbumList(1)
        }
    }

}



@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLAlbumTopHits {

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(ScreenLAlbumTopHitsSM.Factory::class)
    abstract fun bindHiltProfilesScreenModelFactory(
        hiltDetailsScreenModelFactory: ScreenLAlbumTopHitsSM.Factory
    ): ScreenModelFactory

}