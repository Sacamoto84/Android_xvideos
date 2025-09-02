package com.client.xvideos.l.ui.screens.screenAlbum

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.di.ApplicationScope
import com.client.xvideos.common.encrypting.Crypto
import com.client.xvideos.common.encrypting.Password
import com.client.xvideos.l.featured.downloader.DownloaderAlbum
import com.client.xvideos.l.featured.downloader.DownloaderL
import com.client.xvideos.l.featured.saved.SavedL
import com.client.xvideos.l.net.AlbumInfo
import com.client.xvideos.l.net.Luscious
import com.client.xvideos.common.kdownloader.KDownloader
import com.client.xvideos.common.util.toMD5
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.ui.element.lazyRowPictureDetails.LazyRowPictureDetailsHost
import com.client.xvideos.redgifs.common.snackBar.SnackBarEvent
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class ScreenLAlbumSM @AssistedInject constructor(
    @Assisted val idAlbum: Long,
    val luscious: Luscious,
    val saved: SavedL,
    @ApplicationScope val scope: CoroutineScope,
    //val repository: Repository
    val kDownloader: KDownloader,
    val dowloaderL: DownloaderL,
    val snackBarEvent: SnackBarEvent,
    @ApplicationContext val context: Context
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(idAlbum: Long): ScreenLAlbumSM
    }

    val host = LazyRowPictureDetailsHost(idAlbum.toString())


    val downloader: DownloaderAlbum

    init {

        Timber.i("!!!! ScreenLAlbumSM init")

        //Поиск экземпляра DownloaderAlbum в dowloaderL
        val f = dowloaderL.listDownloaderAlbum.find { it.albumName == idAlbum.toString() }
        if (f == null) {
            dowloaderL.listDownloaderAlbum.add(
                DownloaderAlbum(
                    idAlbum.toString(),
                    kDownloader,
                    dowloaderL.scope
                )
            )
        }
        downloader = dowloaderL.listDownloaderAlbum.first { it.albumName == idAlbum.toString() }


    }


    val albumInfo = MutableStateFlow<AlbumInfo?>(null)

    /**
     * Показ только анимированных картинок
     */
    var showOnlyAnimated by mutableStateOf(false)

    //val downloader = DownloaderAlbum(idAlbum.toString(), kDownloader)

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

    fun saveFullAlbum() {
        scope.launch {
            val pic = albumInfo.value?.albumPicsDetails?.pics?.toList()
                ?.map { it.url_to_original } as List<String>
            downloader.saveAlbums(pic, albumInfo.value!!.id.toString())
        }
    }

    fun downloadLike(item: PicsDetails) {
        saved.likes.add(item.copy(album = idAlbum.toString()))
    }

    fun downloadLikeCrypto(item: PicsDetails) {
        //saved.likes.add(item.copy(album = idAlbum.toString()))
        scope.launch {
            val key = Password.key
            if (key == null){
                Timber.i("!!! ScreenLAlbumSM downloadLikeCrypto key == null Ключ отсутствует, не могу сохранять")
                snackBarEvent.error("Ключ шифрования отсутствует")
                return@launch
            }

            val name = item.url_to_original?.substringAfterLast('/')?.substringBefore('?') //xxx.yyy
            val ext = name?.split(".")?.get(1)

            val fileName = item.width.toString()+"_"+item.height+"_"+item.is_animated+"_"+idAlbum.toString()+"_"+
                    name?.toMD5()?.dropLast(24)+"."+ext

            Crypto.downloadAndEncryptFile( item.url_to_original!!, File( AppPath.likesCrypto_l, fileName ), Password.key!! )
                .onSuccess {
                    snackBarEvent.success("Сохранен в сейф")
                    Timber.i("!!! ScreenLAlbumSM downloadLikeCrypto success")
                }
                .onFailure {
                    it.printStackTrace()
                    snackBarEvent.error("Ошибка сохранения в сейф")
                    Timber.e(it, "!!! ScreenLAlbumSM downloadLikeCrypto error")
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