package com.client.xvideos.l

import android.os.Build
import androidx.annotation.RequiresApi
import java.nio.file.InvalidPathException
import java.nio.file.Paths

//@RequiresApi(Build.VERSION_CODES.O)
//class Tag(
//    val id: String,
//    val text: String,
//    val category: String,
//    val url: String
//) {
//    val name: String by lazy {
//        text.split(":").last().trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
//    }
//
//    val fullName: String
//        get() = text
//
//    val sanitizedName: String by lazy {
//        sanitizeFileName(name)
//    }
//
//    val hashtag: String by lazy {
//        "#" + sanitizedName.replace(' ', '_').replace('-', '_')
//    }
//
//    override fun toString(): String = name
//
//    // Пример реализации функции очистки имени файла
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun sanitizeFileName(input: String): String {
//        return try {
//            // Пробуем создать путь, если ошибка - убираем недопустимые символы
//            Paths.get(input)
//            input
//        } catch (e: InvalidPathException) {
//            input.replace(Regex("[\\\\/:*?\"<>|]"), "_")
//        }
//    }
//}
