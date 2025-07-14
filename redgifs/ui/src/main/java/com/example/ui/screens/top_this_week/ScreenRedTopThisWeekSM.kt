package com.example.ui.screens.top_this_week

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.client.common.connectivityObserver.ConnectivityObserver
import com.example.ui.screens.top_this_week.model.VisibleType
import com.example.ui.screens.ui.lazyrow123.LazyRow123Host
import com.example.ui.screens.ui.lazyrow123.TypePager
import com.redgifs.common.di.HostDI
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class ScreenRedTopThisWeekSM @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    val hostDI : HostDI
) : ScreenModel {

    val lazyHost =
        LazyRow123Host(
            connectivityObserver = connectivityObserver, scope = screenModelScope,
            typePager = TypePager.TOP,
            hostDI = hostDI
        )

    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )


    ///////////////////////////
    //Тип отображения Lazy, Pager, две колонки три колонки
    private val _visibleType = MutableStateFlow(VisibleType.TWO)
    val visibleType: StateFlow<VisibleType> = _visibleType.asStateFlow()
    fun changeVisibleType(newSort: VisibleType) {
        _visibleType.value = newSort
    }
    //////////////


    //////////////


//    init {
//        screenModelScope.launch {
//            val list = mutableListOf<MediaResponse>()
//            repeat(1) { it1 ->
//                try {
//                    val a = RedGifs.getTopThisWeek(100, it1 + 1)
//                    list.add(a)
//                    delay(1200)
//                } catch (e: Exception) {
//                    val txt = e.message
//                    Timber.e("!!! Ошибка загрузки ${e.message}")
//                    if (txt != null) {
//                        if (txt.contains("invalid: 429")) {
//                            val regex = """"delay"\s*:\s*(\d+)""".toRegex()
//                            val match = regex.find(txt)
//                            var delay  = 0
//                            if (match != null) {
//                                delay = match.groupValues[1].toInt()
//                                println("Задержка: $delay секунд")
//                            }
//                            Toast("Большая частота запросов, жди $delay сек")
//                        } else {
//                            Toast("!!! Ошибка загрузки ${e.message}")
//                        }
//                    }
//                }
//
//            }
//            val a = list.flatMap { it.gifs }.distinctBy { it.id } // убираем дубликаты по полю id
//            _listGifs.value = a
//
//            val b = list.flatMap { it.users }.distinctBy { it.username } // убираем дубликаты по полю id
//            _listUsers.value = b
//
//        }
//   }

    //////////////////////////////////////////////////////////////////////////////////////////
//    val expandMenuVideoList =
//        listOf(
//            ExpandMenuVideoModel("Скачать", Icons.Filled.FileDownload, onClick = {
//                if (it == null) return@ExpandMenuVideoModel
//                DownloadRed.downloadItem(it)
//            }),
//            ExpandMenuVideoModel("Поделиться", Icons.Default.Share),
//            ExpandMenuVideoModel("Блокировать", Icons.Default.Block, onClick = {
//                if (it == null) return@ExpandMenuVideoModel
//                BlockRed.blockVisibleDialog = true
//            }),
//
//            )
    //////////////////////////////////////////////////////////////////////////////////////////

}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedTopThisWeekBlock {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedTopThisWeekSM::class)
    abstract fun bindScreenRedTopThisWeekBlockScreenModel(hiltListScreenModel: ScreenRedTopThisWeekSM): ScreenModel
}


