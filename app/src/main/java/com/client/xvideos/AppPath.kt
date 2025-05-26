package com.client.xvideos

import android.os.Environment
import kotlinx.io.IOException
import java.io.File

private enum class Folder(val value: String) {
    CACHE_DOWNLOAD_RED("CacheDownloadRed"),

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
    val cache_download_red : String = main + "/${Folder.CACHE_DOWNLOAD_RED.value}"

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
    }

}
