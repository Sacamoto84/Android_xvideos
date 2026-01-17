package com.client.xvideos.l.featured.saved

import androidx.compose.runtime.mutableStateListOf
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.encrypting.Crypto
import com.client.xvideos.common.encrypting.Password
import com.client.xvideos.common.kdownloader.KDownloader
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.common.util.toMD5
import com.client.xvideos.l.model.PicsDetails
import timber.log.Timber
import java.io.File

class SavedL_Crypto(val kDownloader: KDownloader) {

    val listUrl = mutableStateListOf<PicsDetails>()

    init {
        refresh()
    }

    suspend fun add(item: PicsDetails) {
        println("!!! SavedL_Likes addLikes() item:${item.url_to_original}")

        val key = Password.key
        if (key == null) {
            Timber.i("!!! SavedL_Crypto add key == null Ключ отсутствует, не могу сохранять")
            SnackBar.error("Ключ шифрования отсутствует")
            return
        }

        val name = item.url_to_original?.substringAfterLast('/')?.substringBefore('?') //xxx.yyy
        val ext = name?.split(".")?.get(1)

        val fileName =
            item.width.toString() + "_" + item.height + "_" + item.is_animated + "_" + item.album + "_" +
                    name?.toMD5()?.dropLast(24) + "." + ext

        Crypto.downloadAndEncryptFile(
            item.url_to_original!!,
            File(AppPath.l_likesCrypto, fileName),
            Password.key!!
        )
            .onSuccess {
                SnackBar.success("Сохранен в сейф")
                Timber.i("!!! ScreenLAlbumSM downloadLikeCrypto success")
                refresh()
            }
            .onFailure {
                it.printStackTrace()
                SnackBar.error("Ошибка сохранения в сейф")
                Timber.e(it, "!!! ScreenLAlbumSM downloadLikeCrypto error")
            }

    }

    suspend fun addFromLike(item: PicsDetails) {
        println("!!! SavedL_Likes addFromLike() item:${item.url_to_original}")

        val key = Password.key
        if (key == null) {
            Timber.i("!!! SavedL_Crypto add key == null Ключ отсутствует, не могу сохранять")
            SnackBar.error("Ключ шифрования отсутствует")
            return
        }

        val name = item.url_to_original?.substringAfterLast('/')?.substringBefore('?') //xxx.yyy
        val ext = name?.split(".")?.get(1)

        val fileName =
            item.width.toString() + "_" + item.height + "_" + item.is_animated + "_" + item.album + "_" +
                    name?.toMD5()?.dropLast(24) + "." + ext

        Crypto.encryptFile(File(item.url_to_original!!), File(AppPath.l_likesCrypto, fileName), key)
            .onSuccess {
                SnackBar.success("Сохранен в сейф")
                Timber.i("!!! ScreenLAlbumSM downloadLikeCrypto success")
                refresh()
            }
            .onFailure {
                it.printStackTrace()
                SnackBar.error("Ошибка сохранения в сейф")
                Timber.e(it, "!!! ScreenLAlbumSM downloadLikeCrypto error")
            }

    }

    fun remove(fileName: String) {
        println("!!! SavedL_Crypto remove() path:${fileName}")
        val file = File(fileName)
        if (file.exists()) {
            if (!file.delete()) {
                SnackBar.error("Не удалось удалить файл: ${file.absolutePath}")
            } else
                SnackBar.info("Удален из сейфа")
        } else {
            SnackBar.error("Файл не найден: ${file.absolutePath}")
        }
        refresh()
    }

    fun refresh() {

        try {
            println("!!! SavedL_Crypto refresh()")
            val files = File(AppPath.l_likesCrypto).list()?.mapNotNull {
                fileNameToPicsDetails(File(it), AppPath.l_likesCrypto)
            }

            if (files != null) {
                listUrl.clear()
                listUrl.addAll(files)
            }
        } catch (e: Exception) {
            Timber.e("eee Ошибка получения списка crypto ${e.localizedMessage}")
            SnackBar.error("Ошибка получения списка crypto")
        }

    }

}
