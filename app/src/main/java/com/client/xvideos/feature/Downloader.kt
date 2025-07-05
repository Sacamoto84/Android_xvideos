package com.client.xvideos.feature

import com.client.xvideos.App
import com.client.xvideos.AppPath
import com.client.xvideos.redgifs.common.downloader.DownloadRed
import com.client.xvideos.redgifs.common.snackBar.SnackBarEvent
import com.client.xvideos.redgifs.common.snackBar.UiMessage
import com.client.xvideos.util.Toast
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

//Текущее содержимое готового кеша
data class ItemsRedDownload(
    val name: String = "",     //Название креатора соответствует папке
    val id: String,            //Имя файла уникально
    val url: String = "",      //Создается на этапе закачки, и после успешной закачки не используется url mp4  //https://media.redgifs.com/VictoriousGlamorousStud.m4s
)


/**
 * Проверка что данное имя креатор уже есть в кеше
 */


object Downloader {

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
        val a = DownloadRed.findVideoInDownload(id, name)

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

                onError = {
                    println("!!! onError закачки: $it"); percent.value = -3f
                    SnackBarEvent.messages.trySend(UiMessage.Error("Ошибка закачки: $it"))
                },

                onProgress = { it1 -> percent.value = it1 / 100f },
                onCompleted = {
                    println("!!! onCompleted закачки")
                    percent.value = -2f
                    //Toast("Скачивание завершено")
                    SnackBarEvent.messages.trySend(UiMessage.Success("Скачивание завершено"))
                    DownloadRed.refreshDownloadList()
                },
            )
        } else {
            //Toast("Файл есть к кеше")
            SnackBarEvent.messages.trySend(UiMessage.Info("Файл есть к кеше"))
        }

    }


    //Проверка сканирование файлов и обновление таблицы того что уже есть на диске
    fun scanRedCacheDownloadAndUpdate() {

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
            mutableListOf<ItemsRedDownload>() // Key: fileName (name), Value: Item

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

                itemsToInsertInDb.add(
                    ItemsRedDownload(
                        id = fileName,
                        name = creatorName,
                        url = "", // Заполняется при реальной закачке, здесь мы его не знаем
                    )
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
