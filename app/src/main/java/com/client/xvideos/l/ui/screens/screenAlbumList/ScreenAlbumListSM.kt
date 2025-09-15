package com.client.xvideos.l.ui.screens.screenAlbumList

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import com.client.xvideos.l.model.AlbumListFilter
import com.client.xvideos.l.net.AlbumListImpl
import com.client.xvideos.l.net.Luscious
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ScreenLAlbumListSM @AssistedInject constructor(
    @Assisted val inFilter: AlbumListFilter?,
    val luscious: Luscious
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(filter: AlbumListFilter?): ScreenLAlbumListSM
    }



    //Глобальный фильтр
    private val _filter = MutableStateFlow(inFilter)
    val filter: StateFlow<AlbumListFilter?> = _filter.asStateFlow()












    var albumList = MutableStateFlow<AlbumListImpl?>(null)

    // Pull to refresh state
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing

    val state = LazyGridState()

    init {
        Timber.i("iii ScreenLAlbumListSM init")
        loadInitialData()
    }

    private fun loadInitialData() {
        screenModelScope.launch {
            _isRefreshing.value = true
            try {
                val res = luscious.getAlbumList(1 ,filter.value )
                if (res.isFailure){
                    return@launch
                }
                albumList.value?.getAlbumList(1, filter.value)
                albumList.value?.getAlbumListAggregations(1)
            } catch (e: Exception) {
                Timber.e(e, "Error loading initial data")
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
        screenModelScope.launch {
            try {
                albumList.value?.getAlbumList(page, filter.value)
            } catch (e: Exception) {
                Timber.e(e, "Error loading page $page")
            }
        }
    }

    fun loadNextList() {
        if (albumList.value != null) {
            val page = (albumList.value!!.info.page + 1)
            loadAlbumList(page)
        }
    }

    fun loadPrevList() {
        if (albumList.value != null) {
            val page = (albumList.value!!.info.page - 1).coerceAtLeast(1)
            loadAlbumList(page)
        }
    }

    // Pull to refresh function
    fun refreshData() {
        screenModelScope.launch {
            _isRefreshing.value = true
            try {
                val currentPage = albumList.value?.info?.page ?: 1
                val currentFilter = albumList.value?.filter

                Timber.d("Refreshing data for page $currentPage")

                // Reload current page with current filter
                albumList.value?.getAlbumList(currentPage, currentFilter)
                albumList.value?.getAlbumListAggregations(currentPage)

                // Optional: scroll to top after refresh
                state.scrollToItem(0)

            } catch (e: Exception) {
                Timber.e(e, "Error refreshing data")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    // Alternative refresh method that always goes to first page
    fun refreshToFirstPage() {
        screenModelScope.launch {
            _isRefreshing.value = true
            try {
                val currentFilter = albumList.value?.filter

                Timber.d("Refreshing to first page")

                // Always reload first page
                albumList.value?.getAlbumList(1, currentFilter)
                albumList.value?.getAlbumListAggregations(1)

                // Scroll to top
                state.scrollToItem(0)

            } catch (e: Exception) {
                Timber.e(e, "Error refreshing to first page")
            } finally {
                _isRefreshing.value = false
            }
        }
    }
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