package com.client.xvideos.redgifs.common.search

import com.client.xvideos.common.di.ApplicationScope
import com.client.xvideos.common.room.dao.r.R_SearchHistoryNichesDao
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.redgifs.common.saved.SavedRed
import com.client.xvideos.redgifs.model.tag.TagSuggestion
import com.client.xvideos.redgifs.network.api.RedApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class R_SearchNiches @Inject constructor(
    dao: R_SearchHistoryNichesDao,
    val savedRed: SavedRed,
    val redApi: RedApi,
    @ApplicationScope scope: CoroutineScope
) : ISearchTemplate(scope, dao) {

    init{
        scope.launch {
            searchText.collect {
                try {
                    if (it != ""){
                        val a = redApi.searchNichesShort(it).map { itt -> TagSuggestion( text = itt.name, gifs = itt.gifs, type = "type" ) }
                        searchTextSuggestions.value = a
                    }
                }catch (e: Exception){
                    SnackBar.error(e.localizedMessage!!)
                }

            }
        }
    }

}

