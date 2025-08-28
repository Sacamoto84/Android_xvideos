package com.client.xvideos.l.ui.screens.screenAlbum

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import com.client.common.di.ApplicationScope
import com.client.xvideos.l.featured.saved.SavedL
import com.client.xvideos.l.net.Luscious
import com.client.xvideos.l.net.AlbumInfo
import com.client.xvideos.l.repository.AlbumResult
import com.client.xvideos.l.repository.Repository
import com.client.xvideos.l.repository.RepositoryAction
import com.client.xvideos.l.repository.filterResult
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber


// State для ViewModel
data class AlbumsState(
    val isLoading: Boolean = false,
    val albumInfo: AlbumInfo? = null,
    val hasMore: Boolean = false,
    val error: String? = null
)

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


    var albumSaveWait by mutableStateOf(false)

    /**
     * Сохранить альбом
     */
    fun saveAlbum() {
        scope.launch {
            if (albumInfo.value != null) {
                saved.albums.add(albumInfo.value!!.parsed.value)
            }
            //albumSaveWait = true
            //while (album.albumPicsDetails.percentLoad < 1.0f) {
            //    delay(100)
            //}
            //albumSaveWait = false
            //val list = album.albumPicsDetails.pics
            //saved.albums.addAndPicsDetails(album.parsed.value, picsDetails = list)
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