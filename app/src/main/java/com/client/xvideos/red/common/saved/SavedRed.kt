package com.client.xvideos.red.common.saved

import androidx.compose.runtime.mutableStateListOf
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.NichesInfo
import com.client.xvideos.feature.redgifs.types.UserInfo
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

    fun refreshLikesList() {
        likesList.clear()
        val a = getAllLikesFromDisk()
        likesList.addAll(a)
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

    fun refreshCreatorsList() {
        creatorsList.clear()
        val a = getAllCreatorsFromDisk()
        creatorsList.addAll(a)
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

    fun refreshNichesList() {
        nichesList.clear()
        val a = getAllNichesFromDisk()
        nichesList.addAll(a)
    }

}



