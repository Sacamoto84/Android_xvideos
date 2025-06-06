package com.client.xvideos.screens_red.top_this_week.pagin3

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.client.xvideos.feature.redgifs.http.RedGifs
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.screens_red.listAllUsers
import com.client.xvideos.screens_red.top_this_week.model.SortTop
import timber.log.Timber

class ItemTopThisWeekPagingSource (val sortTop : SortTop): PagingSource<Int, GifsInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int,  GifsInfo> {

        val page = params.key ?: 1 // API нумерует страницы с 1

        return try {
            Timber.d("!!! ItemPagingSource::load() page = $page sortTop:$sortTop")

            val response = if (sortTop == SortTop.WEEK) RedGifs.getTopThisWeek(100, page) else RedGifs.getTopThisMounth(100, page)

            Timber.d("!!! load() a.gif.size = ${response.gifs.size}")

            val gifs = response.gifs.distinctBy { it.id }

            val user = response.users.distinctBy { it.username }

            val existingUsernames = listAllUsers.map { it.username }.toSet()
            val newUsers = user.filter { it.username !in existingUsernames }

            listAllUsers.addAll(newUsers)

            LoadResult.Page(
                data = gifs,
                prevKey = null,
                nextKey = page + 1
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

