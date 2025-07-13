package com.redgifs.common.pagin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.redgifs.common.snackBar.SnackBarEvent
import com.redgifs.network.api.RedApi_Explorer
import com.redgifs.model.Niche
import com.redgifs.model.Order
import com.redgifs.network.api.RedApi
import timber.log.Timber

class ItemExplorerNailsPagingSource (val order : Order, val redApi: RedApi, val snackBarEvent: SnackBarEvent): PagingSource<Int, Niche>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int,  Niche> {

        val page = params.key ?: 1 // API нумерует страницы с 1

        return try {
            Timber.d("!!! ItemExplorerNailsPagingSource::load() page = $page sortTop:$order")

            if (order == Order.FORCE_TEMP) {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = page
                )
            }

            val response = redApi.explorer.getExplorerNiches(order, page = page)

            val isEndReached = response.niches.isEmpty() // или, если ты знаешь, что сервер вернул всё

            val nextKey = if (isEndReached) { null } else { page + 1 }

            Timber.d("!!! load() a.gif.size = ${response.niches.size}")

            LoadResult.Page(
                data = response.niches,
                prevKey = null,
                nextKey = nextKey
            )

        } catch (e: Exception) {
            Timber.e("!!! ItemExplorerNailsPagingSource load() page = $page Ошибка = ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Niche>): Int? {
        return null
    }
}