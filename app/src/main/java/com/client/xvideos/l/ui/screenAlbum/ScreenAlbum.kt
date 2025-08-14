package com.client.xvideos.l.ui.screenAlbum

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.client.common.urlVideImage.UrlImage
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.ui.FullScreenImage
import com.client.xvideos.l.ui.UrlImageLusciousGifs
import com.client.xvideos.l.ui.screenAlbum.atom.AlbumInfoAudiences
import com.client.xvideos.l.ui.screenAlbum.atom.AlbumInfoGreeting
import com.client.xvideos.l.ui.screenAlbum.atom.AlbumInfoTags
import net.engawapg.lib.zoomable.ExperimentalZoomableApi

class ScreenLAlbum(val idAlbum : Long) : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalZoomableApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        //val vm: ScreenLAlbumSM = getScreenModel()

        val vm = getScreenModel<ScreenLAlbumSM, ScreenLAlbumSM.Factory> { factory ->
            factory.create(idAlbum)
        }


        val album = vm.album.collectAsStateWithLifecycle().value

        val parsed = vm.album.collectAsStateWithLifecycle().value?.parsed?.collectAsStateWithLifecycle()?.value

        var selectedImage by remember { mutableStateOf<String?>(null) }
        var selectedBounds by remember { mutableStateOf<Rect?>(null) }

        Scaffold(
            containerColor = ThemeL.greyBackground
        ) {

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(6),
                modifier = Modifier.fillMaxSize()
            ) {

                item( span =  StaggeredGridItemSpan.FullLine){
                    Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                        Row {
                            UrlImage(parsed?.cover?.url.toString(), modifier = Modifier.size(72.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                if (parsed != null) {
                                    Text( parsed.title, color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyDMsanss )
                                    Text("${parsed.number_of_animated_pictures} gifs / ${parsed.number_of_pictures} pictures", color = ThemeL.textColor )
                                }
                            }
                        }
                        if (parsed != null) { AlbumInfoGreeting(parsed)  }
                        if (parsed != null) { AlbumInfoAudiences(parsed) }
                    }

                }

//                item(span = StaggeredGridItemSpan.FullLine) { if (parsed != null) { AlbumInfoGreeting(parsed)  } }
//                item(span = StaggeredGridItemSpan.FullLine) { if (parsed != null) { AlbumInfoAudiences(parsed) } }
                item(span = StaggeredGridItemSpan.FullLine) { if (parsed != null) { AlbumInfoTags(parsed) } }



                items(album?.albumPicsDetails?.pics ?: emptyList()) {
                    var imageBounds by remember { mutableStateOf<Rect?>(null) }
                    if (it.url_to_original != null) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            val aspect = it.width.toFloat() / it.height
                            UrlImageLusciousGifs(
                                it.url_to_original,
                                modifier = Modifier.padding(2.dp).aspectRatio(aspect).clipToBounds().border(0.5.dp, Color.Gray)
                                    .onGloballyPositioned { coordinates ->
                                        val rect = coordinates.boundsInRoot()
                                        imageBounds = rect
                                    }
                                    .clickable {
                                        selectedImage = it.url_to_original
                                        selectedBounds = imageBounds
                                    },
                                contentScale = ContentScale.FillBounds,
                            )

                            val targetAlpha = if (selectedImage == it.url_to_original) 1f else 0f

                            val animatedAlpha by animateFloatAsState(
                                targetValue = targetAlpha,
                                animationSpec = if (targetAlpha == 1f) {
                                    tween(durationMillis = 300) // Появление с задержкой
                                } else {
                                    tween(durationMillis = 0)   // Мгновенное исчезновение
                                },
                                label = "imageAlpha"
                            )

                            if (selectedImage == it.url_to_original) { Box( modifier = Modifier.alpha(animatedAlpha).padding(2.dp).aspectRatio(aspect).clipToBounds().border(0.5.dp, Color.Gray).background(Color.Gray) ) }
                        }
                    }
                }

            }

            // Полноэкранное изображение с анимацией
            selectedImage?.let { imageUrl ->
                FullScreenImage(
                    imageUrl = imageUrl,
                    startBounds = selectedBounds,
                    onClose = { selectedImage = null },
                )
            }

        }

    }

}
