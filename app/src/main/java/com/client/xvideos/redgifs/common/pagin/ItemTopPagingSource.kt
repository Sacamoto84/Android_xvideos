package com.client.xvideos.redgifs.common.pagin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.client.xvideos.common.snackbar.SnackBar
import com.client.xvideos.redgifs.common.UsersRed
import com.client.xvideos.redgifs.network.api.RedApi
import com.client.xvideos.redgifs.model.GifsInfo
import com.client.xvideos.redgifs.model.Order
import com.client.xvideos.redgifs.common.block.BlockRed
import timber.log.Timber

class ItemTopPagingSource(
    val sort: Order,
    val searchText: String,
    val block: BlockRed,
    val redApi: RedApi,
) : PagingSource<Int, GifsInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GifsInfo> {
        val page = params.key ?: 1

        return try {
            Timber.i("!!! ItemTopPagingSource::load() page = $page sortTop:$sort searchText:$searchText")

            val response = if (searchText.isNotEmpty()) {
                Timber.i("!!! ItemTopPagingSource::load()  RedGifs.searchGifs($searchText)")
                redApi.search.searchGifs(searchText, sort, 100, page).getOrThrow()
            } else {
                when (sort) {
                    Order.TOP_WEEK -> redApi.getTopThisWeek(100, page)
                    Order.TOP_MONTH -> redApi.getTopThisMonth(100, page)
                    Order.TRENDING -> redApi.getTopTrending(100, page)
                    Order.LATEST -> redApi.getTopLatest(100, page).getOrThrow()
                    else -> redApi.getTopThisWeek(100, page)
                }
            }

            val nextKey = if (page < response.pages) page + 1 else null

            Timber.d("!!! load() a.gif.size = ${response.gifs.size} page:$page pages:${response.pages}")

            val gifs: List<GifsInfo> = response.gifs.distinctBy { it.id }
            val blockedSet = block.blockList.value.map { it.id }.toSet()
            val gifs1 = gifs.filterNot { it.id in blockedSet }

            val user = response.users.distinctBy { it.username }
            for (info in user) {
                UsersRed.addUser(info)
            }

            LoadResult.Page(
                data = gifs1,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Timber.e("!!! ItemPagingSource load() page = $page Ошибка = ${e.message}")
            SnackBar.error("ItemPagingSource load() page = $page Ошибка = ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GifsInfo>): Int? {
        return null
    }
}
