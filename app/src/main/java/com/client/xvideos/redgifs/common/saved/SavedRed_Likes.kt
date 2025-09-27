package com.client.xvideos.redgifs.common.saved

import com.client.xvideos.common.fileDB.FileDB
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.eventBus.snackBarError
import com.client.xvideos.common.eventBus.snackBarInfo
import com.client.xvideos.common.eventBus.snackBarSuccess
import com.client.xvideos.redgifs.model.GifsInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.onSuccess

class SavedRed_Likes() {

    val likesDb = FileDB(AppPath.likes_red, "likes", GifsInfo::class.java)

    var list = likesDb.list

    fun add(item: GifsInfo) {
        println("!!! addLikes() id:${item.id} userName:${item.userName} url:${item.urls.hd}")
        likesDb.insert(item.id, item)
            .onSuccess {
                snackBarSuccess("Like")
                list.add(item)
            }
            .onFailure { e ->
                snackBarError("Ошибка добавления лайка ${e.message}")
            }
    }

    fun remove(item: GifsInfo) {
        println("!!! removeLikes() id:${item.id} userName:${item.userName} url:${item.urls.hd}")
        likesDb.delete(item.id)
            .onSuccess { snackBarInfo("Unlike") }
            .onFailure { e -> snackBarError("Ошибка удаления лайка ${e.message}") }
        refresh()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refresh() {
        likesDb.refresh()
    }

}