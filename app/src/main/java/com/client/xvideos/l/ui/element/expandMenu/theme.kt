package com.client.xvideos.l.ui.element.expandMenu

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.client.xvideos.redgifs.common.ThemeRed

object themeExpandMenu {

    val tintColor = Color(0xFF1F1F1F)  // Почти черный
    val backgroundColor = Color(0xFFFFFAF5)  // Теплый белый с кремовым оттенком
    val style = TextStyle(
        color = tintColor,
        fontFamily = ThemeRed.fontFamilyPopinsRegular,
        fontSize = 20.sp
    )

}

/// Вариант 5: Зеленоватый светлый (природно)
//object themeExpandMenuLightGreen {
//    val tintColor = Color(0xFF2E7D32)  // Темно-зеленый
//    val backgroundColor = Color(0xFFF1F8E9)  // Очень светло-зеленый
//    val style = TextStyle(
//        color = tintColor,
//        fontFamily = ThemeRed.fontFamilyPopinsRegular,
//        fontSize = 20.sp
//    )
//}