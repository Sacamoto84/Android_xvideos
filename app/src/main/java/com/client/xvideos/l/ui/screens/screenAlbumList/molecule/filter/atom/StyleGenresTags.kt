package com.client.xvideos.l.ui.screens.screenAlbumList.molecule.filter.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object StyleGenresTags {


    //
    val modifierSelectTextItem = Modifier
        .padding(start = 4.dp, end = 4.dp, top = 4.dp)
        .fillMaxWidth()
        .clip(RoundedCornerShape(50))
        .background(Color(0xFF416476))
        .padding(start = 8.dp)
        .fillMaxWidth()

    val colorSelectTextItem = Color(0xFFCEEEFC)
}