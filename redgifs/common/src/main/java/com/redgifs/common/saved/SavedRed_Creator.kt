package com.redgifs.common.saved

import com.client.common.fileDB.FileDB
import com.client.common.AppPath
import com.redgifs.model.UserInfo
import com.google.gson.reflect.TypeToken
import com.redgifs.common.snackBar.SnackBarEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.onSuccess

class SavedRed_Creator(val snackBarEvent : SnackBarEvent) {

    val creatorDb = FileDB<UserInfo>(AppPath.creators_red, "creator", object : TypeToken<UserInfo>() {}.type)

    var list = creatorDb.list

    fun add(item: UserInfo) {
        println("!!! addCreator() id:${item.username}")
        creatorDb.insert(item.username, item)
            .onSuccess {
                snackBarEvent.success("Автор добавлен")
                list.add(item)
            }
            .onFailure { e ->
                snackBarEvent.error("Ошибка добавления Автора ${e.message}")
            }
    }

    fun remove(username: String) {
        println("!!! removeCreator() id:${username} ")
        creatorDb.delete(username)
            .onSuccess {
                snackBarEvent.info("Автор удален")
                //creatorsList.remove(item)
                refresh()
            }
            .onFailure { e ->
                snackBarEvent.error("Ошибка удаления Автора ${e.message}")
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refresh() {
        creatorDb.refresh()
    }

}