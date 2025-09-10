package com.client.xvideos.l.ui.screens.screenAlbum

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.common.urlVideImage.UrlImage
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.model.AlbumDetails
import com.client.xvideos.l.ui.element.lazyRowPictureDetails.LazyRowPictureDetails
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumDialogDeleteAlbum
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoAudiences
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoButtonSaveAlbum
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoDownloadButton
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoFilterButton
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoGreeting
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoTags
import com.client.xvideos.l.ui.element.expandMenu.AlbumItemExpandMenu
import com.client.xvideos.l.ui.screens.albumLandingTag.ScreenLAlbumLandingTag
import com.client.xvideos.l.ui.screens.depth
import com.client.xvideos.l.ui.screens.screenAlbum.atom.ScrollToTopButton
import net.engawapg.lib.zoomable.ExperimentalZoomableApi
import timber.log.Timber

class ScreenLAlbum(val idAlbum: Long) : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalZoomableApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        depth = 100

        val navigator = LocalNavigator.currentOrThrow

        val vm = getScreenModel<ScreenLAlbumSM, ScreenLAlbumSM.Factory> { factory -> factory.create(idAlbum) }

        val album = vm.albumInfo.collectAsStateWithLifecycle().value

        val parsed = vm.albumInfo.collectAsStateWithLifecycle().value?.parsed?.collectAsStateWithLifecycle()?.value

        val saved = vm.saved.albums.list.any { it.id == parsed?.id }

        var isDeletingFiles by rememberSaveable { mutableStateOf(false) }
        val deletionState by vm.downloader.deletionProgress.collectAsState()

        val isDownloading = vm.downloader.isDownloading.collectAsStateWithLifecycle().value

        LaunchedEffect(vm.showOnlyAnimated, parsed, album?.albumPicsDetails?.pics?.size) {
            Timber.d("!!! LaunchedEffect vm.showOnlyAnimated = ${vm.showOnlyAnimated} parsed = $parsed")
            if (parsed == null) return@LaunchedEffect

            val allPics = album?.albumPicsDetails?.pics ?: emptyList()
            val newFilteredPics = allPics.filter { it.is_animated == vm.showOnlyAnimated }

            // Если изменился фильтр - полностью пересчитываем список
            val currentFilteredUrls = vm.host.filteredPic.mapNotNull{ it.url_to_original }.toSet()
            val shouldBeFilteredUrls = newFilteredPics.mapNotNull{ it.url_to_original }.toSet()

            // Проверяем, изменился ли набор URL после фильтрации
            if (currentFilteredUrls != shouldBeFilteredUrls) {
                // Удаляем элементы, которых не должно быть
                val toRemove =
                    vm.host.filteredPic.filter { it.url_to_original !in shouldBeFilteredUrls }
                vm.host.filteredPic.removeAll(toRemove.toSet())

                // Добавляем новые элементы
                val existingUrls = vm.host.filteredPic.mapNotNull{ it.url_to_original }.toSet()
                val toAdd = newFilteredPics.filter { it.url_to_original !in existingUrls }
                vm.host.filteredPic.addAll(toAdd)
            }
        }

        val folderSize = vm.downloader.folderSize.collectAsStateWithLifecycle().value
        val fileCountDownloaded =
            vm.downloader.fileCountDownloaded.collectAsStateWithLifecycle().value
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
                    vm.host.state.firstVisibleItemIndex > 3 && vm.host.selectedImage == null,
                    enter = fadeIn(), exit = fadeOut()
                ) { ScrollToTopButton(vm.host.state) }
            },
            bottomBar = {
                if (album?.albumPicsDetails?.percentLoad != 1.0f) {
                    LinearProgressIndicator(
                        progress = {
                            album?.albumPicsDetails?.percentLoad ?: 0f
                        },
                        modifier = Modifier.fillMaxWidth(),
                        color = ProgressIndicatorDefaults.linearColor,
                        trackColor = ProgressIndicatorDefaults.linearTrackColor,
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                    )
                }
            },

            containerColor = ThemeL.greyBackground
        ) { padding ->

            LazyRowPictureDetails(
                host = vm.host,
                expandMenu = { AlbumItemExpandMenu( item = it, onDownload = { it1 ->  vm.downloadLike(it1) } ,
                    onDownloadCrypto = { it1 ->  vm.downloadLikeCrypto(it1) } ) },
                expandMenuFullScreen = { AlbumItemExpandMenu( item = it, onDownload = { it1 ->  vm.downloadLike(it1) }) },
                itemBefore = {
                    Column(modifier = Modifier.padding(horizontal = 4.dp)) {

                        if (parsed != null) {

                            Row {
                                UrlImage(
                                    parsed.cover.url,
                                    modifier = Modifier.size(72.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
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

                            AlbumInfoGreeting(parsed)
                            AlbumInfoAudiences(parsed)
                            AlbumInfoTags(parsed) { navigator.push(ScreenLAlbumLandingTag(it)) }
                            AlbumInfoButtonSaveAlbum(saved, onClick = { if (!saved) { vm.saveAlbum() } else { itemPendingDelete = parsed } })
                            AlbumInfoDownloadButton( folderSize, album, fileCountDownloaded, fileCountError, vm, isDownloading, isDeletingFiles, deletionState, isDeletingChange = { isDeletingFiles = it })
                            AlbumInfoFilterButton( parsed, vm.showOnlyAnimated, { vm.showOnlyAnimated = it })
                        }
                    }
                }
            )

        }

    }

}


