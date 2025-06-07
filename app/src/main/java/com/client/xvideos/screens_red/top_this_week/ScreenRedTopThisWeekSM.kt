package com.client.xvideos.screens_red.top_this_week

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.feature.redgifs.types.UserInfo
import com.client.xvideos.screens_red.top_this_week.model.SortTop
import com.client.xvideos.screens_red.top_this_week.model.VisibleType
import com.client.xvideos.screens_red.top_this_week.pagin3.ItemTopThisWeekPagingSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import timber.log.Timber
import javax.inject.Inject

class ScreenRedTopThisWeekSM @Inject constructor() : ScreenModel {

    ///////////////////////////
    //Тип отображения Lazy, Pager, две колонки три колонки
    private val _visibleType = MutableStateFlow(VisibleType.ONE)
    val visibleType: StateFlow<VisibleType> = _visibleType.asStateFlow()
    fun changeVisibleType(newSort: VisibleType) {_visibleType.value = newSort}
    //////////////

    //////////////
    // StateFlow текущего типа сортировки
    private val _sortType = MutableStateFlow(SortTop.WEEK) // или "popular", "oldest"
    val sortType: StateFlow<SortTop> = _sortType.asStateFlow()
    fun changeSortType(newSort: SortTop) {
        if (_sortType.value != newSort) { // Меняем, только если тип действительно новый
            _sortType.value = newSort
            _scrollToTopAfterSortChange.value = true // Устанавливаем флаг, что нужен сброс
            Timber.d("!!! SM: Sort type changed to $newSort, scrollToTopAfterSortChange set to true")
        }
    }
    //////////////

    // Новый State для отслеживания необходимости сброса
    private val _scrollToTopAfterSortChange = MutableStateFlow(false)
    val scrollToTopAfterSortChange: StateFlow<Boolean> = _scrollToTopAfterSortChange.asStateFlow()


    // Вызовите это после того, как скролл был выполнен в UI
    fun consumedScrollToTopIntent() {
        _scrollToTopAfterSortChange.value = false
        Timber.d("!!! SM: scrollToTopAfterSortChange set to false (consumed)")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pager: Flow<PagingData<GifsInfo>> = sortType
        .flatMapLatest { sort ->
            Timber.d("!!! ScreenRedTopThisWeekSM::pager sort = $sort")
            Pager(
                config = PagingConfig(pageSize = 109, prefetchDistance = 10, initialLoadSize = 109),
                pagingSourceFactory = { ItemTopThisWeekPagingSource(sort)  }
            ).flow

        }
        .cachedIn(screenModelScope)


//    init {
//        screenModelScope.launch {
//            val list = mutableListOf<MediaResponse>()
//            repeat(1) { it1 ->
//                try {
//                    val a = RedGifs.getTopThisWeek(100, it1 + 1)
//                    list.add(a)
//                    delay(1200)
//                } catch (e: Exception) {
//                    val txt = e.message
//                    Timber.e("!!! Ошибка загрузки ${e.message}")
//                    if (txt != null) {
//                        if (txt.contains("invalid: 429")) {
//                            val regex = """"delay"\s*:\s*(\d+)""".toRegex()
//                            val match = regex.find(txt)
//                            var delay  = 0
//                            if (match != null) {
//                                delay = match.groupValues[1].toInt()
//                                println("Задержка: $delay секунд")
//                            }
//                            Toast("Большая частота запросов, жди $delay сек")
//                        } else {
//                            Toast("!!! Ошибка загрузки ${e.message}")
//                        }
//                    }
//                }
//
//            }
//            val a = list.flatMap { it.gifs }.distinctBy { it.id } // убираем дубликаты по полю id
//            _listGifs.value = a
//
//            val b = list.flatMap { it.users }.distinctBy { it.username } // убираем дубликаты по полю id
//            _listUsers.value = b
//
//        }
//   }


    var columns by mutableIntStateOf(1)             //Количество колонок
    var currentIndex by  mutableIntStateOf(0)
    var currentIndexGoto by  mutableIntStateOf(0)

    override fun onDispose() {
        Timber.d("!!!--------------- ScreenRedTopThisWeekSM::onDispose ///////////////////")
    }

}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedTopThisWeekBlock {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedTopThisWeekSM::class)
    abstract fun bindScreenRedTopThisWeekBlockScreenModel(hiltListScreenModel: ScreenRedTopThisWeekSM): ScreenModel
}


