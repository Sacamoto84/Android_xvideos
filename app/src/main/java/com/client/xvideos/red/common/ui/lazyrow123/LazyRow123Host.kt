package com.client.xvideos.red.common.ui.lazyrow123

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.TimeInput
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
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
import com.client.xvideos.red.common.search.SearchRed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
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

private data class SearchParams(val query: String, val sort: Order)

@OptIn(FlowPreview::class)
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

    //var searchText by mutableStateOf("")

    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

//    init {
//        scope.launch {
//
//            SearchRed.searchText
//                .debounce(1000)               // ждать 400мс тишины
//                .map { it.trim() }           // подчистить пробелы
//                .distinctUntilChanged()      // игнорировать дубликаты
//                .filter { it.length >= 2 }   // например, не искать по 1 букве
//                .collectLatest { term ->     // отменять предыдущий поиск
//                    searchText = term        // вызываем API / БД
//                    refresh()
//                }
//
//        }
//    }

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

    //   @OptIn(ExperimentalCoroutinesApi::class)
//    val pager: Flow<PagingData<Any>> =  combine(sortType, refreshTrigger.onStart { emit(Unit) }) { sort, _ -> sort }
//        .flatMapLatest { sort ->
//            Timber.d("!!! >>>pager sort = $sort")
//            Pager(
//                config = PagingConfig(pageSize = 109, prefetchDistance = 10, initialLoadSize = 109),
//                pagingSourceFactory = {
//                    Timber.d("!!! >>>pagingSourceFactory{...}")
//                    createPager(typePager, sort, extraString, searchText)
//                }
//            ).flow
//        }
//        .cachedIn(scope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val pager: Flow<PagingData<Any>> =
        combine(SearchRed.searchTextDone, sortType) { text, sort ->          // ① слепили параметры
            SearchParams(text.trim(), sort)
        }
            //.debounce(2000)                                          // ② ждём паузу ввода
            .distinctUntilChanged()                                 // ③ игнорируем дубли
            .flatMapLatest { params ->                              // ④ НОВЫЙ Pager при каждом изменении
                Pager(
                    config = PagingConfig(
                        pageSize = 100,
                        prefetchDistance = 10,
                        initialLoadSize = 100
                    ),
                    pagingSourceFactory = {
                        Timber.d("!!! >>>pagingSourceFactory{...}")
                        gotoUp()
                        gotoUpColumn()
                        createPager(typePager, params.sort, extraString, params.query)
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

    fun refresh() {
        refreshTrigger.tryEmit(Unit)   // пересоздать Flow
    }


}

fun createPager(
    typePager: TypePager,
    sort: Order,
    extraString: String,
    searchText: String
): PagingSource<Int, Any> {
    val pagingSourceFactory = when (typePager) {
        TypePager.NICHES -> {
            ItemNailsPagingSource(order = sort, nichesName = extraString)
        }

        TypePager.TOP -> {
            ItemTopPagingSource(sort = sort, searchText = searchText)
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