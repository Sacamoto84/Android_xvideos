package com.redgifs.common.saved

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.client.common.AppPath
import com.client.common.collection.CollectionDB
import com.client.common.collection.model.CollectionEntity
import com.redgifs.common.snackBar.SnackBarEvent
import com.redgifs.model.GifsInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow

class SavedRed_Collection(val snackBarEvent : SnackBarEvent) {

    var collectionList = mutableStateListOf<CollectionEntity<GifsInfo>>()


    val collectionDb = CollectionDB<GifsInfo>(AppPath.collection_red, GifsInfo::class.java)

    var collectionVisibleDialog by mutableStateOf(false)  //║ Показ диалога на добавление в блок лист
    var collectionItemGifInfo by mutableStateOf<GifsInfo?>(null)

    var collectionVisibleDialogCreateNew by mutableStateOf(false)  //║ Показ диалога на добавление в блок лист

    var selectedCollection = MutableStateFlow<String?>(null)//mutableStateOf<String?>(null)



    fun addCollection(item: GifsInfo, collectionName: String) {
        println("!!! addCollection() item:${item.id} collectionName:$collectionName")
        //collectionItemSaveToDisk(item, collectionName)
        collectionDb.insert(item.id, collectionName, item)
        refreshCollectionList()
    }

    fun deleteItemFromCollection(item: GifsInfo, collectionName: String) {
        println("!!! deleteItemFromCollection() item:${item.id} collectionName:$collectionName")
        collectionDb.deleteItem(item.id, collectionName)
            .onSuccess {
                snackBarEvent.success("GIF удален из коллекции $collectionName")
                refreshCollectionList()
            }
            .onFailure { e ->
                snackBarEvent.error("Ошибка удаления GIF из коллекции $collectionName ${e.message}")
            }
    }

    fun deleteCollection(collectionName: String) {
            collectionDb.deleteCollection(collectionName)
            .onSuccess {
                snackBarEvent.success("Коллекция $collectionName удалена")
                refreshCollectionList()
            }
            .onFailure { e ->
                snackBarEvent.error("Ошибка удаления коллекции $collectionName ${e.message}")
            }
    }

    fun createCollection(collectionName: String) {
        println("!!! createCollection() collectionName:$collectionName")
            collectionDb.create(collectionName)
            .onSuccess {
                snackBarEvent.success("Коллекция $collectionName создана")
                refreshCollectionList()
            }
            .onFailure { e ->
                snackBarEvent.error("Ошибка создания коллекции $collectionName ${e.message}")
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refreshCollectionList() {
        val a = collectionDb.readAllCollections()
        if (a.isSuccess) {
            collectionList.clear()
            collectionList.addAll(a.getOrThrow())
        } else {
            snackBarEvent.error("Ошибка чтения коллекций ${a.exceptionOrNull()?.message}")
        }
    }

}