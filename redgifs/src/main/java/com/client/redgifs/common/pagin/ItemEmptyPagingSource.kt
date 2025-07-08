package com.client.redgifs.common.pagin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.client.redgifs.network.types.GifsInfo

class ItemEmptyPagingSource (): PagingSource<Int, GifsInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GifsInfo> {
        return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
    }

    override fun getRefreshKey(state: PagingState<Int, GifsInfo>): Int? {
        return null
    }
}