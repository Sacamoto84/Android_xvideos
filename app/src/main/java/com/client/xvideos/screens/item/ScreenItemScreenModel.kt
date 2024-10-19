package com.client.xvideos.screens.item

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.model.HTML5PlayerConfig
import com.client.xvideos.net.readHtmlFromURL
import com.client.xvideos.parcer.parseHTML5Player
import com.client.xvideos.parcer.parserItemVideo
import com.client.xvideos.screens.item.atom.ScreenItemTagsModelPornostars
import com.client.xvideos.screens.tags.ScreenTags
import com.client.xvideos.tags.model.TagsModel
import com.client.xvideos.tags.parserItemVideoTags
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.runBlocking

data class ScreenItemsTags(val uploader: String, val model: String, val tags: List<String>)

class ScreenItemScreenModel @AssistedInject constructor(
    @Assisted val url: String,
    @ApplicationContext appContext: Context,
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(url: String): ScreenItemScreenModel
    }

    var passedString: String = ""

    val a: MutableState<HTML5PlayerConfig?> = mutableStateOf(HTML5PlayerConfig())

    //val mediaItem: MediaItem?

    var tags by mutableStateOf(TagsModel(emptyList(), emptyList(), emptyList()))


    init {
        runBlocking {
            val s = readHtmlFromURL(url)
            val script = parserItemVideo(s)
            a.value = script?.let { parseHTML5Player(it) }
            a

            tags = parserItemVideoTags(s)
            tags

            //mediaItem = a.value?.let { MediaItem.fromUri(it.videoHLS) }
            passedString = a.value?.videoHLS.toString()

//            if (mediaItem != null) {
//                playerM3.player.setMediaItem(mediaItem)
//            }
//            playerM3.player.prepare()
//            playerM3.player.play()

        }
    }

    fun openTag(tag : String,  navigator: Navigator){
        navigator.push(ScreenTags(tag))
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun ComposeTags() {

        val navigator = LocalNavigator.currentOrThrow

        FlowRow(verticalArrangement = Arrangement.Center) {
            tags.mainUploader.forEach {
                ScreenItemTagsModelPornostars(it.name, Color.Black, it.count)
            }

            tags.pornstars.forEach {
                ScreenItemTagsModelPornostars(it.name, Color(0xFFDE2600), it.count)
            }

            tags.tags.sorted().forEach {

                Box(modifier = Modifier.height(28.dp).clickable {
                    openTag(it, navigator)
                }, contentAlignment = Alignment.Center) {
                    Text(
                        it,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = 1.dp, vertical = 1.dp)
                            .background(Color(0xFFD9D9D9))
                            .padding(horizontal = 2.dp, vertical = 0.dp),
                        fontFamily = FontFamily.SansSerif,
                    )
                }
            }

        }

    }


}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleItem {

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(ScreenItemScreenModel.Factory::class)
    abstract fun bindHiltDetailsScreenModelFactory(
        hiltDetailsScreenModelFactory: ScreenItemScreenModel.Factory,
    ): ScreenModelFactory

}





