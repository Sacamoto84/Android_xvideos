package com.client.xvideos.xvideos.screens.videoplayer.atom

import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.R
import com.composables.core.Menu
import com.composables.core.MenuButton
import com.composables.core.MenuContent
import com.composables.core.MenuItem
import com.composables.core.rememberMenuState

@Composable
fun VideoSpeedSelector(speed: Float, onClick: (Float) -> Unit) {

    val state = rememberMenuState(expanded = false)

    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(50))
    ) {

        Menu(
            modifier = Modifier,
            state = state
        ) {

            //Сама кнопка для вызова диалога
            MenuButton(
                Modifier
                    //.fillMaxSize()
                    .fillMaxHeight()
                    .background(Color.Black)
            ) {

                Row(

                    modifier = Modifier
                        .width(100.dp)
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Icon(
                        painter = painterResource(R.drawable.speed),
                        contentDescription = "", tint = Color.White
                    )

                    Spacer(Modifier.width(8.dp))

                    BasicText(
                        "$speed",
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    )
                }
            }

            val list = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f)

            MenuContent(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .wrapContentWidth()
                    //.width(320.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
                    .background(Color.White)
                    .padding(4.dp),
                exit = fadeOut()
            ) {

                list.forEach {

                    MenuItem(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (it == speed) Color.Green else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            ),
                        onClick = { onClick.invoke(it) }) {

                        BasicText(
                            it.toString(),
                            Modifier
                                .padding(vertical = 10.dp, horizontal = 10.dp),
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )

                    }
                }
            }
        }
    }

}