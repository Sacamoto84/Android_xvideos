package com.client.redgifs.common.pagin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.client.redgifs.common.saved.SavedRed
import com.client.redgifs.common.snackBar.SnackBarEvent
import com.client.redgifs.network.types.GifsInfo
import timber.log.Timber

class ItemCollectionPagingSource(val collection: String) : PagingSource<Int, GifsInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GifsInfo> {

        return try {
            Timber.d("!!! ItemCollectionPagingSource::load() collection:${collection}")

            val a = if (collection.isNotEmpty()) {
                SavedRed.collections.collectionList.first { it.collection == collection }.list
            } else
                emptyList()

            LoadResult.Page(
                data = a,
                prevKey = null,
                nextKey = null
            )

        } catch (e: Exception) {
            Timber.e("!!! ItemCollectionPagingSource::load() collection:${collection} Ошибка = ${e.message}")
            SnackBarEvent.error("ItemCollectionPagingSource::load() collection:${collection} Ошибка = ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GifsInfo>): Int? {
        return null
    }
}