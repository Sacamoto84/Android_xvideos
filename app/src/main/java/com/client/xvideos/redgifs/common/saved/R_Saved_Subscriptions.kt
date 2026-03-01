package com.client.xvideos.redgifs.common.saved

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.fileDB.FileDB
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.redgifs.model.GifsInfo
import com.client.xvideos.redgifs.model.MediaType
import com.client.xvideos.redgifs.network.api.RedApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import timber.log.Timber


data class SelectedCreator(val name: String, var select: Boolean)

class R_Saved_Subscriptions(
    val scope: CoroutineScope,
    val redApi: RedApi,
) {

    private val creatorDb = FileDB(AppPath.r_subscriptions, "subscriptions", String::class.java)

    /**
     * Список авторов на которых подписаны
     */
    var listCreators = creatorDb.list

    val selectedListCreator = mutableStateListOf<SelectedCreator>()


    init {
        refresh()
    }

    fun add(item: String) {
        println("!!! add subscriptions() id:${item}")
        creatorDb.insert(item, item)
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


    suspend fun refreshSubscription() : List<GifsInfo>{
        val res  = mutableListOf<GifsInfo>()

        val a = listCreators.map { SelectedCreator(it, true) }

        selectedListCreator.clear()
        selectedListCreator.addAll(a)

        selectedListCreator.filter { it.select }.forEach {
            try {
                res.addAll(read50LastItem(it.name))
            }
            catch (e: Exception){
                Timber.e(e)
            }
        }
        return res
    }


}