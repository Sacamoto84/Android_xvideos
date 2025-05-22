package com.client.xvideos.feature

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.client.xvideos.App
import com.client.xvideos.AppPath
import com.client.xvideos.feature.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton


//Текущее содержимое готового кеша
@Entity(tableName = "red_cache_download")
data class ItemsRedCacheDownload(
    //val id: Long,

    @PrimaryKey
    val name: String,             //Имя файла уникально
    val creator: String = "",     //Название креатора соответсвует папке

    val url: String = "",          //Создается на этапе закачки, и после успешной закачки не используется url mp4  //https://media.redgifs.com/VictoriousGlamorousStud.m4s

    //val urlM3u8: String,    //url m3u8 //https://api.redgifs.com/v2/gifs/victoriousglamorousstud/hd.m3u8
    //val filePath: String,         //Файл hd mp4 -> AppPath.cache_download_red/creator/name.mp4

    val isDownloaded: Boolean = false, //признак того что файл уже скачан .. при запуске прилжения пробуем продолжить прошлые скачивания

)

@Dao
interface RedDownloadDao {

    // Добавить в базу скачанный файл
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: ItemsRedCacheDownload)

    // Метод для удаления по PrimaryKey (name)
    @Query("DELETE FROM red_cache_download WHERE name = :name")
    suspend fun deleteByName(name: String)

    @Query("SELECT * FROM red_cache_download")
    suspend fun getAll(): List<ItemsRedCacheDownload>

    // Получить элемент по имени (PrimaryKey)
    @Query("SELECT * FROM red_cache_download WHERE name = :name LIMIT 1")
    suspend fun getItemByName(name: String): ItemsRedCacheDownload?


    @Query("DELETE FROM red_cache_download") // Метод для удаления всех записей
    suspend fun clearTable()

}





class Downloader(
    val db: AppDatabase,
) {






    suspend fun downloadRedName(name: String, creator : String, url: String) {

        //Проверка того что в базе есть запись с этим именем
        val a  = db.redDownloadDao().getItemByName(name)
        //Записи нет можно скачивать
        if (a == null){



            val request = App.instance.kDownloader.newRequestBuilder(url, AppPath.cache_download_red, "$name.mp4")
                .tag("TAG")
                .build()

            App.instance.kDownloader.enqueue(
                request,
                onStart = {
                    println("Запуск закачки")

                    val b = ItemsRedCacheDownload(
                        name = name,
                        creator = creator,
                        url = url, // Заполняется при реальной закачке, здесь мы его не знаем
                        isDownloaded = false
                    )
                    db.redDownloadDao().insert(b)

                },
                onProgress = { it1 ->
                    println("progress $it1")
                    //percent.value = it1 / 100f
                },
                onCompleted = {
                    println("onCompleted закачки")
                    val b = ItemsRedCacheDownload(
                        name = name,
                        creator = creator,
                        url = url, // Заполняется при реальной закачке, здесь мы его не знаем
                        isDownloaded = true
                    )
                    db.redDownloadDao().insert(b)

                    //state.value = UPDATESTATE.DOWNLOADED //Загрузка завершена
                },
            )



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

        val itemsToInsertInDb = mutableMapOf<String, ItemsRedCacheDownload>() // Key: fileName (name), Value: Item

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
                    isDownloaded = true
                )

            }

            //Полностью очистить таблицу
            db.redDownloadDao().clearTable()
            println("!!! Таблица red_cache_download очищена")


            if (itemsToInsertInDb.isNotEmpty()) {
                itemsToInsertInDb.forEach { item ->
                    db.redDownloadDao().insert(item.value)
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
object moduleDownloader{

    @Provides
    @Singleton
    fun provideDownloader(db :AppDatabase): Downloader {
        println("!!! DI Downloader")
        return Downloader(db)
    }

}