package com.client.xvideos.l.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.client.common.urlVideImage.UrlImage
import com.client.xvideos.l.Album
import com.client.xvideos.l.Luscious
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import javax.inject.Inject

class ScreenLRoot() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val vm: ScreenLRootSM = getScreenModel()

        val album = vm.album.collectAsStateWithLifecycle().value

        val parsed =
            vm.album.collectAsStateWithLifecycle().value?.parsed?.collectAsStateWithLifecycle()?.value

        val selectZoomItemUrl  : String? = null


        Scaffold(
            containerColor = Color(0xFF262626)

        ) {


            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    UrlImage(album?.thumbnail.toString())
                }
                item {
                    Text(parsed?.title.toString(), color = Color.White)
                }
                item {
                    Text(parsed?.created.toString(), color = Color.White)
                }

                item {


                    Row {
                        Text(
                            parsed?.number_of_animated_pictures.toString() + " gifs",
                            color = Color.White
                        )
                        Text(" / ", color = Color.White)
                        Text(
                            parsed?.number_of_pictures.toString() + " pictures",
                            color = Color.White
                        )
                    }

                }
                item {
                    Row {
                        Text("Genres:", color = Color.White)
                        parsed?.genres?.forEach {
                            Text(it.title, color = Color.White)
                        }

                    }
                }
                item {
                    Row {
                        Text("Audiences:", color = Color.White)
                        parsed?.audiences?.forEach {
                            Text(it.title, color = Color.White)
                        }
                    }
                }

                item {
                    Text("Tags:", color = Color.White)
                    parsed?.tags?.forEach {
                        Text(it.text, color = Color.White)
                    }
                }

                items(album?.pics ?: emptyList()) {

                    if (it.url_to_original != null) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {

                            val aspect = it.width.toFloat()/it.height

                            val zoomState = rememberZoomState(maxScale = 3f)

                            UrlImageLusciousZoomedGifs(
                                it.url_to_original,
                                modifier = Modifier.padding(2.dp).aspectRatio(aspect).clipToBounds().border(0.5.dp, Color.Gray)
                                    .zoomable(zoomState = zoomState, zoomEnabled = true, enableOneFingerZoom = false,
                                    onTap = {


                                    }),
                                contentScale = ContentScale.FillBounds
                            )

                        }
                    }

                }
            }


        }


    }


}


class ScreenLRootSM @Inject constructor(
    val luscious: Luscious
) : ScreenModel {

    val album = MutableStateFlow<Album?>(null)

    init {
        screenModelScope.launch {

            if (!luscious.loggedIn) {
                luscious.login()
            }
            album.value = luscious.getAlbum(336743)//(499900)//(374481)
            album
        }
    }


}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLRootBlock {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenLRootSM::class)
    abstract fun bindScreenLRootScreenModel(hiltListScreenModel: ScreenLRootSM): ScreenModel
}
