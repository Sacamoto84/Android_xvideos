package com.client.xvideos.l.ui

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ExperimentalZoomableApi
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import timber.log.Timber
import javax.inject.Inject

class ScreenLRoot() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalZoomableApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val vm: ScreenLRootSM = getScreenModel()

        val album = vm.album.collectAsStateWithLifecycle().value

        val parsed =
            vm.album.collectAsStateWithLifecycle().value?.parsed?.collectAsStateWithLifecycle()?.value

        val selectZoomItemUrl: String? = null

        var selectedImage by remember { mutableStateOf<String?>(null) }
        var selectedBounds by remember { mutableStateOf<Rect?>(null) }

        val density = LocalDensity.current
        val scope = rememberCoroutineScope()


        Scaffold(
            containerColor = Color(0xFF262626)

        ) {


            //LazyColumn(modifier = Modifier.fillMaxSize().zoomableWithScroll(rememberZoomState())) {

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                //.zoomableWithScroll(rememberZoomState())
            ) {


                item {
                    Column {
                        Row {
                            UrlImage(parsed?.cover?.url.toString(), modifier = Modifier.size(96.dp))
                            Column {
                                Text(parsed?.title.toString(), color = Color.White)
                                Text(parsed?.created.toString(), color = Color.White)
                            }
                        }


                    }

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

                    var imageBounds by remember { mutableStateOf<Rect?>(null) }

                    if (it.url_to_original != null) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {

                            val aspect = it.width.toFloat() / it.height

                            val zoomState = rememberZoomState(maxScale = 3f)

                            UrlImageLusciousGifs(
                                it.url_to_original,
                                modifier = Modifier
                                    .padding(2.dp)
                                    .aspectRatio(aspect)
                                    .clipToBounds()
                                    .border(0.5.dp, Color.Gray)
                                    .onGloballyPositioned { coordinates ->
                                        val rect = coordinates.boundsInRoot()
                                        imageBounds = rect
                                    }
                                    .clickable {
                                        selectedImage = it.url_to_original
                                        selectedBounds = imageBounds
                                    },
                                contentScale = ContentScale.FillBounds
                            )

                        }
                    }
                }

            }


            // Полноэкранное изображение с анимацией
            selectedImage?.let { imageUrl ->
                FullScreenImage(
                    imageUrl = imageUrl,
                    startBounds = selectedBounds,
                    onClose = { selectedImage = null }
                )
            }


        }


    }


}


@Composable
fun FullScreenImage(
    imageUrl: String,
    startBounds: Rect?,
    onClose: () -> Unit
) {
    val zoomState = rememberZoomState(maxScale = 3f)

    val density = LocalDensity.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val screenWidthPx = with(density) { screenWidth.toPx() }
    val screenHeightPx = with(density) { screenHeight.toPx() }

    val startRect = startBounds ?: return

    val startOffsetX = startRect.left
    val startOffsetY = startRect.top
    val startWidth = startRect.width
    val startHeight = startRect.height

    val aspectRatio = startWidth / startHeight

    val targetWidth = screenWidthPx
    val targetHeight = targetWidth / aspectRatio

    val targetScale = targetWidth / startWidth

    // Центрирование изображения по вертикали
    val targetOffsetX = (screenWidthPx - startWidth * targetScale) / 2
    val targetOffsetY = (screenHeightPx - startHeight * targetScale) / 2

    var isClosing by remember { mutableStateOf(false) }

    val scale = remember { Animatable(1f) }
    val offsetX = remember { Animatable(startOffsetX) }
    val offsetY = remember { Animatable(startOffsetY) }

    val widthDp = with(density) { startWidth.toDp() }
    val heightDp = with(density) { startHeight.toDp() }

    LaunchedEffect(Unit) {
        coroutineScope {
            launch { scale.animateTo(targetScale, tween(3000)) }
            launch { offsetX.animateTo(targetOffsetX, tween(3000)) }
            launch { offsetY.animateTo(targetOffsetY, tween(3000)) }
        }
    }

    LaunchedEffect(isClosing) {
        if (isClosing) {
            coroutineScope {
                launch { scale.animateTo(1f, tween(3000)) }
                launch { offsetX.animateTo(startOffsetX, tween(3000)) }
                launch { offsetY.animateTo(startOffsetY, tween(3000)) }
            }
            onClose()
        }
    }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable { isClosing = true },
            contentAlignment = Alignment.TopStart
        ) {

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(offsetX.value.toInt(), offsetY.value.toInt())
                    }
                    .graphicsLayer(
                        scaleX = scale.value,
                        scaleY = scale.value,
                        transformOrigin = TransformOrigin(0f, 0f)
                    )
                    .size(widthDp, heightDp)
            ) {
                UrlImageLusciousGifs(
                    url = imageUrl,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
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
