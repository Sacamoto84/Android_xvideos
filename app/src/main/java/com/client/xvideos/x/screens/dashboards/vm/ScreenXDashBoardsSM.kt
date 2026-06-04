package com.client.xvideos.x.screens.dashboards.vm

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.navigator.Navigator
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.x.model.ItemsX
import com.client.xvideos.x.feature.saved.SavedX
import com.client.xvideos.x.screens.videoplayer.ScreenX_VideoPlayer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScreenXDashBoardsScreenModel @Inject constructor(
    val saved : SavedX
) : ScreenModel {
    /** Количество колонок true-2 false-1 */
    val countRow = Settings.xvideos_row2
    val pagerState: PagerState = PagerState(0) { 20000 }
    fun openVideoPlayer(url: String, navigator: Navigator) { navigator.push(ScreenX_VideoPlayer(url)) }

    /**
     * Кэш загруженных страниц дашбордов. Живёт в ScreenModel и переживает переключение
     * вкладок (Дашборды <-> Избранное), поэтому возврат на дашборды не перезагружает
     * страницу из сети — список берётся из кэша.
     */
    private val pageCache = mutableMapOf<Int, SnapshotStateList<ItemsX>>()

    /** Значение [com.client.xvideos.x.feature.country.CountryState.updateTrigger], на котором собран кэш. */
    var cacheCountryTrigger: Int = -1

    /** Возвращает (создавая при первом обращении) список элементов для страницы [index]. */
    fun pageFor(index: Int): SnapshotStateList<ItemsX> = pageCache.getOrPut(index) { mutableStateListOf() }

    /** Очищает содержимое всех кэшированных страниц (при смене страны), сохраняя ссылки на списки. */
    fun clearPageCacheContents() { pageCache.values.forEach { it.clear() } }

    /**
     * Состояния прокрутки сеток по страницам. Тоже живут в ScreenModel, чтобы позиция скролла
     * не сбрасывалась при переключении вкладок (Дашборды <-> Избранное).
     */
    private val gridStateCache = mutableMapOf<Int, LazyGridState>()

    /** Возвращает (создавая при первом обращении) состояние прокрутки сетки для страницы [index]. */
    fun gridStateFor(index: Int): LazyGridState = gridStateCache.getOrPut(index) { LazyGridState() }

    fun addFavorite(item: ItemsX) = screenModelScope.launch { saved.favorites.add(item) }
    fun removeFavorite(item: ItemsX) = screenModelScope.launch { saved.favorites.remove(item) }
    fun isFavorite(id: Long): Boolean = saved.favorites.contains(id) // O(1) по множеству id

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleDashBoards {

    @Binds
    @IntoMap
    @ScreenModelKey(ScreenXDashBoardsScreenModel::class)
    abstract fun bindScreenDashBoardsScreenModel(hiltListScreenModel: ScreenXDashBoardsScreenModel): ScreenModel

}