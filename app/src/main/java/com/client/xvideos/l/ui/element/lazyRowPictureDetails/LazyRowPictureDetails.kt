package com.client.xvideos.l.ui.element.lazyRowPictureDetails

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.screenModelScope
import com.client.xvideos.common.fresco.DownloadQueueManager
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.ui.screens.screenAlbum.atom.FullScreenImage
import com.client.xvideos.l.ui.screens.LocalRootLScreenModel
import com.client.xvideos.common.fresco.UrlImageLusciousGifsGlide
import com.client.xvideos.common.sharedPref.Settings
import com.client.xvideos.redgifs.ui.profile.atom.VerticalScrollbar
import com.client.xvideos.redgifs.ui.profile.rememberVisibleRangePercentIgnoringFirstNForLazyStaggeredGrid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LazyRowPictureDetails(
    host: LazyRowPictureDetailsHost,
    itemBefore: @Composable () -> Unit = {},
    expandMenu: @Composable (PicsDetails) -> Unit = {},
    expandMenuFullScreen: @Composable (PicsDetails) -> Unit = {}
) {
    val rootVm = LocalRootLScreenModel.current
    val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForLazyStaggeredGrid(
        host.state,
        0
    )

    val thumbnailsSize = Settings.thumbalistSize.field.collectAsStateWithLifecycle().value

    val haptic = LocalHapticFeedback.current

    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {

        LazyVerticalStaggeredGrid(
            state = host.state,
            columns = StaggeredGridCells.Fixed(host.columns),
            modifier = Modifier.fillMaxSize()
        ) {

            item(span = StaggeredGridItemSpan.FullLine) {
                itemBefore()
            }

            itemsIndexed(host.filteredPic) { index, item ->
                var imageBounds by remember { mutableStateOf<Rect?>(null) }

                if (item.url_to_original != null) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        val aspect = item.width.toFloat() / item.height

                        val url = if (item.thumbnails.isEmpty()) item.url_to_original else {
                            item.thumbnails.firstOrNull { it.size == thumbnailsSize }?.url
                                ?: item.url_to_original
                        } //"small" large_thumbnail

                        UrlImageLusciousGifsGlide(
                            url,
                            modifier = Modifier
                                .padding(2.dp)
                                .aspectRatio(aspect)
                                .clipToBounds()
                                .border(0.5.dp, Color.Gray)
                                .onGloballyPositioned { coordinates ->
                                    imageBounds = coordinates.boundsInRoot()
                                }
                                .clickable {
                                    rootVm.showOverlay {
                                        FullScreenImage(
                                            item = item,
                                            startBounds = imageBounds,
                                            onClose = {
                                                if (it != -1) {
                                                    coroutineScope.launch {
                                                        host.state.scrollToItem(it)
                                                        delay(100)
                                                        rootVm.hideOverlay()
                                                    }
                                                }else {
                                                    rootVm.hideOverlay()
                                                }
                                            },
                                            albumName = host.albumName,
                                            filteredPic = host.filteredPic,
                                            expandMenu = expandMenuFullScreen,
                                            autoPlay = true,
                                            isAnimated = item.is_animated,

                                            )
                                    }
                                },
                            // contentScale = ContentScale.FillBounds,
                            albumName = host.albumName,
                            isAnimated = item.is_animated
                        )

                        Text(
                            index.toString(),
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .align(Alignment.TopStart),
                            color = ThemeL.textColor,
                            fontFamily = ThemeL.fontFamilyKarla,
                            fontSize = 14.sp
                        )

                        Box(modifier = Modifier.align(Alignment.TopEnd)) {
                            expandMenu(item)
                        }
                    }
                }
            }
        }

        //---- Скролл ----
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .width(2.dp)
        ) {
            VerticalScrollbar(scrollPercent)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            DownloadQueueManager.cancelAll()
        }
    }

}


//@Composable
//fun LazyRowPictureDetails(
//    host: LazyRowPictureDetailsHost,
//    itemBefore: @Composable () -> Unit = {},
//    expandMenu : @Composable (PicsDetails) -> Unit = {}
//) {
//
//    val rootVm = LocalRootLScreenModel.current
//
//    val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForLazyStaggeredGrid(host.state, 0)
//
//    Box(modifier = Modifier.fillMaxSize()) {
//
//        LazyVerticalStaggeredGrid(
//            state = host.state,
//            columns = StaggeredGridCells.Fixed(host.columns),
//            modifier = Modifier.fillMaxSize()
//        ) {
//
//            item(span = StaggeredGridItemSpan.FullLine) {
//                itemBefore()
//            }
//
//            itemsIndexed(host.filteredPic) { index, it ->
//                var imageBounds by remember { mutableStateOf<Rect?>(null) }
//                if (it.url_to_original != null) {
//                    Box(
//                        modifier = Modifier.fillMaxWidth(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        val aspect = it.width.toFloat() / it.height
//
//                        UrlImageLusciousGifsGlide(
//                            it.url_to_original,
//                            modifier = Modifier
//                                .padding(2.dp)
//                                .aspectRatio(aspect)
//                                .clipToBounds()
//                                .border(0.5.dp, Color.Gray)
//                                .onGloballyPositioned { coordinates ->
//                                    val rect = coordinates.boundsInRoot()
//                                    imageBounds = rect
//                                }
//                                .clickable {
//                                    host.selectedImage = it
//                                    host.selectedBounds = imageBounds
//                                },
//                            contentScale = ContentScale.FillBounds,
//                            albumName = host.albumName
//                        )
//
//                        Text(
//                            index.toString(),
//                            modifier = Modifier.padding(start = 4.dp)
//                                .align(Alignment.TopStart),
//                            color = ThemeL.textColor,
//                            fontFamily = ThemeL.fontFamilyKarla,
//                            fontSize = 14.sp
//                        )
//
//                        Box(modifier = Modifier.align(Alignment.TopEnd))
//                        {
//                            expandMenu(it)
////                            AlbumItemExpandMenu(        item = it,
////                            modifier = Modifier.align(Alignment.TopEnd),
////                            onDownload = { it1 ->
////                                //vm.downloadLike(it1)
////                            })
//                        }
//                        val targetAlpha = if (host.selectedImage == it) 1f else 0f
//
//                        val animatedAlpha by animateFloatAsState(
//                            targetValue = targetAlpha,
//                            animationSpec = if (targetAlpha == 1f) {
//                                tween(durationMillis = 300) // Появление с задержкой
//                            } else {
//                                tween(durationMillis = 0)   // Мгновенное исчезновение
//                            },
//                            label = "imageAlpha"
//                        )
//
//                        if (host.selectedImage == it) {
//                            Box(
//                                modifier = Modifier
//                                    .alpha(animatedAlpha)
//                                    .padding(2.dp)
//                                    .aspectRatio(aspect)
//                                    .clipToBounds()
//                                    .border(0.5.dp, Color.Gray)
//                                    .background(Color.Gray)
//                            )
//                        }
//                    }
//                }
//            }
//
//
//
//        }
//
//        //---- Скролл ----
//        Box(
//            modifier = Modifier
//                .fillMaxHeight()
//                .align(Alignment.CenterEnd)
//                .width(2.dp)
//        ) {
//            VerticalScrollbar(scrollPercent)
//        }
//
//        // Полноэкранное изображение с анимацией
//        host.selectedImage?.let { imageUrl ->
//            FullScreenImage(
//                item = imageUrl,
//                startBounds = host.selectedBounds,
//                onClose = { host.selectedImage = null },
//                albumName = host.albumName,
//                filteredPic = host.filteredPic,
//                onDownload = {
//                    //vm.downloadLike(it)
//                }
//            )
//        }
//    }
//
//
//}

