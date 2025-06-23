package com.client.xvideos.red.common.ui.lazyrow123

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.feature.redgifs.types.GifsInfo
import com.client.xvideos.feature.redgifs.types.Niche
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.red.common.block.BlockRed
import com.client.xvideos.red.common.block.ui.DialogBlock
import com.client.xvideos.red.common.expand_menu_video.ExpandMenuVideoModel
import com.client.xvideos.red.screens.niche.ScreenRedNiche
import com.client.xvideos.red.screens.niche.atom.NichePreview
import timber.log.Timber

@Composable
fun LazyRow123ExplorerNiches(
    host: LazyRow123Host,
    modifier: Modifier = Modifier,
    onClickOpenProfile: (String) -> Unit = {},
    gotoPosition: Int,
    option: List<ExpandMenuVideoModel> = emptyList(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    contentBeforeList: @Composable (() -> Unit) = {}
) {

    val navigator = LocalNavigator.currentOrThrow

    SideEffect { Timber.d("!!! LazyRow2::SideEffect columns: ${host.columns} gotoPosition: $gotoPosition") }

    Timber.i("!!! 2 LazyRow123")

    val listNiche = host.pager.collectAsLazyPagingItems() as LazyPagingItems<Niche>

    var fullScreen by remember { mutableStateOf(false) }
    val isConnected by host.isConnected.collectAsState()
    val state = rememberLazyGridState()
    var blockItem by remember { mutableStateOf<GifsInfo?>(null) }

    BackHandler { if (fullScreen) fullScreen = false }

    if (listNiche.itemCount == 0) return

    LaunchedEffect(gotoPosition) {
        if (gotoPosition >= 0 && gotoPosition < listNiche.itemCount) {
            state.scrollToItem(gotoPosition)
        }
    }

    //Диалог для блокировки
    if (BlockRed.blockVisibleDialog) {
        DialogBlock(
            visible = BlockRed.blockVisibleDialog,
            onDismiss = { BlockRed.blockVisibleDialog = false },
            onBlockConfirmed = {
                if ((blockItem != null)) {
                    BlockRed.blockItem(blockItem!!)
                    val temp = host.sortType.value
                    host.changeSortType(Order.FORCE_TEMP)
                    host.changeSortType(temp)
                }
            }
        )
    }

    LazyVerticalGrid(
        state = state,
        columns = GridCells.Fixed(host.columns.coerceIn(1..3)),
        modifier = Modifier.then(modifier),
        contentPadding = contentPadding,
    ) {
        //item(key = "before", span = { GridItemSpan(maxLineSpan) }) { contentBeforeList() }
        items(
            count = listNiche.itemCount,
        ) { index ->
            val item = listNiche[index]
            if (item != null) {
                Box(modifier = Modifier.padding(vertical = 4.dp)) {
                    NichePreview(niches = item, onClick = {
                       navigator.push(ScreenRedNiche(item.id))
                    })
                }
            }
        }
    }

}




