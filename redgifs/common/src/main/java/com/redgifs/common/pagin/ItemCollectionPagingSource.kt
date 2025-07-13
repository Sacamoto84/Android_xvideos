package com.redgifs.common.pagin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.redgifs.model.GifsInfo
import com.redgifs.common.saved.SavedRed
import com.redgifs.common.snackBar.SnackBarEvent
import timber.log.Timber

class ItemCollectionPagingSource(val collection: String, val savedRed: SavedRed, val snackBarEvent: SnackBarEvent) : PagingSource<Int, GifsInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GifsInfo> {

        return try {
            Timber.d("!!! ItemCollectionPagingSource::load() collection:${collection}")

            val a = if (collection.isNotEmpty()) {
                savedRed.collections.collectionList.first { it.collection == collection }.list
            } else
                emptyList()

            LoadResult.Page(
                data = a,
                prevKey = null,
                nextKey = null
            )

        } catch (e: Exception) {
            Timber.e("!!! ItemCollectionPagingSource::load() collection:${collection} Ошибка = ${e.message}")
            snackBarEvent.error("ItemCollectionPagingSource::load() collection:${collection} Ошибка = ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GifsInfo>): Int? {
        return null
    }
}