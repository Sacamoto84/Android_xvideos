package com.client.xvideos.screens_red.use_case

import com.client.xvideos.AppPath
import com.client.xvideos.feature.redgifs.types.GifsInfo
import kotlinx.io.IOException
import timber.log.Timber
import java.io.File

/**
 * Блокирует GIF-элемент путём создания специального файла-флага `.block`.
 *
 * Блокировка осуществляется путём создания файла с именем `<id>.block`
 * внутри директории `<cache_download_red>/<userName>`. Наличие такого файла
 * считается признаком того, что GIF заблокирован.
 *
 * @param item Объект [GifsInfo], содержащий информацию о GIF: ID, имя пользователя, URL и пр.
 * @return [Result.success(true)] — если файл успешно создан или уже существует.
 *         [Result.failure] — если произошла ошибка при создании директории или файла.
 *
 * @see java.io.File.createNewFile
 * @see Result
 */
fun useCaseBlockItem(item: GifsInfo): Result<Boolean> {
    return try {
        Timber.i("!!! Блокировка GIFS -> useCaseBlockItem() id:${item.id} userName:${item.userName} url:${item.urls.hd}")

        // Создаем директорию, если её нет
        val dir = File(AppPath.cache_download_red, item.userName)
        if (!dir.exists()) {
            val created = dir.mkdirs()
            if (!created) {
                return Result.failure(IOException("Не удалось создать директорию: ${dir.absolutePath}"))
            }
        }

        // Создаем файл-блокировку
        val blockFile = File(dir, "${item.id}.block")
        val fileCreated = blockFile.createNewFile() || blockFile.exists() // `exists` на случай, если файл уже есть

        if (fileCreated) {
            Result.success(true)
        } else {
            Result.failure(IOException("Не удалось создать файл блокировки: ${blockFile.absolutePath}"))
        }
    } catch (e: Exception) {
        Timber.e(e, "Ошибка при блокировке GIF")
        Result.failure(e)
    }
}