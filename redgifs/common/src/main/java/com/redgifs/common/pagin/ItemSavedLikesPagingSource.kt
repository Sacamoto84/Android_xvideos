package com.redgifs.common.pagin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.redgifs.model.GifsInfo
import com.redgifs.model.Order
import com.redgifs.common.saved.SavedRed
import timber.log.Timber


class ItemSavedLikesPagingSource (val order : Order, val savedRed: SavedRed): PagingSource<Int, GifsInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int,  GifsInfo> {

        return try {
            Timber.d("!!! >>>ItemLikesPagingSource::load() sortTop:$order")

            //val gif1 = if(order != Order.FORCE_TEMP) SavedRed.likesList.toList() else emptyList()

            LoadResult.Page(
                data = savedRed.likes.list.toList(),
                prevKey = null,
                nextKey = null
            )

        } catch (e: Exception) {
            Timber.e("!!! >>>ItemLikesPagingSource load() Ошибка = ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GifsInfo>): Int? {
        return null
    }
}