package com.client.xvideos.l.ui.screens.screenAlbum

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.client.xvideos.l.ui.UrlImageLusciousGifs
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoAudiences
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoDownload
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoGreeting
import com.client.xvideos.l.ui.screens.screenAlbum.atom.AlbumInfoTags
import com.client.xvideos.l.ui.screens.screenAlbum.atom.FullScreenImage
import com.example.ui.screens.profile.atom.VerticalScrollbar
import com.example.ui.screens.profile.atom.VerticalScrollbar2
import com.example.ui.screens.profile.rememberVisibleRangePercentIgnoringFirstNForLazyStaggeredGrid
import net.engawapg.lib.zoomable.ExperimentalZoomableApi

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

        val album = vm.album.collectAsStateWithLifecycle().value

        val parsed =
            vm.album.collectAsStateWithLifecycle().value?.parsed?.collectAsStateWithLifecycle()?.value

        var selectedImage by remember { mutableStateOf<String?>(null) }
        var selectedBounds by remember { mutableStateOf<Rect?>(null) }

        val saved = vm.saved.albums.list.any { it.id == parsed?.id }

        val state = rememberLazyStaggeredGridState()

        val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForLazyStaggeredGrid(
            state, 0
        )


        /**  ➜ сюда запоминаем элемент, который пользователь хочет удалить  */
        var itemPendingDelete by remember { mutableStateOf<AlbumDetails?>(null) }

        /* ---------- Диалог подтверждения ---------- */
        itemPendingDelete?.let { pending ->
            AlertDialog(
                icon = { UrlImage(pending.cover.url, modifier = Modifier.size(96.dp)) },
                onDismissRequest = { itemPendingDelete = null },
                title = { com.composeunstyled.Text( "Удалить Альбом?", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                text = {
                    com.composeunstyled.Text(buildAnnotatedString {
                        append("Удалить «")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(pending.title)}
                        append("» из сохранённых?")
                    }, fontSize = 16.sp)
                },

                confirmButton = {
                    TextButton(
                        onClick = {
                            vm.saved.albums.remove(pending)   // удаляем
                            itemPendingDelete = null          // закрываем диалог
                        }
                    ) {
                        com.composeunstyled.Text( "Удалить", fontSize = 16.sp, color = Color(0xFF6552A5) )
                    }
                },
                dismissButton = {
                    TextButton( onClick = { itemPendingDelete = null } ) { com.composeunstyled.Text( "Отмена", fontSize = 16.sp, color = Color(0xFF6552A5) ) }
                },

                /* Доп. стили при желании */
                containerColor = Color(0xFFEBE6EE)
            )
        }
        /* ---------- /Диалог ---------- */

        Scaffold(

            bottomBar = {

//                Column {
//
//                    Box( modifier = Modifier.fillMaxWidth().height(48.dp).background(ThemeL.grey7) ) {
//
//                    }
//
//                }

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


            Box(modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()) {

            LazyVerticalStaggeredGrid(
                state = state,
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
            ) {

                item(span = StaggeredGridItemSpan.FullLine) {
                    Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                        Row {
                            UrlImage(parsed?.cover?.url.toString(), modifier = Modifier.size(72.dp))
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
                        if (parsed != null) {
                            AlbumInfoGreeting(parsed)
                        }
                        if (parsed != null) {
                            AlbumInfoAudiences(parsed)
                        }
                    }

                }

//                item(span = StaggeredGridItemSpan.FullLine) { if (parsed != null) { AlbumInfoGreeting(parsed)  } }
//                item(span = StaggeredGridItemSpan.FullLine) { if (parsed != null) { AlbumInfoAudiences(parsed) } }
                item(span = StaggeredGridItemSpan.FullLine) {
                    if (parsed != null) {
                        AlbumInfoTags(parsed)
                    }
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    if (parsed != null) {
                        AlbumInfoDownload(parsed)
                    }
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    if (parsed != null) {


                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .padding(top = 2.dp, bottom = 4.dp)
                                .height(46.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .border(
                                    1.dp, ThemeL.grey3,
                                    RoundedCornerShape(4.dp)
                                ).background(if (!saved) ThemeL.red else ThemeL.grey6)
                                .clickable(onClick = {
                                    if (!saved) {
                                        vm.saveAlbum()
                                    } else {
                                        itemPendingDelete = parsed
                                    }
                                }),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                            ) {
                                if (vm.albumSaveWait) CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    trackColor = Color.DarkGray
                                ) else
                                    Box(modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                if (!saved)
                                    Text(
                                        "Save Album",
                                        color = Color.White,
                                        fontFamily = ThemeL.fontFamilyKarla
                                    )
                                else
                                    Text(
                                        "Remove Album",
                                        color = Color.White,
                                        fontFamily = ThemeL.fontFamilyKarla
                                    )
                            }
                        }
                    }


                }

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

                            if (selectedImage == it.url_to_original) {
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
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .width(2.dp)
            ) {
                VerticalScrollbar(scrollPercent)
                //VerticalScrollbar2(scrollPercent)
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

}
