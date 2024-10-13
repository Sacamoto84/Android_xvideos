package com.client.xvideos.screens.dashboards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.navigator.Navigator
import com.client.xvideos.model.GalleryItem
import com.client.xvideos.net.readHtmlFromURL
import com.client.xvideos.parcer.parserListVideo
import com.client.xvideos.screens.item.ScreenItem
import com.client.xvideos.urlStart
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Dispatcher
import javax.inject.Inject

var currentNumberScreen by mutableIntStateOf(1)

class ScreenDashBoardsScreenModel @Inject constructor(

): ScreenModel{

    var l = mutableStateListOf<GalleryItem>()

    init {
        openNew(currentNumberScreen)
    }

    fun openItem(url : String,  navigator: Navigator){
        navigator.push(ScreenItem(url))
    }

    fun openNew(numberScreen : Int = 1){
        currentNumberScreen = numberScreen.coerceIn(1, 19999)
        val url = urlStart+ if(currentNumberScreen == 1) "" else "/new/${currentNumberScreen-1}"
        GlobalScope.launch {
            //l.clear()
            l = parserListVideo(readHtmlFromURL(url)).toMutableStateList()
        }
    }


}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleDashBoards {

    @Binds
    @IntoMap
    @ScreenModelKey(ScreenDashBoardsScreenModel::class)
    abstract fun bindScreenDashBoardsScreenModel(hiltListScreenModel: ScreenDashBoardsScreenModel): ScreenModel

}