package com.example.ui.screens.explorer.tab.saved.tab

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.common.connectivityObserver.ConnectivityObserver
import com.client.common.urlVideImage.UrlImage
import com.composeunstyled.Text
import com.example.ui.screens.ui.lazyrow123.LazyRow123
import com.example.ui.screens.ui.lazyrow123.LazyRow123Host
import com.example.ui.screens.ui.lazyrow123.TypePager
import com.redgifs.common.ThemeRed
import com.redgifs.common.block.BlockRed
import com.redgifs.common.block.ui.DialogBlock
import com.redgifs.common.di.HostDI
import com.redgifs.common.saved.collection.ui.DaialogNewCollection
import com.redgifs.model.GifsInfo
import com.redgifs.model.Order
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.DelicateCoroutinesApi
import javax.inject.Inject

object SavedCollectionTab : Screen {

    private fun readResolve(): Any = SavedCollectionTab

    override val key: ScreenKey = uniqueScreenKey

    val column = mutableIntStateOf(2)

    fun addColumn() {
        column.intValue += 1
        if (column.intValue > 3)
            column.intValue = 1
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val vm = getScreenModel<ScreenSavedCollectionSM>()

        var blockItem by rememberSaveable { mutableStateOf<GifsInfo?>(null) }

        val savedRed = vm.hostDI.savedRed

        BackHandler { savedRed.collections.selectedCollection = null }

        val list: SnapshotStateList<GifsInfo> = emptyList<GifsInfo>().toMutableStateList()

        val listGifs: LazyPagingItems<Any> = vm.likedHost.pager.collectAsLazyPagingItems()

        LaunchedEffect(column.intValue) {
            vm.likedHost.columns = column.intValue
        }

        LaunchedEffect(savedRed.collections.selectedCollection) {
            if (savedRed.collections.selectedCollection != null) {
                list.clear()
                list.addAll(savedRed.collections.collectionList.first { it.collection == savedRed.collections.selectedCollection }.list)
                vm.likedHost.extraString = savedRed.collections.selectedCollection!!
                listGifs.refresh()
            } else {
                list.clear()
            }
        }

//        LaunchedEffect(SavedRed.collectionList) {
//            listGifs.refresh()
//        }

        /**  ➜ сюда запоминаем элемент, который пользователь хочет удалить  */
        var itemPendingDelete by remember { mutableStateOf<String?>(null) }
        /* ---------- Диалог подтверждения ---------- */
        itemPendingDelete?.let { pending ->
            AlertDialog(

                //icon = { UrlImage(pending.thumbnail, modifier = Modifier.size(96.dp)) },

                onDismissRequest = { itemPendingDelete = null },

                title = {
                    Text(
                        "Удалить коллекцию?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },

                text = {
                    Text(buildAnnotatedString {
                        append("Удалить «")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(pending) }
                        append("» из коллекции")
                    }, fontSize = 16.sp)
                },

                confirmButton = {
                    TextButton(
                        onClick = {
                            savedRed.collections.deleteCollection(pending)
                            itemPendingDelete = null
                        }
                    ) { Text("Удалить", fontSize = 16.sp, color = Color(0xFF6552A5)) }
                },
                dismissButton = {
                    TextButton(
                        onClick = { itemPendingDelete = null }
                    ) { Text("Отмена", fontSize = 16.sp, color = Color(0xFF6552A5)) }
                },

                /* Доп. стили при желании */
                containerColor = Color(0xFFEBE6EE)
            )
        }
        /* ---------- /Диалог ---------- */
        val block = vm.block
        //Диалог для блокировки
        if (block.blockVisibleDialog) {
            DialogBlock(
                visible = block.blockVisibleDialog,
                onDismiss = { block.blockVisibleDialog = false },
                onBlockConfirmed = {
                    if ((blockItem != null)) {
                        block.blockItem(blockItem!!)
                        blockItem = null
                    }
                }
            )
        }

        var collectionVisibleDialogCreateNew by remember { mutableStateOf(false) }

        if (collectionVisibleDialogCreateNew) {
            DaialogNewCollection(
                visible = collectionVisibleDialogCreateNew,
                onDismiss = {
                    collectionVisibleDialogCreateNew = false
                },
                onBlockConfirmed = { collection ->
                    if ((collection != "")) {
                        savedRed.collections.createCollection(collection)
                        collectionVisibleDialogCreateNew = false
                    }
                }
            )
        }




        Scaffold(topBar = {
            Text(
                ">Коллекция>" + savedRed.collections.selectedCollection ?: "---",
                modifier = Modifier.padding(start = 8.dp),
                color = ThemeRed.colorYellow,
                fontSize = 18.sp,
                fontFamily = ThemeRed.fontFamilyPopinsRegular
            )
        }) { padding ->


            if (savedRed.collections.selectedCollection == null) {
                LazyVerticalGrid(
                    modifier = Modifier.padding(padding),
                    state = vm.gridState,
                    columns = GridCells.Fixed(2),

                    ) {

                    items(savedRed.collections.collectionList) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .padding(vertical = 4.dp)
                                .combinedClickable(
                                    onClick = { savedRed.collections.selectedCollection = it.collection },
                                    onLongClick = { itemPendingDelete = it.collection }),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (it.list.isNotEmpty()) {
                                UrlImage(
                                    url = it.list.last().urls.thumbnail,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(25))
                                        .size(72.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(25))
                                        .size(72.dp)
                                        .background(Color.Gray)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                it.collection,
                                color = Color.White,
                                fontFamily = ThemeRed.fontFamilyDMsanss
                            )
                        }
                    }

                    items(listOf(Unit)) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 8.dp, top = 4.dp)
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(25))
                                    .background(ThemeRed.colorYellow)
                                    .clickable(onClick = {
                                        collectionVisibleDialogCreateNew = true
                                    }),
                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )

                            }
                        }
                    }
                }
            } else {

                LazyRow123(
                    host = vm.likedHost,
                    modifier = Modifier
                        .padding(top = padding.calculateTopPadding())
                        .fillMaxSize(),
                    onClickOpenProfile = {},
                )

//                LazyVerticalGrid(state = vm.listState,
//                    modifier = Modifier.padding(padding))
//                {
//
//
//                    item {
//                        Box(
//                            modifier = Modifier
//                                .size(64.dp)
//                                .background(Color.Gray)
//                                .clickable(onClick = { selectedCollection = null })
//                        )
//                    }
//
//                    itemsIndexed(list) { index, item ->
//
//                        var isVideo by remember { mutableStateOf(false) }
//
//                        Box(
//                            modifier = Modifier
//                                .padding(vertical = 8.dp)
//                                .padding(horizontal = 4.dp)
//                                .fillMaxSize()
//                                .clip(RoundedCornerShape(16.dp))
//                                .border(1.dp, Color.DarkGray, RoundedCornerShape(16.dp)),
//                            contentAlignment = Alignment.Center
//                        ) {
//
//                            RedUrlVideoImageAndLongClick(
//                                item, index, onLongClick = {},
//                                onVideo = { isVideo = it },
//                                isVisibleView = false,
//                                isVisibleDuration = false,
//                                play = false,//centrallyLocatedOrMostVisibleItemIndex == index && host.columns == 1,
//                                isNetConnected = true,
//                                onVideoUri = { },
//                                onFullScreen = {
//                                    //blockItem = item
//                                    navigator.push(ScreenRedFullScreen(item))
//                                }
//                            )
//
//                            //Меню на 3 точки
//                            ExpandMenuVideo(
//                                item = item,
//                                modifier = Modifier.align(Alignment.TopEnd),
//                                onClick = {
//                                    blockItem = item //Для блока и идентификации и тема
//                                },
//                                onRunLike = {},
//                                isCollection = true,
//                            )
//
//
//                            ProfileInfo1(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .align(Alignment.BottomStart)
//                                    .offset((4).dp, (-4).dp),
//                                onClick = { navigator.push(ScreenRedProfile(item.userName)) },
//                                videoItem = item,
//                                listUsers = UsersRed.listAllUsers,
//                                visibleUserName = column.intValue <= 2 && !isVideo,
//                                visibleIcon = !isVideo
//                            )
//
//
//                            AnimatedVisibility(
//                                !isVideo, modifier = Modifier
//                                    .fillMaxSize()
//                                    .align(Alignment.BottomCenter),
//                                enter = slideInVertically(
//                                    initialOffsetY = { fullHeight -> fullHeight }, // снизу вверх
//                                    animationSpec = tween(durationMillis = 200)
//                                ),
//                                exit = slideOutVertically(
//                                    targetOffsetY = { fullHeight -> fullHeight }, // сверху вниз
//                                    animationSpec = tween(durationMillis = 200)
//                                )
//                            ) {
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    verticalAlignment = Alignment.Bottom,
//                                    horizontalArrangement = Arrangement.End
//                                ) {
//
//
//                                    if (SavedRed.collectionList.any { it.list.any { it2 -> it2.id == item.id } }) {
//                                        Icon(
//                                            painter = painterResource(R.drawable.collection_multi_input_svgrepo_com),
//                                            contentDescription = null,
//                                            tint = Color.White,
//                                            modifier = Modifier
//                                                .padding(bottom = 6.dp, end = 6.dp)
//                                                .size(18.dp)
//                                        )
//                                    }
//
//                                    //
//                                    if (SavedRed.creatorsList.any { it.username == item.userName }) {
//                                        Icon(
//                                            Icons.Filled.Person,
//                                            contentDescription = null,
//                                            tint = Color.White,
//                                            modifier = Modifier
//                                                .padding(bottom = 6.dp, end = 6.dp)
//                                                .size(18.dp)
//                                        )
//                                    }
//
//                                    //✅ Лайк
//                                    if (SavedRed.likesList.any { it.id == item.id }) {
//                                        Icon(
//                                            Icons.Filled.FavoriteBorder,
//                                            contentDescription = null,
//                                            tint = Color.White,
//                                            modifier = Modifier
//                                                .padding(bottom = 6.dp, end = 6.dp)
//                                                .size(18.dp)
//                                        )
//                                    }
//
//                                    //✅ Иконка того что видео скачано
//                                    if (DownloadRed.downloadList.contains(item.id)) {
//                                        Icon(
//                                            Icons.Default.Save,
//                                            contentDescription = null,
//                                            tint = Color.White,
//                                            modifier = Modifier
//                                                .padding(bottom = 6.dp, end = 6.dp)
//                                                .size(18.dp)
//                                        )
//                                    }
//
//                                }
//
//                            }
//
//
//                        }
//                    }
//                }


            }

        }

    }
}


class ScreenSavedCollectionSM @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    val block: BlockRed,
    val hostDI : HostDI,
) : ScreenModel {
    val gridState = LazyGridState()

    val likedHost = LazyRow123Host(
        connectivityObserver = connectivityObserver,
        scope = screenModelScope,
        typePager = TypePager.SAVED_COLLECTION,
        extraString = "",
        startOrder = Order.LATEST,
        isCollection = true,
        hostDI = hostDI
    )
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedSavedCollection {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenSavedCollectionSM::class)
    abstract fun bindScreenRedSavedCollectionScreenModel(hiltListScreenModel: ScreenSavedCollectionSM): ScreenModel
}



