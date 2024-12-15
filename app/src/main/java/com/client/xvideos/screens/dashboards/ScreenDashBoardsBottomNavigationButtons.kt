package com.client.xvideos.screens.dashboards

import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.core.Menu
import com.composables.core.MenuButton
import com.composables.core.MenuContent
import com.composables.core.MenuItem
import com.composables.core.rememberMenuState
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ScreenDashBoardsBottomNavigationButtons(vm: ScreenDashBoardsScreenModel) {

    val currentNumberScreen = vm.pagerState.currentPage

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Box(
            modifier = Modifier
                .padding(horizontal = (0.5).dp)
                .weight(1f)
                .height(48.dp)
                .background(
                    if (currentNumberScreen == 1)
                        Color(0xFF2c2c2c)
                    else
                        Color(0xFFFF9000)
                )
                .clickable {
                    GlobalScope.launch(Dispatchers.Main) {
                        vm.pagerState.scrollToPage((vm.pagerState.currentPage - 1).coerceAtLeast(1))
                    }
                    //vm.openNew(currentNumberScreen - 1)
                }, contentAlignment = Alignment.Center
        ) {
            Text(
                "<",
                color = if (currentNumberScreen == 1) Color.DarkGray else Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        repeat(10) {

            Box(
                modifier = Modifier
                    .padding(horizontal = (0.5).dp)
                    .weight(1f)
                    .height(48.dp)
                    .border(
                        2.dp,
                        Color(if (vm.pagerState.currentPage == it + 1) 0xFFFF9900 else 0x000000)
                    )
                    .background(
                        Color(0xFF252525)
                    )
                    .clickable {

                        //vm.openNew(it + 1)
                        GlobalScope.launch(Dispatchers.Main) {
                            vm.pagerState.scrollToPage((it + 1).coerceAtLeast(1))
                        }

                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (it + 1).toString(),
                    color = Color(0xFFCCCCCC),
                    textAlign = TextAlign.Center
                )
            }
        }


        Box(
            modifier = Modifier
                .padding(horizontal = (0.5).dp)
                .weight(1f)
                .height(48.dp)
                .background(
                    if (currentNumberScreen == 20000)
                        Color(0xFF2c2c2c)
                    else
                        Color(0xFFFF9000)
                )
                .clickable {
                    GlobalScope.launch(Dispatchers.Main) {
                        vm.pagerState.scrollToPage(
                            (vm.pagerState.currentPage + 1).coerceIn(
                                1, 20000
                            )
                        )
                    }
                }, contentAlignment = Alignment.Center
        ) {
            Text(
                ">",
                color = if (currentNumberScreen == 20000) Color.DarkGray else Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }


    }


}


@Preview
@Composable
fun ScreenDashBoardsBottomNavigationButtonsPreview() {

    var value by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Gray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(value.toString(), fontSize = 32.sp)

        BottomListNavigationButtons(value, {
            value = it
            println("!!! $it")
        }, 20)

    }
}


//Черный цвет текста
private val colorTextBlack = Color(0xFF2C2C2C)

private val colorAccent = Color(0xFFFF9000)

private val colorTextWhite = Color(0xFFCCCCCC)

private val  height = 48.dp
/**
 * Bottom navigation buttons
 * Навигация для переключения экранов, возвращает корая будет выбирать номер экрана
 * max - Максимальный индекс экрана
 * onChange - Функция вызывается при изменении экрана и передается номер экрана
 */
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun BottomListNavigationButtons(value: Int, onChange: (Int) -> Unit, max: Int = 20000) {

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        ///////////////////////////////
        Box(
            modifier = Modifier
                .padding(horizontal = (0.5).dp)
                .weight(1f)
                .height(height)
                .background(
                    if (value == 1) colorTextBlack else colorAccent
                )
                .clickable {
                    GlobalScope.launch(Dispatchers.Main) {
                        onChange.invoke((value - 1).coerceAtLeast(1))
                    }
                    //vm.openNew(currentNumberScreen - 1)
                }, contentAlignment = Alignment.Center
        ) {
            Text(
                "<",
                color = if (value == 1) Color.DarkGray else Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        ///////////////////////////////


        repeat(9) {

            Box(
                modifier = Modifier
                    .padding(horizontal = (0.5).dp)
                    .weight(1f)
                    .height(height)
                    .border(
                        2.dp,
                        Color(if (value == it + 1) 0xFFFF9900 else 0x000000)
                    )
                    .background(
                        Color(0xFF252525)
                    )
                    .clickable {
                        GlobalScope.launch(Dispatchers.Main) {
                            onChange.invoke((it + 1).coerceAtLeast(1))
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (it + 1).toString(),
                    color = colorTextWhite,
                    textAlign = TextAlign.Center
                )
            }
        }




        ///////////////////////////////
//        Box(
//            modifier = Modifier
//                .padding(horizontal = (0.5).dp)
//                .weight(1f)
//                .height(height)
//                .background(Color(0xFF252525))
//                .clickable {
//                    GlobalScope.launch(Dispatchers.Main) {
//
//                    }
//                },
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                "...",
//                color = colorTextWhite,
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }

        Box(Modifier
            .padding(horizontal = (0.5).dp)
            .weight(1f)
            .height(height)
            ) {

            Menu(modifier = Modifier.align(Alignment.TopCenter).width(240.dp), state = rememberMenuState(expanded = true)) {

                MenuButton(
                    Modifier.clip(RoundedCornerShape(6.dp)).background(Color.White)
                        .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(6.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        //modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        BasicText("...", style = TextStyle(fontWeight = FontWeight(500)))
                    }
                }

                MenuContent(
                    modifier = Modifier.padding(top = 4.dp).width(320.dp).clip(RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(6.dp)).background(Color.White).padding(4.dp),
                    exit = fadeOut()
                ) {
                    MenuItem(modifier = Modifier.clip(RoundedCornerShape(6.dp)), onClick = { /* TODO handle click */ }) {
                        BasicText("Option 1", Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 10.dp))
                    }
                    MenuItem(modifier = Modifier.clip(RoundedCornerShape(6.dp)), onClick = { /* TODO handle click */ }) {
                        BasicText("Option 2", Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 10.dp))
                    }
                    MenuItem(modifier = Modifier.clip(RoundedCornerShape(6.dp)), onClick = { /* TODO handle click */ }) {
                        BasicText("Option 3", Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 10.dp))
                    }
                }
            }
        }



        ///////////////////////////////
        Box(
            modifier = Modifier
                .padding(horizontal = (0.5).dp)
                .weight(1f)
                .height(height)
                .background(if (value == max) colorTextBlack else colorAccent)
                .clickable {
                    GlobalScope.launch(Dispatchers.Main) {
                        onChange.invoke((value + 1).coerceIn(1, max))
                    }
                }, contentAlignment = Alignment.Center
        ) {
            Text(
                ">",
                color = if (value == max) Color.DarkGray else Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }


    }




}