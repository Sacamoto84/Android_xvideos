package com.client.xvideos.red.common.pagin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.red.common.saved.SavedRed
import timber.log.Timber


class ItemSavedLikesPagingSource (val order : Order): PagingSource<Int, GifsInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int,  GifsInfo> {

        val page = params.key ?: 1 // API нумерует страницы с 1

        return try {
            Timber.d("!!! ItemLikesPagingSource::load() page = $page sortTop:$order")

            val gif1 = if(order != Order.FORCE_TEMP) SavedRed.likesList.toList() else emptyList()

            LoadResult.Page(
                data = gif1,
                prevKey = null,
                nextKey = null
            )

        } catch (e: Exception) {
            Timber.e("!!! ItemLikesPagingSource load() page = $page Ошибка = ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GifsInfo>): Int? {
        return null
    }
}