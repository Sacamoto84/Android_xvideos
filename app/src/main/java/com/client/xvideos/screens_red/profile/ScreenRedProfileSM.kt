package com.client.xvideos.screens_red.profile

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelKey
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    init {
        runBlocking {
            creator = RedGifs.searchCreator(
                page = 1,
                count = 1,
                type = MediaType.GIF,
                order = order
            )
            maxCreatorGifs = creator?.users[0]?.publishedGifs ?: 0
            maxCreatorImages = creator?.pages ?: 0

            loadNextPage()

        }
    }


//    fun loadProfile(){
//        when (typeGifs){
//            TypeGifs.GIFS -> {loadProfileGif(order)}
//            TypeGifs.IMAGES -> {loadProfileImage(order)}
//            TypeGifs.ALL -> {loadProfileGifAndImage(order)}
//        }
//    }

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


    //////////////
    var percentItemsCount = 0.0f



}


@Composable
fun rememberScrollPercent(gridState: LazyGridState): State<Float> {
    return remember(gridState) {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo

            // 1. Нет элементов или видимых элементов — 0%
            if (layoutInfo.totalItemsCount == 0 || visibleItemsInfo.isEmpty()) {
                return@derivedStateOf 0f
            }

            // 2. Индекс первого видимого элемента
            val firstVisibleItem = visibleItemsInfo.first()
            val firstVisibleItemIndex = firstVisibleItem.index

            // 3. Смещение первого видимого элемента (насколько он "скрыт" сверху)
            // Для вертикальной сетки это смещение по Y.
            // offset.y обычно отрицательный или 0 для первого видимого элемента,
            // поэтому берем absoluteValue.
            val firstVisibleItemScrollOffset = firstVisibleItem.offset.y.absoluteValue.toFloat()

            // 4. Средняя высота видимых элементов.
            // Это все еще оценка, но используется для определения "доли" прокрутки
            // текущего первого видимого элемента.
            val avgItemHeight = visibleItemsInfo.map { it.size.height }.average().toFloat()

            if (avgItemHeight == 0f) { // Избегаем деления на ноль, если высота элементов 0
                return@derivedStateOf if (layoutInfo.totalItemsCount > 0) 0f else 1f
            }

            // 5. Какая часть первого видимого элемента прокручена (от 0.0 до 1.0)
            val fractionOfFirstItemScrolled = (firstVisibleItemScrollOffset / avgItemHeight).coerceIn(0f, 1f)

            // 6. Эффективное количество "прокрученных" элементов:
            // индекс первого видимого + доля прокрутки этого элемента
            val effectiveScrolledItemsCount = firstVisibleItemIndex + fractionOfFirstItemScrolled

            // 7. Процент прокрутки от общего количества элементов
            val scrollPercent = (effectiveScrolledItemsCount / layoutInfo.totalItemsCount.toFloat()).coerceIn(0f, 1f)

            scrollPercent
        }
    }
}


//@Composable
//fun rememberScrollPercentIgnoringFirstN(
//    gridState: LazyGridState,
//    itemsToIgnore: Int = 3 // Количество первых элементов для игнорирования
//): State<Float> {
//    return remember(gridState, itemsToIgnore) {
//        derivedStateOf {
//            val layoutInfo = gridState.layoutInfo
//            val visibleItemsInfo = layoutInfo.visibleItemsInfo
//
//            // Общее количество элементов, учитываемых для расчета процента
//            val relevantTotalItemsCount = (layoutInfo.totalItemsCount - itemsToIgnore).coerceAtLeast(0)
//
//            // 1. Если релевантных элементов нет или видимых элементов нет, или
//            //    первый видимый элемент находится среди игнорируемых (и мы еще не прокрутили до релевантных)
//            if (relevantTotalItemsCount == 0 || visibleItemsInfo.isEmpty()) {
//                return@derivedStateOf 0f
//            }
//
//            val firstVisibleItem = visibleItemsInfo.first()
//            val firstVisibleItemIndex = firstVisibleItem.index
//
//            // Если все видимые элементы находятся среди игнорируемых
//            if (firstVisibleItemIndex < itemsToIgnore && (visibleItemsInfo.lastOrNull()?.index ?: 0) < itemsToIgnore) {
//                // И если мы еще не прокрутили достаточно, чтобы первый релевантный элемент стал видимым,
//                // (т.е. смещение первого видимого элемента не указывает на то, что мы уже за игнорируемыми)
//                // В этом простом варианте, если первый видимый из игнорируемых, считаем 0%
//                return@derivedStateOf 0f
//            }
//
//
//            // 2. Индекс первого видимого элемента относительно *релевантных* элементов
//            // Если первый видимый элемент - один из игнорируемых, но мы уже прокрутили за него,
//            // то для расчета нам нужно будет учесть это смещение.
//            // Но проще считать, что прогресс начинается только после itemsToIgnore.
//
//            val effectiveFirstVisibleItemIndex = (firstVisibleItemIndex - itemsToIgnore).coerceAtLeast(0)
//
//
//            // 3. Смещение первого видимого элемента
//            val firstVisibleItemScrollOffset = firstVisibleItem.offset.y.absoluteValue.toFloat()
//
//            // 4. Средняя высота видимых элементов (можно оставить как есть, или фильтровать, если нужно)
//            // Для простоты оставим расчет по всем видимым, т.к. игнорируемые могут иметь другую высоту.
//            // Если игнорируемые элементы имеют сильно другую высоту и всегда видимы вначале,
//            // это может немного исказить avgItemHeight для расчета доли прокрутки *первого релевантного* элемента,
//            // когда он только появляется.
//            val avgItemHeight = visibleItemsInfo
//                .filter { it.index >= itemsToIgnore } // Фильтруем, чтобы средняя высота считалась по релевантными элементам, если они есть
//                .ifEmpty { visibleItemsInfo } // Если релевантных видимых нет, берем все видимые (крайний случай)
//                .map { it.size.height }
//                .average()
//                .toFloat()
//
//            if (avgItemHeight == 0f) {
//                // Если нет релевантных видимых элементов или их высота 0
//                return@derivedStateOf if (firstVisibleItemIndex < itemsToIgnore) 0f else 1f // Если мы за игнорируемыми, то 100% (т.к. relevantTotalItemsCount может быть 0)
//            }
//
//
//            // 5. Какая часть первого *видимого* элемента прокручена
//            // Эта логика остается прежней, но применяется к первому видимому элементу,
//            // даже если он один из игнорируемых, но мы прокручиваем *сквозь* него к релевантным.
//            val fractionOfFirstVisibleItemScrolled = (firstVisibleItemScrollOffset / avgItemHeight).coerceIn(0f, 1f)
//
//
//            // 6. Эффективное количество "прокрученных" *релевантных* элементов
//            val effectiveScrolledRelevantItemsCount =
//                if (firstVisibleItemIndex < itemsToIgnore) {
//                    // Если первый видимый элемент среди игнорируемых, но мы прокручиваем
//                    // и часть игнорируемых уже скрыта.
//                    // Мы считаем, сколько из *игнорируемых* прокручено, чтобы понять,
//                    // насколько мы близки к первому релевантному.
//                    // Это сложнее, т.к. игнорируемые могут иметь разную высоту.
//                    //
//                    // Упрощенный подход: если первый видимый элемент - игнорируемый,
//                    // но мы уже видим релевантные элементы дальше, то effectiveFirstVisibleItemIndex
//                    // уже будет > 0.
//                    // Если же первый видимый - игнорируемый, и релевантных еще не видно,
//                    // то мы в зоне 0%.
//                    //
//                    // Для более точного расчета, когда мы находимся *внутри* зоны игнорируемых элементов,
//                    // и они имеют разную высоту, пришлось бы итеративно считать их высоты.
//                    //
//                    // Более простой и часто достаточный вариант:
//                    // Если первый видимый элемент - игнорируемый, то релевантный прогресс равен 0,
//                    // ЕСЛИ ТОЛЬКО мы не прокрутили так, что первый релевантный уже показался бы,
//                    // если бы он был сразу за игнорируемыми.
//                    // Эта логика уже покрывается `effectiveFirstVisibleItemIndex`.
//
//                    // Если первый видимый элемент - игнорируемый, то `effectiveFirstVisibleItemIndex` будет 0.
//                    // Доля прокрутки будет относиться к этому игнорируемому элементу.
//                    // Нам нужно начать считать прогресс только когда `firstVisibleItemIndex >= itemsToIgnore`.
//
//                    if (firstVisibleItemIndex >= itemsToIgnore) {
//                        effectiveFirstVisibleItemIndex + fractionOfFirstVisibleItemScrolled
//                    } else {
//                        // Если мы все еще в зоне игнорируемых элементов, считаем прогресс 0
//                        // до тех пор, пока не прокрутим до первого релевантного.
//                        // Однако, если мы уже видим релевантные, это условие не выполнится.
//                        // Этот блок сложен, так как fractionOfFirstVisibleItemScrolled считается от *первого видимого*
//                        // который может быть игнорируемым.
//
//                        // Более надежно:
//                        0f // Пока не достигнем первого релевантного элемента, прогресс 0.
//                        // `effectiveFirstVisibleItemIndex` позаботится о начале отсчета.
//                    }
//                } else {
//                    effectiveFirstVisibleItemIndex + fractionOfFirstVisibleItemScrolled
//                }
//
//
//            // 7. Процент прокрутки от общего количества *релевантных* элементов
//            if (relevantTotalItemsCount == 0) return@derivedStateOf 0f // Если нет релевантных элементов для отображения процента
//
//            // Итоговый расчет прогресса только для релевантных элементов.
//            // Если первый видимый элемент - это первый релевантный (или позже),
//            // то effectiveFirstVisibleItemIndex будет >= 0.
//            val actualScrolledItems = if (firstVisibleItemIndex >= itemsToIgnore) {
//                (firstVisibleItemIndex - itemsToIgnore) + fractionOfFirstVisibleItemScrolled
//            } else {
//                // Если мы еще в зоне игнорируемых, но уже начали их прокручивать,
//                // и первый релевантный элемент еще не виден.
//                // Нужно определить, какая часть игнорируемых прокручена, чтобы
//                // начать показывать >0% только когда *первый релевантный* элемент начнет появляться.
//                //
//                // Самый простой способ: если первый видимый элемент имеет индекс < itemsToIgnore,
//                // то прогресс по релевантным элементам равен 0.
//                0f
//            }
//
//            (actualScrolledItems / relevantTotalItemsCount.toFloat()).coerceIn(0f, 1f)
//        }
//    }
//}
//



@Composable
fun rememberVisibleRangePercentIgnoringFirstN(
    gridState: LazyGridState,
    itemsToIgnore: Int = 3 // Количество первых элементов для игнорирования
): State<Pair<Float, Float>> {
    return remember(gridState, itemsToIgnore) {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo

            val relevantTotalItemsCount = (layoutInfo.totalItemsCount - itemsToIgnore).coerceAtLeast(0)

            if (relevantTotalItemsCount == 0 || visibleItemsInfo.isEmpty()) {
                return@derivedStateOf 0f to 0f // Начало и конец на 0, если нет релевантных или видимых элементов
            }

            // --- Расчет начального процента (подобно предыдущему) ---

            val firstVisibleItem = visibleItemsInfo.first()
            val firstVisibleItemIndex = firstVisibleItem.index
            val firstVisibleItemScrollOffset = firstVisibleItem.offset.y.absoluteValue.toFloat() // Для вертикальной сетки

            // Средняя высота элементов, которые *не* игнорируются и *видимы*
            val avgRelevantItemHeight = visibleItemsInfo
                .filter { it.index >= itemsToIgnore }
                .map { it.size.height.toFloat() } // Для вертикальной сетки
                .ifEmpty { listOf(0f) } // Избегаем ошибки на пустом списке после фильтра
                .average()
                .toFloat()
                .coerceAtLeast(1f) // Избегаем деления на ноль, минимальная высота 1px

            var startPercent = 0f
            if (firstVisibleItemIndex >= itemsToIgnore) {
                // Первый видимый элемент - релевантный
                val effectiveFirstRelevantIndex = firstVisibleItemIndex - itemsToIgnore
                val fractionOfFirstRelevantItemScrolled = (firstVisibleItemScrollOffset / avgRelevantItemHeight).coerceIn(0f, 1f)
                startPercent = ((effectiveFirstRelevantIndex + fractionOfFirstRelevantItemScrolled) / relevantTotalItemsCount.toFloat()).coerceIn(0f, 1f)
            } else {
                // Первый видимый элемент - игнорируемый.
                // Проверяем, не виден ли уже какой-либо релевантный элемент
                val firstRelevantVisibleItem = visibleItemsInfo.find { it.index >= itemsToIgnore }
                if (firstRelevantVisibleItem != null) {
                    // Игнорируемые элементы прокручены, и виден первый релевантный
                    val effectiveFirstRelevantIndex = firstRelevantVisibleItem.index - itemsToIgnore
                    val firstRelevantItemScrollOffset = firstRelevantVisibleItem.offset.y.absoluteValue.toFloat()
                    val fractionOfFirstRelevantItemScrolled = (firstRelevantItemScrollOffset / avgRelevantItemHeight).coerceIn(0f, 1f)
                    startPercent = ((effectiveFirstRelevantIndex + fractionOfFirstRelevantItemScrolled) / relevantTotalItemsCount.toFloat()).coerceIn(0f, 1f)

                } else {
                    // Все видимые элементы - игнорируемые, релевантные еще не начались
                    startPercent = 0f
                }
            }


            // --- Расчет конечного процента ---

            val lastVisibleItem = visibleItemsInfo.last()
            val lastVisibleItemIndex = lastVisibleItem.index

            var endPercent = 0f
            if (relevantTotalItemsCount > 0) { // Только если есть релевантные элементы
                if (lastVisibleItemIndex < itemsToIgnore) {
                    // Последний видимый элемент все еще из игнорируемых. Значит, конец видимой релевантной области = 0%
                    endPercent = 0f
                } else {
                    // Последний видимый элемент - релевантный (или мы прокрутили за все игнорируемые)
                    val effectiveLastRelevantIndex = lastVisibleItem.index - itemsToIgnore

                    // Определяем, насколько последний видимый элемент "заполняет" свое пространство
                    // (offset.y + size.height) - viewportEndOffset
                    // Для вертикальной сетки viewportEndOffset - это gridState.layoutInfo.viewportSize.height
                    val lastItemBottomEdge = lastVisibleItem.offset.y + lastVisibleItem.size.height
                    val viewportBottomEdge = layoutInfo.viewportSize.height // Для вертикальной сетки

                    // Какая часть последнего видимого релевантного элемента находится *внутри* видимой области
                    // (относительно его нижней границы)
                    val visiblePartOfLastItemPx = (avgRelevantItemHeight - (lastItemBottomEdge - viewportBottomEdge).coerceAtLeast(0)).coerceAtLeast(0f)
                    val fractionOfLastRelevantItemVisible = (visiblePartOfLastItemPx / avgRelevantItemHeight).coerceIn(0f, 1f)

                    endPercent = ((effectiveLastRelevantIndex + fractionOfLastRelevantItemVisible) / relevantTotalItemsCount.toFloat()).coerceIn(0f, 1f)
                }
            }


            // Если общее количество элементов меньше или равно количеству игнорируемых,
            // или если все видимые элементы являются игнорируемыми и мы не дошли до релевантных.
            if (layoutInfo.totalItemsCount <= itemsToIgnore || (lastVisibleItem.index < itemsToIgnore && startPercent == 0f)) {
                0f to 0f
            } else {
                // Убедимся, что startPercent не больше endPercent (может случиться в крайних случаях с очень маленьким списком)
                if (startPercent > endPercent && relevantTotalItemsCount == 1) { // Частный случай для одного релевантного элемента
                    startPercent to startPercent
                } else {
                    startPercent.coerceAtMost(1f) to endPercent.coerceAtMost(1f)
                }
            }
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

