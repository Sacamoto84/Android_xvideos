package com.client.xvideos.screens.common.bottomKeyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.core.Menu
import com.composables.core.MenuButton
import com.composables.core.MenuContent
import com.composables.core.rememberMenuState

private val height = 48.dp

//Черный цвет текста
private val colorTextBlack = Color(0xFF2C2C2C)

private val colorAccent = Color(0xFFFF9000)

private val colorTextWhite = Color(0xFFCCCCCC)

private val colorBlackBackground = Color(0xff3b3b3b)

/**
 * Кнопка с вызываемым диалогом
 */
@Composable
fun MenuDot(modifier: Modifier = Modifier, value: Int, onChange: (Int) -> Unit, max: Int) {

    val state = rememberMenuState(expanded = false)

    Box(
        Modifier
            .padding(horizontal = (0.5).dp)
            .then(modifier)
            .height(height)
    ) {

        Menu(
            modifier = Modifier,
            state = state
        ) {

            //Сама кнопка для вызова диалога
            MenuButton(
                Modifier
                    .fillMaxSize()
                    .background(colorBlackBackground)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    BasicText(
                        "...",
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
                    .padding(bottom = 0.dp)
                    .width(312.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFF23242A)),
                // exit = fadeOut()
                //, enter = fadeIn()
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = 8.dp), contentAlignment = Alignment.Center
                ) {
                    //Клавиатура возвращает число
                    KeyboardNumber(
                        value,
                        { onChange.invoke(it); state.expanded = false },
                        max = max
                    )
                }

            }

        }
    }

}
