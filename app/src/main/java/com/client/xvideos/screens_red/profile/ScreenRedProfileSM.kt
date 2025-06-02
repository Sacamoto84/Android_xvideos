package com.client.xvideos.screens_red.profile

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.client.xvideos.App
import com.client.xvideos.AppPath
import com.client.xvideos.feature.Downloader
import com.client.xvideos.feature.preference.PreferencesRepository
import com.client.xvideos.feature.redgifs.extractNameFromUrl
import com.client.xvideos.feature.redgifs.http.RedGifs
import com.client.xvideos.feature.redgifs.types.CreatorResponse
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.MediaType
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.feature.room.AppDatabase
import com.client.xvideos.screens_red.use_case.block.useCaseBlockItem
import com.client.xvideos.screens_red.use_case.block.useCaseGetAllBlockedGifs
import com.client.xvideos.screens_red.use_case.useCaseShareFile
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
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
) : ScreenModel//, PagingSource<Int, MediaInfo>() {
{

    private val _list = MutableStateFlow<List<GifsInfo>>(emptyList())
    val list: StateFlow<List<GifsInfo>> = _list

    //private var currentPage = 0
    private var totalItems = Int.MAX_VALUE   // узнаём после 1-го ответа
    var isLoading = MutableStateFlow(false)

    suspend fun loadNextPage(items : Int = 100, page : Int = 1) {

        Timber.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! loadNextPage isLoading.value ${isLoading.value}")

        if (isLoading.value) return
        if (_list.value.size >= totalItems) return   // всё скачали

            isLoading.value = true
            try {
                //val nextPage = currentPage + 1
                val r = loadProfileGif(
                    page,
                    order,
                    if (typeGifs == TypeGifs.GIFS) MediaType.GIF else MediaType.IMAGE
                )
                val resp = r.gifs
                // обновляем данные
                _list.update { it + resp }
                //currentPage = nextPage
                totalItems = maxCreatorGifs
            } catch (e: Exception) {
                // TODO: обработка ошибки (Snackbar / Retry)
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

        //currentPage = 0
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

    ///////////////////////////////////////////////
    val selector = pref.flowRedSelector
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setSelector(value: Int) {
        screenModelScope.launch {
            pref.setRedSelector(value)
        }
    }

    ///////////////////////////////////////////////
    init {

        screenModelScope.launch {

            creator = RedGifs.searchCreator( page = 1,  count = 1, type = MediaType.GIF,  order = order )
            //maxCreatorGifs = creator?.users[0]?.publishedGifs ?: 0
            maxCreatorGifs = creator?.pages ?: 0

            val repeats = maxCreatorGifs / 100 + 1

            repeat(repeats) {
                loadNextPage(100, it+1)
                delay(100)
            }

            //Фильтруем список тегов убрав из списка блокируемые gif
            blockList.clear()
            blockList.addAll(useCaseGetAllBlockedGifs())
            val blockedSet = blockList.toSet()
            _list.value = _list.value.filterNot { it.id in blockedSet }

        }

    }

    suspend fun loadProfileGif(
        page: Int = 1,
        ord: Order = Order.NEW,
        type: MediaType = MediaType.GIF
    ): CreatorResponse {
        val res = RedGifs.searchCreator(
            count = 100,
            page = page,
            type = type,
            order = ord
        )
        _tags.update { it + res.tags }
        return res
    }


    var currentPlayerControls by mutableStateOf<PlayerControls?>(null)

    var mute by mutableStateOf(true)

    /**
     * Текущее время плеера
     */
    var currentPlayerTime by mutableStateOf(0f)
    var currentPlayerDuration by mutableIntStateOf(0)

    //var isPaused by mutableStateOf(false)

    //---- AB ----
    var play by mutableStateOf(true)
    var enableAB by mutableStateOf(false)
    var timeA by mutableFloatStateOf(3f)
    var timeB by mutableFloatStateOf(6f)
    val listABbyId = mutableListOf<String>()


    //Для тикток

    /**
     * Индекс текущей страницы которая выводит видео на тикток
     */
    var currentTikTokPage by mutableIntStateOf(0)

    /**
     * Возвращает текущий GIF из списка `list` по индексу `currentTikTokPage`.
     *
     * Если индекс выходит за пределы списка, возвращается `null`, чтобы избежать ошибки `IndexOutOfBoundsException`.
     *
     * @return Объект [GifsInfo] для текущей страницы или `null`, если индекс некорректен.
     */
    val currentTikTokGifInfo: GifsInfo?
        get() = list.value.getOrNull(currentTikTokPage)


    //---- Меню верхнее----
    var menuCenter by mutableStateOf(false)

    //---- Downloader ----

    //Загрузить текущую отображаемую страницу
    fun downloadCurrentItem() {
        screenModelScope.launch {
            val item = list.value[currentTikTokPage]
            val hiName =
                extractNameFromUrl(item.urls.hd.toString()) //https://media.redgifs.com/HealthyPettyRedhead.mp4 > HealthyPettyRedhead
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



    //--- Блокировка ---
    var blockVisibleDialog by mutableStateOf(false) //Показ диалога на добавление в блок лист

    var blockList = mutableStateListOf<String>()


    /**
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
        val result = useCaseBlockItem(item)
        if (result.isSuccess) {
            Timber.i("!!! GIF успешно заблокирован")
            Toast.makeText(App.instance.applicationContext, "GIFs заблокирован", Toast.LENGTH_SHORT).show()
        } else {
            val exception = result.exceptionOrNull()
            val errorMsg = exception?.localizedMessage ?: "Неизвестная ошибка"
            Timber.e(exception, "Не удалось заблокировать GIF")
            Toast.makeText(App.instance.applicationContext, "Ошибка блокировки: $errorMsg", Toast.LENGTH_SHORT).show()
        }

        blockList.clear()
        blockList.addAll(useCaseGetAllBlockedGifs())

    }



    //!--- Блокировка ---


    //--- Поделиться ---
    fun shareGifs(context : Context, item: GifsInfo){

        val path = "${AppPath.cache_download_red}/${item.userName}/${item.id}.mp4"
        val file = File(path)

        try {
            if (file.exists()) {
                useCaseShareFile(context, file)
            } else {
                Toast.makeText(context, "Файл не найден: $path", Toast.LENGTH_SHORT).show()
                Timber.w("shareGifs -> Файл не существует: $path")
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка при попытке поделиться файлом", Toast.LENGTH_SHORT).show()
            Timber.e(e, "shareGifs -> Ошибка при работе с файлом: $path")
        }

    }
    //!--- Поделиться ---





    /**
     * --- Вкллючить автоматический поворот ---
     */
    var autoRotate by mutableStateOf(false)



}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedProfile {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedProfileSM::class)
    abstract fun bindScreenRedProfileScreenModel(hiltListScreenModel: ScreenRedProfileSM): ScreenModel
}

