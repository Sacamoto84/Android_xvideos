package com.client.xvideos.redgifs.common.saved

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.client.xvideos.redgifs.common.saved.collection.collectionCreateToDisk
import com.client.xvideos.redgifs.common.saved.collection.collectionDeleteFromDisk
import com.client.xvideos.redgifs.common.saved.collection.collectionItemDeleteFromDisk
import com.client.xvideos.redgifs.common.saved.collection.collectionItemSaveToDisk
import com.client.xvideos.redgifs.common.saved.collection.model.CollectionEntity
import com.client.xvideos.redgifs.common.saved.collection.readAllCollections
import com.client.xvideos.redgifs.common.snackBar.SnackBarEvent
import com.client.xvideos.redgifs.network.types.GifsInfo
import kotlinx.coroutines.DelicateCoroutinesApi

class SavedRed_Collection {

    var collectionList = mutableStateListOf<CollectionEntity>()

    var collectionVisibleDialog by mutableStateOf(false)  //║ Показ диалога на добавление в блок лист
    var collectionItemGifInfo by mutableStateOf<GifsInfo?>(null)

    var collectionVisibleDialogCreateNew by mutableStateOf(false)  //║ Показ диалога на добавление в блок лист

    var selectedCollection by mutableStateOf<String?>(null)

    fun addCollection(item: GifsInfo, collectionName: String) {
        println("!!! addCollection() item:${item.id} collectionName:$collectionName")
        collectionItemSaveToDisk(item, collectionName)
        refreshCollectionList()
    }

    fun deleteItemFromCollection(item: GifsInfo, collectionName: String) {
        println("!!! deleteItemFromCollection() item:${item.id} collectionName:$collectionName")
        collectionItemDeleteFromDisk(item.id, collectionName)
            .onSuccess {
                SnackBarEvent.success("GIF удален из коллекции $collectionName")
                refreshCollectionList()
            }
            .onFailure { e ->
                SnackBarEvent.error("Ошибка удаления GIF из коллекции $collectionName ${e.message}")
            }
    }

    fun deleteCollection(collectionName: String) {
        collectionDeleteFromDisk(collectionName)
            .onSuccess {
                SnackBarEvent.success("Коллекция $collectionName удалена")
                refreshCollectionList()
            }
            .onFailure { e ->
                SnackBarEvent.error("Ошибка удаления коллекции $collectionName ${e.message}")
            }
    }

    fun createCollection(collectionName: String) {
        println("!!! createCollection() collectionName:$collectionName")
        collectionCreateToDisk(collectionName)
            .onSuccess {
                SnackBarEvent.success("Коллекция $collectionName создана")
                refreshCollectionList()
            }
            .onFailure { e ->
                SnackBarEvent.error("Ошибка создания коллекции $collectionName ${e.message}")
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refreshCollectionList() {
        val a = readAllCollections()
        if (a.isSuccess) {
            collectionList.clear()
            collectionList.addAll(a.getOrThrow())
        } else {
            SnackBarEvent.error("Ошибка чтения коллекций ${a.exceptionOrNull()?.message}")
        }
    }

}