package com.client.xvideos.screens_red.common.favorite.useCase

import com.client.xvideos.AppPath
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.google.gson.Gson
import kotlinx.io.IOException
import timber.log.Timber
import java.io.File


fun setFavoriteItem(item : GifsInfo, favorite : Boolean): Result<Boolean>  {

    return try {
        Timber.i("!!! Фаворит GIFS -> setFavoriteItem() id:${item.id} userName:${item.userName} url:${item.urls.hd}")

        // Создаем директорию <userName>/block, если её нет
        val favoriteDir = File(AppPath.favorite_red, item.userName)

        if (!favoriteDir.exists()) {
            val created = favoriteDir.mkdirs()
            if (!created) { return Result.failure(IOException("Не удалось создать директорию: ${favoriteDir.absolutePath}")) }
        }

        val favoriteFile = File(favoriteDir, "${item.id}.favorite")

        if(favorite) {
            // Сохраняем URL как JSON в файл
            val gson = Gson()
            val json = gson.toJson(item)
            favoriteFile.writeText(json, Charsets.UTF_8)
        }else
            favoriteFile.delete()

        Result.success(true)
    } catch (e: Exception) {
        Timber.e(e, "Ошибка при блокировке GIF")
        Result.failure(e)
    }

}