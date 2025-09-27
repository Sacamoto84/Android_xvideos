package com.client.xvideos.xvideos.feature.saved

import com.client.xvideos.common.AppPath
import com.client.xvideos.common.eventBus.snackBarError
import com.client.xvideos.common.eventBus.snackBarInfo
import com.client.xvideos.common.fileDB.FileDB
import com.client.xvideos.xvideos.model.ItemsX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.onSuccess

class SavedX_Favorites(val scope: CoroutineScope) {

    private val favoritesDb = FileDB(AppPath.favorites_x, "ItemsX", ItemsX::class.java)

    val list = favoritesDb.list

    init {
        refresh() // Перенести в сплеш TODO
    }

    fun add(item: ItemsX) {
        println("!!! add favorite id:${item.id} name:${item.title}")
        favoritesDb.insert(item.id.toString(), item)
            .onSuccess {
                snackBarInfo("Добавлено в избранное")
                list.add(item)
            }
            .onFailure { e ->
                snackBarError("Ошибка добавления группы ${e.message}")
            }
    }

    fun remove(item: ItemsX) {
        println("!!! removeAlbum() id:${item.id} name:${item.title}")
        favoritesDb.delete(item.id.toString())
            .onSuccess {
                snackBarInfo("Удален из избранного")
                list.remove(item)
            }
            .onFailure { e ->
                snackBarError("Ошибка удаления группы ${e.message}")
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refresh() {
        favoritesDb.refresh()
    }

}