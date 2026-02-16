package com.client.xvideos.redgifs.common.saved

import com.client.xvideos.common.AppPath
import com.client.xvideos.common.collectionDB.model.ISavedLCollection
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.redgifs.model.GifsInfo
import kotlinx.coroutines.DelicateCoroutinesApi

class R_Saved_Collection : ISavedLCollection<GifsInfo>(
    AppPath.r_collection,
    GifsInfo::class.java
)
{

    override fun addCollection(item: GifsInfo, collectionName: String) {
        println("!!! addCollection() item:${item.id} collectionName:$collectionName")
        collectionDb.insert(item.id, collectionName, item)
        refreshCollectionList()
    }

    override fun deleteItemFromCollection(itemId: String, collectionName: String) {
        println("!!! deleteItemFromCollection() item:${itemId} collectionName:$collectionName")
        collectionDb.deleteItem(itemId, collectionName)
            .onSuccess {
                SnackBar.success("GIF удален из коллекции $collectionName")
                refreshCollectionList()
            }
            .onFailure { e -> SnackBar.error("Ошибка удаления GIF из коллекции $collectionName ${e.message}") }
    }

    override fun deleteCollection(collectionName: String) {
            collectionDb.deleteCollection(collectionName)
            .onSuccess {
                SnackBar.success("Коллекция $collectionName удалена")
                refreshCollectionList()
            }
            .onFailure { e -> SnackBar.error("Ошибка удаления коллекции $collectionName ${e.message}") }
    }

    override fun createCollection(collectionName: String) {
        println("!!! createCollection() collectionName:$collectionName")
            collectionDb.create(collectionName)
            .onSuccess {
                SnackBar.success("Коллекция $collectionName создана")
                refreshCollectionList()
            }
            .onFailure { e ->
                SnackBar.error("Ошибка создания коллекции $collectionName ${e.message}")
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun refreshCollectionList() {
        val a = collectionDb.readAllCollections()
        if (a.isSuccess) {
            collectionList.clear()
            collectionList.addAll(a.getOrThrow())
        } else {
            SnackBar.error("Ошибка чтения коллекций ${a.exceptionOrNull()?.message}")
        }
    }

}