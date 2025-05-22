package com.client.xvideos

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.ContextCompat.startActivity
import java.io.File

private enum class Folder(val value: String) {
    CACHE_DOWNLOAD_RED("CacheDownloadRed"),

}

enum class EnvironmentStorage {
    INTERNAL,
    EXTERNAL,
    EXTERNAL_STORAGE,
}

/**
 * Предоставляет пути хранения файлов
 */
object AppPath {

    val root: EnvironmentStorage = EnvironmentStorage.EXTERNAL_STORAGE

    private val appMain = "xvideos"

    private val envoriment = when (root) {
        EnvironmentStorage.INTERNAL -> Environment.getDataDirectory() // /data
        EnvironmentStorage.EXTERNAL -> (App.instance.applicationContext as Application).getExternalFilesDir(null) // /storage/sdcard0/Android/data/package/files
        EnvironmentStorage.EXTERNAL_STORAGE -> Environment.getExternalStorageDirectory() // /storage/sdcard0
    }

    /**
     * Путь до sdcard
     */
    val sdcard = Environment.getExternalStorageDirectory().absolutePath.toString()

    val main = envoriment?.absolutePath + "/" + appMain

    /**
     * Пусть к папке с кешем загруженных файлов для предросмотра
     */
    val cache_download_red = envoriment?.absolutePath  + "/${appMain}/${Folder.CACHE_DOWNLOAD_RED.value}"

    val assets = "file:///android_asset/"

    init {
        println("---AppPath---")
        println("sdcard: $sdcard")

        File(main).mkdirs()
        File(cache_download_red).mkdirs()
    }

}
