package com.client.xvideos.feature

import android.widget.Toast
import com.client.xvideos.App
import com.client.xvideos.AppPath
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
fun findVideoOnRedCacheDownload(name: String, creator: String): Boolean {
    val mainPath = AppPath.cache_download_red + "/" + creator + "/" + name + ".mp4"
    val file = File(mainPath)
    return file.exists()

}


class Downloader() {

    //Процент скачивания 0..1 - начало скачивания, -2 busy, -3 error
    var percent = MutableStateFlow(-2f)

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun downloadRedName(name: String, creator: String, url: String) {

        if ((url == "") || (creator == "")) {

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    App.instance.applicationContext,
                    "Ошибка в названии файла или креатор",
                    Toast.LENGTH_SHORT
                ).show()
            }

            percent.value = -3f

            return

        }

        percent.value = -2f

        //Проверка того что в кеше есть запись с этим именем и кретором
        val a = findVideoOnRedCacheDownload(name, creator)

        //Записи нет можно скачивать
        if (a == false) {

            val p = AppPath.cache_download_red + "/" + creator
            File(p).mkdirs()

            val request = App.instance.kDownloader.newRequestBuilder(
                url,
                p,
                "$name.mp4"
            )
                .tag("TAG")
                .build()

            App.instance.kDownloader.enqueue(
                request,
                onStart = {
                    println("!!! Запуск закачки")

                    val b = ItemsRedCacheDownload(
                        name = name,
                        creator = creator,
                        url = url, // Заполняется при реальной закачке, здесь мы его не знаем

                    )

                    percent.value = 0f

                },

                onError = {
                    println("!!! onError закачки: $it")
                    percent.value = -3f
                },
                onProgress = { it1 ->
                    //println("!!! progress $it1")
                    percent.value = it1 / 100f
                },
                onCompleted = {
                    println("!!! onCompleted закачки")

                    percent.value = -2f

                    val b = ItemsRedCacheDownload(
                        name = name,
                        creator = creator,
                        url = url, // Заполняется при реальной закачке, здесь мы его не знаем

                    )

                    GlobalScope.launch {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                App.instance.applicationContext,
                                "Скачивание завершено",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    //state.value = UPDATESTATE.DOWNLOADED //Загрузка завершена
                },
            )
        } else {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        App.instance.applicationContext,
                        "Файл есть к кеше",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }


    //Проверка сканирование файлов и одновление таблицы того что уже есть на диске
    suspend fun scanRedCacheDowmdoadAndUpdate() {

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
object moduleDownloader {

    @Provides
    @Singleton
    fun provideDownloader(): Downloader {
        println("!!! DI Downloader")
        return Downloader()
    }

}