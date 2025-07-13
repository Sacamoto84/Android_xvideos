package com.redgifs.common.pagin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.redgifs.common.UsersRed
import com.redgifs.network.api.RedApi
import com.redgifs.model.GifsInfo
import com.redgifs.model.Order
import com.redgifs.common.block.BlockRed
import com.redgifs.common.snackBar.SnackBarEvent
import timber.log.Timber

class ItemNailsPagingSource (val order : Order, val nichesName : String, val block: BlockRed, val redApi: RedApi, val snackBarEvent: SnackBarEvent): PagingSource<Int, GifsInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int,  GifsInfo> {

        val page = params.key ?: 1 // API нумерует страницы с 1

        return try {
            Timber.d("!!! ItemNailsPagingSource::load() page = $page sortTop:$order nichesName:$nichesName")

            if (order == Order.FORCE_TEMP) {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = page
                )
            }

            val response = redApi.getNiches(niches = nichesName, page = page, order = order)

            val isEndReached = response.gifs.isEmpty() // или, если ты знаешь, что сервер вернул всё

            val nextKey = if (isEndReached) { null } else { page + 1 }

            Timber.d("!!! load() a.gif.size = ${response.gifs.size}")

            val gifs : List<GifsInfo> = response.gifs.distinctBy { it.id }
            val blockedSet = block.blockList.value.map{it.id}.toSet()
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
            Timber.e("!!! ItemNailsPagingSource load() page = $page nichesName:$nichesName Ошибка = ${e.message}")
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