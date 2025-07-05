package com.client.xvideos.redgifs.screens.profile

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import com.client.xvideos.feature.Downloader
import com.client.xvideos.feature.connectivityObserver.ConnectivityObserver
import com.client.xvideos.feature.preference.PreferencesRepository
import com.client.xvideos.redgifs.network.api.RedApi
import com.client.xvideos.redgifs.network.types.GifsInfo
import com.client.xvideos.redgifs.network.types.MediaType
import com.client.xvideos.redgifs.network.types.Order
import com.client.xvideos.redgifs.network.types.UserInfo
import com.client.xvideos.feature.room.AppDatabase
import com.client.xvideos.redgifs.common.block.BlockRed
import com.client.xvideos.redgifs.common.network.loadGifs
import com.client.xvideos.redgifs.common.share.useCaseShareGifs
import com.client.xvideos.redgifs.common.ui.lazyrow123.LazyRow123Host
import com.client.xvideos.redgifs.common.ui.lazyrow123.TypePager
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

enum class TypeGifs(val value: String) {
    ALL("All"),
    GIFS("GIFs"),
    IMAGES("Images"),
}

class ScreenRedProfileSM @AssistedInject constructor(
    @Assisted val profileName: String,
    private val db: AppDatabase,
    private val pref: PreferencesRepository,
    connectivityObserver: ConnectivityObserver
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(profileName: String): ScreenRedProfileSM
    }

    val _list = MutableStateFlow<List<GifsInfo>>(emptyList())
    val list: StateFlow<List<GifsInfo>> = _list

    //var selector by mutableIntStateOf(0) // 0- 1 елемент  1-2 елемента показывать

    var creator: UserInfo? by mutableStateOf(null)

    //═════════════════════════════════════════════════════════════════════════════════════════════════════╗
    //* Список тегов                                                                                       ║
    //══════════════════════════════════════════════════════════════╦══════════════════════════════════════╣
    private val _tags = MutableStateFlow<Set<String>>(emptySet()) //║                                      ║
    val tags: StateFlow<Set<String>> = _tags                      //║                                      ║
    //══════════════════════════════════════════════════════════════╩══════════════════════════════════════╝

    //═════════════════════════════════════════════════════════════════════════════════════════════════════╗
    //* Выбор сортировки                                                                                   ║
    //═════════════════════════════════════════════════════════════════════════════════════════════════════╣
    val orderList = listOf(Order.TOP, Order.LATEST, Order.OLDEST, Order.TOP28, Order.TRENDING)           //║
    var order by mutableStateOf(Order.NEW)                         //║ Текущий вид сортировки              ║
    //═══════════════════════════════════════════════════════════════╩═════════════════════════════════════╝

    val typeGifsList = listOf(TypeGifs.GIFS, TypeGifs.IMAGES)
    var typeGifs by mutableStateOf(TypeGifs.GIFS)

    var maxCreatorGifs = 0                  //║ Общее количество Gif у профиля не важно Gif или Images
    var isLoading = MutableStateFlow(false) //║ Запрос к серверу п процессе

    ///////////////////////////////////////////////
    val selector = pref.flowRedSelector.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), 0)
    fun setSelector(value: Int) { screenModelScope.launch { pref.setRedSelector(value) } }
    ///////////////////////////////////////////////



    val likedHost = LazyRow123Host(
        connectivityObserver = connectivityObserver,
        scope = screenModelScope,
        typePager = TypePager.PROFILE,
        extraString = profileName,
        visibleProfileInfo = false
    )


    init {

        screenModelScope.launch {

            clear()

            setSelector(2)

            creator = RedApi.readCreator(profileName)
            //maxCreatorGifs = creator?.users[0]?.publishedGifs ?: 0
           // maxCreatorGifs = creator?.pages ?: 0

//            val repeats = 1//maxCreatorGifs / 100 + 1
//
//            repeat(repeats) {
//                loadNextPage(userName = profileName, items = 100, page = it+1)
//                delay(1000)
//            }

            //Фильтруем список тегов убрав из списка блокируемые gif
            BlockRed.refreshListAndBlock(_list)

        }

    }




    //═════════════════════════════════════════════════════════════════════════════════════════════════════╗
    // Управление плеером                                                                                  ║
    //══════════════════════════════════════════════════╦══════════════════════════════════════════════════╣
    var play by mutableStateOf(true)                  //║                                                  ║
    var mute by mutableStateOf(true)                  //║                                                  ║
    var autoRotate by mutableStateOf(false)           //║ Включить автоматический поворот                  ║
    //══════════════════════════════════════════════════╬══════════════════════════════════════════════════╣
    var enableAB by mutableStateOf(false)             //║                                                  ║
    var timeA by mutableFloatStateOf(3f)              //║                                                  ║
    var timeB by mutableFloatStateOf(6f)              //║                                                  ║
    //══════════════════════════════════════════════════╩══════════════════════════════════════════════════╣
    var currentPlayerControls by mutableStateOf<PlayerControls?>(null)                                   //║
    //═════════════════════════════════════════════════════════════════════════════════════════════════════╝
    //═══ Состояния плеера ═════════════════════════════╦══════════════════════════════════════════════════╗
    var currentPlayerTime by mutableFloatStateOf(0f)  //║ Текущее время                                    ║
    var currentPlayerDuration by mutableIntStateOf(0) //║ Продолжительность видео                          ║
    //══════════════════════════════════════════════════╩══════════════════════════════════════════════════╝

    //═══ Тикток ═════════════════════════════════════════╦═════════════════════════════════════════════════════════════════════════════════════╗
    var currentTikTokPage by mutableIntStateOf(0)       //║ Индекс текущей страницы которая выводит видео на тикток                             ║
    //════════════════════════════════════════════════════╬═════════════════════════════════════════════════════════════════════════════════════╣
    val currentTikTokGifInfo: GifsInfo?                 //║ Возвращает текущий GIF из списка `list` по индексу `currentTikTokPage               ║
        get() = list.value.getOrNull(currentTikTokPage) //║ @return Объект [GifsInfo] для текущей страницы или `null`, если индекс некорректен. ║
    //════════════════════════════════════════════════════╬═════════════════════════════════════════════════════════════════════════════════════╣
    var menuCenter by mutableStateOf(false)             //║ Отобразить в центре меню контент                                                    ║
    //════════════════════════════════════════════════════╩═════════════════════════════════════════════════════════════════════════════════════╝
    var tictikStartIndex by mutableIntStateOf(0)


    //---- Downloader ----
    //Загрузить текущую отображаемую страницу
    fun downloadCurrentItem() {
        screenModelScope.launch {
            val item = list.value[currentTikTokPage]
            //val hiName = extractNameFromUrl(item.urls.hd.toString()) //https://media.redgifs.com/HealthyPettyRedhead.mp4 > HealthyPettyRedhead
            Timber.i("!!! downloadItem() id:${item.id} userName:${item.userName} url:${item.urls.hd}")
            Downloader.downloadRedName(item.id, item.userName, item.urls.hd.toString())
            Timber.i("!!! downloadItem() ... завершено")
        }
    }

    fun scanCacheDowmload() {
        screenModelScope.launch {
            Downloader.scanRedCacheDownloadAndUpdate()
        }
    }

    // Методы
    //════════════════ Поделиться ═══════════════════════════════════════════════════════╗
    fun shareGifs(context : Context, item: GifsInfo){useCaseShareGifs(context, item)}  //║
    //═══════════════════════════════════════════════════════════════════════════════════╝

    suspend fun loadNextPage(userName : String, items : Int = 100, page : Int = 1) {
        Timber.d("!!! loadNextPage isLoading.value ${isLoading.value}")
        if (isLoading.value) return

        isLoading.value = true
        try {
            val r = loadGifs(userName = userName, items = items, page = page, ord = order, type = if (typeGifs == TypeGifs.GIFS) MediaType.GIF else MediaType.IMAGE)
            _tags.update { it + r.tags }
            val resp = r.gifs
            _list.update { it + resp }
        } catch (e: Exception) {

        } finally {
            isLoading.value = false
        }

    }

    fun clear() {
        while (isLoading.value) {
            Thread.sleep(100)
        }
        _list.update { emptyList() }
        _tags.update { emptySet() }
    }

    fun openFullScreen(index : Int){
        setSelector(1)
        tictikStartIndex = index
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedProfile {

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(ScreenRedProfileSM.Factory::class)
    abstract fun bindHiltProfilesScreenModelFactory(
        hiltDetailsScreenModelFactory: ScreenRedProfileSM.Factory
    ): ScreenModelFactory

}
