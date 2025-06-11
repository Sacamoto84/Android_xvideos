package com.client.xvideos.screens_red.niche

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import com.client.xvideos.feature.connectivityObserver.ConnectivityObserver
import com.client.xvideos.feature.preference.PreferencesRepository
import com.client.xvideos.feature.redgifs.http.RedGifs
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.NichesInfo
import com.client.xvideos.feature.redgifs.types.NichesResponse
import com.client.xvideos.feature.redgifs.types.TopCreatorsResponse
import com.client.xvideos.feature.room.AppDatabase
import com.client.xvideos.screens_red.common.block.BlockRed
import com.client.xvideos.screens_red.common.downloader.DownloadRed
import com.client.xvideos.screens_red.common.expand_menu_video.ExpandMenuVideoModel
import com.client.xvideos.screens_red.common.favorite.FavoriteRed
import com.client.xvideos.screens_red.niche.model.SortByNiches
import com.client.xvideos.screens_red.niche.pagin3.ItemNailsPagingSource
import com.client.xvideos.screens_red.profile.ScreenRedProfileSM
import com.client.xvideos.screens_red.top_this_week.model.SortTop
import com.client.xvideos.screens_red.top_this_week.pagin3.ItemTopThisWeekPagingSource
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class ScreenNicheSM @AssistedInject constructor(
    @Assisted val nicheName: String,
    connectivityObserver: ConnectivityObserver
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(nicheName: String): ScreenNicheSM
    }

    var niche: NichesInfo by mutableStateOf(NichesInfo())
    var related by mutableStateOf(NichesResponse(emptyList()))
    var topCreator by mutableStateOf(TopCreatorsResponse(emptyList()))

    init {
        screenModelScope.launch {
            niche = RedGifs.getNiche(nicheName).niche                  //Нужно кешировать

            related = RedGifs.getNichesRelated(nicheName)        //Нужно кешировать
            topCreator = RedGifs.getNichesTopCreators(nicheName) //Нужно кешировать

        }
    }

    val isConnected = connectivityObserver.isConnected.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000L),
        false
    )


    //////////////
    // StateFlow текущего типа сортировки
    private val _sortType = MutableStateFlow(SortByNiches.LATEST) // или "popular", "oldest"
    val sortType: StateFlow<SortByNiches> = _sortType.asStateFlow()

    fun changeSortType(newSort: SortByNiches) {
        if (_sortType.value != newSort) { // Меняем, только если тип действительно новый
            _sortType.value = newSort
            _scrollToTopAfterSortChange.value = true // Устанавливаем флаг, что нужен сброс
            Timber.d("!!! SM: Sort type changed to $newSort, scrollToTopAfterSortChange set to true")
        }
    }

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
            Timber.d("!!! ScreenNicheSM::pager sort = $sort")
            Pager(
                config = PagingConfig(pageSize = 109, prefetchDistance = 10, initialLoadSize = 109),
                pagingSourceFactory = {
                    ItemNailsPagingSource(
                        sortTop = sort,
                        nichesName = nicheName
                    )
                }
            ).flow

        }
        .cachedIn(screenModelScope)

    val expandMenuVideoList =
        listOf(
            ExpandMenuVideoModel("Скачать", Icons.Filled.FileDownload, onClick = {
                if (it == null) return@ExpandMenuVideoModel
                DownloadRed.downloadItem(it)
            }),
            ExpandMenuVideoModel("Поделиться", Icons.Default.Share),
            ExpandMenuVideoModel("Блокировать", Icons.Default.Block, onClick = {
                if (it == null) return@ExpandMenuVideoModel
                BlockRed.blockVisibleDialog = true
            }),

            ExpandMenuVideoModel("Фаворит", Icons.Default.StarBorder, onClick = {
                if (it == null) return@ExpandMenuVideoModel
                FavoriteRed.invertFavorite(it)
            }),


            )

    var columns by mutableIntStateOf(1)             //Количество колонок
    var currentIndex by mutableIntStateOf(0)
    var currentIndexGoto by mutableIntStateOf(0)

}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedNiche {

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(ScreenNicheSM.Factory::class)
    abstract fun bindHiltNicheScreenModelFactory(
        hiltDetailsScreenModelFactory: ScreenNicheSM.Factory
    ): ScreenModelFactory

}