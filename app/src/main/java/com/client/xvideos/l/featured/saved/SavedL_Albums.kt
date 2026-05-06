package com.client.xvideos.l.featured.saved

import com.client.xvideos.common.AppPath
import com.client.xvideos.common.fileDB.FileDB
import com.client.xvideos.common.room.AppDatabase
import com.client.xvideos.common.room.entity.l.L_AlbumPictureCacheEntity
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.l.model.AlbumDetails
import com.client.xvideos.l.model.PicsDetails
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class SavedL_Albums(val db: AppDatabase, val scope: CoroutineScope) {

    val albumDb = FileDB(AppPath.l_albums, "album", AlbumDetails::class.java)
    val list = albumDb.list

    fun add(item: AlbumDetails) {
        Timber.i("addAlbum() id:${item.id} name:${item.title}")
        albumDb.insert(item.id, item)
            .onSuccess {
                SnackBar.info("Альбом сохранен")
                list.add(item)
            }
            .onFailure { e ->
                SnackBar.error("Ошибка добавления группы ${e.message}")
            }
    }

    fun addAndPicsDetails(item: AlbumDetails, picsDetails: List<PicsDetails>) {
        Timber.i("addAndPicsDetails() id:${item.id} name:${item.title} picsDetails:${picsDetails.size}")
        albumDb.insert(item.id, item)
            .onSuccess {
                SnackBar.info("Альбом сохранен")
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
                SnackBar.error("Ошибка добавления группы ${e.message}")
            }
    }

    fun remove(item: AlbumDetails) {
        Timber.i("removeAlbum() id:${item.id} name:${item.title}")
        albumDb.delete(item.id)
            .onSuccess {
                SnackBar.info("Альбом удален")
                list.remove(item)
            }
            .onFailure { e ->
                SnackBar.error("Ошибка удаления группы ${e.message}")
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refresh() {
        albumDb.refresh()
    }


}