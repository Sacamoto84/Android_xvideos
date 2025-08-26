package com.client.common.collection.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow

abstract class ISavedLCollection<T> {

    var collectionList = mutableStateListOf<CollectionEntity<T>>()

    var collectionVisibleDialog by mutableStateOf(false)
    var collectionItemGifInfo by mutableStateOf<T?>(null)
    var collectionVisibleDialogCreateNew by mutableStateOf(false)
    var selectedCollection = MutableStateFlow<String?>(null)

    abstract fun addCollection(item: T, collectionName: String)

    abstract fun deleteItemFromCollection(itemId: String, collectionName: String)

    abstract fun deleteCollection(collectionName: String)

    abstract fun createCollection(collectionName: String)

    abstract fun refreshCollectionList()
}
