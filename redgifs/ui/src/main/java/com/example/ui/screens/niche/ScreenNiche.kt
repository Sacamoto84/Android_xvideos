package com.example.ui.screens.niche

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.common.sharedPref.Settings
import com.client.common.urlVideImage.UrlImage
import com.example.ui.screens.explorer.tab.gifs.ColumnSelect
import com.example.ui.screens.niche.atom.NichePreview
import com.example.ui.screens.niche.atom.NicheProfile
import com.example.ui.screens.niche.atom.NicheTopCreator
import com.example.ui.screens.profile.ScreenRedProfile
import com.example.ui.screens.ui.atom.ButtonUp
import com.example.ui.screens.ui.atom.TabBarPoints
import com.example.ui.screens.ui.lazyrow123.LazyRow123
import com.example.ui.screens.ui.sortByOrder.SortByOrder
import com.redgifs.common.ThemeRed
import com.redgifs.model.Order

class ScreenRedNiche(val nicheName: String = "pumped-pussy") : Screen {

    override val key: ScreenKey = uniqueScreenKey

    //@Transient
    //val columnSelect = ColumnSelect(Settings.current_count_niches)

    @Composable
    override fun Content() {

        val columnSelect = remember { ColumnSelect(Settings.current_count_niches) }

        val g0 = Settings.gallery_count[0].field.collectAsStateWithLifecycle().value
        val g1 = Settings.gallery_count[1].field.collectAsStateWithLifecycle().value
        val g2 = Settings.gallery_count[2].field.collectAsStateWithLifecycle().value
        val g3 = Settings.gallery_count[3].field.collectAsStateWithLifecycle().value
        val g4 = Settings.gallery_count[4].field.collectAsStateWithLifecycle().value
        val navigator = LocalNavigator.currentOrThrow
        val vm = getScreenModel<ScreenNicheSM, ScreenNicheSM.Factory> { factory -> factory.create(nicheName) }
        val sort = vm.lazyHost.sortType.collectAsStateWithLifecycle().value
        LaunchedEffect(Unit) { vm.lazyHost.columns = columnSelect.column }

        Scaffold(
            bottomBar = {
                Column {
                    HorizontalDivider(color = ThemeRed.colorBorderGray)
                    Row(
                        modifier = Modifier.fillMaxWidth().height(48.dp).background(ThemeRed.colorTabLevel1),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(2.dp))
                            SortByOrder(
                                listOf(Order.TRENDING, Order.TOP, Order.LATEST),
                                sort,
                                onSelect = { vm.lazyHost.changeSortType(it) },
                                containerColor = ThemeRed.colorCommonBackground
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            UrlImage(
                                vm.niche.thumbnail,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )

                        }

                        BasicText(
                            vm.niche.name,
                            modifier = Modifier.padding(horizontal = 4.dp).weight(1f),
                            style = TextStyle(
                                color = Color.LightGray,
                                fontSize = 18.sp,
                                fontFamily = ThemeRed.fontFamilyDMsanss
                            ),
                            autoSize = TextAutoSize.StepBased(10.sp, 18.sp)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.width(46.dp).height(46.dp).clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp)).background(ThemeRed.colorCommonBackground)
                                    .clickable(onClick = {
                                        columnSelect.addColumn( g0, g1, g2, g3, g4 ); vm.lazyHost.columns = columnSelect.column
                                    }), contentAlignment = Alignment.Center
                            ) { TabBarPoints(vm.lazyHost.columns, true) }
                            Spacer(modifier = Modifier.width(2.dp))
                            ButtonUp(44.dp) { vm.lazyHost.gotoUp() }
                            Spacer(modifier = Modifier.width(2.dp))
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize(), containerColor = Color(0xFF0F0F0F)
        ) { padding ->

            Box(
                modifier = Modifier
                    .padding(bottom = padding.calculateBottomPadding())
                    .fillMaxSize()
            )
            {
                LazyRow123(
                    host = vm.lazyHost, modifier = Modifier.fillMaxWidth(),
                    onClickOpenProfile = {
                        //vm.lazyHost.currentIndexGoto = vm.lazyHost.currentIndex
                        navigator.push(ScreenRedProfile(it))
                    },
                    //gotoPosition = vm.lazyHost.currentIndexGoto,
                    //contentPadding = PaddingValues(top = 48.dp),
                    contentBeforeList = {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F0F0F))
                        )
                        {
                            NicheProfile(vm.hostDI.savedRed, vm.niche)
                            Text(
                                "Related Niches",
                                color = Color.White,
                                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                                fontFamily = ThemeRed.fontFamilyDMsanss
                            )
                            LazyRow {
                                items(vm.related.niches.size) {
                                    NichePreview(vm.related.niches[it], onClick = {
                                        navigator.push(
                                            ScreenRedNiche(vm.related.niches[it].id)
                                        )
                                    })
                                }
                            }
                            Text(
                                "✨ Top Creators in ${vm.niche.name}",
                                color = Color.White,
                                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                                fontFamily = ThemeRed.fontFamilyDMsanss
                            )
                            LazyRow {
                                items(vm.topCreator.creators) {
                                    NicheTopCreator(
                                        it,
                                        onClick = { navigator.push(ScreenRedProfile(it.username)) })
                                }
                            }

                        }
                    }
                )
            }
        }
    }
}