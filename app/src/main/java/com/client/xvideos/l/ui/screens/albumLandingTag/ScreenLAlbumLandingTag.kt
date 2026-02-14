package com.client.xvideos.l.ui.screens.albumLandingTag

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.screen.depth
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.model.AlbumListFilter
import com.client.xvideos.l.model.Landing_page_albumSection
import com.client.xvideos.l.model.Landing_page_albumType
import com.client.xvideos.l.model.enum.AlbumType
import com.client.xvideos.l.model.enum.ContentId
import com.client.xvideos.l.net.Luscious
import com.client.xvideos.l.ui.element.AlbumListItem
import com.client.xvideos.l.ui.screens.screenAlbum.ScreenLAlbum
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ExperimentalZoomableApi
import timber.log.Timber

class ScreenLAlbumLandingTag(val tag: String) : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalZoomableApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val vm = getScreenModel<ScreenLAlbumLandingTagSM, ScreenLAlbumLandingTagSM.Factory> { factory ->  factory.create(tag) }

        val items = vm.albumTopHits.collectAsState().value?.sections

        val title = vm.albumTopHits.collectAsState().value?.title

        val haptic = LocalHapticFeedback.current


        Scaffold(
            containerColor = ThemeL.greyBackground,
        ) {

            LazyColumn(state = vm.state) {

                item{
                    if (title != null){

                        Text(
                            "Tag: $title",
                            color = ThemeL.textColor,
                            fontSize = 32.sp,
                            fontFamily = ThemeL.fontFamilyKarla,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp, top = 16.dp)
                        )

                    }
                }

                items(items?.size ?: 0) { index ->
                    val item = items?.get(index)
                    if (item == null) return@items

                    Text(
                        item.title,
                        color = ThemeL.textColor,
                        fontSize = 24.sp,
                        fontFamily = ThemeL.fontFamilyKarla,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp, top = 16.dp)
                    )

                    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

                    FlowRow(
                        maxItemsInEachRow = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val itemWidth = (screenWidth - 8.dp) / 3  // учитываем padding
                        item.items.dropLast(1).forEach { item ->
                            Box(
                                modifier = Modifier
                                    .width(itemWidth)
                                    .padding(vertical = 2.dp)
                            ) {
                                AlbumListItem(
                                    modifier = Modifier.fillMaxWidth(),
                                    title = item.title,
                                    coverUrl = item.cover.url,
                                    numberOfAnimatedPictures = item.numberOfAnimatedPictures,
                                    numberOfPictures = item.numberOfPictures,
                                    onClick = { navigator.push(ScreenLAlbum(item.id.toLong())) }
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .padding(horizontal = 4.dp)
                            .fillMaxWidth()
                            .height(40.dp)
                            .border(2.dp, ThemeL.grey3, RoundedCornerShape(8.dp))
                            .clickable(onClick = {

                                val filter = vm.createFilter(item)

                               // navigator.push(ScreenLAlbumList.create(filter))


                            })
                        ,
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "See All >",
                            color = ThemeL.textColor,
                            modifier = Modifier,
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp,
                            fontFamily = ThemeL.fontFamilyKarla,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                item{
                    Spacer(Modifier.height(64.dp))
                }
            }
        }
    }
}

class ScreenLAlbumLandingTagSM @AssistedInject constructor(
    @Assisted val tag: String,
    val luscious: Luscious
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(tag: String): ScreenLAlbumLandingTagSM
    }

    val state = LazyListState()

    var albumTopHits = MutableStateFlow<Landing_page_albumType?>(null)

    init {
        Timber.i("iii ScreenLAlbumLandingTagSM init")
        screenModelScope.launch {
            albumTopHits.value = luscious.getLandingPageAlbumTag(tag).getOrThrow()
        }

        depth = 100
    }

    override fun onDispose() {
        super.onDispose()
        Timber.i("iii ScreenLAlbumLandingTagSM onDispose")
    }

    //section title

    //Hentai Manga
    //Hentai Pictures
    //Porn Pictures

    fun createFilter (item: Landing_page_albumSection): AlbumListFilter {

        val title = item.title

        val albumType = when (title) {
            "Hentai Manga" -> AlbumType.Manga
            "Hentai Pictures" -> AlbumType.Pictures
            "Porn Pictures" -> AlbumType.Pictures
            else -> AlbumType.Pictures
        }

        val contentId = when (title) {
            "Hentai Manga" -> ContentId.All
            "Hentai Pictures" -> ContentId.Hentai
            "Porn Pictures" -> ContentId.RealPeople
            else -> ContentId.All
        }

        val f = AlbumListFilter(
            display = "date_trending",
            album_type = albumType,
            content_id = contentId,
            tagPlus = listOf("$tag")
        )

        return f

    }

}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLAlbumLandingTag {

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(ScreenLAlbumLandingTagSM.Factory::class)
    abstract fun bindHiltLandingTagScreenModelFactory(
        hiltDetailsScreenModelFactory: ScreenLAlbumLandingTagSM.Factory
    ): ScreenModelFactory

}