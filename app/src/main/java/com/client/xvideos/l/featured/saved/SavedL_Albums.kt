package com.client.xvideos.l.featured.saved

import com.client.xvideos.common.AppPath
import com.client.xvideos.common.eventBus.snackBarError
import com.client.xvideos.common.eventBus.snackBarInfo
import com.client.xvideos.common.fileDB.FileDB
import com.client.xvideos.common.room.AppDatabase
import com.client.xvideos.common.room.entity.l.L_AlbumPictureCacheEntity
import com.client.xvideos.l.model.AlbumDetails
import com.client.xvideos.l.model.PicsDetails
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedL_Albums(val db: AppDatabase, val scope: CoroutineScope) {

    val albumDb = FileDB(AppPath.albums_l, "album", AlbumDetails::class.java)
    val list = albumDb.list

    fun add(item: AlbumDetails) {
        println("!!! addAlbum() id:${item.id} name:${item.title}")
        albumDb.insert(item.id, item)
            .onSuccess {
                snackBarInfo("Альбом сохранен")
                list.add(item)
            }
            .onFailure { e ->
                snackBarError("Ошибка добавления группы ${e.message}")
            }
    }

    fun addAndPicsDetails(item: AlbumDetails, picsDetails: List<PicsDetails>) {
        println("!!! addAndPicsDetails() id:${item.id} name:${item.title} picsDetails:${picsDetails.size}")
        albumDb.insert(item.id, item)
            .onSuccess {
                snackBarInfo("Альбом сохранен")
                list.add(item)

                scope.launch(Dispatchers.IO) {
                    val gson = Gson()
                    db.albumPictureCacheDao().insert(
                        L_AlbumPictureCacheEntity(
                            item.id.toLong(),
                            gson.toJson(picsDetails)
                        )
                    )
                }

            }
            .onFailure { e ->
                snackBarError("Ошибка добавления группы ${e.message}")
            }
    }

    fun remove(item: AlbumDetails) {
        println("!!! removeAlbum() id:${item.id} name:${item.title}")
        albumDb.delete(item.id)
            .onSuccess {
                snackBarInfo("Альбом удален")
                list.remove(item)
            }
            .onFailure { e ->
                snackBarError("Ошибка удаления группы ${e.message}")
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refresh() {
        albumDb.refresh()
    }


}