package com.client.xvideos.xvideos.screens.dashboards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.feature.country.ComposeCountry
import com.client.xvideos.noRippleClickable
import com.client.xvideos.xvideos.screens.config.ScreenConfig
import com.client.xvideos.xvideos.screens.favorites.ScreenFavorites
import com.client.xvideos.ui.theme.grayColor

@Composable
fun TopBarDashboard(){

    val navigator = LocalNavigator.currentOrThrow

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(grayColor(0x0E)),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {
        ComposeCountry(modifier = Modifier)



        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f))



        IconButton(onClick = {
            navigator.push(ScreenFavorites())
        }) {
            Icon(
                imageVector = Icons.Filled.FavoriteBorder,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier
                    .size(32.dp)

            )
        }


        //////////// Настройка ////////////
        Box(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .size(48.dp)
                //.border(1.dp,Color.Gray)
                .noRippleClickable(onClick = {
                    navigator.push(ScreenConfig())
                }),
            contentAlignment = Alignment.Center
        ) {

//                        BasicText(
//                            "?",
//                            style = TextStyle(
//                                fontWeight = FontWeight.Medium,
//                                color = Color(0xFFCCCCCC),
//                                fontSize = 24.sp
//                            )
//                        )

            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Настройки",
                tint = Color.LightGray,
                modifier = Modifier.size(32.dp)
            )
        }
        /////////// END Настройка ////////////


    }
}