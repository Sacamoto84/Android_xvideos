package com.client.xvideos.r.common.downloader

import com.client.xvideos.common.AppPath
import com.client.xvideos.common.di.ApplicationScope
import com.google.gson.GsonBuilder
import com.client.xvideos.common.kdownloader.KDownloader
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.r.model.GifsInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

//Текущее содержимое готового кеша
data class ItemsRedDownload(
    val name: String = "",     //Название креатора соответствует папке
    val id: String,            //Имя файла уникально
    val url: String = "",      //Создается на этапе закачки, и после успешной закачки не используется url mp4  //https://media.redgifs.com/VictoriousGlamorousStud.m4s
)

data class RedDownloadEnqueueReport(
    val queuedVideo: Int = 0,
    val queuedPreview: Int = 0,
    val skippedNoVideoUrl: Int = 0,
    val skippedNoPreviewUrl: Int = 0
)

/**
 * Проверка что данное имя креатор уже есть в кеше
 */
@Singleton
class Downloader @Inject constructor(
    val kDownloader: KDownloader,
    @ApplicationScope private val scope: CoroutineScope,
) {

    //Процент скачивания 0..1 - начало скачивания, -2 busy, -3 error
    var percent = MutableStateFlow(-2f)

    /**
     * Прогресс активных загрузок видео (id → 0..1). Индикатор один на все загрузки,
     * поэтому показываем среднее, а не значение последнего колбэка.
     * Обычно колбэки приходят на главном потоке, но cancel() вызывает onError синхронно.
     */
    private val activeProgress = ConcurrentHashMap<String, Float>()

    private fun publishProgress() {
        percent.value = if (activeProgress.isEmpty()) -2f else activeProgress.values.average().toFloat()
    }

    private fun onVideoStart(id: String) {
        activeProgress[id] = 0f
        publishProgress()
    }

    private fun onVideoProgress(id: String, progress: Int) {
        activeProgress[id] = progress / 100f
        publishProgress()
    }

    private fun onVideoFinished(id: String, failed: Boolean) {
        activeProgress.remove(id)
        if (failed && activeProgress.isEmpty()) percent.value = -3f else publishProgress()
    }

    /** id и имя креатора становятся частью пути — не пускаем разделители и "..". */
    private fun String.isSafePathSegment(): Boolean =
        isNotBlank() && this != "." && this != ".." && none { it == '/' || it == '\\' || it == '\u0000' }

    fun downloadRedName(item: GifsInfo, onComplete: () -> Unit = {}) {

        val videoUrl = item.downloadVideoUrl()
        if (videoUrl == null || !item.userName.isSafePathSegment() || !item.id.isSafePathSegment()) {
            //Toast("Ошибка в названии файла или креатор")
            percent.value = -3f
            return
        }

        percent.value = -2f

        //Проверка того что в кеше есть запись с этим именем и кретором

        //Записи нет можно скачивать
        if (!findVideoInDownload(item.id, item.userName)) {

            val p = AppPath.r_cache_download + "/" + item.userName
            File(p).mkdirs()

            item.previewUrl()?.let { imageUrl ->
                val requestImage = kDownloader.newRequestBuilder(imageUrl, p, "${item.id}.jpg").build()
                kDownloader.enqueue(requestImage)
            }

            val request = kDownloader.newRequestBuilder(videoUrl, p, "${item.id}.mp4").tag(item.id).build()

            kDownloader.enqueue(
                request,
                onStart = {
                    Timber.i("!!! Запуск закачки ${item.id}")
                    onVideoStart(item.id)
                },

                onError = {
                    Timber.w("!!! onError закачки ${item.id}: $it")
                    onVideoFinished(item.id, failed = true)
                    SnackBar.error("Ошибка закачки: $it")
                },

                onProgress = { progress -> onVideoProgress(item.id, progress) },
                onCompleted = {
                    onVideoFinished(item.id, failed = false)
                    SnackBar.success("Скачивание завершено")
                    // Запись на диск — не на главном потоке, где приходят колбэки.
                    scope.launch(Dispatchers.IO) {
                        runCatching { File(p, "${item.id}.info").writeText(GsonBuilder().create().toJson(item)) }
                            .onFailure { Timber.e(it, "!!! Не удалось записать ${item.id}.info") }
                        onComplete()
                    }
                },
            )
        } else {
            //Toast("Файл есть к кеше")
            SnackBar.info("Файл есть к кеше")
        }

    }

    fun downloadMissingFiles(
        item: GifsInfo,
        onComplete: () -> Unit = {},
        onEvent: (String) -> Unit = {},
        showSnackBarErrors: Boolean = true
    ): RedDownloadEnqueueReport {
        if (!item.id.isSafePathSegment() || !item.userName.isSafePathSegment()) {
            return RedDownloadEnqueueReport()
        }

        val p = AppPath.r_cache_download + "/" + item.userName
        File(p).mkdirs()

        val videoFile = File(p, "${item.id}.mp4")
        val previewFile = File(p, "${item.id}.jpg")
        var queuedVideo = 0
        var queuedPreview = 0
        var skippedNoVideoUrl = 0
        var skippedNoPreviewUrl = 0

        if (!previewFile.exists()) {
            val previewUrl = item.previewUrl()
            if (previewUrl == null) {
                skippedNoPreviewUrl++
            } else {
                val requestImage = kDownloader.newRequestBuilder(previewUrl, p, "${item.id}.jpg").build()
                kDownloader.enqueue(
                    requestImage,
                    onStart = { onEvent("R Download: старт preview ${item.id}") },
                    onError = { error ->
                        onEvent("R Download: preview не скачан ${item.id}: $error")
                        if (showSnackBarErrors) {
                            SnackBar.error("Ошибка загрузки preview: $error")
                        }
                    },
                    onCompleted = { onEvent("R Download: preview готов ${item.id}") }
                )
                queuedPreview++
            }
        }

        if (!videoFile.exists()) {
            val videoUrl = item.downloadVideoUrl()
            if (videoUrl == null) {
                skippedNoVideoUrl++
            } else {
                val request = kDownloader.newRequestBuilder(videoUrl, p, "${item.id}.mp4").tag(item.id).build()
                kDownloader.enqueue(
                    request,
                    onStart = {
                        onVideoStart(item.id)
                    },
                    onError = {
                        onVideoFinished(item.id, failed = true)
                        onEvent("R Download: video не скачан ${item.id}: $it")
                        SnackBar.error("Ошибка закачки: $it")
                    },
                    onProgress = { progress -> onVideoProgress(item.id, progress) },
                    onCompleted = {
                        onVideoFinished(item.id, failed = false)
                        onEvent("R Download: video готов ${item.id}")
                        onComplete()
                    }
                )
                queuedVideo++
            }
        }

        if (queuedVideo == 0) onComplete()

        return RedDownloadEnqueueReport(
            queuedVideo = queuedVideo,
            queuedPreview = queuedPreview,
            skippedNoVideoUrl = skippedNoVideoUrl,
            skippedNoPreviewUrl = skippedNoPreviewUrl
        )
    }

    fun downloadMissingFilesForRecovery(
        item: GifsInfo,
        onComplete: () -> Unit = {},
        onEvent: (String) -> Unit = {}
    ): RedDownloadEnqueueReport {
        if (!item.id.isSafePathSegment() || !item.userName.isSafePathSegment()) {
            return RedDownloadEnqueueReport()
        }

        val folderPath = AppPath.r_cache_download + "/" + item.userName
        File(folderPath).mkdirs()

        val videoFile = File(folderPath, "${item.id}.mp4")
        val previewFile = File(folderPath, "${item.id}.jpg")
        var queuedVideo = 0
        var queuedPreview = 0
        var skippedNoVideoUrl = 0
        var skippedNoPreviewUrl = 0

        if (!previewFile.exists()) {
            val previewUrl = item.previewUrl()
            if (previewUrl == null) {
                skippedNoPreviewUrl++
                onEvent("R Download: нет preview URL ${item.id}")
            } else {
                val requestImage = kDownloader.newRequestBuilder(previewUrl, folderPath, "${item.id}.jpg").build()
                kDownloader.enqueue(
                    requestImage,
                    onStart = { onEvent("R Download: старт preview ${item.id}") },
                    onError = { error -> onEvent("R Download: preview не скачан ${item.id}: $error") },
                    onCompleted = { onEvent("R Download: preview готов ${item.id}") }
                )
                queuedPreview++
            }
        }

        if (!videoFile.exists()) {
            val videoUrl = item.downloadVideoUrl()
            if (videoUrl == null) {
                skippedNoVideoUrl++
                onEvent("R Download: нет video URL ${item.id}")
            } else {
                val request = kDownloader.newRequestBuilder(videoUrl, folderPath, "${item.id}.mp4").tag(item.id).build()
                kDownloader.enqueue(
                    request,
                    onStart = {
                        onVideoStart(item.id)
                        onEvent("R Download: старт video ${item.id}")
                    },
                    onError = { error ->
                        onVideoFinished(item.id, failed = true)
                        onEvent("R Download: video не скачан ${item.id}: $error")
                    },
                    onProgress = { progress -> onVideoProgress(item.id, progress) },
                    onCompleted = {
                        onVideoFinished(item.id, failed = false)
                        onEvent("R Download: video готов ${item.id}")
                        onComplete()
                    }
                )
                queuedVideo++
            }
        }

        if (queuedVideo == 0) onComplete()

        return RedDownloadEnqueueReport(
            queuedVideo = queuedVideo,
            queuedPreview = queuedPreview,
            skippedNoVideoUrl = skippedNoVideoUrl,
            skippedNoPreviewUrl = skippedNoPreviewUrl
        )
    }

    fun findVideoInDownload(id: String, name: String): Boolean {
        //val mainPath = AppPath.cache_download_red + "/" + name + "/" + id + ".mp4"
        val mainPath = "${AppPath.r_cache_download}/$name/$id.mp4"
        val file = File(mainPath)
        return file.exists()
    }

    private fun GifsInfo.downloadVideoUrl(): String? {
        return urls.hd?.takeIf { it.isNotBlank() }
            ?: urls.sd.takeIf { it.isNotBlank() }
            ?: urls.silent?.takeIf { it.isNotBlank() }
    }

    private fun GifsInfo.previewUrl(): String? {
        return urls.poster?.takeIf { it.isNotBlank() }
            ?: urls.thumbnail.takeIf { it.isNotBlank() }
    }

}
