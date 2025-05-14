package com.client.xvideos.screens.dashboards

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.model.GalleryItem
import com.client.xvideos.net.readHtmlFromURL
import com.client.xvideos.parcer.parserListVideo
import com.client.xvideos.screens.dashboards.molecule.DashBoardVideoImage
import com.client.xvideos.urlStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

private suspend fun openNew(numberScreen: Int = 0): SnapshotStateList<GalleryItem> {
    val currentNumberScreen = numberScreen.coerceIn(0, 19999)
    val url = urlStart + if (currentNumberScreen == 0) "" else "/new/${currentNumberScreen}"
    Timber.i("!!! openNew numberScreen:$numberScreen url:$url")
    return parserListVideo(readHtmlFromURL(url)).toMutableStateList()
}

@Composable
fun DashboardsPaginatedListScreen(pageIndex: Int, vm: ScreenDashBoardsScreenModel) {

    println("!!! DashboardsPaginatedListScreen pageIndex:$pageIndex")

    val l = remember { mutableStateListOf<GalleryItem>() }

    LaunchedEffect(pageIndex) {
        withContext(Dispatchers.IO) {
            l.clear()
            l.addAll(openNew(pageIndex).filter { !it.href.contains("THUMBNUM") })
        }
    }

    val navigator = LocalNavigator.currentOrThrow

    val itemsPerRow =
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else vm.rowCount

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
                        DashBoardVideoImage(cell, onLongClick = {vm.openItem(urlStart + cell.href, navigator)})
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