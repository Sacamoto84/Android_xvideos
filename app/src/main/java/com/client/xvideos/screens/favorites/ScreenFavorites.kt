package com.client.xvideos.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.model.GalleryItem
import com.client.xvideos.feature.room.entity.FavoriteGalleryItem
import com.client.xvideos.screens.dashboards.UrlVideoImageAndLongClick
import com.client.xvideos.screens.profile.ScreenProfile
import com.composables.core.HorizontalSeparator


class ScreenFavorites() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val vm: ScreenFavoritesSM = getScreenModel()

        val favorites = vm.favorites.collectAsState(initial = emptyList()).value

        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {

            Column {


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        "Избранное",
                        color = Color.White,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )



                    IconButton(onClick = { navigator.push(ScreenProfile())}) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(32.dp)

                        )
                    }
                }
                HorizontalSeparator(color = Color(0xFF9E9E9E))
            }

        }) {

            LazyColumn(modifier = Modifier.padding(it)) {
                items(favorites) { item ->

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                //.weight(1f
                                .aspectRatio(352f / 198f)
                                //.padding(1.dp)
                                .background(Color.DarkGray)
                        ) {
                            val cell = GalleryItem(
                                id = item.id,
                                title = item.title,
                                duration = item.duration,
                                views = item.views,
                                channel = item.channel,
                                previewImage = item.previewImage,
                                previewVideo = item.previewVideo,
                                href = item.href,
                                nameProfile = item.nameProfile,
                                linkProfile = item.linkProfile
                            )
                            UrlVideoImageAndLongClick(cell, onLongClick = {
                                //vm.openItem(urlStart + cell.link, navigator)
                            }, onDoubleClick = {}){

                                Box(modifier = Modifier.fillMaxSize()) {
                                    val offsetY = (-3).dp

                                    //Продолжительность видео
                                    Text(
                                        text = item.duration.dropLast(1),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .offset(1.dp, offsetY + 1.dp),
                                        textAlign = TextAlign.Right,
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )

                                    Text(
                                        text = item.duration.dropLast(1),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .offset(0.dp, offsetY),
                                        textAlign = TextAlign.Right,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )

                                }

                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black)
                        ) {


                            IconButton(onClick = { vm.removeFavorite(item) }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .size(32.dp)

                                )
                            }

                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .size(32.dp)

                                )
                            }

                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowCircleDown,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .size(32.dp)

                                )
                            }

                        }

                    }
                }
            }

        }
    }
}
