package com.client.xvideos.l.ui.screens.screenAlbum

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import com.client.common.di.ApplicationScope
import com.client.xvideos.l.featured.saved.SavedL
import com.client.xvideos.l.net.AlbumInfo
import com.client.xvideos.l.net.Luscious
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ScreenLAlbumSM @AssistedInject constructor(
    @Assisted val idAlbum: Long,
    val luscious: Luscious,
    val saved: SavedL,
    @ApplicationScope val scope: CoroutineScope,
    //val repository: Repository
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(idAlbum: Long): ScreenLAlbumSM
    }

    val albumInfo = MutableStateFlow<AlbumInfo?>(null)

    init {
        screenModelScope.launch {
            albumInfo.value = luscious.getAlbum(idAlbum)
        }
    }

    /**
     * Сохранить альбом
     */
    fun saveAlbum() {
        scope.launch {
            if (albumInfo.value != null) {
                saved.albums.add(albumInfo.value!!.parsed.value)
            }
        }
    }

    init {
        Timber.e("!!! ScreenLAlbumSM init")
    }

    override fun onDispose() {
        super.onDispose()
        Timber.e("!!! ScreenLAlbumSM onDispose")
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLAlbum {

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(ScreenLAlbumSM.Factory::class)
    abstract fun bindHiltProfilesScreenModelFactory(
        hiltDetailsScreenModelFactory: ScreenLAlbumSM.Factory
    ): ScreenModelFactory

}