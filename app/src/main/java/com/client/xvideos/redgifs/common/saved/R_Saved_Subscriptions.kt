package com.client.xvideos.redgifs.common.saved

import androidx.compose.runtime.mutableStateListOf
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.fileDB.FileDB
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.redgifs.model.GifsInfo
import com.client.xvideos.redgifs.model.MediaType
import com.client.xvideos.redgifs.model.UserInfo
import com.client.xvideos.redgifs.network.api.RedApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class R_Saved_Subscriptions(
    val scope: CoroutineScope,
    val redApi: RedApi,
) {

    val creatorDb = FileDB(AppPath.r_subscriptions, "subscriptions", UserInfo::class.java)

    /**
     * Список авторов на которых подписаны
     */
    var listCreators = creatorDb.list



    fun add(item: UserInfo) {
        println("!!! add subscriptions() id:${item.username}")
        creatorDb.insert(item.username, item)
            .onSuccess {
                SnackBar.success("Автор добавлен")
                listCreators.add(item)
            }
            .onFailure { e ->
                SnackBar.error("Ошибка добавления Автора ${e.message}")
            }
    }

    fun remove(username: String) {
        println("!!! remove subscriptions() id:${username} ")
        creatorDb.delete(username)
            .onSuccess {
                SnackBar.info("Автор удален")
                //creatorsList.remove(item)
                refresh()
            }
            .onFailure { e -> SnackBar.error("Ошибка удаления Автора ${e.message}") }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refresh() {
        creatorDb.refresh()
    }


    private suspend fun read50LastItem(name: String): List<GifsInfo> {
        return redApi.searchCreator(userName = name, count = 50, type = MediaType.ALL).getOrThrow().gifs
    }

    suspend fun refreshSubcription() : List<GifsInfo>{
        val res  = mutableListOf<GifsInfo>()
        listCreators.forEach {
            try { res.addAll(read50LastItem(it.username)) }
            catch (e: Exception){ }
        }
        return res
    }


}