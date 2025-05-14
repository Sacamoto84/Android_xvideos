package com.client.xvideos.screens.config

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.screens.config.atom.CheckboxPreference
import com.composables.core.HorizontalSeparator
import com.composeunstyled.Text


class ScreenConfig() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: ScreenConfigSM = getScreenModel()

        val countRow = vm.countRow.collectAsState().value
        val shemale = vm.shemale.collectAsState().value

        Scaffold(

            topBar = {
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                        .background(Color(0xFF4D62F7)),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        "Настройки",
                        modifier = Modifier
                            .padding(bottom = 0.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 32.sp, color = Color.White
                    )

                }

            },

            modifier = Modifier
                .fillMaxSize(),
            //containerColor = Color(0xFFE4E7EC)
            containerColor = Color.White
        ) {

            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(16.dp)
            ) {

                HorizontalSeparator(color = Color(0xFF9E9E9E))

                CheckboxPreference(
                    title = "2 столбика",
                    state = countRow,
                    onChange = { it1 -> vm.saveCountRow(it1) }
                )

                HorizontalSeparator(color = Color(0xFF9E9E9E))

                CheckboxPreference(
                    title = "Shemale",
                    state = shemale,
                    onChange = { it1 -> vm.saveShemale(it1) }
                )

                HorizontalSeparator(color = Color(0xFF9E9E9E))
            }

        }


    }

}
