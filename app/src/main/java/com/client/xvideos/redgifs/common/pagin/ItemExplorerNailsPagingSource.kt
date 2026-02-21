package com.client.xvideos.redgifs.common.pagin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.client.xvideos.redgifs.common.saved.R_Saved_NichesCaches
import com.client.xvideos.redgifs.model.Niche
import com.client.xvideos.redgifs.model.Order
import com.client.xvideos.redgifs.network.api.RedApi
import timber.log.Timber

class ItemExplorerNailsPagingSource (val order : Order, val textNiches : String, val cache : R_Saved_NichesCaches): PagingSource<Int, Niche>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int,  Niche> {

        val page = params.key ?: 1 // API нумерует страницы с 1

        return try {
            Timber.d("!!! ItemExplorerNailsPagingSource::load() page = $page sortTop:$order")

            val response = cache.list.toList()

            // Фильтрация по подстроке в name
            val filtered = if (textNiches.isNotBlank()) {
                response.filter { it.name.contains(textNiches, ignoreCase = true) }
            } else {
                response
            }

            //order
            val res = when(order){
                Order.NICHES_SUBSCRIBERS_D -> {filtered.sortedByDescending { it.subscribers }}
                Order.NICHES_POST_D -> {filtered.sortedByDescending  { it.gifs}}
                Order.NICHES_SUBSCRIBERS_A -> {filtered.sortedBy{ it.subscribers }}
                Order.NICHES_POST_A -> {filtered.sortedBy{ it.gifs}}
                Order.NICHES_NAME_A_Z -> {filtered.sortedBy { it.name }}
                Order.NICHES_NAME_Z_A ->{filtered.sortedByDescending{ it.name }}
                else -> {filtered.sortedBy { it.subscribers }}
            }

            LoadResult.Page( data = res,  prevKey = null, nextKey = null )

        } catch (e: Exception) {
            Timber.e("!!! ItemExplorerNailsPagingSource load() page = $page Ошибка = ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Niche>): Int? {
        return null
    }
}