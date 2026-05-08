package com.client.xvideos.common

import android.os.Environment
import java.io.IOException
import java.io.File

private enum class Folder(val value: String) {
    CACHE_DOWNLOAD_RED("Download"),
    RED("R"),
    L("L"),
    X("X")
}

/**
 * Центральное место для путей файлового хранилища приложения.
 *
 * Объект строит структуру каталогов внутри `xvideos` на внешнем хранилище:
 * отдельно для X, RedGifs и L-раздела. При первой инициализации создаёт
 * недостающие папки и `.nomedia`, чтобы системная галерея не индексировала
 * служебные медиафайлы приложения.
 */
object AppPath {

    private const val appMain = "xvideos"

    /**
     * Путь до внешнего хранилища
     */
    val sdcard: String = Environment.getExternalStorageDirectory().toString()

    val main : String = "$sdcard/$appMain"

    //--- X ---
    val x_favorites : String = "${main}/${Folder.X.value}/Saved/Favorites"

    //--- R ---
    /**
     * Пусть к папке с кешем загруженных файлов для предросмотра
     */
    val r_cache_download : String = "${main}/${Folder.RED.value}/${Folder.CACHE_DOWNLOAD_RED.value}"
    val r_offline : String = "${main}/${Folder.RED.value}/Offline"
    val r_block : String = "${main}/${Folder.RED.value}/Block"

    val r_likes : String = "${main}/${Folder.RED.value}/Saved/Likes"
    val r_collection : String = "${main}/${Folder.RED.value}/Saved/Collection"
    val r_niches : String = "${main}/${Folder.RED.value}/Saved/Niches"
    val r_nichesCache : String = "${main}/${Folder.RED.value}/Saved/NichesCache"
    val r_creators : String = "${main}/${Folder.RED.value}/Saved/Creators"

    val r_subscriptions: String = "${main}/${Folder.RED.value}/Saved/Subscriptions"

    //--- L ---
    val l_likes: String = "${main}/${Folder.L.value}/Saved/Downloaded/Likes"
    val l_cacheDownload: String = "${main}/${Folder.L.value}/Saved/Downloaded/Cache"
    val l_albums: String = "${main}/${Folder.L.value}/Saved/Album"
    val l_downloaded_albums: String = "${main}/${Folder.L.value}/Saved/Downloaded/Album"
    val l_collection: String = "${main}/${Folder.L.value}/Saved/Collection"
    val l_db: String = "${main}/${Folder.L.value}/db"


    /**
     * Создаёт базовую структуру директорий при первом обращении к `AppPath`.
     *
     * Kotlin `object` инициализируется лениво, поэтому папки создаются не при
     * запуске процесса, а когда код впервые обращается к одному из путей.
     */
    init {

        println("---AppPath---")
        println("sdcard: $sdcard")

        File(main).mkdirs()

        // Создание .nomedia
        val nomedia = File(main, ".nomedia")
        if (!nomedia.exists()) {
            try {
                nomedia.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        File(r_cache_download).mkdirs()
        File(r_offline).mkdirs()
        File(r_block).mkdirs()

        File(r_likes).mkdirs()
        File(r_collection).mkdirs()
        File(r_niches).mkdirs()
        File(r_creators).mkdirs()
        File(r_subscriptions).mkdirs()

        File(r_nichesCache).mkdirs()

        File(l_likes).mkdirs()
        File(l_db).mkdirs()

        File(l_albums).mkdirs()
        File(l_collection).mkdirs()

        File(l_downloaded_albums).mkdirs()

        File(l_cacheDownload).mkdirs()


        File(x_favorites).mkdirs()

    }

}
