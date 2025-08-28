package com.client.xvideos.l.featured.saved

import com.client.common.AppPath
import com.client.common.fileDB.FileDB
import com.client.xvideos.l.db.AlbumPictureCacheEntity
import com.client.xvideos.l.db.AppLDatabase
import com.client.xvideos.l.model.Album
import com.client.xvideos.l.model.AlbumDetails
import com.client.xvideos.l.model.PicsDetails
import com.google.gson.Gson
import com.redgifs.common.snackBar.SnackBarEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedL_Albums(val snackBarEvent: SnackBarEvent, val db: AppLDatabase, val scope: CoroutineScope) {

    val albumDb = FileDB(AppPath.albums_l, "album", AlbumDetails::class.java)
    val list = albumDb.list

    fun add(item: AlbumDetails) {
        println("!!! addAlbum() id:${item.id} name:${item.title}")
        albumDb.insert(item.id, item)
            .onSuccess {
                snackBarEvent.info("Альбом сохранен")
                list.add(item)
            }
            .onFailure { e ->
                snackBarEvent.error("Ошибка добавления группы ${e.message}")
            }
    }

    fun addAndPicsDetails(item: AlbumDetails, picsDetails: List<PicsDetails>) {
        println("!!! addAndPicsDetails() id:${item.id} name:${item.title} picsDetails:${picsDetails.size}")
        albumDb.insert(item.id, item)
            .onSuccess {
                snackBarEvent.info("Альбом сохранен")
                list.add(item)

                scope.launch(Dispatchers.IO) {
                    val gson = Gson()
                    db.albumPictureCacheDao().insert( AlbumPictureCacheEntity( item.id.toLong(), gson.toJson(picsDetails)) )
                }

            }
            .onFailure { e ->
                snackBarEvent.error("Ошибка добавления группы ${e.message}")
            }
    }

    fun remove(item: AlbumDetails) {
        println("!!! removeAlbum() id:${item.id} name:${item.title}")
        albumDb.delete(item.id)
            .onSuccess {
                snackBarEvent.info("Альбом удален")
                list.remove(item)
            }
            .onFailure { e ->
                snackBarEvent.error("Ошибка удаления группы ${e.message}")
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refresh() {
        albumDb.refresh()
    }


}