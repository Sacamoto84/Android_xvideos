package com.client.xvideos.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.model.GalleryItem
import com.client.xvideos.room.entity.FavoriteGalleryItem
import com.client.xvideos.screens.dashboards.UrlVideoImageAndLongClick


class ScreenFavorites() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val vm: ScreenFavoritesSM = getScreenModel()

        LaunchedEffect(Unit) {
            vm.addFavorite(
                FavoriteGalleryItem(
                    id = 0,
                    title = "TODO()",
                    duration = "TODO(),",
                    views = "TODO()",
                    channel = "TODO()",
                    previewImage = "TODO()",
                    previewVideo = "TODO()",
                    href = "TODO()",
                    nameProfile = "TODO()",
                    linkProfile = "TODO()",
                )
            )
        }


        val favorites = vm.favorites.collectAsState(initial = emptyList()).value


        Scaffold(modifier = Modifier.fillMaxSize()) {

            LazyColumn(modifier = Modifier.padding(it)) {
                items(favorites) { item ->

                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {

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
                            })
                        }

                        Row(modifier = Modifier.fillMaxWidth().background(Color.Black)) {


                            IconButton(onClick = { vm.removeFavorite(item) }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .size(32.dp)

                                )
                            }

                            IconButton(onClick = {  }) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .size(32.dp)

                                )
                            }

                            IconButton(onClick = {  }) {
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
