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
import com.client.xvideos.common.kdownloader.KDownloader
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.l.featured.downloader.DownloaderAlbum
import com.client.xvideos.l.featured.downloader.DownloaderL
import com.client.xvideos.l.featured.saved.SavedL
import com.client.xvideos.l.featured.share.useCaseShareFile
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.model.lDownloadUrl
import com.client.xvideos.l.model.lSavedFileName
import com.client.xvideos.l.net.AlbumInfo
import com.client.xvideos.l.net.Luscious
import com.client.xvideos.l.ui.element.lazyRowPictureDetails.LazyRowPictureDetailsHost
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
                saved.albums.add(albumInfo.value!!.albumInfo.value)
            }
        }
    }

    fun saveFullAlbum() {
        scope.launch {
            val pic = albumInfo.value?.albumPicsDetails?.pics?.toList()
                ?.mapNotNull { it.lDownloadUrl() }
                ?: emptyList()
            downloader.saveAlbums(pic, albumInfo.value!!.id.toString())
        }
    }

    fun downloadLike(item: PicsDetails) {
        saved.likes.add(item.copy(album = idAlbum.toString()))
    }

    fun downloadLikeCrypto(item: PicsDetails) {
        scope.launch {
            saved.crypto.add(item.copy(album = idAlbum.toString()))
        }
    }

    init {
        Timber.e("!!! ScreenLAlbumSM init")
    }

    override fun onDispose() {
        super.onDispose()
        Timber.e("!!! ScreenLAlbumSM onDispose")
    }


    fun share(item: PicsDetails) {
        scope.launch(Dispatchers.Main) {
            Timber.i("!!! share item = ${item.url_to_original} isAnimated: ${item.is_animated}")
            val fileName = item.lSavedFileName()
            val url = item.lDownloadUrl()
            if (fileName == null || url == null) {
                SnackBar.error("Нет ссылки для файла")
                return@launch
            }
            val client = HttpClient()
            val downloadsDir = AppPath.l_cacheDownload
            val file = File(downloadsDir, fileName)
            try {
                val response: HttpResponse = client.get(url)
                val bytes: ByteArray = response.readBytes()
                file.writeBytes(bytes)
                println("!!! Файл сохранен: ${file.absolutePath}")

                if (file.exists()) { useCaseShareFile(context, file) }
                else
                {
                    SnackBar.error("Файл не найден: ${file.absolutePath}")
                    Timber.w("shareGifs -> Файл не существует: ${file.absolutePath}")
                }
            } catch (e: Exception) {
                SnackBar.error("shareGifs -> Ошибка при работе с файлом: ${file.absolutePath}")
                Timber.e(e, "shareGifs -> Ошибка при работе с файлом: ${file.absolutePath}")
            } finally {
                client.close()
            }

        }
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
