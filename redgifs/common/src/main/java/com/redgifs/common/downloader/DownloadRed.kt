package com.redgifs.common.downloader

import androidx.compose.runtime.mutableStateSetOf
import com.client.common.AppPath
import com.client.common.di.ApplicationScope
import com.google.gson.GsonBuilder
import com.redgifs.model.GifsInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                downloader.downloadRedName(item, onComplete = {
                    refreshDownloadList()
                })
                Timber.i("Загрузка завершена: ${item.id}")
            } catch (e: Exception) {
                Timber.e(e, "Ошибка при загрузке: ${item.id}")
            }
        }
    }

    fun refreshDownloadList() {
        scope.launch(Dispatchers.IO) {
            val rootDir = File(AppPath.cache_download_red)

            val infoFiles = if (rootDir.exists() && rootDir.isDirectory) {
                rootDir.walkTopDown()
                    .filter { it.isFile && it.extension == "info" }
                    .toList()
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
            File(AppPath.cache_download_red).deleteRecursively()
            refreshDownloadList()
            onComplete()
        }
    }

    fun delete(item: GifsInfo) {
        scope.launch(Dispatchers.IO) {
            val path0 = AppPath.cache_download_red+ SystemPathSeparator + item.userName + SystemPathSeparator + item.id+".mp4"
            val path1 = AppPath.cache_download_red+ SystemPathSeparator + item.userName + SystemPathSeparator + item.id+".info"
            val path2 = AppPath.cache_download_red+ SystemPathSeparator + item.userName + SystemPathSeparator + item.id+".jpg"
            File(path0).delete()
            File(path1).delete()
            File(path2).delete()
            refreshDownloadList()
        }
    }

}