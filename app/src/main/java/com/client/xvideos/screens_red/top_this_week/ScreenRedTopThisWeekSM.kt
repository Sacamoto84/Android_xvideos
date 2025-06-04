package com.client.xvideos.screens_red.top_this_week

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.client.xvideos.feature.redgifs.http.RedGifs
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.MediaResponse
import com.client.xvideos.feature.redgifs.types.UserInfo
import com.client.xvideos.util.Toast
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ScreenRedTopThisWeekSM @Inject constructor() : ScreenModel {

    private val _listGifs = MutableStateFlow<List<GifsInfo>>(emptyList())
    val listGifs: StateFlow<List<GifsInfo>> = _listGifs

    private val _listUsers = MutableStateFlow<List<UserInfo>>(emptyList())
    val listUsers: StateFlow<List<UserInfo>> = _listUsers

    init {
        screenModelScope.launch {
            val list = mutableListOf<MediaResponse>()
            repeat(1) { it1 ->
                try {
                    val a = RedGifs.getTopThisWeek(100, it1 + 1)
                    list.add(a)
                    delay(1200)
                } catch (e: Exception) {
                    val txt = e.message
                    Timber.e("!!! Ошибка загрузки ${e.message}")
                    if (txt != null) {
                        if (txt.contains("invalid: 429")) {
                            val regex = """"delay"\s*:\s*(\d+)""".toRegex()
                            val match = regex.find(txt)
                            var delay  = 0
                            if (match != null) {
                                delay = match.groupValues[1].toInt()
                                println("Задержка: $delay секунд")
                            }
                            Toast("Большая частота запросов, жди $delay сек")
                        } else {
                            Toast("!!! Ошибка загрузки ${e.message}")
                        }
                    }
                }

            }
            val a = list.flatMap { it.gifs }.distinctBy { it.id } // убираем дубликаты по полю id
            _listGifs.value = a

            val b = list.flatMap { it.users }.distinctBy { it.username } // убираем дубликаты по полю id
            _listUsers.value = b

        }
    }


}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedTopThisWeekBlock {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedTopThisWeekSM::class)
    abstract fun bindScreenRedTopThisWeekBlockScreenModel(hiltListScreenModel: ScreenRedTopThisWeekSM): ScreenModel
}


