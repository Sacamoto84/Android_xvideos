package com.client.xvideos.redgifs.common.pagin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.client.xvideos.redgifs.network.api.RedApi
import com.client.xvideos.redgifs.network.api.RedApi_Search
import com.client.xvideos.redgifs.network.types.GifsInfo
import com.client.xvideos.redgifs.network.types.MediaResponse
import com.client.xvideos.redgifs.network.types.Order
import com.client.xvideos.redgifs.common.block.BlockRed
import com.client.xvideos.redgifs.common.snackBar.SnackBarEvent
import com.client.xvideos.redgifs.common.users.UsersRed
import timber.log.Timber

class ItemTopPagingSource(val sort: Order, val searchText: String, val block: BlockRed) : PagingSource<Int, GifsInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GifsInfo> {

        val page = params.key ?: 1 // API нумерует страницы с 1

        return try {
            Timber.i("!!! ItemTopPagingSource::load() page = $page sortTop:$sort searchText:$searchText")

            val response: MediaResponse = if (searchText != "") {
                Timber.i("!!! ItemTopPagingSource::load()  RedGifs.searchGifs($searchText)")
                RedApi_Search.searchGifs(searchText, sort, 100, page)
            } else {
                when (sort) {
                    Order.TOP_WEEK -> RedApi.getTopThisWeek(100, page)
                    Order.TOP_MONTH -> RedApi.getTopThisMonth(100, page)
                    Order.TRENDING -> RedApi.getTopTrending(100, page)
                    Order.LATEST -> RedApi.getTopLatest(100, page)
                    else -> {
                        RedApi.getTopThisWeek(100, page)
                    }
                }

            }

            val nextKey =  if (page < response.pages) page+1 else null

            Timber.d("!!! load() a.gif.size = ${response.gifs.size} page:${page} pages:${response.pages}")

            val gifs: List<GifsInfo> = response.gifs.distinctBy { it.id }
            val blockedSet = block.blockList.value.map { it.id }.toSet()
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