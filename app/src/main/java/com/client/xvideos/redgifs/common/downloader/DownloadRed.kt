package com.client.xvideos.redgifs.common.downloader

import com.client.xvideos.common.AppPath
import com.client.xvideos.common.di.ApplicationScope
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.redgifs.model.GifsInfo
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.io.files.SystemPathSeparator
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRed @Inject constructor(
    val downloader: Downloader,
    @ApplicationScope private val scope: CoroutineScope
) {

    //var downloadList = mutableStateSetOf<String>()

    private val _downloadList = MutableStateFlow<List<GifsInfo>>(emptyList())
    val downloadList: StateFlow<List<GifsInfo>> = _downloadList.asStateFlow()


    init {
        refreshDownloadList()
    }

    fun downloadItem(item: GifsInfo) {
        scope.launch {
            try {
                Timber.i("Начало загрузки: ${item.id}")
                downloader.downloadRedName(item, onComplete = { refreshDownloadList() })
                Timber.i("Загрузка завершена: ${item.id}")
            } catch (e: Exception) {
                Timber.e(e, "Ошибка при загрузке: ${item.id}")
            }
        }
    }

    fun refreshDownloadList() {
        scope.launch(Dispatchers.IO) {
            val rootDir = File(AppPath.r_cache_download)

            val infoFiles = if (rootDir.exists() && rootDir.isDirectory) {
                rootDir.walkTopDown().filter { it.isFile && it.extension == "info" }.toList()
            } else {
                emptyList()
            }

            val gson = GsonBuilder().create()

            val result = mutableListOf<GifsInfo>()

            // Пример обработки каждого файла
            infoFiles.forEach { file ->
                try {
                    val content = file.readText()
                    val obj = gson.fromJson(content, GifsInfo::class.java)
                    result.add(obj)
                } catch (e: Exception) {
                    // Можно логгировать имя файла или путь
                    println("Ошибка при чтении файла ${file.absolutePath}: ${e.message}")
                }
            }

            // Можно отдать список путей или объектов
            _downloadList.emit(result)
        }
    }

    fun deleteAll(onComplete: () -> Unit = {}) {
        scope.launch(Dispatchers.IO) {
            File(AppPath.r_cache_download).deleteRecursively()
            refreshDownloadList()
            onComplete()
        }
    }

    fun delete(item: GifsInfo) {
        scope.launch(Dispatchers.IO) {

            val userDirPath = AppPath.r_cache_download + SystemPathSeparator + item.userName
            val userDir = File(userDirPath)

            val path0 = AppPath.r_cache_download+ SystemPathSeparator + item.userName + SystemPathSeparator + item.id+".mp4"
            val path1 = AppPath.r_cache_download+ SystemPathSeparator + item.userName + SystemPathSeparator + item.id+".info"
            val path2 = AppPath.r_cache_download+ SystemPathSeparator + item.userName + SystemPathSeparator + item.id+".jpg"
            File(path0).delete()
            File(path1).delete()
            File(path2).delete()

            // Проверяем, осталась ли папка пользователя пустой
            if (userDir.exists() && userDir.isDirectory) {
                val files = userDir.listFiles()
                if (files == null || files.isEmpty()) {
                    userDir.delete()  // папка пустая → удаляем
                }
            }

            refreshDownloadList()

            SnackBar.success("Gif удален")
        }
    }

}