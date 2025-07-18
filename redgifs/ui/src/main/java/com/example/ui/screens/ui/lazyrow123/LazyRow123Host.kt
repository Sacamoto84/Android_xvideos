package com.example.ui.screens.ui.lazyrow123

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.client.common.connectivityObserver.ConnectivityObserver
import com.redgifs.common.block.BlockRed
import com.redgifs.common.di.HostDI
import com.redgifs.common.pagin.ItemCollectionPagingSource
import com.redgifs.common.pagin.ItemEmptyPagingSource
import com.redgifs.common.pagin.ItemExplorerNailsPagingSource
import com.redgifs.common.pagin.ItemNailsPagingSource
import com.redgifs.common.pagin.ItemProfilePagingSource
import com.redgifs.common.pagin.ItemSavedLikesPagingSource
import com.redgifs.common.pagin.ItemTopPagingSource
import com.redgifs.common.saved.SavedRed
import com.redgifs.common.snackBar.SnackBarEvent
import com.redgifs.model.Order
import com.redgifs.network.api.RedApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

enum class TypePager {
    NICHES,
    TOP,
    SAVED_LIKES,
    SAVED_COLLECTION,
    PROFILE,
    EXPLORER_NICHES,
    EMPTY
}

private data class SearchParams(val query: String, val sort: Order, val tags : String, val queryNiches : String)

@OptIn(FlowPreview::class)
class LazyRow123Host(
    val connectivityObserver: ConnectivityObserver,
    val scope: CoroutineScope,
    val typePager: TypePager,
    var extraString: String = "",
    val startOrder: Order = Order.LATEST,
    val startColumns: Int = 2,
    val visibleProfileInfo: Boolean = true,
    val isCollection: Boolean = false,
    //val block: BlockRed,
    //val search : SearchRed,
    //val redApi : RedApi,
    //val savedRed: SavedRed,
    val tags : StateFlow<Set<String>> = MutableStateFlow(emptySet()),
    //val snackBarEvent: SnackBarEvent,
    //val downloadRed: DownloadRed,
    val hostDI : HostDI
) {

    //var searchText by mutableStateOf("")

    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

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
        combine(hostDI.search.searchTextDone, sortType,hostDI.block.blockList, tags, hostDI.searchNiches.searchTextDone) { text, sort, blockList, tags, textNiches ->
            SearchParams(text.trim(), sort, tags.joinToString(","), textNiches)
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
                        createPager(typePager, params.sort, extraString, params.query, tags.value.toList(), hostDI = hostDI, textNiches = params.queryNiches)
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
    searchText: String,
    tags : List<String> = emptyList(),
    hostDI : HostDI,
    textNiches : String
): PagingSource<Int, Any> {
    val pagingSourceFactory = when (typePager) {
        TypePager.NICHES -> {
            ItemNailsPagingSource(order = sort, nichesName = extraString, block = hostDI.block, redApi = hostDI.redApi, hostDI.snackBarEvent)
        }

        TypePager.TOP -> {
            ItemTopPagingSource(sort = sort, searchText = searchText, block = hostDI.block, redApi = hostDI.redApi, hostDI.snackBarEvent)
        }

        TypePager.SAVED_LIKES -> {
            ItemSavedLikesPagingSource(sort, hostDI.savedRed)
        }

        TypePager.EXPLORER_NICHES -> {
            ItemExplorerNailsPagingSource(order = sort, textNiches = textNiches, redApi = hostDI.redApi, hostDI.snackBarEvent, hostDI.savedRed.nichesCache)
        }

        TypePager.PROFILE -> {
            ItemProfilePagingSource(profileName = extraString, sort = sort, block = hostDI.block, redApi = hostDI.redApi, tags = tags, hostDI.snackBarEvent)
        }

        TypePager.EMPTY -> {
            ItemEmptyPagingSource()
        }

        TypePager.SAVED_COLLECTION -> {
            ItemCollectionPagingSource(extraString, hostDI.savedRed, hostDI.snackBarEvent)
        }

    }
    return pagingSourceFactory as PagingSource<Int, Any>
}