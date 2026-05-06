package com.client.xvideos.redgifs.common.saved

import com.client.xvideos.common.fileDB.FileDB
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.redgifs.model.UserInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import timber.log.Timber
import kotlin.onSuccess

class R_Saved_Creator {

    val creatorDb = FileDB(AppPath.r_creators, "creator", UserInfo::class.java)

    var list = creatorDb.list

    fun add(item: UserInfo) {
        Timber.i("R_Saved_Creator add() id:${item.username}")
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
        Timber.i("R_Saved_Creator remove() id:${username}")
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