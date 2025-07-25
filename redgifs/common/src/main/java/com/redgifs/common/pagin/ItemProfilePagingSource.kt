package com.redgifs.common.pagin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.redgifs.common.UsersRed
import com.redgifs.network.api.RedApi
import com.redgifs.model.GifsInfo
import com.redgifs.model.MediaType
import com.redgifs.model.Order
import com.redgifs.common.block.BlockRed
import com.redgifs.common.snackBar.SnackBarEvent
import timber.log.Timber

class ItemProfilePagingSource (val profileName : String, val sort : Order, val block: BlockRed, val redApi: RedApi, val tags : List<String> = emptyList(), val snackBarEvent: SnackBarEvent): PagingSource<Int, GifsInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int,  GifsInfo> {

        val page = params.key ?: 1 // API нумерует страницы с 1

        return try {
            Timber.d("!!! ItemProfilePagingSource::load() page = $page profileName:${profileName} sortTop:$sort")

//            if (sort == Order.FORCE_TEMP) {
//                LoadResult.Page(
//                    data = emptyList(),
//                    prevKey = null,
//                    nextKey = page
//                )
//            }

            val response = if (tags.isEmpty())
                redApi.searchCreator(userName = profileName, page = page,  count = 100, type = MediaType.GIF,  order = sort)
            else
                redApi.searchCreator(userName = profileName, page = page,  count = 100, type = MediaType.GIF,  order = sort , tags = tags)

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
            snackBarEvent.error("ItemProfilePagingSource load() profileName:${profileName} page = $page Ошибка = ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GifsInfo>): Int? {
        return null
    }
}