package com.client.xvideos.redgifs.common.search

import com.client.xvideos.common.di.ApplicationScope
import com.client.xvideos.common.room.dao.r.R_SearchHistoryExplorerDao
import com.client.xvideos.redgifs.network.api.RedApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class SearchRed @Inject constructor(
    @ApplicationScope scope: CoroutineScope,
    dao: R_SearchHistoryExplorerDao,

    //val savedRed: SavedRed,

    redApiIn: Provider<RedApi>,

) : ISearchTemplate(scope, dao) {

    val  redApi = redApiIn.get()

    init {
        scope.launch {
            searchText.collect {
                try {
                    val request = if (it == "") " " else it
                    val a = redApi.getTagSuggestions(request)
                    searchTextSuggestions.value = a.getOrThrow()
                }catch (e: Exception){
                    Timber.e("!!! SearchRed searchText.collect ${e.localizedMessage}")
                }
            }
        }
    }

    //Dao
    override fun add(text: String) = scope.launch {
        dao.insertAndTrim(R_SearchHistoryEntity(text = text))
    }

    override fun delete(text: String) = scope.launch {
        dao.deleteByTexts(text = text)
    }

    override fun clear() = scope.launch { dao.deleteAll() }

}


