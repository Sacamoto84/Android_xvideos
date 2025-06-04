package com.client.xvideos.screens_red.top_this_week.pagin3

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.client.xvideos.feature.redgifs.http.RedGifs
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.screens_red.listAllUsers
import timber.log.Timber

class ItemPagingSource : PagingSource<Int, GifsInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int,  GifsInfo> {

        val page = params.key ?: 1 // API нумерует страницы с 1

        return try {
            Timber.d("!!! load() page = $page")
            // Эмуляция сети или базы
            val response = RedGifs.getTopThisWeek(100, page)
            Timber.d("!!! load() a.gif.size = ${response.gifs.size}")

            val gifs = response.gifs.distinctBy { it.id } // Убираем дубликаты по полю id

            val user = response.users.distinctBy { it.username } // убираем дубликаты по полю id

            val existingUsernames = listAllUsers.map { it.username }.toSet()
            val newUsers = user.filter { it.username !in existingUsernames }
            listAllUsers.addAll(newUsers)

            LoadResult.Page(
                data = gifs,
                prevKey = if (page == 1) null else page - 1,
                nextKey = page + 1
            )

        } catch (e: Exception) {
            Timber.e("!!! load() error = ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GifsInfo>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }
}

