package com.client.xvideos.screens.dashboards

import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


class ScreenDashBoardsScreenModel @Inject constructor(

): ScreenModel{

    var l = mutableStateListOf<GalleryItem>()



    init {

        runBlocking {
            val s = readHtmlFromURL(urlStart)//readHtmlFromURL("https://www.xv-ru.com/video.uedlbibe330/shame4k._")
            l = parserListVideo(s).toMutableStateList()
            //val script = parserItemVideo(s)
            //val a = script?.let { parseHTML5Player(it) }
            //a
        }

    }


    fun openItem(url : String,  navigator: Navigator){
        navigator.push(ScreenItem(url))
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