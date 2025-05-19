package com.client.xvideos.screens_red.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.paging.PagingSource
import androidx.paging.PagingState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.client.xvideos.feature.redgifs.http.RedGifs
import com.client.xvideos.feature.redgifs.http.RedGifs.api
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


enum class TypeGifs(val value: String) {
    ALL("All"),
    GIFS("GIFs"),
    IMAGES("Images"),
}

class ScreenRedProfileSM @Inject constructor(
    private val db: AppDatabase,
) : ScreenModel//, PagingSource<Int, MediaInfo>() {
{
    //------------- Пагинация -------------
//    override fun getRefreshKey(state: PagingState<Int, MediaInfo>): Int? {
//        state.anchorPosition?.let { pos ->
//            state.closestPageToPosition(pos)?.prevKey?.plus(1)
//                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
//        }
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaInfo> {
//        val page = params.key ?: 1
//        return try {
//            val resp = api.getCreatorMedia(page)      // → CreatorResponse
//
//            LoadResult.Page(
//                data = resp.gifs,                    // текущая страница
//                prevKey = if (page == 1) null else page - 1,
//                nextKey = if (page >= resp.pages) null else page + 1
//            )
//
//        } catch (e: Exception) {
//            LoadResult.Error(e)
//        }
//    }
    //---------------------------------------

    init {
        //loadProfile()
    }

    private val _list = MutableStateFlow<List<MediaInfo>>(emptyList())
    val list: StateFlow<List<MediaInfo>> = _list

    private var currentPage = 0
    private var totalItems = Int.MAX_VALUE   // узнаём после 1-го ответа
    var isLoading = MutableStateFlow(false)

    fun loadNextPage() {
        if (isLoading.value) return
        if (_list.value.size >= totalItems) return   // всё скачали

        screenModelScope.launch {
            isLoading.value = true
            try {
                val nextPage = currentPage + 1

                loadProfileGif(nextPage)

                val resp = creator?.gifs ?: emptyList()



                // обновляем данные
                _list.update { it + resp }
                currentPage = nextPage
                totalItems = maxCreatorGifs

            } catch (e: Exception) {
                // TODO: обработка ошибки (Snackbar / Retry)
            } finally {
                isLoading.value= false
            }
        }
    }

        var creator: CreatorResponse? by mutableStateOf(null)

        //Выбор сортировки
        var order by mutableStateOf(Order.NEW)
        val orderList = listOf(Order.TOP, Order.LATEST, Order.OLDEST, Order.TOP28, Order.TRENDING)

        val typeGifsList = listOf(TypeGifs.GIFS, TypeGifs.IMAGES)
        var typeGifs by mutableStateOf(TypeGifs.GIFS)

        var maxCreatorGifs = 0


//    fun loadProfile(){
//        when (typeGifs){
//            TypeGifs.GIFS -> {loadProfileGif(order)}
//            TypeGifs.IMAGES -> {loadProfileImage(order)}
//            TypeGifs.ALL -> {loadProfileGifAndImage(order)}
//        }
//    }

        suspend fun loadProfileGif(page : Int = 1) {
            withContext(Dispatchers.IO) {

                maxCreatorGifs= RedGifs.searchCreator(page =1 ,count = 1, type = MediaType.GIF, order = order).users[0].publishedGifs

                creator = RedGifs.searchCreator(
                    count = 100,
                    page = page,
                    type = MediaType.GIF,
                    order = order
                )
            }
        }

        suspend fun loadProfileImage() {
            withContext(Dispatchers.IO) {
                creator = RedGifs.searchCreator(type = MediaType.IMAGE, order = order)
            }
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

