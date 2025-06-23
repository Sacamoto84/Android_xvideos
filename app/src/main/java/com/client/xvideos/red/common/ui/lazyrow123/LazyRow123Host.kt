package com.client.xvideos.red.common.ui.lazyrow123

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.client.xvideos.feature.connectivityObserver.ConnectivityObserver
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.red.common.pagin.ItemExplorerNailsPagingSource
import com.client.xvideos.red.common.pagin.ItemLikesPagingSource
import com.client.xvideos.red.common.pagin.ItemNailsPagingSource
import com.client.xvideos.red.common.pagin.ItemTopPagingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

enum class TypePager {
    NICHES,
    TOP,
    LIKES,


    //
    EXPLORER_NICHES,



}

class LazyRow123Host(
    val connectivityObserver: ConnectivityObserver,
    val scope : CoroutineScope,
    val typePager : TypePager,
    val extraString : String = "",
    val startOrder : Order = Order.LATEST,
    val startColumns : Int = 2
) {

    val isConnected = connectivityObserver.isConnected.stateIn(scope, SharingStarted.WhileSubscribed(5000L), false)

    //////////////
    // StateFlow текущего типа сортировки
    private val _sortType = MutableStateFlow(startOrder) // или "popular", "oldest"
    val sortType: StateFlow<Order> = _sortType.asStateFlow()

    fun changeSortType(newSort: Order) {
        if (_sortType.value != newSort) { // Меняем, только если тип действительно новый
            _sortType.value = newSort
            _scrollToTopAfterSortChange.value = true // Устанавливаем флаг, что нужен сброс
            Timber.d("!!! ??? SM: Sort type changed to $newSort, scrollToTopAfterSortChange set to true")
        }
    }

    init {

        Timber.d("!!!  ▶\uFE0F LazyRow123Host init{...}")

        scope.launch {
            sortType.collect {
                Timber.d("!!! *** sortType changed to $it")
            }
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    // ✅ Один общий поток Pager для LazyPagingItems
    val pager: Flow<PagingData<Any>> = sortType
        .flatMapLatest { sort ->
            Timber.d("!!! pager sort = $sort")
            Pager(
                config = PagingConfig(pageSize = 109, prefetchDistance = 10, initialLoadSize = 109),
                pagingSourceFactory = {
                    Timber.d("!!! pagingSourceFactory{...}")
                    createPager(typePager, sort, extraString)
                }
            ).flow
        }
        .cachedIn(GlobalScope)




    // Новый State для отслеживания необходимости сброса
    private val _scrollToTopAfterSortChange = MutableStateFlow(false)
    val scrollToTopAfterSortChange: StateFlow<Boolean> = _scrollToTopAfterSortChange.asStateFlow()


    // Вызовите это после того, как скролл был выполнен в UI
    fun consumedScrollToTopIntent() {
        _scrollToTopAfterSortChange.value = false
        Timber.d("!!! SM: scrollToTopAfterSortChange set to false (consumed)")
    }

    var columns by mutableIntStateOf(startColumns)             //Количество колонок
    var currentIndex by mutableIntStateOf(0)
    var currentIndexGoto by mutableIntStateOf(0)

}

fun createPager(typePager: TypePager, sort : Order, extraString : String) : PagingSource<Int, Any>{
    val pagingSourceFactory = when (typePager) {
        TypePager.NICHES -> {
            ItemNailsPagingSource(order = sort, nichesName = extraString)
        }
        TypePager.TOP -> {
            ItemTopPagingSource(sort)
        }
        TypePager.LIKES -> {
            ItemLikesPagingSource(sort)
        }

        TypePager.EXPLORER_NICHES -> {
            ItemExplorerNailsPagingSource(order = sort)
        }

    }
    return pagingSourceFactory as PagingSource<Int, Any>
}