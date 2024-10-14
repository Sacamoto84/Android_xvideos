package com.client.xvideos.screens.dashboards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class ScreenDashBoards : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        //val vm = getScreenModel<ScreenDashBoardsScreenModel>()
        val vm: ScreenDashBoardsScreenModel = getScreenModel()
        //val pagerState = rememberPagerState(1) { 20000 }
        // ...
        Scaffold(
            bottomBar = { ScreenDashBoardsBottomNavigationButtons(vm) },
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Black,


        ) { innerPadding ->

            HorizontalPager(
                state = vm.pagerState,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 1
            ) { pageIndex ->

                Box(modifier = Modifier.padding(innerPadding)) {
                    DashboardsPaginatedListScreen(pageIndex, vm)
                }

            }

//            LazyColumn(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//
//                item {
//                    //if(currentNumberScreen == 1)
//                    ScreenDashBoardsBottomNavigationButtons(1, vm)
//                }
//
//                items(vm.l.filter { !it.link.contains("THUMBNUM") }.chunked(itemsPerRow)) { row ->
//                    Row(modifier = Modifier.fillMaxWidth()) {
//                        row.forEach { cell ->
//
//                            Box(
//                                modifier = Modifier
//                                    .weight(1f)
//                                    .padding(1.dp)
//                                    //.border(1.dp, Color.Black)
//                                    //.fillParentMaxWidth()
//                                    .clickable { vm.openItem(urlStart + cell.link, navigator) },
//                            ) {
//
//
//                                AsyncImage(
//                                    model = cell.previewImage,
//                                    contentDescription = null,
//                                    contentScale = ContentScale.FillWidth,
//                                    modifier = Modifier
//                                        //.weight(1f)
//                                        .fillMaxWidth()
//                                )
//                                //PlayerLite(it.previewVideo)
//                                //Text(text = it.id.toString())
//                                Text(
//                                    text = cell.duration.dropLast(1),
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .offset(1.dp, 1.dp),
//                                    textAlign = TextAlign.Right,
//                                    fontSize = 14.sp,
//                                    color = Color.Black
//                                )
//
//                                Text(
//                                    text = cell.duration.dropLast(1),
//                                    modifier = Modifier.fillMaxWidth(),
//                                    textAlign = TextAlign.Right,
//                                    fontSize = 14.sp,
//                                    color = Color.White
//                                )
//
//                            }
//
//
//                        }
//                        // Если элементов в строке меньше, чем itemsPerRow, добавляем пустые ячейки
//                        if (row.size < itemsPerRow) {
//                            repeat(itemsPerRow - row.size) {
//                                Spacer(modifier = Modifier.weight(1f))
//                            }
//                        }
//                    }
//
//
//                }
//
//                item {
//                    ScreenDashBoardsBottomNavigationButtons(1, vm)
//                }
//
//            }

        }


    }
}