package com.client.xvideos.screens_red.profile

import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.isEmpty
import androidx.lifecycle.distinctUntilChanged
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.client.xvideos.feature.preference.PreferencesRepository
import com.client.xvideos.feature.redgifs.http.RedGifs
import com.client.xvideos.feature.redgifs.types.CreatorResponse
import com.client.xvideos.feature.redgifs.types.MediaInfo
import com.client.xvideos.feature.redgifs.types.MediaType
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.feature.room.AppDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.absoluteValue

enum class TypeGifs(val value: String) {
    ALL("All"),
    GIFS("GIFs"),
    IMAGES("Images"),
}

class ScreenRedProfileSM @Inject constructor(
    private val db: AppDatabase,
    private val pref : PreferencesRepository
) : ScreenModel//, PagingSource<Int, MediaInfo>() {
{

    private val _list = MutableStateFlow<List<MediaInfo>>(emptyList())
    val list: StateFlow<List<MediaInfo>> = _list

    private var currentPage = 0
    private var totalItems = Int.MAX_VALUE   // узнаём после 1-го ответа
    var isLoading = MutableStateFlow(false)

    fun loadNextPage() {

        Timber.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! loadNextPage isLoading.value ${isLoading.value}")

        if (isLoading.value) return
        if (_list.value.size >= totalItems) return   // всё скачали

        screenModelScope.launch(Dispatchers.IO) {
            isLoading.value = true
            try {
                val nextPage = currentPage + 1
                val r = loadProfileGif(nextPage, order, if (typeGifs == TypeGifs.GIFS) MediaType.GIF else MediaType.IMAGE)
                val resp = r.gifs
                // обновляем данные
                _list.update { it + resp }
                currentPage = nextPage
                totalItems = maxCreatorGifs
            } catch (e: Exception) {
                // TODO: обработка ошибки (Snackbar / Retry)
            } finally {
                isLoading.value = false
            }

        }
    }

    fun clear(){
        while( isLoading.value){
            Thread.sleep(100)
        }
        _list.update { emptyList() }
        _tags.update { emptySet() }

        currentPage = 0
    }

    //var selector by mutableIntStateOf(0) // 0- 1 елемент  1-2 елемента показывать

    var creator: CreatorResponse? by mutableStateOf(null)

    private val _tags = MutableStateFlow<Set<String>>(emptySet())
    val tags: StateFlow<Set<String>> = _tags

    //Выбор сортировки
    var order by mutableStateOf(Order.NEW)
    val orderList = listOf(Order.TOP, Order.LATEST, Order.OLDEST, Order.TOP28, Order.TRENDING)

    val typeGifsList = listOf(TypeGifs.GIFS, TypeGifs.IMAGES)
    var typeGifs by mutableStateOf(TypeGifs.GIFS)

    /**
     * Общее количество Gif у профиля
     */
    var maxCreatorGifs = 0
    var maxCreatorImages = 0


    ///////////////////////////////////////////////
    val selector = pref.flowRedSelector
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setSelector(value: Int){
        screenModelScope.launch {
            pref.setRedSelector(value)
        }
    }
    ///////////////////////////////////////////////
    init {

        screenModelScope.launch {

            creator = RedGifs.searchCreator(
                page = 1,
                count = 1,
                type = MediaType.GIF,
                order = order
            )
            maxCreatorGifs = creator?.users[0]?.publishedGifs ?: 0
            maxCreatorImages = creator?.pages ?: 0

            loadNextPage()

            delay(1000)

            repeat(maxCreatorGifs/100) {
                loadNextPage()
                delay(1000)
            }

        }

    }

    suspend fun loadProfileGif(page: Int = 1, ord: Order = Order.NEW, type: MediaType = MediaType.GIF): CreatorResponse {
        val res = RedGifs.searchCreator(
            count = 100,
            page = page,
            type = type,
            order = ord
        )
        _tags.update { it + res.tags }
        return res
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedProfile {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedProfileSM::class)
    abstract fun bindScreenRedProfileScreenModel(hiltListScreenModel: ScreenRedProfileSM): ScreenModel
}

