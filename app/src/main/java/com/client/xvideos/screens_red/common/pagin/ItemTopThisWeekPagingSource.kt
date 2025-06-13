package com.client.xvideos.screens_red.common.pagin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.client.xvideos.feature.redgifs.http.RedGifs
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.screens_red.common.block.BlockRed
import com.client.xvideos.screens_red.common.users.UsersRed
import timber.log.Timber

class ItemTopThisWeekPagingSource (val sort : Order): PagingSource<Int, GifsInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int,  GifsInfo> {

        val page = params.key ?: 1 // API нумерует страницы с 1

        return try {
            Timber.d("!!! ItemTopThisWeekPagingSource::load() page = $page sortTop:$sort")

            if (sort == Order.FORCE_TEMP) {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = page
                )
            }

            val response =  when(sort) {
                Order.WEEK -> RedGifs.getTopThisWeek(100, page)
                Order.MONTH -> RedGifs.getTopThisMonth(100, page)
                Order.TRENDING -> RedGifs.getTopTrending(100, page)
                Order.LATEST -> RedGifs.getTopLatest(100, page)
                else -> {
                    RedGifs.getTopThisWeek(100, page)
                }
            }

            val isEndReached = response.gifs.isEmpty() // или, если ты знаешь, что сервер вернул всё

            val nextKey = if (isEndReached) { null } else { page + 1 }

            Timber.d("!!! load() a.gif.size = ${response.gifs.size}")

            val gifs : List<GifsInfo> = response.gifs.distinctBy { it.id }
            val blockedSet = BlockRed.blockList.value.map{it.id}.toSet()
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
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GifsInfo>): Int? {
//        return state.anchorPosition?.let { position ->
//            state.closestPageToPosition(position)?.prevKey?.plus(1)
//                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
//        }
        return null
    }
}