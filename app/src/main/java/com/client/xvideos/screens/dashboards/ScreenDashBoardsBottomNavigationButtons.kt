package com.client.xvideos.screens.dashboards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
import timber.log.Timber
import kotlin.math.absoluteValue

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
                        colorBlackBackground
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
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(value.toString(), fontSize = 32.sp)

        BottomListNavigationButtons(value, {
            value = it
            println("!!! $it")
        }, 20000)

    }
}


//Черный цвет текста
private val colorTextBlack = Color(0xFF2C2C2C)

private val colorAccent = Color(0xFFFF9000)

private val colorTextWhite = Color(0xFFCCCCCC)

private val colorBlackBackground = Color(0xFF252525)

private val height = 48.dp

/**
 * Bottom navigation buttons
 * Навигация для переключения экранов, возвращает корая будет выбирать номер экрана
 * max - Максимальный индекс экрана
 * onChange - Функция вызывается при изменении экрана и передается номер экрана
 */
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun BottomListNavigationButtons(value: Int, onChange: (Int) -> Unit, max: Int) {

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


        if (max > 9) {
            if (value <= 9) {
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
                                colorBlackBackground
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
            } else {


            }
        } else {

            if (max < 9) {

                repeat(max) {
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
                                colorBlackBackground
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

        MenuDot(
            modifier = Modifier.weight(1f),
            value,
            onChange = { onChange.invoke(it) },
            max = max
        )


        ///////////////////////////////
        Box(
            modifier = Modifier
                .padding(horizontal = (0.5).dp)
                .weight(1f)
                .height(height)
                .background(if (value >= max) colorTextBlack else colorAccent)
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

/**
 * Кнопка с вызываемым диалогом
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MenuDot(modifier: Modifier = Modifier, value: Int, onChange: (Int) -> Unit, max: Int) {

    val state = rememberMenuState(expanded = true)

    Box(
        Modifier
            .padding(horizontal = (0.5).dp)
            .then(modifier)
            .height(height)
    ) {

        Menu(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(240.dp),
            state = state
        ) {

            MenuButton(
                Modifier.fillMaxSize().background(colorBlackBackground)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    BasicText("...",
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            color = colorTextWhite,
                            fontSize = 24.sp
                        )
                    )
                }
            }

            MenuContent(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
                    .background(colorBlackBackground),
                // exit = fadeOut()
                //, enter = fadeIn()
            ) {

                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.Center) {



                    //Клавиатура возвращает число
                    KeyboardNumber(value, { onChange.invoke(it); state.expanded = false }, max = max)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("of $max", color = colorTextWhite, fontSize = 24.sp)

                }

//                    MenuItem(
//                        modifier = Modifier.clip(RoundedCornerShape(6.dp)),
//                        onClick = { /* TODO handle click */ }) {
//                        BasicText(
//                            "Option 1",
//                            Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 10.dp, horizontal = 10.dp)
//                        )
//                    }

            }

        }
    }

}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun KeyboardNumber(value: Int, onClick: (Int) -> Unit, max: Int) {

    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(value.toString(), TextRange(value.toString().length)))
    }

    Column {

        Box(
            modifier = Modifier
                .height(48.dp)
                .width(48.dp * 4 + 2.dp)
                .border(1.dp, Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                textFieldValue.text,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row(modifier = Modifier.padding(bottom = 8.dp)) {

            ButtonNumber("1", w =  48.dp, h = 48.dp * 4 + 1.dp + 1.dp, onClick =  { textFieldValue = TextFieldValue("1", TextRange(1)) })

            FlowRow(maxItemsInEachRow = 3) {
                ButtonNumber("7", onClick =  { textFieldValue = addCharToTextField(textFieldValue, "7") })
                ButtonNumber("8", onClick = { textFieldValue = addCharToTextField(textFieldValue, "8") })
                ButtonNumber("9", onClick = { textFieldValue = addCharToTextField(textFieldValue, "7") })

                ButtonNumber("4",onClick =  { textFieldValue = addCharToTextField(textFieldValue, "4") })
                ButtonNumber("5", onClick = { textFieldValue = addCharToTextField(textFieldValue, "5") })
                ButtonNumber("6", onClick = { textFieldValue = addCharToTextField(textFieldValue, "6") })

                ButtonNumber("1", onClick = { textFieldValue = addCharToTextField(textFieldValue, "1") })
                ButtonNumber("2", onClick = { textFieldValue = addCharToTextField(textFieldValue, "2") })
                ButtonNumber("3", onClick = { textFieldValue = addCharToTextField(textFieldValue, "3") })

                ButtonNumber("C", onClick = { textFieldValue = TextFieldValue("", TextRange(0)) })
                ButtonNumber("0",onClick =  { textFieldValue = addCharToTextField(textFieldValue, "0") })
                ButtonNumber("<-", onClick = {
                    val newText = textFieldValue.text.dropLast(1)
                    val newCursorPosition = newText.length
                    textFieldValue = TextFieldValue(newText, TextRange(newCursorPosition))
                })
            }

            Box(
                modifier = Modifier
                    .padding(top = 0.25.dp)
                    .width(48.dp)
                    .height(48.dp * 4 + 1.dp + 1.dp)
                    .border(1.dp, Color.Gray)
                    .clickable {
                        val a = textFieldValue.text.toIntOrNull()
                        if (a != null) {
                            try {
                                val i = a
                                    .toInt()
                                    .coerceIn(1, max)
                                onClick.invoke(i)
                            } catch (e: Exception) {
                                Timber.e(e.localizedMessage)
                            }
                        }


                    },
                contentAlignment = Alignment.Center
            ) {
                Text("E", color = Color.White, fontSize = 24.sp)
            }
        }
    }

}

private fun addCharToTextField(textFieldValue: TextFieldValue, char: String): TextFieldValue {
    val newText = textFieldValue.text + char
    val newCursorPosition = newText.length  // Курсор перемещаем в конец текста
    return TextFieldValue(newText, TextRange(newCursorPosition))
}

@Composable
private fun ButtonNumber(text: String, w : Dp = 48.dp, h: Dp = 48.dp, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(0.25.dp)
            .width(w)
            .height(h)
            .border(1.dp, Color.Gray)
            .clickable { onClick.invoke() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontSize = 24.sp)
    }
}