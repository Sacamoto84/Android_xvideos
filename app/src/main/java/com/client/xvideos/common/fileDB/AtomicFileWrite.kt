package com.client.xvideos.common.fileDB

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset

/**
 * Записывает текст через временный файл и переименование: если запись оборвётся
 * (падение, нет места), на диске останется прежнее содержимое, а не обрезанный JSON.
 */
internal fun File.writeTextAtomically(text: String, charset: Charset = Charsets.UTF_8) {
    val tmp = File(parentFile, "$name.tmp")
    FileOutputStream(tmp).use { out ->
        out.write(text.toByteArray(charset))
        out.fd.sync()
    }
    if (tmp.renameTo(this)) return
    // Не все файловые системы заменяют существующий файл при rename.
    if (exists() && !delete()) {
        tmp.delete()
        throw IOException("Не удалось заменить файл: $absolutePath")
    }
    if (!tmp.renameTo(this)) {
        throw IOException("Не удалось сохранить файл: $absolutePath")
    }
}
