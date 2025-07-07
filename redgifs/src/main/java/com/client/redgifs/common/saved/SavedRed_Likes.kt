package com.client.redgifs.common.saved

import com.client.xvideos.AppPath
import com.client.xvideos.feature.fileDB.FileDB
import com.client.xvideos.redgifs.common.snackBar.SnackBarEvent
import com.client.xvideos.redgifs.network.types.GifsInfo
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.onSuccess

class SavedRed_Likes {

    val likesDb = FileDB<GifsInfo>(AppPath.likes_red, "likes", object : TypeToken<GifsInfo>() {}.type)

    var list = likesDb.list

    fun add(item: GifsInfo) {
        println("!!! addLikes() id:${item.id} userName:${item.userName} url:${item.urls.hd}")
        likesDb.insert(item.id, item)
            .onSuccess {
                SnackBarEvent.success("Like")
                list.add(item)
            }
            .onFailure { e ->
                SnackBarEvent.error("Ошибка добавления лайка ${e.message}")
            }
    }

    fun remove(item: GifsInfo) {
        println("!!! removeLikes() id:${item.id} userName:${item.userName} url:${item.urls.hd}")
        likesDb.delete(item.id)
            .onSuccess { SnackBarEvent.info("Unlike") }
            .onFailure { e ->  SnackBarEvent.error("Ошибка удаления лайка ${e.message}") }
        refresh()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refresh() {
        likesDb.refresh()
    }

}