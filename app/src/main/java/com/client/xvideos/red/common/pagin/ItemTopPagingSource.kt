package com.client.xvideos.red.common.pagin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.client.xvideos.feature.redgifs.http.RedGifs
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.MediaResponse
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.red.common.block.BlockRed
import com.client.xvideos.red.common.snackBar.SnackBarEvent
import com.client.xvideos.red.common.users.UsersRed
import timber.log.Timber

class ItemTopPagingSource(val sort: Order, val searchText: String) : PagingSource<Int, GifsInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GifsInfo> {

        val page = params.key ?: 1 // API нумерует страницы с 1

        return try {
            Timber.i("!!! ItemTopPagingSource::load() page = $page sortTop:$sort searchText:$searchText")

            val response: MediaResponse = if (searchText != "") {
                Timber.i("!!! ItemTopPagingSource::load()  RedGifs.searchGifs($searchText)")
                RedGifs.searchGifs(searchText, sort, 100, page)
            } else {
                when (sort) {
                    Order.TOP_WEEK -> RedGifs.getTopThisWeek(100, page)
                    Order.TOP_MONTH -> RedGifs.getTopThisMonth(100, page)
                    Order.TRENDING -> RedGifs.getTopTrending(100, page)
                    Order.LATEST -> RedGifs.getTopLatest(100, page)
                    else -> {
                        RedGifs.getTopThisWeek(100, page)
                    }
                }

            }

            val nextKey =  if (page < response.pages) page+1 else null

            Timber.d("!!! load() a.gif.size = ${response.gifs.size} page:${page} pages:${response.pages}")

            val gifs: List<GifsInfo> = response.gifs.distinctBy { it.id }
            val blockedSet = BlockRed.blockList.value.map { it.id }.toSet()
            val gifs1 = gifs.filterNot { it.id in blockedSet }

            val user = response.users.distinctBy { it.username }
            for (info in user) { UsersRed.addUser(info) }

            LoadResult.Page(
                data = gifs1,
                prevKey = null,
                nextKey = nextKey
            )

        } catch (e: Exception) {
            Timber.e("!!! ItemPagingSource load() page = $page Ошибка = ${e.message}")
            SnackBarEvent.error("ItemPagingSource load() page = $page Ошибка = ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GifsInfo>): Int? {
        return null
    }
}