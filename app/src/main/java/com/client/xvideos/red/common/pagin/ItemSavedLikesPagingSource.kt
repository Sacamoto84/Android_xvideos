package com.client.xvideos.red.common.pagin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.red.common.saved.SavedRed
import timber.log.Timber


class ItemSavedLikesPagingSource (val order : Order): PagingSource<Int, GifsInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int,  GifsInfo> {

        return try {
            Timber.d("!!! >>>ItemLikesPagingSource::load() sortTop:$order")

            //val gif1 = if(order != Order.FORCE_TEMP) SavedRed.likesList.toList() else emptyList()

            LoadResult.Page(
                data = SavedRed.likesList.toList(),
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