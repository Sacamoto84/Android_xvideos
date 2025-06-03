package com.client.xvideos.screens_red.profile

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.client.xvideos.App
import com.client.xvideos.feature.Downloader
import com.client.xvideos.feature.preference.PreferencesRepository
import com.client.xvideos.feature.redgifs.extractNameFromUrl
import com.client.xvideos.feature.redgifs.http.RedGifs
import com.client.xvideos.feature.redgifs.types.CreatorResponse
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.MediaType
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.feature.room.AppDatabase
import com.client.xvideos.screens_red.use_case.block.blockGetAllBlockedGifs
import com.client.xvideos.screens_red.use_case.network.userCaseLoadGifs
import com.client.xvideos.screens_red.use_case.share.useCaseShareGifs
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

enum class TypeGifs(val value: String) {
    ALL("All"),
    GIFS("GIFs"),
    IMAGES("Images"),
}

class ScreenRedProfileSM @Inject constructor(
    private val db: AppDatabase,
    private val pref: PreferencesRepository,
    val downloader: Downloader
) : ScreenModel
{

    private val _list = MutableStateFlow<List<GifsInfo>>(emptyList())
    val list: StateFlow<List<GifsInfo>> = _list




    //var selector by mutableIntStateOf(0) // 0- 1 елемент  1-2 елемента показывать

    var creator: CreatorResponse? by mutableStateOf(null)

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
    init {

        screenModelScope.launch {

            creator = RedGifs.searchCreator( page = 1,  count = 1, type = MediaType.GIF,  order = order )
            //maxCreatorGifs = creator?.users[0]?.publishedGifs ?: 0
            maxCreatorGifs = creator?.pages ?: 0

            val repeats = maxCreatorGifs / 100 + 1

            repeat(repeats) {
                loadNextPage(items = 100, page = it+1)
                delay(10)
            }

            //Фильтруем список тегов убрав из списка блокируемые gif
            refreshListAndBlock()

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



    //---- Downloader ----
    //Загрузить текущую отображаемую страницу
    fun downloadCurrentItem() {
        screenModelScope.launch {
            val item = list.value[currentTikTokPage]
            val hiName = extractNameFromUrl(item.urls.hd.toString()) //https://media.redgifs.com/HealthyPettyRedhead.mp4 > HealthyPettyRedhead
            Timber.i("!!! downloadItem() id:${item.id} userName:${item.userName} url:${item.urls.hd}")
            downloader.downloadRedName(item.id, item.userName, item.urls.hd.toString())
            Timber.i("!!! downloadItem() ... завершено")
        }
    }

    fun scanCacheDowmload() {
        screenModelScope.launch {
            downloader.scanRedCacheDowmdoadAndUpdate()
        }
    }



    //Region══════════ Блокировка ═════════════════════╦══════════════════════════════════════════════════════════════╗
    var blockVisibleDialog by mutableStateOf(false)  //║ Показ диалога на добавление в блок лист                      ║
    var blockList = mutableStateListOf<String>()     //║                                                              ║
    //═════════════════════════════════════════════════╬══════════════════════════════════════════════════════════════╣
    /*
     * Выполняет блокировку GIF-элемента, используя [useCaseBlockItem].
     *
     * Функция вызывает `useCaseBlockItem` для создания файла блокировки,
     * затем выводит лог и показывает пользователю Toast с результатом операции.
     * В случае ошибки — логирует исключение с помощью Timber и показывает
     * сообщение об ошибке.
     *
     * @param item Объект [GifsInfo], описывающий GIF, который нужно заблокировать.
     *
     * @see useCaseBlockItem
     */
    fun blockItem(item: GifsInfo) {
        val result = com.client.xvideos.screens_red.use_case.block.blockItem(item)
        if (result.isSuccess) {
            Timber.i("!!! GIF успешно заблокирован")
            Toast.makeText(App.instance.applicationContext, "GIFs заблокирован", Toast.LENGTH_SHORT).show()
        } else {
            val exception = result.exceptionOrNull()
            val errorMsg = exception?.localizedMessage ?: "Неизвестная ошибка"
            Timber.e(exception, "Не удалось заблокировать GIF")
            Toast.makeText(App.instance.applicationContext, "Ошибка блокировки: $errorMsg", Toast.LENGTH_SHORT).show()
        }
        refreshListAndBlock()
    }
    //                                                                                                                  ║
    //End ══════════════════════════════════════════════════════════════════════════════════════════════════════════════╝

    // Методы
    //════════════════ Поделиться ═══════════════════════════════════════════════════════╗
    fun shareGifs(context : Context, item: GifsInfo){useCaseShareGifs(context, item)}  //║
    //═══════════════════════════════════════════════════════════════════════════════════╝


    //═══════════════════════════════════════════════════════════════════════════════════╗
    //                                                                                   ║
    //                                                                                   ║
    //═══════════════════════════════════════════════════════════════════════════════════╣
    fun refreshListAndBlock(){                                                         //║
        blockList.clear()                                                              //║
        blockList.addAll(blockGetAllBlockedGifs())                                   //║
        val blockedSet = blockList.toSet()                                             //║
        _list.value = _list.value.filterNot { it.id in blockedSet }                    //║
    }                                                                                  //║
    //═══════════════════════════════════════════════════════════════════════════════════╝

    suspend fun loadNextPage(items : Int = 100, page : Int = 1) {
        Timber.d("!!! loadNextPage isLoading.value ${isLoading.value}")
        if (isLoading.value) return

        isLoading.value = true
        try {
            val r = userCaseLoadGifs(items = items, page = page, ord = order, type = if (typeGifs == TypeGifs.GIFS) MediaType.GIF else MediaType.IMAGE)
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

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedProfile {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedProfileSM::class)
    abstract fun bindScreenRedProfileScreenModel(hiltListScreenModel: ScreenRedProfileSM): ScreenModel
}
