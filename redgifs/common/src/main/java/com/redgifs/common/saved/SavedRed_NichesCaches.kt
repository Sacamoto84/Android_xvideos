package com.redgifs.common.saved

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.client.common.AppPath
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.redgifs.common.snackBar.SnackBarEvent
import com.redgifs.model.Niche
import com.redgifs.network.api.RedApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class SavedRed_NichesCaches(val scope : CoroutineScope, val redApi: RedApi, val snackBarEvent : SnackBarEvent) {

    val list = mutableListOf<Niche>()

    var size by mutableIntStateOf(-1)

    var isDownloading by mutableStateOf(false)

    var progress by mutableFloatStateOf(0f)

    var lastModifiedHour by mutableLongStateOf(-1)
    var lastModifiedMinute by mutableLongStateOf(-1)

    init{
        readFromDisk()
    }

    fun refresh(){
        scope.launch {
            try {
                isDownloading = true
                progress = 0f
                val niches = mutableListOf<Niche>()
                val res = redApi.explorer.getExplorerNiches(page = 1, count = 100)
                val pages = res.pages

                val step = 1f/(pages-1)

                niches.addAll(res.niches)
                for (i in 2..pages) {
                    delay(200)
                    val res2 = redApi.explorer.getExplorerNiches(page = i, count = 100)
                    niches.addAll(res2.niches)
                    progress += step
                }
                list.clear()
                list.addAll(niches)
                val gson = GsonBuilder().setPrettyPrinting().create()
                val json = gson.toJson(niches)
                val file = File(AppPath.nichesCache_red, "niches.json")
                file.writeText(json)
                size = list.size
                snackBarEvent.success("Обновление завершено")
                isDownloading = false
            }
            catch (e: Exception) {
                snackBarEvent.error("Ошибка обновления ${e.toString()}")
                isDownloading = false
            }
        }
    }

    fun readFromDisk(){
        val file = File(AppPath.nichesCache_red, "niches.json")
        if (!file.exists()) {
            return
        }
        val json = file.readText()
        val gson = GsonBuilder().setPrettyPrinting().create()
        val niches = gson.fromJson<List<Niche>>(json, object : TypeToken<List<Niche>>() {}.type)
        list.clear()
        list.addAll(niches)
        size = list.size

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