package com.client.xvideos.redgifs.screens.fullscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.client.common.AppPath
import com.client.xvideos.feature.connectivityObserver.ConnectivityObserver
import com.redgifs.model.GifsInfo
import com.redgifs.common.ThemeRed
import com.redgifs.common.downloader.DownloadRed
import com.redgifs.common.downloader.ui.DownloadIndicator
import com.client.xvideos.redgifs.common.video.RedVideoPlayerWithMenu
import com.client.xvideos.redgifs.screens.fullscreen.bottom_bar.line0.FeedControls_Container_Line0
import com.client.xvideos.redgifs.screens.profile.PlayerControls
import com.client.xvideos.redgifs.screens.profile.atom.CanvasTimeDurationLine
import com.composables.core.HorizontalSeparator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import timber.log.Timber
import javax.inject.Inject

class ScreenRedFullScreen(val item: GifsInfo) : Screen {

    override val key: ScreenKey = "ScreenRedFullScreen"

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val vm = getScreenModel<ScreenRedFullScreenSM>()

        val videoUri: String = remember(item.id, item.userName) {
            Timber.tag("???").i("Перерасчет videoItem.id = ${item.id}")
            //Определяем адрес откуда брать видео, из кеша или из сети
            if (vm.downloadRed.downloader.findVideoInDownload(item.id, item.userName))
                "${AppPath.cache_download_red}/${item.userName}/${item.id}.mp4"
            else
                "https://api.redgifs.com/v2/gifs/${item.id.lowercase()}/hd.m3u8"
        }


        Scaffold(


            bottomBar = {
                Column {

                    Box(
                        Modifier
                            .padding(bottom = 4.dp)
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(0))
                            .height(16.dp)
                            .fillMaxWidth()
                            //.alpha(al.value)
                            .background(ThemeRed.colorCommonBackground2),
                        contentAlignment = Alignment.BottomCenter
                    ) {

                        CanvasTimeDurationLine(
                            currentTime = vm.currentPlayerTime, duration = vm.currentPlayerDuration,
                            timeA = vm.timeA, timeB = vm.timeB,
                            timeABEnable = vm.enableAB, play = vm.play,
                            onSeek = {
                                if (vm.currentPlayerControls != null) {
                                    vm.currentPlayerControls!!.seekTo(it)
                                }
                            },
                            onSeekFinished = { }, modifier = Modifier.padding(end = 148.dp)
                        )

//                        BasicText(
//                            vm.currentTikTokPage.toString() + "/" + vm.list.collectAsState().value.lastIndex,
//                            style = TextStyle(
//                                color = Color.White,
//                                fontFamily = ThemeRed.fontFamilyPopinsRegular,
//                                fontSize = 8.sp
//                            ),
//                            modifier = Modifier
//                                //.align(Alignment.TopEnd)
//                                .offset(x = 0.dp, y = (-0).dp)
//                        )

                    }

                    //   }
                    Column {

                        //Индикатор загрузки
                        DownloadIndicator(vm.downloadRed.downloader.percent.collectAsStateWithLifecycle().value)
                        FeedControls_Container_Line0(vm)
                        HorizontalSeparator(Color.Transparent, thickness = 4.dp)
                        //FeedControls_Container_Line1(vm)
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
                menuContent = {},
                autoRotate = vm.autoRotate,
                isCurrentPage = true
            )

        }

    }
}

class ScreenRedFullScreenSM @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    val downloadRed: DownloadRed
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
