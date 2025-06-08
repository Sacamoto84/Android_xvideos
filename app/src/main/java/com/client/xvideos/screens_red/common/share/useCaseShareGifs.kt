package com.client.xvideos.screens_red.common.share

import android.content.Context
import android.widget.Toast
import com.client.xvideos.AppPath
import com.client.xvideos.feature.redgifs.types.GifsInfo
import timber.log.Timber
import java.io.File

//--- Поделиться ---
fun useCaseShareGifs(context : Context, item: GifsInfo){

    val path = "${AppPath.cache_download_red}/${item.userName}/${item.id}.mp4"
    val file = File(path)

    try {
        if (file.exists()) {
            useCaseShareFile(context, file)
        } else {
            Toast.makeText(context, "Файл не найден: $path", Toast.LENGTH_SHORT).show()
            Timber.w("shareGifs -> Файл не существует: $path")
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Ошибка при попытке поделиться файлом", Toast.LENGTH_SHORT).show()
        Timber.e(e, "shareGifs -> Ошибка при работе с файлом: $path")
    }

}
