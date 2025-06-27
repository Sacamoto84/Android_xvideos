package com.client.xvideos.red.common.saved

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.NichesInfo
import com.client.xvideos.feature.redgifs.types.UserInfo
import com.client.xvideos.red.common.saved.collection.collectionCreateToDisk
import com.client.xvideos.red.common.saved.collection.collectionDeleteFromDisk
import com.client.xvideos.red.common.saved.collection.collectionItemSaveToDisk
import com.client.xvideos.red.common.saved.collection.model.CollectionEntity
import com.client.xvideos.red.common.saved.collection.readAllCollections
import com.client.xvideos.red.common.saved.creators.creatorsItemRemoveFromDisk
import com.client.xvideos.red.common.saved.creators.creatorsItemSaveToDisk
import com.client.xvideos.red.common.saved.creators.getAllCreatorsFromDisk
import com.client.xvideos.red.common.saved.likes.getAllLikesFromDisk
import com.client.xvideos.red.common.saved.likes.likesItemRemoveFromDisk
import com.client.xvideos.red.common.saved.likes.likesItemSaveToDisk
import com.client.xvideos.red.common.saved.niches.getAllNichesFromDisk
import com.client.xvideos.red.common.saved.niches.nickesItemRemoveFromDisk
import com.client.xvideos.red.common.saved.niches.nickesItemSaveToDisk
import com.client.xvideos.red.common.snackBar.SnackBarEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SavedRed {

    /////////////////////////////////////////////////////////////////////////////////////////////
    var likesList = mutableStateListOf<GifsInfo>()

    fun addLikes(item: GifsInfo) {
        println("!!! addLikes() id:${item.id} userName:${item.userName} url:${item.urls.hd}")
        likesItemSaveToDisk(item)
            .onSuccess {
                SnackBarEvent.success("Like")
                likesList.add(item)
            }
            .onFailure { e ->
                SnackBarEvent.error("Ошибка добавления лайка ${e.message}")
            }
    }

    fun removeLikes(item: GifsInfo) {
        println("!!! removeLikes() id:${item.id} userName:${item.userName} url:${item.urls.hd}")

        likesItemRemoveFromDisk(item)
            .onSuccess {
                SnackBarEvent.info("Unlike")
                likesList.remove(item)
            }
            .onFailure { e ->
                SnackBarEvent.error("Ошибка удаления лайка ${e.message}")
            }

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refreshLikesList() {
        GlobalScope.launch(Dispatchers.IO) {
            val a = getAllLikesFromDisk()
            withContext(Dispatchers.Main) {
                likesList.clear()
                likesList.addAll(a)
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////

    var creatorsList = mutableStateListOf<UserInfo>()

    fun addCreator(item: UserInfo) {
        println("!!! addCreator() id:${item.username}")
        creatorsItemSaveToDisk(item)
            .onSuccess {
                SnackBarEvent.success("Автор добавлен")
                creatorsList.add(item)
            }
            .onFailure { e ->
                SnackBarEvent.error("Ошибка добавления Автора ${e.message}")
            }
    }

    fun removeCreator(username: String) {
        println("!!! removeCreator() id:${username} ")
        creatorsItemRemoveFromDisk(username)
            .onSuccess {
                SnackBarEvent.info("Автор удален")
                //creatorsList.remove(item)
                refreshCreatorsList()
            }
            .onFailure { e ->
                SnackBarEvent.error("Ошибка удаления Автора ${e.message}")
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refreshCreatorsList() {
        GlobalScope.launch(Dispatchers.IO) {
            val a = getAllCreatorsFromDisk()
            withContext(Dispatchers.Main) {
                creatorsList.clear()
                creatorsList.addAll(a)
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    var nichesList = mutableStateListOf<NichesInfo>()

    fun addNiches(item: NichesInfo) {
        println("!!! addNiches() id:${item.id} name:${item.name}")
        nickesItemSaveToDisk(item)
            .onSuccess {
                SnackBarEvent.info("Группа добавлена")
                nichesList.add(item)
            }
            .onFailure { e ->
                SnackBarEvent.error("Ошибка добавления группы ${e.message}")
            }
    }

    fun removeNiches(item: NichesInfo) {
        println("!!! removeNiches() id:${item.id} name:${item.name}")
        nickesItemRemoveFromDisk(item.id)
            .onSuccess {
                SnackBarEvent.info("Группа удалена")
                nichesList.remove(item)
            }
            .onFailure { e ->
                SnackBarEvent.error("Ошибка удаления группы ${e.message}")
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refreshNichesList() {
        GlobalScope.launch(Dispatchers.IO) {
            val a = getAllNichesFromDisk()
            withContext(Dispatchers.Main) {
                nichesList.clear()
                nichesList.addAll(a)
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////

    var collectionList = mutableStateListOf<CollectionEntity>()

    var collectionVisibleDialog by mutableStateOf(false)  //║ Показ диалога на добавление в блок лист
    var collectionItemGifInfo by mutableStateOf<GifsInfo?>(null)

    var collectionVisibleDialogCreateNew by mutableStateOf(false)  //║ Показ диалога на добавление в блок лист

    fun addCollection(item: GifsInfo, collectionName: String) {
        println("!!! addCollection() item:${item.id} collectionName:$collectionName")
        val a = collectionItemSaveToDisk(item, collectionName)
        refreshCollectionList()
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
        GlobalScope.launch(Dispatchers.IO) {
            val a = readAllCollections()
            withContext(Dispatchers.Main) {
                if (a.isSuccess) {
                    collectionList.clear()
                    collectionList.addAll(a.getOrThrow())
                } else {
                    SnackBarEvent.error("Ошибка чтения коллекций ${a.exceptionOrNull()?.message}")
                }
            }
        }
    }


}



