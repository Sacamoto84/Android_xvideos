package com.client.xvideos.l.featured.saved

import com.client.xvideos.common.collectionDB.model.ISavedLCollection
import com.client.xvideos.common.eventBus.snackBarError
import com.client.xvideos.common.eventBus.snackBarSuccess
import com.client.xvideos.common.util.toMD5
import com.client.xvideos.l.model.PicsDetails
import kotlinx.coroutines.DelicateCoroutinesApi

class SavedL_Collection(): ISavedLCollection<PicsDetails>(PicsDetails::class.java) {

    override fun addCollection(item: PicsDetails, collectionName: String) {
        println("!!! addCollection() item:${item.url_to_original?.toMD5() ?: "0"} collectionName:$collectionName")
        collectionDb.insert(item.url_to_original?.toMD5() ?: "0", collectionName, item)
        refreshCollectionList()
    }

    override fun deleteItemFromCollection(itemId: String, collectionName: String) {
        println("!!! deleteItemFromCollection() item:$itemId collectionName:$collectionName")
        collectionDb.deleteItem(itemId, collectionName)
            .onSuccess {
                snackBarSuccess("GIF удален из коллекции $collectionName")
                refreshCollectionList()
            }
            .onFailure { e -> snackBarError("Ошибка удаления GIF из коллекции $collectionName ${e.message}") }
    }

    override fun deleteCollection(collectionName: String) {
        collectionDb.deleteCollection(collectionName)
            .onSuccess {
                snackBarSuccess("Коллекция $collectionName удалена")
                refreshCollectionList()
            }
            .onFailure { e -> snackBarError("Ошибка удаления коллекции $collectionName ${e.message}") }
    }

    override fun createCollection(collectionName: String) {
        println("!!! createCollection() collectionName:$collectionName")
        collectionDb.create(collectionName)
            .onSuccess {
                snackBarSuccess("Коллекция $collectionName создана")
                refreshCollectionList()
            }
            .onFailure { e -> snackBarError("Ошибка создания коллекции $collectionName ${e.message}") }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun refreshCollectionList() {
        val a = collectionDb.readAllCollections()
        if (a.isSuccess) {
            collectionList.clear()
            collectionList.addAll(a.getOrThrow())
        } else {
            snackBarError("Ошибка чтения коллекций ${a.exceptionOrNull()?.message}")
        }
    }

}