package com.client.xvideos.x.feature.saved

import com.client.xvideos.common.AppPath
import com.client.xvideos.common.fileDB.FileDB
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.x.model.ItemsX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.onSuccess

class SavedX_Favorites(val scope: CoroutineScope) {

    private val favoritesDb = FileDB(AppPath.x_favorites, "ItemsX", ItemsX::class.java)

    val list = favoritesDb.list

    init {
        refresh() // Перенести в сплеш TODO
    }

    fun add(item: ItemsX) {
        println("!!! add favorite id:${item.id} name:${item.title}")
        favoritesDb.insert(item.id.toString(), item)
            .onSuccess {
                SnackBar.info("Добавлено в избранное")
                list.add(item)
            }
            .onFailure { e ->
                SnackBar.error("Ошибка добавления группы ${e.message}")
            }
    }

    fun remove(item: ItemsX) {
        println("!!! removeAlbum() id:${item.id} name:${item.title}")
        favoritesDb.delete(item.id.toString())
            .onSuccess {
                SnackBar.info("Удален из избранного")
                list.remove(item)
            }
            .onFailure { e ->
                SnackBar.error("Ошибка удаления группы ${e.message}")
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refresh() {
        favoritesDb.refresh()
    }

}