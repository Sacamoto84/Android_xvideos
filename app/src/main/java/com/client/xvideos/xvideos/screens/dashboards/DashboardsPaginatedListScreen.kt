package com.client.xvideos.xvideos.screens.dashboards

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.feature.country.currentCountriesUpdate
import com.client.xvideos.feature.net.readHtmlFromURL
import com.client.xvideos.xvideos.model.GalleryItem
import com.client.xvideos.xvideos.parcer.parserListVideo
import com.client.xvideos.urlStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

private suspend fun openNew(numberScreen: Int = 0): SnapshotStateList<GalleryItem> {
    val currentNumberScreen = numberScreen.coerceIn(0, 19999)
    val url = urlStart + if (currentNumberScreen == 0) "" else "/new/${currentNumberScreen}"
    Timber.i("!!! openNew numberScreen:$numberScreen url:$url")
    return parserListVideo(readHtmlFromURL(url)).toMutableStateList()
}


/**
 *
 * ![Логотип Markdown](https://ah-img.luscious.net/Joking42/499900/sample_3941cb87cea03_01J9ZXQ9XTDKY6PQ01ZRWF1FFZ.1680x0.jpg)
 *
 *
 */
@Composable
fun DashboardsPaginatedListScreen(pageIndex: Int, vm: ScreenDashBoardsScreenModel) {

    println("!!! DashboardsPaginatedListScreen pageIndex:$pageIndex")


    val l = remember { mutableStateListOf<GalleryItem>() }

    LaunchedEffect(key1 = pageIndex, key2 = currentCountriesUpdate) {
        withContext(Dispatchers.IO) {
            l.clear()
            l.addAll(openNew(pageIndex).filter { !it.href.contains("THUMBNUM") })
            l
        }
    }

    val navigator = LocalNavigator.currentOrThrow

    val itemsPerRow =
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else if (vm.countRow.collectAsState().value) 2 else 1


    val favorites =
        vm.getAll.collectAsStateWithLifecycle(emptyList()).value



    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(l.chunked(itemsPerRow))
        { row ->

            Row(modifier = Modifier.fillMaxWidth()) {

                row.forEachIndexed { _, cell ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(352f / 198f)
                            .padding(1.dp)
                            .background(Color.DarkGray)
                    ) {
                        //Отобразить карточку картинка видео
                        UrlVideoImageAndLongClick(
                            cell, onLongClick = {
                                //Открыть экран прлеера
                                vm.openVideoPlayer(urlStart + cell.href, navigator)
                            },
                            onDoubleClick = {

                            }
                        )
                        {

                            Box(modifier = Modifier.fillMaxSize()) {
                                val offsetY = (-3).dp

                                //Продолжительность видео
                                Text(
                                    text = cell.duration.dropLast(1),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .offset(1.dp, offsetY + 1.dp),
                                    textAlign = TextAlign.Right,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )

                                Text(
                                    text = cell.duration.dropLast(1),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .offset(0.dp, offsetY),
                                    textAlign = TextAlign.Right,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )


                            }

                            //Название канала
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .background(Color(0x60000000)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cell.channel,
                                    modifier = Modifier
                                        .align(Alignment.Center), fontSize = 14.sp,
                                    color = Color.White
                                )
                            }

                            if (favorites.any { it.favorite.id == cell.id }) {
                                //Индикатор что видео в фаворитах

                                Box(modifier = Modifier.align(Alignment.BottomStart)) { IconFavorite(vm) }
                            }

                            Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                                DropMenu(
                                    cell,
                                    vm
                                )
                            }

                        }
                    }
                }
                // Если элементов в строке меньше, чем itemsPerRow, добавляем пустые ячейки
                if (row.size < itemsPerRow) {
                    repeat(itemsPerRow - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}


@Composable
private fun IconFavorite(vm: ScreenDashBoardsScreenModel) {

    val count = vm.countRow.collectAsStateWithLifecycle().value
    val size = if (count) 26.dp else 32.dp

    //Индикатор что видео в фаворитах
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center
    ) {

        IconButton(onClick = { }, enabled = false) {

            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Like",
                tint = Color.Black,//grayColor(0xC6),
                modifier = Modifier
                    .size(size)
                    .offset(1.dp, 1.dp)
            )

            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Like",
                tint = Color.White,//grayColor(0xC6),
                modifier = Modifier
                    .padding(0.dp)
                    .size(size)
            )


        }
    }

}

@Composable
private fun DropMenu(cell: GalleryItem, vm: ScreenDashBoardsScreenModel) {

    var expanded by remember { mutableStateOf(false) }

    val count = vm.countRow.collectAsStateWithLifecycle().value

    val size = if (count) 26.dp else 32.dp

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center
    ) {

        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "Localized description",
                tint = Color.Black, modifier = Modifier.size(size).offset(1.dp, 1.dp)
            )

            Icon(
                Icons.Default.MoreVert,
                contentDescription = "Localized description",
                tint = Color.White, modifier = Modifier.size(size).offset(0.dp, 0.dp)
            )

        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color(0xFFF2EDF7),
            shadowElevation = 2.dp, tonalElevation = 16.dp

        )
        {

            val isFavorite = vm.isFavorite(cell.id)

            DropdownMenuItem(
                text = {

                    Text("Избранное")
                },
                onClick = {
                    expanded = false
                    scope.launch {
                        delay(50)
                        when(isFavorite) {
                            true ->  vm.removeFavorite(cell.id)
                            false -> {
                                vm.addFavorite(cell)
                            }
                        }
                    }
                },
                leadingIcon = {
                    Icon(
                        if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null
                    )
                }
            )
            DropdownMenuItem(
                text = { Text("TODO") },
                onClick = { /* Handle settings! */ },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Save,
                        contentDescription = null
                    )
                }
            )

            DropdownMenuItem(
                text = { Text("TODO") },
                onClick = { /* Handle settings! */ },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Save,
                        contentDescription = null
                    )
                }
            )

            HorizontalDivider()
            DropdownMenuItem(
                text = { Text("TODO") },
                onClick = { /* Handle send feedback! */ },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Email,
                        contentDescription = null
                    )
                },
                trailingIcon = { Text(">", textAlign = TextAlign.Center) }
            )
        }

    }
}
