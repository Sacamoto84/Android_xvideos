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
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.common.coil.UrlImage
import com.client.xvideos.screenRoot.depth
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.model.AlbumDetails
import com.client.xvideos.l.ui.element.expandMenu.ExpandMenuType
import com.client.xvideos.l.ui.element.lazyRowPictureDetails.L_LazyRowPictureDetails
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumDialogDeleteAlbum
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoAudiences
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoButtonSaveAlbum
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoDownloadButton
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoFilterButton
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoGreeting
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoTags
import com.client.xvideos.l.ui.screens.albumLandingTag.ScreenLAlbumLandingTag
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

        val parsed = vm.albumInfo.collectAsStateWithLifecycle().value?.albumInfo?.collectAsStateWithLifecycle()?.value

        val saved = vm.saved.albums.list.any { it.id == parsed?.id }

        var isDeletingFiles by rememberSaveable { mutableStateOf(false) }
        val deletionState by vm.downloader.deletionProgress.collectAsState()

        val isDownloading = vm.downloader.isDownloading.collectAsStateWithLifecycle().value

        LaunchedEffect(vm.showOnlyAnimated, parsed, album?.albumPicsDetails?.pics?.size) {

            Timber.d("!!! iiii ScreenLAlbum LaunchedEffect animated = ${vm.showOnlyAnimated} size:${album?.albumPicsDetails?.pics?.size}")

            if (parsed == null) return@LaunchedEffect

            val allPics = album?.albumPicsDetails?.pics?.toList() ?: emptyList()

            val newFilteredAnimatedPics = allPics.filter { it.is_animated } //Список анимированных елементов
            val newFilteredNoAnimatedPics = allPics.filter { !it.is_animated } //Список анимированных елементов

            if (vm.showOnlyAnimated) {
                //val a = vm.host.filteredPic.toMutableList()
                //a.removeAll(newFilteredNoAnimatedPics)
                vm.host.filteredPic.clear()
                vm.host.filteredPic.addAll(newFilteredAnimatedPics)
            } else {
                vm.host.filteredPic.clear()
                vm.host.filteredPic.addAll(allPics)
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
                        progress = { album?.albumPicsDetails?.percentLoad ?: 0f },
                        modifier = Modifier.fillMaxWidth(),
                        color = ProgressIndicatorDefaults.linearColor,
                        trackColor = ProgressIndicatorDefaults.linearTrackColor,
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                    )
                }
            },

            containerColor = ThemeL.greyBackground
        ) { padding ->

            L_LazyRowPictureDetails(
                host = vm.host,
                expandMenu = ExpandMenuType.ALBUM,
                itemBefore = {
                    Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                        if (parsed != null) {

                            Row {
                                UrlImage( parsed.cover.url, modifier = Modifier.size(72.dp) )
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text( parsed.title, color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyDMsanss )
                                    Text( "${parsed.number_of_animated_pictures} gifs / ${parsed.number_of_pictures} pictures", color = ThemeL.textColor )
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


