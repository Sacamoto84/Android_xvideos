package com.example.ui.screens.fullscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.common.AppPath
import com.client.common.R
import com.client.common.connectivityObserver.ConnectivityObserver
import com.client.common.urlVideImage.UrlImage
import com.composeunstyled.DropdownPanelAnchor
import com.example.ui.screens.explorer.ScreenRedExplorer
import com.example.ui.screens.fullscreen.bottom_bar.FeedControls_Container_Line0
import com.example.ui.screens.profile.ScreenRedProfile
import com.redgifs.common.ThemeRed
import com.redgifs.common.UsersRed
import com.redgifs.common.block.ui.DialogBlock
import com.redgifs.common.di.HostDI
import com.redgifs.common.downloader.DownloadRed
import com.redgifs.common.downloader.ui.DownloadIndicator
import com.redgifs.common.expand_menu_video.ExpandMenuVideo
import com.redgifs.common.expand_menu_video.ExpandMenuVideoTags
import com.redgifs.common.video.CanvasTimeDurationLine1
import com.redgifs.common.video.PlayerControls
import com.redgifs.common.video.RedVideoPlayerWithMenu
import com.redgifs.model.GifsInfo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.flow.any
import timber.log.Timber
import javax.inject.Inject

class ScreenRedFullScreen(val item: GifsInfo) : Screen {

    override val key: ScreenKey = "ScreenRedFullScreen"

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val vm = getScreenModel<ScreenRedFullScreenSM>()

        val haptic = LocalHapticFeedback.current

        var blockItem by remember { mutableStateOf<GifsInfo?>(null) }

        val downloadList = vm.hostDI.downloadRed.downloadList.collectAsState().value

        val videoUri: String = remember(item.id, item.userName) {
            Timber.tag("???").i("Перерасчет videoItem.id = ${item.id}")
            //Определяем адрес откуда брать видео, из кеша или из сети
            if (vm.downloadRed.downloader.findVideoInDownload(item.id, item.userName))
                "${AppPath.cache_download_red}/${item.userName}/${item.id}.mp4"
            else
                "https://api.redgifs.com/v2/gifs/${item.id.lowercase()}/hd.m3u8"
        }

        //Диалог для блокировки
        if (vm.hostDI.block.blockVisibleDialog) {
            DialogBlock(
                visible = vm.hostDI.block.blockVisibleDialog,
                onDismiss = { vm.hostDI.block.blockVisibleDialog = false },
                onBlockConfirmed = {
                    if ((blockItem != null)) {
                        vm.hostDI.block.blockItem(blockItem!!)
                        blockItem = null
                    }
                }
            )
        }


        Scaffold(
            bottomBar = {
                Column(modifier = Modifier.background(ThemeRed.colorCommonBackground)) {

                    Box(
                        Modifier
                            .padding(bottom = 1.dp)
                            //.padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(0))
                            .height(32.dp)
                            .fillMaxWidth()
                            //.alpha(al.value)
                            .background(ThemeRed.colorTabLevel0),
                        contentAlignment = Alignment.BottomCenter
                    ) {

                        CanvasTimeDurationLine1(
                            currentTime = vm.currentPlayerTime, duration = vm.currentPlayerDuration,
                            timeA = vm.timeA, timeB = vm.timeB,
                            timeABEnable = vm.enableAB, play = vm.play,
                            onSeek = {
                                if (vm.currentPlayerControls != null) {
                                    vm.currentPlayerControls!!.seekTo(it)
                                }
                            },
                            onSeekFinished = { }, modifier = Modifier.padding(horizontal = 0.dp)
                        )

                    }

                    //   }
                    Box(modifier = Modifier.background(ThemeRed.colorTabLevel1)) {
                        FeedControls_Container_Line0(vm)
                        //HorizontalSeparator(Color.Transparent, thickness = 4.dp)
                        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                            DownloadIndicator(vm.downloadRed.downloader.percent.collectAsStateWithLifecycle().value)
                        }
                    }
                }
            }
        ) {

            RedVideoPlayerWithMenu(
                url = videoUri,
                play = vm.play,
                onChangeTime = { it1 ->
                    vm.currentPlayerTime = it1.first
                    vm.currentPlayerDuration = it1.second
                },
                isMute = vm.mute,
                onPlayerControlsReady = { controls ->
                    vm.currentPlayerControls = controls
                },
                timeA = vm.timeA,
                timeB = vm.timeB,
                enableAB = vm.enableAB,
                onClick = { vm.play = !vm.play },
                autoRotate = vm.autoRotate,
                isCurrentPage = true
            )

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable(onClick = {
                            navigator.push(
                                ScreenRedProfile(item.userName)
                            )
                        }),
                    verticalAlignment = Alignment.CenterVertically,
                )
                {
                    val user = UsersRed.listAllUsers.firstOrNull { it.username == item.userName }
                    if (user != null) {
                        if (user.profileImageUrl != null) {
                            UrlImage(
                                user.profileImageUrl!!,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White, modifier = Modifier.padding(end = 8.dp).clip(RoundedCornerShape(12.dp)).size(40.dp).background(Color.DarkGray).size(10.dp)
                            )
                        }
                    }else
                    {
                        Box(modifier = Modifier.padding(end = 8.dp).clip(RoundedCornerShape(12.dp)).size(40.dp).background(Color.DarkGray), contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }


                    if (vm.hostDI.savedRed.collections.collectionList.any { it.list.any { it2 -> it2.id == item.id } }) {
                        Icon(
                            painter = painterResource(R.drawable.collection_multi_input_svgrepo_com),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(bottom = 6.dp, end = 6.dp)
                                .size(18.dp)
                        )
                    }

                    //
                    if (vm.hostDI.savedRed.creators.list.any { it.username == item.userName }) {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(bottom = 6.dp, end = 6.dp)
                                .size(22.dp)
                        )
                    }

                    //✅ Лайк
                    if (vm.hostDI.savedRed.likes.list.any { it.id == item.id }) {
                        Icon(
                            Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(bottom = 6.dp, end = 6.dp)
                                .size(22.dp)
                        )
                    }

                    //✅ Иконка того что видео скачано
                    if (
                        downloadList.any { it.id == item.id }
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(bottom = 6.dp, end = 6.dp)
                                .size(20.dp)
                        )
                    }
                }


                Row(verticalAlignment = Alignment.CenterVertically) {

                    IconButton(onClick = {
                        vm.autoRotate = !vm.autoRotate
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }) {
                        Icon(
                            Icons.Default.ScreenRotation,
                            contentDescription = null,
                            tint = Color.White, modifier = Modifier.size(22.dp)
                        )
                    }

                    ExpandMenuVideoTags(
                        item = item,
                        modifier = Modifier,
                        onClick = {
                            vm.hostDI.search.searchText.value = it
                            vm.hostDI.search.searchTextDone.value = it
                            ScreenRedExplorer.screenType = 0
                            navigator.pop()
                        },
                    )

                    //Меню на 3 точки
                    ExpandMenuVideo(
                        item = item,
                        modifier = Modifier,
                        onClick = {
                            blockItem = item //Для блока и идентификации и тема
                        },
                        haptic = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) },
                        onRunLike = {
                            // if (isRunLike) {
                            //    listGifs.refresh()
                            //}
                        },
                        onRefresh = {},
                        isCollection = false,
                        block = vm.hostDI.block,
                        redApi = vm.hostDI.redApi,
                        savedRed = vm.hostDI.savedRed,
                        downloadRed = vm.hostDI.downloadRed
                    )
                }

            }


        }

    }
}

class ScreenRedFullScreenSM @Inject constructor(
    val connectivityObserver: ConnectivityObserver,
    val downloadRed: DownloadRed,
    val hostDI: HostDI
) : ScreenModel {


    //═════════════════════════════════════════════════════════════════════════════════════════════════════╗
    // Управление плеером                                                                                  ║
    //══════════════════════════════════════════════════╦══════════════════════════════════════════════════╣
    var play by mutableStateOf(true)                  //║                                                  ║
    var mute by mutableStateOf(true)                  //║                                                  ║
    var autoRotate by mutableStateOf(false)           //║ Включить автоматический поворот                  ║

    //══════════════════════════════════════════════════╬══════════════════════════════════════════════════╣
    var enableAB by mutableStateOf(false)             //║                                                  ║
    var timeA by mutableFloatStateOf(3f)              //║                                                  ║
    var timeB by mutableFloatStateOf(6f)              //║                                                  ║

    //══════════════════════════════════════════════════╩══════════════════════════════════════════════════╣
    var currentPlayerControls by mutableStateOf<PlayerControls?>(null)                                   //║

    //═════════════════════════════════════════════════════════════════════════════════════════════════════╝
    //═══ Состояния плеера ═════════════════════════════╦══════════════════════════════════════════════════╗
    var currentPlayerTime by mutableFloatStateOf(0f)  //║ Текущее время                                    ║
    var currentPlayerDuration by mutableIntStateOf(0) //║ Продолжительность видео                          ║
    //══════════════════════════════════════════════════╩══════════════════════════════════════════════════╝


}

//
@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedАFullScreen {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedFullScreenSM::class)
    abstract fun bindScreenRedFulScreenSreenModel(hiltListScreenModel: ScreenRedFullScreenSM): ScreenModel
}
