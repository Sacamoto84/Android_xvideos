package com.client.xvideos.l.ui.screens.screenAlbum

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.client.common.urlVideImage.UrlImage
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.model.AlbumDetails
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.ui.UrlImageLusciousGifsGlide
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumDialogDeleteAlbum
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoAudiences
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoButtonSaveAlbum
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoDownloadButton
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoFilterButton
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoGreeting
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoTags
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumItemExpandMenu
import com.client.xvideos.l.ui.screens.screenAlbum.atom.FullScreenImage
import com.client.xvideos.l.ui.screens.screenAlbum.atom.ScrollToTopButton
import com.example.ui.screens.profile.atom.VerticalScrollbar
import com.example.ui.screens.profile.rememberVisibleRangePercentIgnoringFirstNForLazyStaggeredGrid
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ExperimentalZoomableApi
import timber.log.Timber
import kotlin.collections.remove

class ScreenLAlbum(val idAlbum: Long) : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalZoomableApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        //val vm: ScreenLAlbumSM = getScreenModel()

        val vm = getScreenModel<ScreenLAlbumSM, ScreenLAlbumSM.Factory> { factory ->
            factory.create(idAlbum)
        }

        val album = vm.albumInfo.collectAsStateWithLifecycle().value

        val parsed =
            vm.albumInfo.collectAsStateWithLifecycle().value?.parsed?.collectAsStateWithLifecycle()?.value

        var selectedImage by remember { mutableStateOf<PicsDetails?>(null) }
        var selectedBounds by remember { mutableStateOf<Rect?>(null) }

        val saved = vm.saved.albums.list.any { it.id == parsed?.id }

        val state = rememberLazyStaggeredGridState()

        val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForLazyStaggeredGrid(state, 0)

        val filteredPic = remember { mutableStateListOf<PicsDetails>() }

        var isDeletingFiles by rememberSaveable { mutableStateOf(false) }
        val deletionState by vm.downloader.deletionProgress.collectAsState()

        val isDownloading = vm.downloader.isDownloading.collectAsStateWithLifecycle().value

        LaunchedEffect(vm.showOnlyAnimated, parsed, album?.albumPicsDetails?.pics?.size) {
            Timber.d("!!! LaunchedEffect vm.showOnlyAnimated = ${vm.showOnlyAnimated} parsed = $parsed")
            if (parsed == null) return@LaunchedEffect

            val allPics = album?.albumPicsDetails?.pics ?: emptyList()
            val newFilteredPics = allPics.filter { it.is_animated == vm.showOnlyAnimated }

            // Если изменился фильтр - полностью пересчитываем список
            val currentFilteredUrls = filteredPic.map { it.url_to_original }.toSet()
            val shouldBeFilteredUrls = newFilteredPics.map { it.url_to_original }.toSet()

            // Проверяем, изменился ли набор URL после фильтрации
            if (currentFilteredUrls != shouldBeFilteredUrls) {
                // Удаляем элементы, которых не должно быть
                val toRemove = filteredPic.filter { it.url_to_original !in shouldBeFilteredUrls }
                filteredPic.removeAll(toRemove.toSet())

                // Добавляем новые элементы
                val existingUrls = filteredPic.map { it.url_to_original }.toSet()
                val toAdd = newFilteredPics.filter { it.url_to_original !in existingUrls }
                filteredPic.addAll(toAdd)
            }
        }

        val folderSize = vm.downloader.folderSize.collectAsStateWithLifecycle().value
        val fileCountDownloaded =
            vm.downloader.fileCountDownloaded.collectAsStateWithLifecycle().value
        val fileCountRaw = vm.downloader.fileCountRaw.collectAsStateWithLifecycle().value
        val fileCountError = vm.downloader.fileCountError.collectAsStateWithLifecycle().value


        /**  ➜ сюда запоминаем элемент, который пользователь хочет удалить  */
        var itemPendingDelete by remember { mutableStateOf<AlbumDetails?>(null) }

        /* ---------- Диалог подтверждения ---------- */
        itemPendingDelete?.let { pending ->
            AlbumDialogDeleteAlbum(pending, onDismiss = { itemPendingDelete = null }, {
                vm.saved.albums.remove(pending)
                itemPendingDelete = null
            })
        }
        /* ---------- /Диалог ---------- */

        Scaffold(
            floatingActionButton = {
                AnimatedVisibility(
                    state.firstVisibleItemIndex > 3 && selectedImage == null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) { ScrollToTopButton(state) }
            },
            bottomBar = {
                if (album?.albumPicsDetails?.percentLoad != 1.0f) {
                    LinearProgressIndicator(
                        progress = { album?.albumPicsDetails?.percentLoad ?: 0f },
                        modifier = Modifier.fillMaxWidth(),
                        color = ProgressIndicatorDefaults.linearColor,
                        trackColor = ProgressIndicatorDefaults.linearTrackColor,
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                    )
                }
            },

            containerColor = ThemeL.greyBackground
        ) { padding ->

            Box( modifier = Modifier.fillMaxSize() )
            {

                LazyVerticalStaggeredGrid(
                    state = state, columns = StaggeredGridCells.Fixed(2), modifier = Modifier.fillMaxSize()
                ) {

                    item(span = StaggeredGridItemSpan.FullLine) {
                        Column(modifier = Modifier.padding(horizontal = 4.dp)) {

                            if (parsed != null) {

                                Row {
                                    UrlImage(
                                        parsed?.cover?.url.toString(),
                                        modifier = Modifier.size(72.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column {
                                        if (parsed != null) {
                                            Text(
                                                parsed.title,
                                                color = ThemeL.textColor,
                                                fontFamily = ThemeL.fontFamilyDMsanss
                                            )
                                            Text(
                                                "${parsed.number_of_animated_pictures} gifs / ${parsed.number_of_pictures} pictures",
                                                color = ThemeL.textColor
                                            )
                                        }
                                    }
                                }

                                AlbumInfoGreeting(parsed)
                                AlbumInfoAudiences(parsed)
                                AlbumInfoTags(parsed)
                                AlbumInfoButtonSaveAlbum(saved, onClick = { if (!saved) { vm.saveAlbum() } else { itemPendingDelete = parsed } })
                                AlbumInfoDownloadButton( folderSize, album, fileCountDownloaded, fileCountError, vm, isDownloading, isDeletingFiles, deletionState, isDeletingChange = { isDeletingFiles = it } )
                                AlbumInfoFilterButton(parsed, vm.showOnlyAnimated, { vm.showOnlyAnimated = it })
                            }
                        }

                    }

                    itemsIndexed(filteredPic) { index, it ->
                        var imageBounds by remember { mutableStateOf<Rect?>(null) }
                        if (it.url_to_original != null) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                val aspect = it.width.toFloat() / it.height

                                UrlImageLusciousGifsGlide(
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
                                            selectedImage = it
                                            selectedBounds = imageBounds
                                        },
                                    contentScale = ContentScale.FillBounds,
                                    albumName = idAlbum.toString()
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

                                AlbumItemExpandMenu(
                                    item = it,
                                    modifier = Modifier.align(Alignment.TopEnd),
                                    onDownload = { it1 -> vm.downloadLike(it1) })


//                                UrlImageLusciousGifs(
//                                    it.url_to_original,
//                                    modifier = Modifier
//                                        .padding(2.dp)
//                                        .aspectRatio(aspect)
//                                        .clipToBounds()
//                                        .border(0.5.dp, Color.Gray)
//                                        .onGloballyPositioned { coordinates ->
//                                            val rect = coordinates.boundsInRoot()
//                                            imageBounds = rect
//                                        }
//                                        .clickable {
//                                            selectedImage = it.url_to_original
//                                            selectedBounds = imageBounds
//                                        },
//                                    contentScale = ContentScale.FillBounds,
//                                    albumName = idAlbum.toString()
//                                )

                                val targetAlpha = if (selectedImage == it) 1f else 0f

                                val animatedAlpha by animateFloatAsState(
                                    targetValue = targetAlpha,
                                    animationSpec = if (targetAlpha == 1f) {
                                        tween(durationMillis = 300) // Появление с задержкой
                                    } else {
                                        tween(durationMillis = 0)   // Мгновенное исчезновение
                                    },
                                    label = "imageAlpha"
                                )

                                if (selectedImage == it) {
                                    Box(
                                        modifier = Modifier
                                            .alpha(animatedAlpha)
                                            .padding(2.dp)
                                            .aspectRatio(aspect)
                                            .clipToBounds()
                                            .border(0.5.dp, Color.Gray)
                                            .background(Color.Gray)
                                    )
                                }
                            }
                        }
                    }

                }

                //---- Скролл ----
                Box( modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd).width(2.dp) ) { VerticalScrollbar(scrollPercent) }

                // Полноэкранное изображение с анимацией
                selectedImage?.let { imageUrl ->
                    FullScreenImage(
                        item = imageUrl,
                        startBounds = selectedBounds,
                        onClose = { selectedImage = null },
                        albumName = idAlbum.toString(),
                        filteredPic = filteredPic,
                        onDownload = {
                            vm.downloadLike(it)
                        }
                    )
                }
            }

        }

    }

}


