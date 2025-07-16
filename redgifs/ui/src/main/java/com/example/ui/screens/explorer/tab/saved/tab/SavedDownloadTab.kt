package com.example.ui.screens.explorer.tab.saved.tab

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageButton
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Shower
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.common.AppPath
import com.client.common.connectivityObserver.ConnectivityObserver
import com.client.common.urlVideImage.UrlImage
import com.client.common.util.toPrettyCount3
import com.composeunstyled.Text
import com.example.ui.screens.fullscreen.ScreenRedFullScreen
import com.example.ui.screens.profile.atom.VerticalScrollbar
import com.example.ui.screens.profile.rememberVisibleRangePercentIgnoringFirstNForLazyColumn
import com.example.ui.screens.ui.lazyrow123.LazyRow123Host
import com.example.ui.screens.ui.lazyrow123.TypePager
import com.redgifs.common.ThemeRed
import com.redgifs.common.di.HostDI
import com.redgifs.common.share.useCaseShareGifs
import com.redgifs.model.GifsInfo
import com.redgifs.model.Order
import com.redgifs.model.UserInfo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import java.io.File
import javax.inject.Inject

object SavedDownloadTab : Screen {

    private fun readResolve(): Any = SavedDownloadTab

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow


        val vm = getScreenModel<ScreenSavedDownloadSM>()

        val context = LocalContext.current

        val state = rememberLazyListState()

        val downloadRed = vm.hostDI.downloadRed.downloadList.collectAsState().value.toList()

        /**  ➜ сюда запоминаем элемент, который пользователь хочет удалить  */
        var itemPendingDelete by remember { mutableStateOf<UserInfo?>(null) }

        //Расчет процентов для скролл

        val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForLazyColumn(
            gridState = state, itemsToIgnore = 0
        )

        /* ---------- Диалог подтверждения ---------- */
        itemPendingDelete?.let { pending ->
            AlertDialog(

                icon = {
                    pending.profileImageUrl?.let {
                        UrlImage(pending.profileImageUrl!!, modifier = Modifier.size(96.dp))
                    }
                },

                onDismissRequest = { itemPendingDelete = null },

                title = { Text("Удалить автора?", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                text = {
                    Text(buildAnnotatedString {
                        append("Удалить «")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(pending.name) }
                        append("» из сохранённых?")
                    }, fontSize = 16.sp)
                },

                confirmButton = {
                    TextButton(
                        onClick = {
                            //savedRed.creators.remove(pending.username)   // удаляем
                            itemPendingDelete = null         // закрываем диалог
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


        Scaffold(topBar = {
            Text(
                ">Авторы",
                modifier = Modifier.padding(start = 8.dp),
                color = ThemeRed.colorYellow,
                fontSize = 18.sp,
                fontFamily = ThemeRed.fontFamilyPopinsRegular
            )
        }) { padding ->
            Box(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding())
                    .fillMaxSize()
            ) {
                LazyColumn(
                    state = state,
                    modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(4.dp)
                )
                {
                    items(downloadRed) { it1 ->

                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.dp, ThemeRed.colorBorderGray,
                                    RoundedCornerShape(8.dp)
                                )
                                .background(ThemeRed.colorTabLevel3)
                        ) {

                            val imagePath =
                                AppPath.cache_download_red + "/" + it1.userName + "/" + it1.id + ".jpg"

                            val mp4Path =
                                AppPath.cache_download_red + "/" + it1.userName + "/" + it1.id + ".mp4"

                            val size = File(mp4Path).length().toPrettyCount3()

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((72 * 1920f / 1080).toInt().dp)
                            ) {
                                UrlImage(
                                    imagePath,
                                    modifier = Modifier
                                        .width(72.dp)
                                        .fillMaxHeight()
                                        .clickable(onClick = {
                                            navigator.push(ScreenRedFullScreen(it1))
                                        }),
                                    contentScale = ContentScale.Crop
                                )
                                Column(
                                    modifier = Modifier
                                        .padding(start = 8.dp, top = 4.dp)
                                        .weight(1f)
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Name: " + it1.userName,
                                        color = Color.White,
                                        fontFamily = ThemeRed.fontFamilyPopinsRegular,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        "ID: " + it1.id,
                                        color = Color.White,
                                        fontFamily = ThemeRed.fontFamilyPopinsRegular,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        "Size: $size",
                                        color = Color.White,
                                        fontFamily = ThemeRed.fontFamilyPopinsRegular,
                                        fontSize = 18.sp
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.End),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {


                                        IconButton(onClick = {
                                            navigator.push(ScreenRedFullScreen(it1))
                                        }, modifier = Modifier) {
                                            Icon(
                                                Icons.Outlined.Fullscreen,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }


                                        IconButton(onClick = {
                                            useCaseShareGifs(context, it1)
                                        }, modifier = Modifier) {
                                            Icon(
                                                Icons.Outlined.Share,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                        }

                                        IconButton(onClick = {
                                            vm.delete(it1)
                                        }, modifier = Modifier) {
                                            Icon(
                                                Icons.Outlined.Delete,
                                                contentDescription = null,
                                                tint = Color.White, modifier = Modifier.size(24.dp)
                                            )
                                        }

                                    }
                                }
                            }
                        }
                    }
                }

                //---- Скролл ----
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .width(2.dp)
                ) {
                    VerticalScrollbar(scrollPercent)
                }

            }

        }


    }
}

class ScreenSavedDownloadSM @Inject constructor(
    val hostDI: HostDI
) : ScreenModel {

    fun delete(item: GifsInfo) {
        hostDI.downloadRed.delete(item)
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedSavedDownload {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenSavedDownloadSM::class)
    abstract fun bindScreenRedSavedDownloadScreenModel(hiltListScreenModel: ScreenSavedDownloadSM): ScreenModel
}
