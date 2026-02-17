package com.client.xvideos.redgifs.common.saved

import com.client.xvideos.common.fileDB.FileDB
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.redgifs.model.UserInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.onSuccess

class R_Saved_Creator {

    val creatorDb = FileDB(AppPath.r_creators, "creator", UserInfo::class.java)

    var list = creatorDb.list

    fun add(item: UserInfo) {
        println("!!! addCreator() id:${item.username}")
        creatorDb.insert(item.username, item)
            .onSuccess {
                SnackBar.success("Автор добавлен")
                list.add(item)
            }
            .onFailure { e ->
                SnackBar.error("Ошибка добавления Автора ${e.message}")
            }
    }

    fun remove(username: String) {
        println("!!! removeCreator() id:${username} ")
        creatorDb.delete(username)
            .onSuccess {
                SnackBar.info("Автор удален")
                //creatorsList.remove(item)
                refresh()
            }
            .onFailure { e -> SnackBar.error("Ошибка удаления Автора ${e.message}") }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refresh() {
        creatorDb.refresh()
    }

}