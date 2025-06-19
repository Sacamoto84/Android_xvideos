package com.client.xvideos.red.screens.niche

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.feature.redgifs.types.Order
import com.client.xvideos.red.common.ui.atom.Selector
import com.client.xvideos.red.common.ui.lazyrow123.LazyRow123
import com.client.xvideos.red.common.ui.sortByOrder.SortByOrder
import com.client.xvideos.red.screens.niche.atom.NichePreview
import com.client.xvideos.red.screens.niche.atom.NicheProfile
import com.client.xvideos.red.screens.niche.atom.NicheTopCreator
import com.client.xvideos.red.screens.profile.ScreenRedProfile
import com.client.xvideos.red.screens.saved.ScreenRedSaved
import timber.log.Timber
import kotlin.math.roundToInt

data class ScreenRedNiche(val nicheName: String = "pumped-pussy") : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedBoxWithConstraintsScope")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val vm = getScreenModel<ScreenNicheSM, ScreenNicheSM.Factory> { factory ->
            Timber.d("!!! factory.create(${nicheName})")
            factory.create(nicheName)
        }

        val sort = vm.lazyHost.sortType.collectAsStateWithLifecycle().value

        val isConnected by vm.lazyHost.isConnected.collectAsState()

        val toolbarHeight = 50.dp
        val minToolbarHeight = 0.dp // высота лишь третьей строки
        val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.toPx() }
        val minToolbarHeightPx = with(LocalDensity.current) { minToolbarHeight.toPx() }

        val offsetY = remember { mutableFloatStateOf(0f) }

        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val delta = available.y
                    val newOffset = (offsetY.floatValue + delta).coerceIn(-(toolbarHeightPx - minToolbarHeightPx), 0f)
                    offsetY.floatValue = newOffset
                    return Offset.Zero
                }
            }
        }





        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color(0xFF0F0F0F)) {

            Box(modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection)) {

                LazyRow123(
                    host = vm.lazyHost,
                    modifier = Modifier.fillMaxWidth(),
                    onClickOpenProfile = {
                        vm.lazyHost.currentIndexGoto = vm.lazyHost.currentIndex
                        navigator.push(ScreenRedProfile(it))
                    },
                    gotoPosition = vm.lazyHost.currentIndexGoto,
                    option = vm.expandMenuVideoList,
                    contentPadding = PaddingValues(top = toolbarHeight),
                    contentBeforeList = {

                        Column(
                            modifier = Modifier.fillMaxWidth().background(Color(0xFF0F0F0F))) {

                            NicheProfile(vm.niche)
                            Text("Related Niches", color = Color.White, modifier = Modifier.padding(start = 16.dp, top = 16.dp))
                            LazyRow {
                                items(vm.related.niches.size) {
                                    NichePreview(vm.related.niches[it], onClick = {navigator.push(
                                        ScreenRedNiche(vm.related.niches[it].id))})
                                }
                            }

                            Text("✨ Top Creators in ${vm.niche.name}", color = Color.White, modifier = Modifier.padding(start = 16.dp, top = 16.dp))
                            LazyRow {
                                items(vm.topCreator.creators) {
                                    NicheTopCreator(it, onClick = { navigator.push(ScreenRedProfile(it.username)) })
                                }
                            }

                        }
                    }
                )

            }

        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(toolbarHeight)
                .offset { IntOffset(x = 0, y = offsetY.floatValue.roundToInt()) }
                .background(MaterialTheme.colorScheme.primaryContainer)) {

            SortByOrder(
                listOf(
                    Order.TRENDING, Order.TOP,
                    Order.LATEST,
                ), sort, onSelect = { vm.lazyHost.changeSortType(it) })

            Button(onClick = {navigator.push(ScreenRedSaved())}) {
            }

            Selector(vm.lazyHost.columns) { vm.lazyHost.columns  = it }

        }

    }

}