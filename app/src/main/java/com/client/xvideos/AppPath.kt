package com.client.xvideos

import android.os.Environment
import kotlinx.io.IOException
import java.io.File

private enum class Folder(val value: String) {
    CACHE_DOWNLOAD_RED("Download"),
    RED("Red"),

}

/**
 * Предоставляет пути хранения файлов
 */
object AppPath {

    private const val appMain = "xvideos"

    /**
     * Путь до внешнего хранилища
     */
    val sdcard: String = Environment.getExternalStorageDirectory().toString()

    val main : String = "$sdcard/$appMain"

    /**
     * Пусть к папке с кешем загруженных файлов для предросмотра
     */
    val cache_download_red : String = "${main}/${Folder.RED.value}/${Folder.CACHE_DOWNLOAD_RED.value}"
    val offline_red : String = "${main}/${Folder.RED.value}/Offline"
    val block_red : String = "${main}/${Folder.RED.value}/Block"
    val favorite_red : String = "${main}/${Folder.RED.value}/Favorite"

    val users_red : String = "${main}/${Folder.RED.value}/Users"

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

        File(cache_download_red).mkdirs()
        File(offline_red).mkdirs()
        File(block_red).mkdirs()
        File(favorite_red).mkdirs()
        File(users_red).mkdirs()
    }

}
