package com.client.xvideos.screens_red.common.saved

import androidx.compose.runtime.mutableStateListOf
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.screens_red.common.saved.likes.getAllLikesFromDisk
import com.client.xvideos.screens_red.common.saved.likes.likesItemRemoveFromDisk
import com.client.xvideos.screens_red.common.saved.likes.likesItemSaveToDisk
import com.client.xvideos.util.Toast

object SavedRed {

    var likesList = mutableStateListOf<GifsInfo>()


    fun addLikes(item: GifsInfo) {
        println("!!! addLikes() id:${item.id} userName:${item.userName} url:${item.urls.hd}")
        val res = likesItemSaveToDisk(item)
        if (res.isSuccess) {
            Toast("Лайк")
            likesList.add(item)
        }
    }

    fun removeLikes(item: GifsInfo) {
        println("!!! removeLikes() id:${item.id} userName:${item.userName} url:${item.urls.hd}")
        val res = likesItemRemoveFromDisk(item)
        if (res.isSuccess) {
            Toast("Удалил лайк")
            likesList.remove(item)
        }
    }

    fun refreshLikesList() {
        likesList.clear()
        val a = getAllLikesFromDisk()
        likesList.addAll(a)
    }


}



