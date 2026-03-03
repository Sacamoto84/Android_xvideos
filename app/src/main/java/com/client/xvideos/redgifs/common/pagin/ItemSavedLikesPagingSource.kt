package com.client.xvideos.redgifs.common.pagin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.client.xvideos.redgifs.model.GifsInfo
import com.client.xvideos.redgifs.model.Order
import com.client.xvideos.redgifs.common.saved.SavedRed
import timber.log.Timber

class ItemSavedLikesPagingSource (val order : Order, val savedRed: SavedRed): PagingSource<Int, GifsInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int,  GifsInfo> {
        return try {
            Timber.i("!!! >>>ItemSavedLikesPagingSource::load() sortTop:$order")
            LoadResult.Page( data = savedRed.likes.list.toList(), prevKey = null, nextKey = null )
        } catch (e: Exception) {
            Timber.e("!!! >>>ItemSavedLikesPagingSource load() Ошибка = ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GifsInfo>): Int? { return null }
}