package com.client.xvideos.common.collectionDB.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.client.xvideos.common.collectionDB.CollectionDB
import kotlinx.coroutines.flow.MutableStateFlow

abstract class ISavedLCollection<T>(
    path: String,
    clazz: Class<T>
){
    //private val type = TypeToken.getParameterized(List::class.java, clazz).type

    val collectionDb = CollectionDB<T>(path, clazz)

    var collectionList = mutableStateListOf<CollectionEntity<T>>()


    //----- Диалоги -----
    /**
     * Отобразить диалог коллекции
     */
    var visibleDialog by mutableStateOf(false)

    /**
     * Отобразить диалог создания новой коллекции
     */
    var visibleDialogCreateNew by mutableStateOf(false)
    //-------------------


    var collectionItemGifInfo by mutableStateOf<T?>(null)



    var selectedCollection = MutableStateFlow<String?>(null)

    abstract fun addCollection(item: T, collectionName: String)

    abstract fun deleteItemFromCollection(itemId: String, collectionName: String)

    abstract fun deleteCollection(collectionName: String)

    abstract fun createCollection(collectionName: String)

    abstract fun refreshCollectionList()
}
