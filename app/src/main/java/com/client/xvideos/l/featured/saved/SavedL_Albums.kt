package com.client.xvideos.l.featured.saved

import com.client.common.AppPath
import com.client.common.fileDB.FileDB
import com.client.xvideos.l.model.Album
import com.redgifs.common.snackBar.SnackBarEvent
import kotlinx.coroutines.DelicateCoroutinesApi

class SavedL_Albums (val snackBarEvent : SnackBarEvent){

        val albumDb = FileDB(AppPath.albums_l, "album", Album::class.java)
        val list = albumDb.list

        fun add(item: Album) {
            println("!!! addAlbum() id:${item.id} name:${item.title}")
            albumDb.insert(item.id, item)
                .onSuccess {
                    snackBarEvent.info("Группа добавлена")
                    list.add(item)
                }
                .onFailure { e ->
                    snackBarEvent.error("Ошибка добавления группы ${e.message}")
                }
        }

        fun remove(item: Album) {
            println("!!! removeAlbum() id:${item.id} name:${item.title}")
            albumDb.delete(item.id)
                .onSuccess {
                    snackBarEvent.info("Группа удалена")
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