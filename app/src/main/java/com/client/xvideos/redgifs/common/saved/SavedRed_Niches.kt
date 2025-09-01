package com.redgifs.common.saved

import com.client.xvideos.common.fileDB.FileDB
import com.client.xvideos.common.AppPath
import com.client.xvideos.redgifs.model.NichesInfo
import com.redgifs.common.snackBar.SnackBarEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.onSuccess

class SavedRed_Niches(val snackBarEvent : SnackBarEvent) {

    val nichesDb = FileDB(AppPath.niches_red, "niches", NichesInfo::class.java)
    val list = nichesDb.list

    fun add(item: NichesInfo) {
        println("!!! addNiches() id:${item.id} name:${item.name}")
        nichesDb.insert(item.id, item)
            .onSuccess {
                snackBarEvent.info("Группа добавлена")
                list.add(item)
            }
            .onFailure { e ->
                snackBarEvent.error("Ошибка добавления группы ${e.message}")
            }
    }

    fun remove(item: NichesInfo) {
        println("!!! removeNiches() id:${item.id} name:${item.name}")
        nichesDb.delete(item.id)
            .onSuccess {
                snackBarEvent.info("Группа удалена")
                list.remove(item)
            }
            .onFailure { e ->
                snackBarEvent.error("Ошибка удаления группы ${e.message}")
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refresh() {
        nichesDb.refresh()
    }

}