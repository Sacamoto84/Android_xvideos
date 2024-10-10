package com.client.xvideos.screens.dashboards

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.client.xvideos.screens.item.util.Player
import com.client.xvideos.urlStart


class ScreenDashBoards : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        //val vm = getScreenModel<ScreenDashBoardsScreenModel>()
        val vm: ScreenDashBoardsScreenModel = getScreenModel()
        // ...
        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.Black) { innerPadding ->

            val itemsPerRow =
                if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else 3

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                item {
                    //if(currentNumberScreen == 1)
                    ScreenDashBoardsBottomNavigationButtons(1, vm)
                }

                items(vm.l.filter { !it.link.contains("THUMBNUM") }.chunked(itemsPerRow)) { row ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        row.forEach { cell ->

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(1.dp)
                                    //.border(1.dp, Color.Black)
                                    //.fillParentMaxWidth()
                                    .clickable { vm.openItem(urlStart + cell.link, navigator) },
                            ) {


                                AsyncImage(
                                    model = cell.previewImage,
                                    contentDescription = null,
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier
                                        //.weight(1f)
                                        .fillMaxWidth()
                                )
                                //PlayerLite(it.previewVideo)
                                //Text(text = it.id.toString())
                                Text(
                                    text = cell.duration.dropLast(1),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .offset(1.dp, 1.dp),
                                    textAlign = TextAlign.Right,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )

                                Text(
                                    text = cell.duration.dropLast(1),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Right,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )

                            }


                        }
                        // Если элементов в строке меньше, чем itemsPerRow, добавляем пустые ячейки
                        if (row.size < itemsPerRow) {
                            repeat(itemsPerRow - row.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }


                }

                item {
                    ScreenDashBoardsBottomNavigationButtons(1, vm)
                }

            }

        }


    }
}