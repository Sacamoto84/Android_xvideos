package com.redgifs.common.saved

import com.client.common.fileDB.FileDB
import com.client.common.AppPath
import com.redgifs.model.GifsInfo
import com.google.gson.reflect.TypeToken
import com.redgifs.common.snackBar.SnackBarEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.onSuccess

class SavedRed_Likes(val snackBarEvent : SnackBarEvent) {

    val likesDb = FileDB<GifsInfo>(AppPath.likes_red, "likes", object : TypeToken<GifsInfo>() {}.type)

    var list = likesDb.list

    fun add(item: GifsInfo) {
        println("!!! addLikes() id:${item.id} userName:${item.userName} url:${item.urls.hd}")
        likesDb.insert(item.id, item)
            .onSuccess {
                snackBarEvent.success("Like")
                list.add(item)
            }
            .onFailure { e ->
                snackBarEvent.error("Ошибка добавления лайка ${e.message}")
            }
    }

    fun remove(item: GifsInfo) {
        println("!!! removeLikes() id:${item.id} userName:${item.userName} url:${item.urls.hd}")
        likesDb.delete(item.id)
            .onSuccess { snackBarEvent.info("Unlike") }
            .onFailure { e ->  snackBarEvent.error("Ошибка удаления лайка ${e.message}") }
        refresh()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refresh() {
        likesDb.refresh()
    }

}