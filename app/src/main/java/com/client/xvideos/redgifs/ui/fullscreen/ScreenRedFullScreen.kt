package com.client.xvideos.redgifs.ui.fullscreen

import android.annotation.SuppressLint
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.ScreenTransition
import com.client.xvideos.R
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.coil.UrlImage
import com.client.xvideos.common.connectivityObserver.ConnectivityObserver
import com.client.xvideos.redgifs.ui.explorer.ScreenRedExplorer
import com.client.xvideos.redgifs.ui.fullscreen.bottom_bar.FeedControls_Container_Line0
import com.client.xvideos.redgifs.ui.profile.ScreenRedProfile
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.common.di.HostDI
import com.client.xvideos.redgifs.model.GifsInfo
import com.client.xvideos.redgifs.common.UsersRed
import com.redgifs.common.block.ui.DialogBlock
import com.client.xvideos.redgifs.common.downloader.DownloadRed
import com.redgifs.common.downloader.ui.DownloadIndicator
import com.client.xvideos.redgifs.common.expand_menu_video.ExpandMenuVideo
import com.redgifs.common.expand_menu_video.ExpandMenuVideoTags
import com.client.xvideos.redgifs.common.video.CanvasTimeDurationLine1
import com.redgifs.common.video.PlayerControls
import com.redgifs.common.video.RedVideoPlayerWithMenu
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalVoyagerApi::class)
class ScreenRedFullScreen(val item: GifsInfo) : Screen, ScreenTransition {

    override val key: ScreenKey = uniqueScreenKey

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
            if (vm.downloadRed.downloader.findVideoInDownload(item.id, item.userName))
                "${AppPath.r_cache_download}/${item.userName}/${item.id}.mp4"
            else
                "https://api.redgifs.com/v2/gifs/${item.id.lowercase()}/hd.m3u8"
        }

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
                            .clip(RoundedCornerShape(0))
                            .height(32.dp)
                            .fillMaxWidth()
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

                    Box(modifier = Modifier.background(ThemeRed.colorTabLevel1)) {
                        FeedControls_Container_Line0(vm)
                        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                            DownloadIndicator(vm.downloadRed.downloader.percent.collectAsStateWithLifecycle().value)
                        }
                    }
                }
            }
        ) {

            RedVideoPlayerWithMenu(
                modifier = Modifier.padding(bottom = it.calculateBottomPadding()/2),
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
                                user.profileImageUrl,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .size(40.dp)
                                    .background(Color.DarkGray)
                                    .size(10.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .size(40.dp)
                                .background(Color.DarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }


                    if (vm.hostDI.savedRed.collections.collectionList.any { it.items.any { it2 -> it2.id == item.id } }) {
                        Icon(
                            painter = painterResource(R.drawable.collection_multi_input_svgrepo_com),
                            contentDescription = null,
                            tint = Color.White, modifier = Modifier.padding(bottom = 6.dp, end = 6.dp).size(18.dp)
                        )
                    }

                    if (vm.hostDI.savedRed.creators.list.any { it.username == item.userName }) {
                        Icon( Icons.Outlined.Person, contentDescription = null, tint = Color.White,  modifier = Modifier.padding(bottom = 6.dp, end = 6.dp).size(22.dp) )
                    }

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
                        onClick = { it1 ->
                            vm.hostDI.search.searchText.value = TextFieldValue(text = it1, selection = TextRange(it1.length))
                            vm.hostDI.search.searchTextDone.value = it1
                            ScreenRedExplorer.Companion.screenType = 0
                            navigator.pop()
                        },
                    )

                    ExpandMenuVideo(
                        item = item,
                        modifier = Modifier,
                        onClick = {
                            blockItem = item
                        },
                        haptic = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) },
                        onRunLike = {
                        },
                        onRefresh = {},
                        isCollection = false,
                        block = {vm.hostDI.block},
                        redApi = {vm.hostDI.redApi},
                        savedRed = {vm.hostDI.savedRed},
                        downloadRed = {vm.hostDI.downloadRed}
                    )
                }

            }


        }

    }

//    override fun enter(lastEvent: StackEvent): EnterTransition {
//        return slideIn { size ->
//            val x = if (lastEvent == StackEvent.Pop) -size.width else size.width
//            IntOffset(x = x, y = 0)
//        }
//    }

//    override fun exit(lastEvent: StackEvent): ExitTransition {
//        return slideOut { size ->
//            val x = if (lastEvent == StackEvent.Pop) size.width else -size.width
//            IntOffset(x = x, y = 0)
//        }
//    }


    override fun enter(lastEvent: StackEvent): EnterTransition {
        return fadeIn(tween(300))
    }

    override fun exit(lastEvent: StackEvent): ExitTransition {
        return fadeOut(tween(300))
    }

}

class ScreenRedFullScreenSM @Inject constructor(
    val connectivityObserver: ConnectivityObserver,
    val downloadRed: DownloadRed,
    val hostDI: HostDI
) : ScreenModel {


    var play by mutableStateOf(true)
    var mute by mutableStateOf(true)
    var autoRotate by mutableStateOf(false)

    var enableAB by mutableStateOf(false)
    var timeA by mutableFloatStateOf(3f)
    var timeB by mutableFloatStateOf(6f)

    var currentPlayerControls by mutableStateOf<PlayerControls?>(null)

    var currentPlayerTime by mutableFloatStateOf(0f)
    var currentPlayerDuration by mutableIntStateOf(0)


}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedАFullScreen {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenRedFullScreenSM::class)
    abstract fun bindScreenRedFulScreenSreenModel(hiltListScreenModel: ScreenRedFullScreenSM): ScreenModel
}
