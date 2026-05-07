package com.client.xvideos.r.common.saved

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.snackbar.SnackBar
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.client.xvideos.r.model.Niche
import com.client.xvideos.r.network.api.RedApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class R_Saved_NichesCaches(
    val scope: CoroutineScope,
    val redApi: RedApi,
) {

    val list = mutableListOf<Niche>()

    var size by mutableIntStateOf(-1)

    var isDownloading by mutableStateOf(false)

    var progress by mutableFloatStateOf(0f)

    var isDownloaded by mutableStateOf(false)

    var lastModifiedHour by mutableLongStateOf(-1)
    var lastModifiedMinute by mutableLongStateOf(-1)

    init {
        readFromDisk()
    }

    fun refresh() {
        scope.launch {
            try {
                isDownloading = true
                progress = 0f
                val niches = mutableListOf<Niche>()
                val res = redApi.explorer.getExplorerNiches(page = 1, count = 100).getOrNull()
                val pages = res!!.pages
                val step = 1f / (pages - 1)
                niches.addAll(res.niches)
                for (i in 2..pages) {
                    delay(200)
                    val res2 = redApi.explorer.getExplorerNiches(page = i, count = 100).getOrNull()
                    niches.addAll(res2!!.niches)
                    progress += step
                }
                list.clear()
                list.addAll(niches)
                val gson = GsonBuilder().setPrettyPrinting().create()
                val json = gson.toJson(niches)
                val file = File(AppPath.r_nichesCache, "niches.json")
                if (file.exists()) {
                    file.delete()
                }
                file.writeText(json)
                size = list.size
                timeRefresh()
                SnackBar.success("Обновление завершено")
                isDownloading = false
                isDownloaded = true
            } catch (e: Exception) {
                SnackBar.error("Ошибка обновления ${e.toString()}")
                isDownloading = false
            }
        }
    }

    fun readFromDisk() {
        val file = File(AppPath.r_nichesCache, "niches.json")
        if (!file.exists()) {
            return
        }
        val json = file.readText()
        val gson = GsonBuilder().setPrettyPrinting().create()
        val niches = gson.fromJson<List<Niche>>(json, object : TypeToken<List<Niche>>() {}.type)
        list.clear()
        list.addAll(niches)
        size = list.size
        timeRefresh()
    }

    private fun timeRefresh() {
        val file = File(AppPath.r_nichesCache, "niches.json")
        if (!file.exists()) {
            return
        }
        // Получаем время последней модификации
        val lastModified = file.lastModified() // время в миллисекундах с эпохи
        val now = System.currentTimeMillis()
        val diffMillis = now - lastModified
        lastModifiedMinute = diffMillis / (60 * 1000)
        lastModifiedHour = diffMillis / (60 * 60 * 1000)
        lastModifiedMinute
        lastModifiedMinute
    }

}