package com.client.xvideos.screens_red.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.client.xvideos.feature.redgifs.http.RedGifs
import com.client.xvideos.feature.redgifs.types.CreatorResponse
import com.client.xvideos.feature.redgifs.types.MediaType
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.feature.room.AppDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class ScreenRedProfileSM @Inject constructor(
    private val db: AppDatabase,
) : ScreenModel {

    var creator: CreatorResponse? by mutableStateOf(null)

    //Выбор сортировки
    var order by mutableStateOf(Order.NEW)

    val orderList = listOf(Order.TOP, Order.LATEST, Order.OLDEST, Order.TOP28, Order.TRENDING)


    init {
        loadProfileGif()
    }

    fun loadProfileGif(order: Order = Order.LATEST) {
        screenModelScope.launch(Dispatchers.IO) {
            creator = RedGifs.searchCreator(order = order)
        }
    }

    fun loadProfileImage(order: Order = Order.NEW) {
        screenModelScope.launch(Dispatchers.IO) {
            creator = RedGifs.searchCreator(type = MediaType.IMAGE)
        }
    }

    fun loadProfileGifAndImage(order: Order = Order.NEW) {
        screenModelScope.launch(Dispatchers.IO) {
            val images = RedGifs.searchCreator(type = MediaType.IMAGE, order = order)
            val gifs = RedGifs.searchCreator(type = MediaType.GIF, order = order)
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