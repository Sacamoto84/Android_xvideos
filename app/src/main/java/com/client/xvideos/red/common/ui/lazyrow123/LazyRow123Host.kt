package com.client.xvideos.red.common.ui.lazyrow123

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.TimeInput
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.client.xvideos.feature.connectivityObserver.ConnectivityObserver
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.red.common.pagin.ItemCollectionPagingSource
import com.client.xvideos.red.common.pagin.ItemEmptyPagingSource
import com.client.xvideos.red.common.pagin.ItemExplorerNailsPagingSource
import com.client.xvideos.red.common.pagin.ItemSavedLikesPagingSource
import com.client.xvideos.red.common.pagin.ItemNailsPagingSource
import com.client.xvideos.red.common.pagin.ItemProfilePagingSource
import com.client.xvideos.red.common.pagin.ItemTopPagingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

enum class TypePager {
    NICHES,
    TOP,

    SAVED_LIKES,

    SAVED_COLLECTION,

    PROFILE,

    //
    EXPLORER_NICHES,

    EMPTY

}

class LazyRow123Host(
    val connectivityObserver: ConnectivityObserver,
    val scope: CoroutineScope,
    val typePager: TypePager,
    var extraString: String = "",
    val startOrder: Order = Order.LATEST,
    val startColumns: Int = 2,
    val visibleProfileInfo: Boolean = true,
    val isCollection: Boolean = false
) {

    val state: LazyGridState = LazyGridState()

    val stateColumn = LazyListState()

    val isConnected = connectivityObserver.isConnected.stateIn(
        scope,
        SharingStarted.WhileSubscribed(5000L),
        false
    )

    //////////////
    // StateFlow текущего типа сортировки
    private val _sortType = MutableStateFlow(startOrder) // или "popular", "oldest"
    val sortType: StateFlow<Order> = _sortType.asStateFlow()

    //private val _sortType = MutableSharedFlow<Order>(replay = 1)
    //val sortType: SharedFlow<Order> = _sortType.asSharedFlow()

    fun changeSortType(newSort: Order) {
        //if (_sortType.value != newSort) { // Меняем, только если тип действительно новый
        _sortType.value = newSort
        //_sortType.tryEmit(newSort)
        Timber.d("!!! *** Sort тип изменен в $newSort")
        //}
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pager: Flow<PagingData<Any>> = sortType
        .flatMapLatest { sort ->
            Timber.d("!!! >>>pager sort = $sort")
            Pager(
                config = PagingConfig(pageSize = 109, prefetchDistance = 10, initialLoadSize = 109),
                pagingSourceFactory = {
                    Timber.d("!!! >>>pagingSourceFactory{...}")
                    createPager(typePager, sort, extraString)
                }
            ).flow
        }
        .cachedIn(scope)
    var columns by mutableIntStateOf(startColumns)             //Количество колонок
    var currentIndex by mutableIntStateOf(0)
    var currentIndexGoto by mutableIntStateOf(0)

    fun gotoUp() {
        scope.launch { state.scrollToItem(0) }
    }

    fun gotoUpColumn() {
        scope.launch { stateColumn.scrollToItem(0) }
    }



}

fun createPager(typePager: TypePager, sort: Order, extraString: String): PagingSource<Int, Any> {
    val pagingSourceFactory = when (typePager) {
        TypePager.NICHES -> {
            ItemNailsPagingSource(order = sort, nichesName = extraString)
        }

        TypePager.TOP -> {
            ItemTopPagingSource(sort)
        }

        TypePager.SAVED_LIKES -> {
            ItemSavedLikesPagingSource(sort)
        }

        TypePager.EXPLORER_NICHES -> {
            ItemExplorerNailsPagingSource(order = sort)
        }

        TypePager.PROFILE -> {
            ItemProfilePagingSource(profileName = extraString, sort)
        }

        TypePager.EMPTY -> {
            ItemEmptyPagingSource()
        }

        TypePager.SAVED_COLLECTION -> {
            ItemCollectionPagingSource(extraString)
        }

    }
    return pagingSourceFactory as PagingSource<Int, Any>
}