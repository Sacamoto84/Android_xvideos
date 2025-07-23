package com.example.ui.screens.explorer.top

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.redgifs.common.ThemeRed

@Composable
fun TabRow(
    titlesIcon: List<ImageVector>, onChangeState: (Int) -> Unit,
    value: Int,
    containerColor: Color = ThemeRed.colorCommonBackground2,
    overlay0: @Composable () -> Unit = {},
    overlay1: @Composable () -> Unit = {},
    overlay2: @Composable () -> Unit = {},
    overlay3: @Composable () -> Unit = {},
    overlay4: @Composable () -> Unit = {},
    overlay5: @Composable () -> Unit = {},
) {

    val haptic = LocalHapticFeedback.current

    var state by remember(value){ mutableIntStateOf(value) }

    SecondaryTabRow(
        modifier = Modifier.height(48.dp),
        selectedTabIndex = state,
        containerColor = containerColor,
        contentColor = Color.White,
        indicator = {
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(state, matchContentSize = false),
                height = 4.dp,
                color = ThemeRed.colorRed
            )
        }
    ) {
        titlesIcon.forEachIndexed { index, item ->
            Box(modifier = Modifier.background(if (index == state) Color.Transparent else Color.Transparent)) {
                Tab(
                    selected = index == state,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        state = index
                        onChangeState.invoke(index)
                    },
                    icon = {
                        Icon(
                            item, contentDescription = null,
                            tint = if (index == state) Color.White else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )

                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                    when (index) {
                        0 -> overlay0.invoke()
                        1 -> overlay1.invoke()
                        2 -> overlay2.invoke()
                        3 -> overlay3.invoke()
                        4 -> overlay4.invoke()
                        5 -> overlay5.invoke()
                    }
                }

            }
        }
    }
}

@Preview
@Composable
fun TabRowPreview() {
    val l = listOf(
        Icons.Outlined.Movie,
        Icons.Outlined.Image,
        Icons.Outlined.Person,
        Icons.Outlined.Group,
        Icons.Outlined.BookmarkBorder
    )
    TabRow(l, onChangeState = {}, 2)
}

