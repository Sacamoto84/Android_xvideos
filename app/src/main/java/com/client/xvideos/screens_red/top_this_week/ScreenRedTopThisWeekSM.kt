package com.client.xvideos.screens_red.top_this_week

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.client.xvideos.feature.redgifs.types.GifsInfo
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

    //Тип отображения Lazy, Pager, две колонки три колонки
    private val _visibleType = MutableStateFlow(VisibleType.PAGER)
    val visibleType: StateFlow<VisibleType> = _visibleType.asStateFlow()

    fun changeVisibleType(newSort: VisibleType) {
        _visibleType.value = newSort
    }

    // StateFlow текущего типа сортировки
    private val _sortType = MutableStateFlow(SortTop.WEEK) // или "popular", "oldest"
    val sortType: StateFlow<SortTop> = _sortType.asStateFlow()

    // Новый State для отслеживания необходимости сброса
    private val _scrollToTopAfterSortChange = MutableStateFlow(false)
    val scrollToTopAfterSortChange: StateFlow<Boolean> = _scrollToTopAfterSortChange.asStateFlow()

    fun changeSortType(newSort: SortTop) {
        if (_sortType.value != newSort) { // Меняем, только если тип действительно новый
            _sortType.value = newSort
            _scrollToTopAfterSortChange.value = true // Устанавливаем флаг, что нужен сброс
            Timber.d("!!! SM: Sort type changed to $newSort, scrollToTopAfterSortChange set to true")
        }
    }

    // Вызовите это после того, как скролл был выполнен в UI
    fun consumedScrollToTopIntent() {
        _scrollToTopAfterSortChange.value = false
        Timber.d("!!! SM: scrollToTopAfterSortChange set to false (consumed)")
    }

    fun getPhotos(sort: SortTop): Pager<Int, GifsInfo> = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = {
            ItemTopThisWeekPagingSource(sort)
        }
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val pager: Flow<PagingData<GifsInfo>> = sortType
        .flatMapLatest { sort ->
            Timber.d("!!! ScreenRedTopThisWeekSM::pager sort = $sort")
            getPhotos(sort).flow
        }
        .cachedIn(screenModelScope)

//    val pager = Pager(
//        config = PagingConfig(pageSize = 1),
//        pagingSourceFactory = { ItemTopThisWeekPagingSource() }
//    ).flow.cachedIn(screenModelScope)

    private val _listGifs = MutableStateFlow<List<GifsInfo>>(emptyList())
    val listGifs: StateFlow<List<GifsInfo>> = _listGifs

    private val _listUsers = MutableStateFlow<List<UserInfo>>(emptyList())
    val listUsers: StateFlow<List<UserInfo>> = _listUsers

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


}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedTopThisWeekBlock {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedTopThisWeekSM::class)
    abstract fun bindScreenRedTopThisWeekBlockScreenModel(hiltListScreenModel: ScreenRedTopThisWeekSM): ScreenModel
}


