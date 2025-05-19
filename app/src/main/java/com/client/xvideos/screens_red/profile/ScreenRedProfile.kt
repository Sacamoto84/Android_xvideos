package com.client.xvideos.screens_red.profile

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.imageLoader
import com.client.xvideos.R
import com.client.xvideos.screens_red.ThemeRed
import com.client.xvideos.screens_red.profile.atom.RedProfileCreaterInfo
import com.client.xvideos.screens_red.profile.atom.RedProfileTile
import com.client.xvideos.screens_red.profile.feedControl.RedProfileFeedControlsContainer
import com.client.xvideos.screens_red.profile.tags.TagsBlock
import com.composables.core.HorizontalSeparator
import com.composeunstyled.Disclosure
import com.composeunstyled.DisclosureHeading
import com.composeunstyled.DisclosurePanel
import com.composeunstyled.rememberDisclosureState

class ScreenRedProfile() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val vm: ScreenRedProfileSM = getScreenModel()

        // val list = vm.creator?.gifs

        val gridState = rememberLazyGridState()
        val imgLoader = LocalContext.current.imageLoader


        val list = vm.list.collectAsState()

        val isLoading = vm.isLoading.collectAsState().value

        val stateDisclosure = rememberDisclosureState()

        val stateDisclosureDescription = rememberDisclosureState()

        // триггерим подгрузку, когда остаётся ≤6 элементов до конца
        LaunchedEffect(gridState) {
            snapshotFlow { gridState.layoutInfo }
                .collect { info ->
                    val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: 0
                    val total = info.totalItemsCount
                    if (total - lastVisible <= 6) {
                        vm.loadNextPage()
                    }
                }
        }

        Scaffold(
            bottomBar = { RedBottomBar() },
            containerColor = ThemeRed.colorCommonBackground
        ) { padding ->

            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(4.dp) // Отступы по краям сетки
            ) {

                //Описание и теги
                item(
                    span = { GridItemSpan(maxLineSpan) } // Заставляет этот item занять все столбцы
                ) {
                    vm.creator?.let { profileData ->
                        RedProfileCreaterInfo(profileData)
                    }
                }

                //Описание и теги
                item(
                    span = { GridItemSpan(maxLineSpan) } // Заставляет этот item занять все столбцы
                ) {
                    vm.creator?.let { profileData ->

                        Disclosure(state = stateDisclosure) {
                            DisclosureHeading(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (stateDisclosure.expanded) ThemeRed.colorBorderGray else Color.Transparent)
                                    .border(
                                        1.dp,
                                        ThemeRed.colorBorderGray,
                                        RoundedCornerShape(8.dp)
                                    )
                                ,
                                //shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(
                                    //vertical = 12.dp,
                                    horizontal = 16.dp
                                ),
                                onClick = {
                                    stateDisclosure.expanded = stateDisclosure.expanded.not()
                                }) {

                                val degrees by animateFloatAsState(
                                    if (stateDisclosure.expanded) -180f else 0f,
                                    tween()
                                )

                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Tags",
                                        color = Color.White,
                                        fontFamily = ThemeRed.fontFamilyPopinsRegular,
                                                fontSize = 18.sp
                                    )
                                    Icon(
                                        painter = painterResource(R.drawable.arrow_down),
                                        contentDescription = null, tint = Color.White,
                                        modifier = Modifier.size(12.dp).rotate(degrees)
                                    )
                                }


                            }
                            DisclosurePanel {
                                Box(
                                    Modifier
                                        .padding(top = 4.dp)
                                        .padding(horizontal = 16.dp)
                                        .fillMaxWidth()
                                ) {
                                    TagsBlock(vm.tags.collectAsStateWithLifecycle().value.toList())
                                }
                            }
                        }

                        //TagsBlock(vm.tags.collectAsStateWithLifecycle().value.toList())


                    }
                }

                //Управление списком
                item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    RedProfileFeedControlsContainer(vm)
                }

                items(list.value, key = { it.id }) { item ->
                    RedProfileTile(item)
                }

            }


            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }


        }

    }
}


@Composable
fun RedBottomBar() {

    Column {
        HorizontalSeparator(ThemeRed.colorBottomBarDivider, thickness = 2.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(ThemeRed.colorBottomBarBackground)
        ) {


        }
    }


}