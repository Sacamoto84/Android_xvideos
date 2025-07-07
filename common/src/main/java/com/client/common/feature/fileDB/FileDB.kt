package com.client.common.feature.fileDB

import com.google.gson.Gson
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.reflect.Type
import androidx.compose.runtime.mutableStateListOf
import timber.log.Timber

class FileDB<T>(val dirPath: String, val extension: String,  private val type: Type) {

    var list = mutableStateListOf<T>()

    val gson = Gson()

    fun insert(nameFile: String, value: T): Result<Boolean> {
        return try {

            val dir = File(dirPath)
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw IOException("Не удалось создать директорию: ${dir.absolutePath}")
                }
            }

            val file = File(dirPath, "${nameFile}.${extension}")

            gson.toJson(value).also { json ->
                require(json != "null") { "Сериализация вернула null" }
                file.writeText(json, Charsets.UTF_8)
            }

            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "!!! eee Ошибка при сохранении файла $nameFile")
            Result.failure(e)
        }
    }

    fun delete(name: String): Result<Boolean> {
        return try {
            val file = File(dirPath, "$name.$extension")
            if (file.exists()) {
                if (!file.delete()) {
                    return Result.failure(IOException("!!! Не удалось удалить файл: ${file.absolutePath}"))
                }
            }
            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "!!! Ошибка при удалении $name")
            Result.failure(e)
        }
    }


    fun read(nameFile: String, clazz: Class<T>): Result<T> {
        return try {
            val file = File(dirPath, "$nameFile.$extension")
            if (!file.exists()) {
                return Result.failure(FileNotFoundException("!!! Файл не найден: ${file.absolutePath}"))
            }
            val json = file.readText(Charsets.UTF_8)
            val obj = gson.fromJson(json, clazz)
                ?: return Result.failure(NullPointerException("!!! Десериализация вернула null"))
            Result.success(obj)
        } catch (e: Exception) {
            Timber.e(e, "!!! Ошибка при чтении файла $nameFile")
            Result.failure(e)
        }
    }

    fun refresh(): Result<Boolean> {
        return try {
            val dir = File(dirPath)
            if (!dir.exists() || !dir.isDirectory) {
                return Result.failure(IOException("!!! Директория не существует: $dirPath"))
            }

            val files = dir.listFiles { file -> file.extension == extension } ?: emptyArray()

            val loaded = files.mapNotNull { file ->
                try {
                    val json = file.readText(Charsets.UTF_8)
                    //val type = object : TypeToken<T>() {}.type
                    gson.fromJson<T>(json, type)
                } catch (e: Exception) {
                    Timber.e(e, "!!! Ошибка при чтении файла ${file.name}")
                    null
                }
            }

            list.clear()
            list.addAll(loaded)

            Result.success(true)
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при обновлении списка из директории $dirPath")
            Result.failure(e)
        }
    }

}