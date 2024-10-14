package com.client.xvideos.screens.dashboards

import android.content.res.Configuration
import android.provider.SyncStateContract.Columns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.client.xvideos.model.GalleryItem
import com.client.xvideos.net.readHtmlFromURL
import com.client.xvideos.parcer.parserListVideo
import com.client.xvideos.urlStart
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private suspend fun openNew(numberScreen: Int = 0): SnapshotStateList<GalleryItem> {
    val currentNumberScreen = numberScreen.coerceIn(1, 19999)
    val url = urlStart + if (currentNumberScreen == 1) "" else "/new/${currentNumberScreen - 1}"
    return parserListVideo(readHtmlFromURL(url)).toMutableStateList()
}

@Composable
fun DashboardsPaginatedListScreen(pageIndex: Int, vm: ScreenDashBoardsScreenModel) {

    val context = LocalContext.current

    val l = remember { mutableStateListOf<GalleryItem>() }

    LaunchedEffect(pageIndex) {
        withContext(Dispatchers.IO) {
            l.clear()
            l.addAll(openNew(pageIndex).filter { !it.link.contains("THUMBNUM") })
        }
    }


    val navigator = LocalNavigator.currentOrThrow

    //val items = remember { mutableStateListOf<Int>() }
    //val listState = rememberLazyListState()
    //var isLoading by remember { mutableStateOf(false) }
    //val coroutineScope = rememberCoroutineScope()

    val numberOfPages = 3 // Количество страниц (списков)

    val itemsPerRow =
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else 2

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        items(l.chunked(itemsPerRow))
        { row ->

            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { cell ->

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(352f / 198f)
                            .padding(1.dp)
                            .background(Color.DarkGray)
                            //.border(1.dp, Color.Black)
                            //.fillParentMaxWidth()
                            .clickable { vm.openItem(urlStart + cell.link, navigator) },
                    ) {


                        val imageRequest = ImageRequest.Builder(context)
                            .data(cell.previewImage)
                            //.listener(listener)
                            //.dispatcher(Dispatcher.IO)
                            .memoryCacheKey(cell.previewImage)
                            .diskCacheKey(cell.previewImage)
                            //.placeholder(placeholder)
                            //.error(placeholder)
                            //.fallback(placeholder)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build()


                        CoilImage(
                            imageRequest = { imageRequest },
                            imageOptions = ImageOptions(
                                contentScale = ContentScale.FillWidth,
                                alignment = Alignment.Center
                            ),
                            //contentDescription = null,
                            //contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                //.weight(1f)
                                .fillMaxWidth()
                        )


                        val offsetY = (-6).dp
                        //PlayerLite(it.previewVideo)
                        Text(
                            text = cell.duration.dropLast(1),
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(1.dp, offsetY+ 1.dp),
                            textAlign = TextAlign.Right,
                            fontSize = 14.sp,
                            color = Color.Black
                        )

                        Text(
                            text = cell.duration.dropLast(1),
                            modifier = Modifier.fillMaxWidth().offset(0.dp, offsetY),
                            textAlign = TextAlign.Right,
                            fontSize = 14.sp,
                            color = Color.White
                        )

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


//    LazyColumn(
//        state = listState,
//        modifier = Modifier.fillMaxSize()
//    ) {
//
//        items(items.size) { index ->
//            Text(
//                text = "Item $index in List $pageIndex",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp), color = Color.White
//            )
//        }
//
//        // Показ индикатора загрузки внизу списка
//        if (isLoading) {
//            item {
//                CircularProgressIndicator(
//                    modifier = Modifier
//                        .padding(16.dp)
//                        //.align(Alignment.CenterHorizontally)
//                )
//            }
//        }
//    }

//    // Проверка, если пользователь дошел до конца списка
//    LaunchedEffect(listState) {
//
//        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
//            .collect { lastVisibleItemIndex ->
//                if (lastVisibleItemIndex == items.size - 1 && !isLoading) {
//                    coroutineScope.launch {
//                        isLoading = true
//                        loadMoreItems(items)
//                        isLoading = false
//                    }
//                }
//            }
//
//    }


//    LaunchedEffect(Unit) {
//        l.clear()
//        l = openNew(pageIndex)
//        l = openNew(pageIndex)
//    }

}


suspend fun loadMoreItems(items: MutableList<Int>, batchSize: Int = 20) {
    //delay(1000) // Имитация задержки для загрузки данных
    val start = items.size
    val end = start + batchSize
    items.addAll((start until end).toList())
}