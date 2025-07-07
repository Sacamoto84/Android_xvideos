package com.client.redgifs.screens.explorer.tab.saved.tab

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.redgifs.network.types.UserInfo
import com.client.xvideos.redgifs.ThemeRed
import com.client.xvideos.redgifs.common.saved.SavedRed
import com.client.xvideos.redgifs.screens.profile.ScreenRedProfile
import com.client.xvideos.redgifs.screens.profile.atom.VerticalScrollbar
import com.client.xvideos.redgifs.screens.profile.rememberVisibleRangePercentIgnoringFirstNForLazyColumn
import com.client.xvideos.xvideos.screens.common.urlVideImage.UrlImage
import com.composeunstyled.Text


object SavedCreatorsTab : Screen {

    private fun readResolve(): Any = SavedCreatorsTab

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val state = rememberLazyListState()

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
                        UrlImage(pending.profileImageUrl, modifier = Modifier.size(96.dp))
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
                            SavedRed.creators.remove(pending.username)   // удаляем
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
            Box(modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()) {
                LazyColumn(
                    state = state,
                    modifier = Modifier.fillMaxSize()
                )
                {

                    items(SavedRed.creators.list) { it1 ->

                            Row(
                                modifier = Modifier
                                    .padding(vertical = 2.dp)
                                    .padding(horizontal = 8.dp)
                                    .fillMaxWidth()
                                    .background(ThemeRed.colorBottomBarDivider)
                                    .clickable(onClick = {
                                        navigator.push(
                                            ScreenRedProfile(it1.username)
                                        )
                                    }),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                if (it1.profileImageUrl != null) {
                                    UrlImage(
                                        it1.profileImageUrl,
                                        modifier = Modifier.size(96.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(0.dp))
                                            .size(96.dp)
                                            .background(Color.DarkGray),
                                        contentAlignment = Alignment.Center
                                    )
                                    {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            tint = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    it1.name,
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
                                        .clickable { itemPendingDelete = it1 },
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
