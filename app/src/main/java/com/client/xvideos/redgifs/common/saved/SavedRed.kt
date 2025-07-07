package com.client.xvideos.redgifs.common.saved

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.client.xvideos.AppPath
import com.client.xvideos.feature.fileDB.FileDB
import com.client.xvideos.redgifs.common.saved.collection.collectionCreateToDisk
import com.client.xvideos.redgifs.common.saved.collection.collectionDeleteFromDisk
import com.client.xvideos.redgifs.common.saved.collection.collectionItemDeleteFromDisk
import com.client.xvideos.redgifs.common.saved.collection.collectionItemSaveToDisk
import com.client.xvideos.redgifs.common.saved.collection.model.CollectionEntity
import com.client.xvideos.redgifs.common.saved.collection.readAllCollections
import com.client.xvideos.redgifs.common.snackBar.SnackBarEvent
import com.client.xvideos.redgifs.network.api.RedApi_Tags
import com.client.xvideos.redgifs.network.types.GifsInfo
import com.client.xvideos.redgifs.network.types.NichesInfo
import com.client.xvideos.redgifs.network.types.UserInfo
import com.client.xvideos.redgifs.network.types.tag.TagInfo
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object SavedRed {

    /////////////////////////////////////////////////////////////////////////////////////////////
    val likesDb = FileDB<GifsInfo>(AppPath.likes_red, "likes", object : TypeToken<GifsInfo>() {}.type)

    var likesList = likesDb.list

    fun addLikes(item: GifsInfo) {
        println("!!! addLikes() id:${item.id} userName:${item.userName} url:${item.urls.hd}")
        likesDb.insert(item.id, item)
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
        likesDb.delete(item.id)
            .onSuccess { SnackBarEvent.info("Unlike") }
            .onFailure { e ->  SnackBarEvent.error("Ошибка удаления лайка ${e.message}") }
        refreshLikesList()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refreshLikesList() {
        likesDb.refresh()
    }
    /////////////////////////////////////////////////////////////////////////////////////////////

    //var creatorsList = mutableStateListOf<UserInfo>()

    val creatorDb = FileDB<UserInfo>(AppPath.creators_red, "creator", object : TypeToken<UserInfo>() {}.type)

    var creatorsList = creatorDb.list

    fun addCreator(item: UserInfo) {
        println("!!! addCreator() id:${item.username}")
        creatorDb.insert(item.username, item)
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
        creatorDb.delete(username)
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
        creatorDb.refresh()
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    val nichesDb = FileDB<NichesInfo>(AppPath.niches_red, "niches", object : TypeToken<NichesInfo>() {}.type)
    val nichesList = nichesDb.list

    fun addNiches(item: NichesInfo) {
        println("!!! addNiches() id:${item.id} name:${item.name}")
        nichesDb.insert(item.id, item)
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
        nichesDb.delete(item.id)
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
        nichesDb.refresh()
    }
    /////////////////////////////////////////////////////////////////////////////////////////////

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

    var tagsList = listOf<TagInfo>()

    @OptIn(DelicateCoroutinesApi::class)
    fun refreshTagList() { GlobalScope.launch(Dispatchers.IO) { tagsList = RedApi_Tags.getTags().tags } }

}



