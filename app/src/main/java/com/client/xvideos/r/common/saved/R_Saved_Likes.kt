package com.client.xvideos.r.common.saved

import com.client.xvideos.common.fileDB.FileDB
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.r.model.GifsInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import timber.log.Timber
import kotlin.onSuccess

class R_Saved_Likes {

    val likesDb = FileDB(AppPath.r_likes, "likes", GifsInfo::class.java)

    var list = likesDb.list

    fun add(item: GifsInfo) {
        Timber.i("R_Saved_Likes add() id:${item.id} userName:${item.userName} url:${item.urls.hd}")
        likesDb.insert(item.id, item)
            .onSuccess {
                SnackBar.success("Like")
                list.add(item)
            }
            .onFailure { e ->
                SnackBar.error("Ошибка добавления лайка ${e.message}")
            }
    }

    fun remove(item: GifsInfo) {
        Timber.i("R_Saved_Likes remove() id:${item.id} userName:${item.userName} url:${item.urls.hd}")
        likesDb.delete(item.id)
            .onSuccess { SnackBar.info("Unlike") }
            .onFailure { e -> SnackBar.error("Ошибка удаления лайка ${e.message}") }
        refresh()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refresh() {
        likesDb.refresh()
    }

}