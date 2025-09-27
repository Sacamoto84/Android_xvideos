package com.client.xvideos.redgifs.common.saved

import com.client.xvideos.common.fileDB.FileDB
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.eventBus.snackBarError
import com.client.xvideos.common.eventBus.snackBarInfo
import com.client.xvideos.redgifs.model.NichesInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.onSuccess

class SavedRed_Niches() {

    val nichesDb = FileDB(AppPath.niches_red, "niches", NichesInfo::class.java)
    val list = nichesDb.list

    fun add(item: NichesInfo) {
        println("!!! addNiches() id:${item.id} name:${item.name}")
        nichesDb.insert(item.id, item)
            .onSuccess {
                snackBarInfo("Группа добавлена")
                list.add(item)
            }
            .onFailure { e ->
                snackBarError("Ошибка добавления группы ${e.message}")
            }
    }

    fun remove(item: NichesInfo) {
        println("!!! removeNiches() id:${item.id} name:${item.name}")
        nichesDb.delete(item.id)
            .onSuccess {
                snackBarInfo("Группа удалена")
                list.remove(item)
            }
            .onFailure { e -> snackBarError("Ошибка удаления группы ${e.message}") }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refresh() {
        nichesDb.refresh()
    }

}