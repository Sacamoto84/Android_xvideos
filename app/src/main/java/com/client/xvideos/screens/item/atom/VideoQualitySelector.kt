package com.client.xvideos.screens.item.atom

import androidx.annotation.OptIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import com.client.xvideos.screens.item.ScreenItemScreenModel
import com.composables.core.Menu
import com.composables.core.MenuButton
import com.composables.core.MenuContent
import com.composables.core.MenuItem
import com.composables.core.rememberMenuState

@OptIn(UnstableApi::class)
@Preview
@Composable
fun VideoQualitySelectorPreview() {

    var q by remember { mutableIntStateOf(250) }

    val list = remember {
        mutableStateListOf<ScreenItemScreenModel.FORMAT>().apply {
            add(ScreenItemScreenModel.FORMAT(0, 250, 250, bitrate = 0, isSelect = true))
            add(ScreenItemScreenModel.FORMAT(1, 360, 360, bitrate = 0, isSelect = true))
            add(ScreenItemScreenModel.FORMAT(2, 480, 480, bitrate = 0, isSelect = true))
            add(ScreenItemScreenModel.FORMAT(3, 720, 720, bitrate = 0, isSelect = true))
            add(ScreenItemScreenModel.FORMAT(4, 1080, 1080, bitrate = 0, isSelect = true))
            add(ScreenItemScreenModel.FORMAT(5, 1440, 1440, bitrate = 0, isSelect = true))
        }
    }

    VideoQualitySelector(
        q,
        list = list
    ) { q = it }
}


@OptIn(UnstableApi::class)
@Composable
fun VideoQualitySelector(
    h: Int,
    list: SnapshotStateList<ScreenItemScreenModel.FORMAT>,
    onClick: (Int) -> Unit,
) {

    val state = rememberMenuState(expanded = false)

    Box(
        modifier = Modifier
            .height(40.dp)
            //.width(64.dp)
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
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        "${h}p",
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    )
                }
            }

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
                                if (it.height == h) Color.Green else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            ),
                        onClick = { onClick.invoke(it.height) }) {

                        BasicText(
                            it.height.toString(),
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