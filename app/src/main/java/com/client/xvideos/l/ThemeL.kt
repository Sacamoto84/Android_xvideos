package com.client.xvideos.l

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.client.xvideos.R
import com.client.xvideos.redgifs.common.ThemeRed

object ThemeL {

    val g0 = Color(0xFF4CAF50)
    val r0 = Color(0xFFF44336)
    val b0 = Color(0xFF2196F3)

    val grey0 = Color(0xFFdedede)
    val grey1 = Color(0xFFbababa)
    val grey2 = Color(0xFF9c9c9c)
    val grey3 = Color(0xFF3b3b3b)
    val grey4 = Color(0xFF333333)
    val grey5 = Color(0xFF292929)
    val grey6 = Color(0xFF262626)
    val grey7 = Color(0xFF1c1c1c)


    val lavender = Color(0xFFa3aff5)

    val primaryColor = Color(0xFFff96a3)

    val red = Color(0xFFC9554C)

    val secondaryColor = Color(0xFF3b3b3b)
    val textColor = grey1

    val greyBackground = grey6


    val fontFamilyPopinsRegular = FontFamily(Font(R.font.poppins_regular))
    val fontFamilyPopinsMedium = FontFamily(Font(R.font.poppins_medium))
    val fontFamilyPopinsSemiBold = FontFamily(Font(R.font.poppins_semibold))
    val fontFamilyPopinsBold = FontFamily(Font(R.font.poppins_bold))
    val fontFamilyPopinsExtraBold = FontFamily(Font(R.font.poppins_extrabold))

    val fontFamilyDMsanss = FontFamily(Font(R.font.dm_sans))

    val fontFamilyKarla = FontFamily(Font(R.font.karla))




    //--- Screen Config ---
    val styleTextConfigL = TextStyle(fontSize = 20.sp, color = textColor, fontFamily = fontFamilyKarla)




    //--- Expand Menu ---
    object ExpandMenu {
        val tintColor = Color(0xFF1F1F1F)  // Почти черный
        val backgroundColor = Color(0xFFFFFAF5)  // Теплый белый с кремовым оттенком
        val style = TextStyle(  color = tintColor, fontFamily = ThemeRed.fontFamilyPopinsRegular, fontSize = 20.sp )
    }




}