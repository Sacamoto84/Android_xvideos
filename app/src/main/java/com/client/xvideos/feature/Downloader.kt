package com.client.xvideos.feature

import com.client.xvideos.App
import com.client.xvideos.AppPath
import com.client.xvideos.util.Toast
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import javax.inject.Singleton

//Текущее содержимое готового кеша
data class ItemsRedCacheDownload(
    val name: String,             //Имя файла уникально
    val creator: String = "",     //Название креатора соответствует папке

    val url: String = "",          //Создается на этапе закачки, и после успешной закачки не используется url mp4  //https://media.redgifs.com/VictoriousGlamorousStud.m4s

    //val urlM3u8: String,    //url m3u8 //https://api.redgifs.com/v2/gifs/victoriousglamorousstud/hd.m3u8
    //val filePath: String,         //Файл hd mp4 -> AppPath.cache_download_red/creator/name.mp4

)


/**
 * Проверка что данное имя креатор уже есть в кеше
 */
fun findVideoOnRedCacheDownload(id: String, name: String): Boolean {
    val mainPath = AppPath.offline_red + "/" + name + "/" + id + ".mp4"
    val file = File(mainPath)
    return file.exists()
}


class Downloader() {

    //Процент скачивания 0..1 - начало скачивания, -2 busy, -3 error
    var percent = MutableStateFlow(-2f)

    @OptIn(DelicateCoroutinesApi::class)
    fun downloadRedName(id: String, name: String, url: String) {

        if ((url == "") || (name == "")) {
                     Toast("Ошибка в названии файла или креатор")
            percent.value = -3f
            return
        }

        percent.value = -2f

        //Проверка того что в кеше есть запись с этим именем и кретором
        val a = findVideoOnRedCacheDownload(id, name)

        //Записи нет можно скачивать
        if (!a) {

            val p = AppPath.cache_download_red + "/" + name
            File(p).mkdirs()

            val request = App.instance.kDownloader.newRequestBuilder(url, p, "$id.mp4").tag(id).build()

            App.instance.kDownloader.enqueue(
                request,
                onStart = {
                    println("!!! Запуск закачки")
                    percent.value = 0f
                },

                onError = {println("!!! onError закачки: $it"); percent.value = -3f},
                onProgress = {it1 -> percent.value = it1 / 100f},
                onCompleted = {
                    println("!!! onCompleted закачки")
                    percent.value = -2f
                    Toast("Скачивание завершено")
                },
            )
        } else {
            Toast("Файл есть к кеше")
        }

    }


    //Проверка сканирование файлов и одновление таблицы того что уже есть на диске
    fun scanRedCacheDowmdoadAndUpdate() {

        val mainPath = AppPath.cache_download_red //Путь до базовой папки для скачивания

        val baseDir = File(mainPath)
        if (!baseDir.exists() || !baseDir.isDirectory) {
            println("!!! Ошибка: Базовый каталог $mainPath не существует или не является каталогом.")
            return
        }

        // Получить список всех папок (креаторов), файлы не учитываем на этом уровне
        val creatorDirs = baseDir.listFiles { file -> file.isDirectory }

        if (creatorDirs == null) {
            println("!!! Ошибка: Не удалось найти каталоги в $mainPath.")
            return
        }

        val itemsToInsertInDb =
            mutableMapOf<String, ItemsRedCacheDownload>() // Key: fileName (name), Value: Item

        for (creatorDir in creatorDirs) {

            val creatorName = creatorDir.name // Имя папки = имя креатора

            val mp4Files = creatorDir.listFiles { file ->
                file.isFile && file.name.endsWith(".mp4", ignoreCase = true)
            }

            mp4Files?.forEach { mp4File ->
                val fileName = mp4File.name // Это будет @PrimaryKey
                // При сканировании мы не знаем 'url', если он не хранится где-то еще.
                // Если файл найден, он точно скачан.
                // Если запись с таким 'name' уже есть в БД, её 'url' будет сохранен при REPLACE.
                // Если записи нет, 'url' будет пустой строкой, пока не будет обновлен другим процессом.

                itemsToInsertInDb[fileName] = ItemsRedCacheDownload(
                    name = fileName,
                    creator = creatorName,
                    url = "", // Заполняется при реальной закачке, здесь мы его не знаем
                    //isDownloaded = true
                )

            }

            //Полностью очистить таблицу
            //db.redDownloadDao().clearTable()
            println("!!! Таблица red_cache_download очищена")


            if (itemsToInsertInDb.isNotEmpty()) {
                itemsToInsertInDb.forEach { item ->
                    //db.redDownloadDao().insert(item.value)
                }
                println("В таблицу red_cache_download добавлено ${itemsToInsertInDb.size} записей.")
            } else {
                println("На диске не найдено файлов для добавления в таблицу red_cache_download.")
            }
            println("!!! Перезаполнение таблицы данными с диска завершено.")
        }


    }


}


@Module
@InstallIn(SingletonComponent::class)
object ModuleDownloader {

    @Provides
    @Singleton
    fun provideDownloader(): Downloader {
        println("!!! DI Downloader")
        return Downloader()
    }

}