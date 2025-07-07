package com.client.redgifs.common.pagin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.client.xvideos.redgifs.network.api.RedApi
import com.client.xvideos.redgifs.network.types.GifsInfo
import com.client.xvideos.redgifs.network.types.MediaType
import com.client.xvideos.redgifs.network.types.Order
import com.client.xvideos.redgifs.common.block.BlockRed
import com.client.xvideos.redgifs.common.snackBar.SnackBarEvent
import com.client.xvideos.redgifs.common.users.UsersRed
import timber.log.Timber

class ItemProfilePagingSource (val profileName : String, val sort : Order, val block: BlockRed): PagingSource<Int, GifsInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int,  GifsInfo> {

        val page = params.key ?: 1 // API нумерует страницы с 1

        return try {
            Timber.d("!!! ItemProfilePagingSource::load() page = $page profileName:${profileName} sortTop:$sort")

            if (sort == Order.FORCE_TEMP) {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = page
                )
            }

            val response = RedApi.searchCreator(userName = profileName, page = page,  count = 100, type = MediaType.GIF,  order = sort )

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
            Timber.e("!!! ItemProfilePagingSource load() profileName:${profileName} page = $page Ошибка = ${e.message}")
            SnackBarEvent.error("ItemProfilePagingSource load() profileName:${profileName} page = $page Ошибка = ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GifsInfo>): Int? {
        return null
    }
}