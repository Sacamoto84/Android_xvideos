package com.example.ui.screens.explorer.tab.saved.tab

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.common.connectivityObserver.ConnectivityObserver
import com.redgifs.model.NichesInfo
import com.redgifs.common.ThemeRed
import com.redgifs.common.saved.SavedRed
import com.client.common.urlVideImage.UrlImage
import com.composeunstyled.Text
import com.example.ui.screens.niche.ScreenRedNiche
import com.example.ui.screens.profile.atom.VerticalScrollbar
import com.example.ui.screens.profile.rememberVisibleRangePercentIgnoringFirstNForLazyColumn
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject


object SavedNichesTab : Screen {

    private fun readResolve(): Any = SavedNichesTab

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: ScreenSavedNichesSM = getScreenModel()

        val state = rememberLazyListState()

        val scrollPercent by rememberVisibleRangePercentIgnoringFirstNForLazyColumn(
            gridState = state, itemsToIgnore = 0
        )

        /**  ➜ сюда запоминаем элемент, который пользователь хочет удалить  */
        var itemPendingDelete by remember { mutableStateOf<NichesInfo?>(null) }

        /* ---------- Диалог подтверждения ---------- */
        itemPendingDelete?.let { pending ->
            AlertDialog(

                icon = { UrlImage(pending.thumbnail, modifier = Modifier.size(96.dp)) },

                onDismissRequest = { itemPendingDelete = null },

                title = { Text("Удалить группу?", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
                            vm.savedRed.niches.remove(pending)   // удаляем
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
            Text(">Группы", modifier = Modifier.padding(start = 8.dp), color = ThemeRed.colorYellow, fontSize = 18.sp, fontFamily = ThemeRed.fontFamilyPopinsRegular)
        }) { padding ->

            Box(modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()) {

                LazyColumn(
                    state = state,
                    modifier = Modifier.fillMaxSize()
                )
                {
                    items(vm.savedRed.niches.list) {

                        Row(
                            modifier = Modifier
                                .padding(vertical = 2.dp)
                                .padding(horizontal = 8.dp)
                                .fillMaxWidth()
                                .background(ThemeRed.colorBottomBarDivider)
                                .clickable(onClick = {
                                    navigator.push(
                                        ScreenRedNiche(it.id)
                                    )
                                }),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            UrlImage(it.thumbnail, modifier = Modifier.size(96.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                it.name,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontFamily = ThemeRed.fontFamilyDMsanss,
                                maxLines = 3,              // сколько строк допускаем (можно убрать, чтобы было неограниченно)
                                overflow = TextOverflow.Ellipsis,   // «…» если всё-таки не влезло
                                modifier = Modifier.weight(1f)
                            )


                            Box(
                                modifier = Modifier
                                    .width(96.dp)
                                    .height(48.dp)
                                    .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                                    .background(Color.Black)
                                    .clickable { itemPendingDelete = it },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Выйти",
                                    fontFamily = ThemeRed.fontFamilyDMsanss,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

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

class ScreenSavedNichesSM @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    val savedRed: SavedRed
) : ScreenModel {


}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedSavedNiches {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenSavedNichesSM::class)
    abstract fun bindScreenRedSavedNichesScreenModel(hiltListScreenModel: ScreenSavedNichesSM): ScreenModel
}
