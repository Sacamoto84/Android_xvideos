package com.client.xvideos.l.ui.screens.screenAlbumList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import com.client.xvideos.common.util.toMD5
import com.client.xvideos.l.model.AlbumListFilter
import com.client.xvideos.l.model.FacetCollectionInfo
import com.client.xvideos.l.net.AlbumListFilterGenreCountResponse
import com.client.xvideos.l.net.AlbumListImplInfoAndList
import com.client.xvideos.l.net.Luscious
import com.client.xvideos.redgifs.common.snackBar.SnackBarEvent
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@OptIn(ExperimentalFoundationApi::class)
class ScreenLAlbumListSM @AssistedInject constructor(
    @Assisted val inFilter: AlbumListFilter?,
    val luscious: Luscious,
    val snackBarEvent: SnackBarEvent
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory { fun create(filter: AlbumListFilter?): ScreenLAlbumListSM }

    //Глобальный фильтр
    private val _filter = MutableStateFlow( inFilter ?: AlbumListFilter() )
    val filter: StateFlow<AlbumListFilter> = _filter.asStateFlow()

    fun filterUpdate(filter: AlbumListFilter) {
        _filter.value = filter
    }

    val info = MutableStateFlow<FacetCollectionInfo?>(null)

    var filterGenreStateCount = MutableStateFlow(emptyList<AlbumListFilterGenreCountResponse>())
    var filterTaggedStateCount = MutableStateFlow(emptyList<AlbumListFilterGenreCountResponse>())
    var filterPictureCountStateCount = MutableStateFlow(emptyList<AlbumListFilterGenreCountResponse>())

    val bigList = mutableStateMapOf<Int, AlbumListImplInfoAndList>()

    var savedPagerPage by  mutableIntStateOf(0)

    //var albumList = MutableStateFlow<AlbumListImpl?>(null)

    // Pull to refresh state
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _isRequest = MutableStateFlow(false)
    val isRequest = _isRequest.asStateFlow()

    val state = LazyGridState()

    init {
        Timber.i("iii ScreenLAlbumListSM init")


        screenModelScope.launch {
            try {
                _isRefreshing.value = true

                val agr = luscious.getAlbumListAggregations(1, filter.value)
                if (agr.isFailure) {
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    val agrRes = agr.getOrThrow()
                    filterGenreStateCount.value = agrRes.filterGenreStateCount
                    filterTaggedStateCount.value = agrRes.filterTaggedStateCount
                    filterPictureCountStateCount.value = agrRes.filterPictureCountStateCount
                }
            }
            catch (e: Exception) {
                Timber.e(e, "Error loading initial data")
            } finally {
                _isRefreshing.value = false
            }
        }
        //loadInitialData()
        //val a = filter.value!!.toString().toMD5()
    }

    fun loadInitialData() {
        screenModelScope.launch {
            _isRefreshing.value = true
            try {
                val a = luscious.getAlbumList(1 ,filter.value )
                if (a.isFailure){ return@launch }

                withContext(Dispatchers.Main) {
                    val res = a.getOrThrow()
                    info.value = res.info
                    bigList.clear()
                    delay(100)
                    bigList.put(0, res)
                }

                val agr = luscious.getAlbumListAggregations(1, filter.value)
                if (agr.isFailure){ return@launch }

                withContext(Dispatchers.Main) {
                    val agrRes = agr.getOrThrow()
                    filterGenreStateCount.value = agrRes.filterGenreStateCount
                    filterTaggedStateCount.value = agrRes.filterTaggedStateCount
                    filterPictureCountStateCount.value = agrRes.filterPictureCountStateCount
                }

                //albumList.value?.getAlbumList(1, filter.value)
                //albumList.value?.getAlbumListAggregations(1)
            } catch (e: Exception) {
                Timber.e(e, "Error loading initial data")
                snackBarEvent.error(e.message ?: "Error loading initial data")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    override fun onDispose() {
        super.onDispose()
        Timber.i("iii ScreenLAlbumListSM onDispose")
    }

    fun loadAlbumList(page: Int) {

        if (bigList.containsKey(page)) {  return  }

        screenModelScope.launch(Dispatchers.IO) {
            try {
                _isRequest.value = true
                Timber.i("!!! loadAlbumList page:$page")
                val a = luscious.getAlbumList(page+1,filter.value )
                if (a.isFailure){ return@launch }
                val res = a.getOrThrow()
                info.value = res.info
                bigList.put(page, res)
                //Timber.i("!!! loadAlbumList page:$page bigList size:${bigList.size}")
            } catch (e: Exception) {
                Timber.e(e, "!!! eee Error loading page $page")
                snackBarEvent.error(e.message ?: "Error loading page $page")
            }finally {
                _isRequest.value = false
            }
        }
    }

    fun loadNextList() {
        if (info.value != null) {
            val page = (info.value!!.page + 1)
            loadAlbumList(page)
        }
    }

    fun loadPrevList() {
        if (info.value != null) {
            val page = (info.value!!.page - 1).coerceAtLeast(1)
            loadAlbumList(page)
        }
    }

    // Pull to refresh function
    fun refreshData() {
//        screenModelScope.launch {
//            _isRefreshing.value = true
//            try {
//                val currentPage = albumList.value?.info?.page ?: 1
//                val currentFilter = albumList.value?.filter
//
//                Timber.d("Refreshing data for page $currentPage")
//
//                // Reload current page with current filter
//                albumList.value?.getAlbumList(currentPage, currentFilter)
//                albumList.value?.getAlbumListAggregations(currentPage)
//
//                // Optional: scroll to top after refresh
//                state.scrollToItem(0)
//
//            } catch (e: Exception) {
//                Timber.e(e, "Error refreshing data")
//            } finally {
//                _isRefreshing.value = false
//            }
//        }
    }

//    // Alternative refresh method that always goes to first page
//    fun refreshToFirstPage() {
//        screenModelScope.launch {
//            _isRefreshing.value = true
//            try {
//                val currentFilter = albumList.value?.filter
//
//                Timber.d("Refreshing to first page")
//
//                // Always reload first page
//                albumList.value?.getAlbumList(1, currentFilter)
//                albumList.value?.getAlbumListAggregations(1)
//
//                // Scroll to top
//                state.scrollToItem(0)
//
//            } catch (e: Exception) {
//                Timber.e(e, "Error refreshing to first page")
//            } finally {
//                _isRefreshing.value = false
//            }
//        }
//    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLAlbumList {

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(ScreenLAlbumListSM.Factory::class)
    abstract fun bindHiltProfilesScreenModelFactory(
        hiltDetailsScreenModelFactory: ScreenLAlbumListSM.Factory
    ): ScreenModelFactory
}