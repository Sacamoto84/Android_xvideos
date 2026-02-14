package com.client.xvideos.l.ui.element.lazyRowPictureDetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.screen.LocalRootScreenModel
import com.client.xvideos.common.coil.UrlImage
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.ui.element.expandMenu.ExpandMenuType
import com.client.xvideos.l.ui.element.expandMenu.ExpandMenuViewModel
import com.client.xvideos.l.ui.screens.screenFullScreen.L_FullScreenImage
import com.client.xvideos.l.ui.screens.screenFullScreen.fullScreenImageFilteredPicArray
import com.client.xvideos.redgifs.ui.profile.atom.VerticalScrollbar
import com.client.xvideos.redgifs.ui.profile.rememberVisibleRangePercentIgnoringFirstNForLazyStaggeredGrid
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Staggered grid для отображения миниатюр изображений/видео альбома.
 *
 * Поддерживает кликабельные превью, полноэкранный просмотр с возвратом на позицию,
 * контекстное меню, кастомные размеры миниатюр и индикатор прогресса прокрутки.
 *
 * @param host Контейнер состояния и данных альбома
 * @param itemBefore Header-контент перед списком (опционально)
 * @param expandMenu Тип меню дополнительных действий для элементов
 * @param tag Тег для UI-тестов
 */

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun L_LazyRowPictureDetails(
    host: LazyRowPictureDetailsHost,
    itemBefore: @Composable () -> Unit = {},
    expandMenu: ExpandMenuType,
    tag: String = ""
) {
    val expandMenuViewModel: ExpandMenuViewModel = hiltViewModel()
    val navigator = LocalNavigator.currentOrThrow
    val rootVm = LocalRootScreenModel.current
    val haptic = LocalHapticFeedback.current

    val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForLazyStaggeredGrid( host.state, 0 )

    val thumbnailsSize = Settings.thumbalistSize.field.collectAsStateWithLifecycle().value

    /** Показывать ли кнопку "вверх" */
    val showScrollToTop by remember { derivedStateOf { host.state.firstVisibleItemIndex > 2 } }

    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {

        LazyVerticalStaggeredGrid(
            state = host.state,
            columns = StaggeredGridCells.Fixed(host.columns),
            modifier = Modifier.fillMaxSize().then(if (tag.isNotEmpty()) Modifier.testTag(tag) else Modifier)
        ) {

            item(span = StaggeredGridItemSpan.FullLine) {
                itemBefore()
            }

            itemsIndexed(
                host.filteredPic,
                key = { _, item -> item.url_to_original!! }
            ) { index, item ->

                if (item.url_to_original != null) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        val aspect = item.width.toFloat() / item.height

                        val url =
                            if (item.thumbnails.isEmpty()) {
                                item.url_to_original
                            } else {
                                item.thumbnails.firstOrNull { it.size == thumbnailsSize }?.url
                                    ?: item.url_to_original
                            }

                        UrlImage(
                            url = url,
                            contentScale = ContentScale.FillHeight,
                            urlGif = item.url_to_original,
                            modifier = Modifier
                                .padding(2.dp)
                                .aspectRatio(aspect)
                                .clipToBounds()
                                .border(0.5.dp, Color.Gray)
                                .clickable {
                                    fullScreenImageFilteredPicArray =
                                        host.filteredPic.toList()

                                    navigator.push(
                                        L_FullScreenImage(
                                            item = item,
                                            onClose = { position ->
                                                Timber.i("scrollToItem $position")
                                                if (position != -1) {
                                                    rootVm.screenModelScope.launch {
                                                        host.state.scrollToItem(position)
                                                        delay(100)
                                                    }
                                                }
                                            },
                                            albumName = host.albumName,
                                            expandMenu = expandMenu,
                                            autoPlay = true,
                                            isAnimated = item.is_animated,
                                        )
                                    )
                                },
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
                            expandMenuViewModel.ExpandMenu(
                                expandMenu,
                                item,
                                host.albumName
                            )
                        }
                    }
                }
            }
        }

        /** Вертикальный индикатор прокрутки */
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .width(2.dp)
        ) {
            VerticalScrollbar(scrollPercent)
        }

        /** FloatingButton "Вверх" */
        AnimatedVisibility(
            visible = showScrollToTop,
            modifier = Modifier.align(Alignment.BottomEnd),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            FloatingActionButton(
                onClick = {
                    haptic.performHapticFeedback(
                        androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove
                    )
                    scope.launch { host.state.scrollToItem(0) }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Scroll to top"
                )
            }
        }
    }
}


//@OptIn(DelicateCoroutinesApi::class)
//@Composable
//fun L_LazyRowPictureDetails(
//    host: LazyRowPictureDetailsHost,
//    itemBefore: @Composable () -> Unit = {},
//    expandMenu: ExpandMenuType,
//    tag: String = ""
//) {
//
//    val expandMenuViewModel: ExpandMenuViewModel = hiltViewModel()
//
//    val navigator = LocalNavigator.currentOrThrow
//
//    val rootVm = LocalRootScreenModel.current
//
//    val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForLazyStaggeredGrid( host.state, 0 )
//
//    val thumbnailsSize = Settings.thumbalistSize.field.collectAsStateWithLifecycle().value
//
//    val haptic = LocalHapticFeedback.current
//
//    Box(modifier = Modifier.fillMaxSize()) {
//
//        LazyVerticalStaggeredGrid(
//            state = host.state,
//            columns = StaggeredGridCells.Fixed(host.columns),
//            modifier = Modifier.fillMaxSize().then(if (tag.isNotEmpty()) Modifier.testTag(tag) else Modifier)
//        ) {
//
//            item(span = StaggeredGridItemSpan.FullLine) { itemBefore() }
//
//            itemsIndexed(host.filteredPic, key = { index, item -> item.url_to_original!! }
//
//            ) { index, item ->
//
//                if (item.url_to_original != null) {
//                    Box(
//                        modifier = Modifier.fillMaxWidth(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        val aspect = item.width.toFloat() / item.height
//
//                        val url =
//                            if (item.thumbnails.isEmpty()) item.url_to_original else {
//                                item.thumbnails.firstOrNull { it.size == thumbnailsSize }?.url
//                                    ?: item.url_to_original
//                            } //"small" large_thumbnail
//
//
//                        UrlImageGifsCoil(
//                            url,
//                            urlGif = item.url_to_original,
//                            modifier = Modifier
//                                .padding(2.dp)
//                                .aspectRatio(aspect)
//                                .clipToBounds()
//                                .border(0.5.dp, Color.Gray)
//                                .clickable {
//
//                                    fullScreenImageFilteredPicArray = host.filteredPic.toList()
//
//                                    navigator.push(
//                                        FullScreenImage(
//                                            item = item,
//                                            onClose = { it1 ->
//                                                Timber.i("scrollToItem 1 $it1")
//                                                if (it1 != -1) {
//                                                    rootVm.screenModelScope.launch {
//                                                        host.state.scrollToItem(it1)
//                                                        delay(100)
//                                                    }
//                                                }
//                                            },
//                                            albumName = host.albumName,
//                                            //filteredPicArray = host.filteredPic.toList(),
//                                            expandMenu = expandMenu,
//                                            autoPlay = true,
//                                            isAnimated = item.is_animated,
//                                        )
//                                    )
//                                },
//                            // contentScale = ContentScale.FillBounds,
//                            albumName = host.albumName,
//                            isAnimated = item.is_animated
//                        )
//
//                        Text(
//                            index.toString(),
//                            modifier = Modifier
//                                .padding(start = 4.dp)
//                                .align(Alignment.TopStart),
//                            color = ThemeL.textColor,
//                            fontFamily = ThemeL.fontFamilyKarla,
//                            fontSize = 14.sp
//                        )
//
//                        Box(modifier = Modifier.align(Alignment.TopEnd)) {
//                            expandMenuViewModel.ExpandMenu(expandMenu, item, host.albumName)
//                        }
//
//                    }
//                }
//            }
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
//    }
//
//}
