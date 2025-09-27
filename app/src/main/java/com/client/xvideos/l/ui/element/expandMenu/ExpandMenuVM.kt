package com.client.xvideos.l.ui.element.expandMenu

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.ViewModel
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.di.ApplicationScope
import com.client.xvideos.common.eventBus.snackBarError
import com.client.xvideos.common.kdownloader.KDownloader
import com.client.xvideos.common.util.toMD5
import com.client.xvideos.l.featured.downloader.DownloaderL
import com.client.xvideos.l.featured.saved.SavedL
import com.client.xvideos.l.featured.share.useCaseShareFile
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.net.Luscious
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@Immutable
enum class ExpandMenuType {
    NONE,
    ALBUM,
    CRYPTO,
    LIKES
}


/**
 *  val expandMenuViewModel: ExpandMenuViewModel = hiltViewModel()
 */
@HiltViewModel
class ExpandMenuViewModel @Inject constructor(
    val luscious: Luscious,
    val saved: SavedL,
    @ApplicationScope val scope: CoroutineScope,
    //val repository: Repository
    val kDownloader: KDownloader,
    val dowloaderL: DownloaderL,
    @ApplicationContext val context: Context
) : ViewModel() {




    @Composable
    fun ExpandMenu(type: ExpandMenuType, item: PicsDetails, idAlbum: String) {
        when (type) {
            ExpandMenuType.NONE -> {}
            ExpandMenuType.ALBUM -> ExpandMenuAlbum(item, idAlbum)
            ExpandMenuType.CRYPTO -> ExpandMenuCrypto(item)
            ExpandMenuType.LIKES -> ExpandMenuLikes(item)
        }
    }

    ////


    @Composable
    fun ExpandMenuAlbum(item: PicsDetails, idAlbum: String) {

        val album = when(idAlbum){
            "likes" -> 0
            "crypto" -> 0
            else -> idAlbum.toLong()
        }

        AlbumItemExpandMenu(
            item = item, onDownload = { it1 -> downloadLike(it1, album) },
            onDownloadCrypto = { it1 -> downloadLikeCrypto(it1, album) },
            onShare = { it1 -> share(it1) })
    }


    @Composable
    fun ExpandMenuCrypto(item: PicsDetails) {
        SavedCryptoItemExpandMenu( item = item, onDelete = { cryptoDelete(item) },  onDownloadToLikes = { })
    }

    @Composable
    fun ExpandMenuLikes(item: PicsDetails) {
        val haptic = LocalHapticFeedback.current
        SavedLikesItemExpandMenu(
            item,
            onDelete = { it ->
                saved.likes.remove(item.url_to_original!!)
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            },
            onDownloadCrypto = { it ->  saveCrypto(it)
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        )
    }




    ///////
    fun downloadLike(item: PicsDetails, idAlbum: Long) {
        saved.likes.add(item.copy(album = idAlbum.toString()))
    }

    fun downloadLikeCrypto(item: PicsDetails, idAlbum: Long) {
        scope.launch {
            saved.crypto.add(item.copy(album = idAlbum.toString()))
        }
    }

    fun share(item: PicsDetails) {
        scope.launch(Dispatchers.Main) {
            Timber.i("!!! share item = ${item.url_to_original} isAnimated: ${item.is_animated}")
            val name = item.url_to_original?.substringAfterLast('/')?.substringBefore('?') //xxx.yyy

            val ext = if (item.is_animated && name?.split(".")
                    ?.get(1) == "jpg"
            ) "gif" else name?.split(".")?.get(1)

            val fileName =
                item.width.toString() + "_" + item.height + "_" + item.is_animated + "_" + item.album + "_" + name?.toMD5()
                    ?.dropLast(24) + "." + ext
            val url = item.url_to_original!!
            val client = HttpClient()
            val downloadsDir = AppPath.cacheDownload_l
            val file = File(downloadsDir, fileName)
            try {
                val response: HttpResponse = client.get(url)
                val bytes: ByteArray = response.readBytes()
                file.writeBytes(bytes)
                println("!!! Файл сохранен: ${file.absolutePath}")

                if (file.exists()) {
                    useCaseShareFile(context, file)
                } else {
                    snackBarError("Файл не найден: ${file.absolutePath}")
                    Timber.w("shareGifs -> Файл не существует: ${file.absolutePath}")
                }
            } catch (e: Exception) {
                snackBarError("shareGifs -> Ошибка при работе с файлом: ${file.absolutePath}")
                Timber.e(e, "shareGifs -> Ошибка при работе с файлом: ${file.absolutePath}")
            } finally {
                client.close()
            }

        }
    }


    fun cryptoDelete(item: PicsDetails) {
        scope.launch {
            saved.crypto.remove(item.url_to_original!!)
        }
    }

    fun saveCrypto(item: PicsDetails){
        scope.launch {
            saved.crypto.addFromLike(item)
        }
    }

}

