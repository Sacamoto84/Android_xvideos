package com.client.xvideos.screens.config

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.room.util.TableInfo
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.composables.core.HorizontalSeparator
import com.composeunstyled.Button
import com.composeunstyled.Text


class ScreenConfig() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: ScreenConfigSM = getScreenModel()




        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE4E7EC))

            .drawBehind(onDraw ={
            drawRect(Color(0xFF4D62F7),Offset(0f,0f), size = Size(this.size.width, this.size.height/2.5f)) })



            ,
            contentAlignment = Alignment.Center
        ) {


            Card(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp)
            ) {

                //(
                //modifier = Modifier
                //.fillMaxSize()
                //.background(Color(0xFFE5E8ED)
                // )
                //.displayCutoutPadding()

               Text("Настройки", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
//                HorizontalSeparator(color = Color(0xFF9E9E9E))
//                Text("Количество столбцов: " + vm.countRow.toString())
//                Row(modifier = Modifier.fillMaxWidth()) {
//                }
//
//                HorizontalSeparator(color = Color(0xFF9E9E9E))

            }


        }
    }

}
