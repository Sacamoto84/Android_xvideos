package com.client.xvideos.screens_red.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.screens_red.ThemeRed
import com.client.xvideos.screens_red.profile.atom.RedProfileCreaterInfo
import com.composables.core.HorizontalSeparator

class ScreenRedProfile() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val vm: ScreenRedProfileSM = getScreenModel()


        Scaffold(
            bottomBar = { RedBottomBar() },
            containerColor = ThemeRed.colorCommonBackground
        ) {

            LazyColumn(modifier = Modifier.fillMaxSize()) {

                item {
                    if (vm.b != null) {
                        RedProfileCreaterInfo(vm.b!!)
                    }
                }


            }


        }


    }
}


@Composable
fun RedBottomBar() {

    Column {
        HorizontalSeparator(ThemeRed.colorBottomBarDivider, thickness = 2.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(ThemeRed.colorBottomBarBackground)
        ) {


        }
    }


}