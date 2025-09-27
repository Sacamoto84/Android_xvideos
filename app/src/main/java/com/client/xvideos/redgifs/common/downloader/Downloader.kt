package com.client.xvideos.redgifs.common.downloader

import com.client.xvideos.common.AppPath
import com.google.gson.GsonBuilder
import com.client.xvideos.common.kdownloader.KDownloader
import com.client.xvideos.common.eventBus.UiMessage
import com.client.xvideos.common.eventBus.snackBarError
import com.client.xvideos.common.eventBus.snackBarInfo
import com.client.xvideos.common.eventBus.snackBarSuccess
import com.client.xvideos.redgifs.model.GifsInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

//Текущее содержимое готового кеша
data class ItemsRedDownload(
    val name: String = "",     //Название креатора соответствует папке
    val id: String,            //Имя файла уникально
    val url: String = "",      //Создается на этапе закачки, и после успешной закачки не используется url mp4  //https://media.redgifs.com/VictoriousGlamorousStud.m4s
)

/**
 * Проверка что данное имя креатор уже есть в кеше
 */
@Singleton
class Downloader @Inject constructor(
    val kDownloader: KDownloader,
) {

    //Процент скачивания 0..1 - начало скачивания, -2 busy, -3 error
    var percent = MutableStateFlow(-2f)

    @OptIn(DelicateCoroutinesApi::class)
    fun downloadRedName(item: GifsInfo, onComplete: () -> Unit = {}) {

        if ((item.urls.hd.toString() == "") || (item.userName == "")) {
            //Toast("Ошибка в названии файла или креатор")
            percent.value = -3f
            return
        }

        percent.value = -2f

        //Проверка того что в кеше есть запись с этим именем и кретором

        //Записи нет можно скачивать
        if (!findVideoInDownload(item.id, item.userName)) {

            val p = AppPath.cache_download_red + "/" + item.userName
            File(p).mkdirs()

            val imageUrl = if(item.urls.poster != null) item.urls.poster else item.urls.thumbnail
            val requestImage = kDownloader.newRequestBuilder(imageUrl!!, p, "${item.id}.jpg").build()
            kDownloader.enqueue(
                requestImage
            )

            val request = kDownloader.newRequestBuilder(item.urls.hd.toString(), p, "${item.id}.mp4").tag(item.id).build()

            kDownloader.enqueue(
                request,
                onStart = {
                    println("!!! Запуск закачки")
                    percent.value = 0f
                },

                onError = {
                    println("!!! onError закачки: $it"); percent.value = -3f
                    snackBarError("Ошибка закачки: $it")
                },

                onProgress = { it1 -> percent.value = it1 / 100f },
                onCompleted = {
                    println("!!! onCompleted закачки")
                    percent.value = -2f

                    snackBarSuccess("Скачивание завершено")
                    val gson = GsonBuilder().create()
                    val text = gson.toJson(item)
                    File(p, "${item.id}.info").writeText(text.toString())

                    onComplete()
                    //DownloadRed.refreshDownloadList()
                },
            )
        } else {
            //Toast("Файл есть к кеше")
            snackBarInfo("Файл есть к кеше")
        }

    }

    fun findVideoInDownload(id: String, name: String): Boolean {
        //val mainPath = AppPath.cache_download_red + "/" + name + "/" + id + ".mp4"
        val mainPath = "${AppPath.cache_download_red}/$name/$id.mp4"
        val file = File(mainPath)
        return file.exists()
    }

}
