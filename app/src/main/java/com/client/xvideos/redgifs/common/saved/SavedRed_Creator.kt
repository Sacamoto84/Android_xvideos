package com.client.xvideos.redgifs.common.saved

import com.client.xvideos.common.fileDB.FileDB
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.eventBus.snackBarError
import com.client.xvideos.common.eventBus.snackBarInfo
import com.client.xvideos.common.eventBus.snackBarSuccess
import com.client.xvideos.redgifs.model.UserInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.onSuccess

class SavedRed_Creator() {

    val creatorDb = FileDB(AppPath.creators_red, "creator", UserInfo::class.java)

    var list = creatorDb.list

    fun add(item: UserInfo) {
        println("!!! addCreator() id:${item.username}")
        creatorDb.insert(item.username, item)
            .onSuccess {
                snackBarSuccess("Автор добавлен")
                list.add(item)
            }
            .onFailure { e ->
                snackBarError("Ошибка добавления Автора ${e.message}")
            }
    }

    fun remove(username: String) {
        println("!!! removeCreator() id:${username} ")
        creatorDb.delete(username)
            .onSuccess {
                snackBarInfo("Автор удален")
                //creatorsList.remove(item)
                refresh()
            }
            .onFailure { e -> snackBarError("Ошибка удаления Автора ${e.message}") }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refresh() {
        creatorDb.refresh()
    }

}