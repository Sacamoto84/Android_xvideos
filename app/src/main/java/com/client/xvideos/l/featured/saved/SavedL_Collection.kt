package com.client.xvideos.l.featured.saved

import com.client.xvideos.common.collection.model.ISavedLCollection
import com.client.xvideos.common.util.toMD5
import com.client.xvideos.l.model.PicsDetails
import com.redgifs.common.snackBar.SnackBarEvent
import kotlinx.coroutines.DelicateCoroutinesApi

class SavedL_Collection(val snackBarEvent : SnackBarEvent): ISavedLCollection<PicsDetails>(PicsDetails::class.java) {

    override fun addCollection(item: PicsDetails, collectionName: String) {
        println("!!! addCollection() item:${item.url_to_original?.toMD5() ?: "0"} collectionName:$collectionName")
        collectionDb.insert(item.url_to_original?.toMD5() ?: "0", collectionName, item)
        refreshCollectionList()
    }

    override fun deleteItemFromCollection(itemId: String, collectionName: String) {
        println("!!! deleteItemFromCollection() item:$itemId collectionName:$collectionName")
        collectionDb.deleteItem(itemId, collectionName)
            .onSuccess {
                snackBarEvent.success("GIF удален из коллекции $collectionName")
                refreshCollectionList()
            }
            .onFailure { e -> snackBarEvent.error("Ошибка удаления GIF из коллекции $collectionName ${e.message}") }
    }

    override fun deleteCollection(collectionName: String) {
        collectionDb.deleteCollection(collectionName)
            .onSuccess {
                snackBarEvent.success("Коллекция $collectionName удалена")
                refreshCollectionList()
            }
            .onFailure { e -> snackBarEvent.error("Ошибка удаления коллекции $collectionName ${e.message}") }
    }

    override fun createCollection(collectionName: String) {
        println("!!! createCollection() collectionName:$collectionName")
        collectionDb.create(collectionName)
            .onSuccess {
                snackBarEvent.success("Коллекция $collectionName создана")
                refreshCollectionList()
            }
            .onFailure { e -> snackBarEvent.error("Ошибка создания коллекции $collectionName ${e.message}") }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun refreshCollectionList() {
        val a = collectionDb.readAllCollections()
        if (a.isSuccess) {
            collectionList.clear()
            collectionList.addAll(a.getOrThrow())
        } else {
            snackBarEvent.error("Ошибка чтения коллекций ${a.exceptionOrNull()?.message}")
        }
    }

}