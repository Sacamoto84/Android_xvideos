package com.client.xvideos.redgifs.common.saved

import com.client.xvideos.AppPath
import com.client.xvideos.feature.fileDB.FileDB
import com.client.xvideos.redgifs.common.snackBar.SnackBarEvent
import com.client.xvideos.redgifs.network.types.UserInfo
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.onSuccess

class SavedRed_Creator {

    val creatorDb = FileDB<UserInfo>(AppPath.creators_red, "creator", object : TypeToken<UserInfo>() {}.type)

    var list = creatorDb.list

    fun add(item: UserInfo) {
        println("!!! addCreator() id:${item.username}")
        creatorDb.insert(item.username, item)
            .onSuccess {
                SnackBarEvent.success("Автор добавлен")
                list.add(item)
            }
            .onFailure { e ->
                SnackBarEvent.error("Ошибка добавления Автора ${e.message}")
            }
    }

    fun remove(username: String) {
        println("!!! removeCreator() id:${username} ")
        creatorDb.delete(username)
            .onSuccess {
                SnackBarEvent.info("Автор удален")
                //creatorsList.remove(item)
                refresh()
            }
            .onFailure { e ->
                SnackBarEvent.error("Ошибка удаления Автора ${e.message}")
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refresh() {
        creatorDb.refresh()
    }

}