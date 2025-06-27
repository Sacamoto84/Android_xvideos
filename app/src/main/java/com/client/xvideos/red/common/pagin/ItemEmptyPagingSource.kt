package com.client.xvideos.red.common.pagin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.client.xvideos.feature.redgifs.types.GifsInfo
import timber.log.Timber

class ItemEmptyPagingSource (): PagingSource<Int, GifsInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int,  GifsInfo> {
        return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
    }

    override fun getRefreshKey(state: PagingState<Int, GifsInfo>): Int? {
        return null
    }
}