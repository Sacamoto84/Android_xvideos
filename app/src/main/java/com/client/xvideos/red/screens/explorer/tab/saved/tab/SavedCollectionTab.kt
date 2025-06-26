package com.client.xvideos.red.screens.explorer.tab.saved.tab

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.red.ThemeRed
import com.composeunstyled.Text

object SavedCollectionTab : Screen {

    private fun readResolve(): Any = SavedCollectionTab

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(topBar = {
            Text(">Коллекция", modifier = Modifier.padding(start = 8.dp), color = ThemeRed.colorYellow, fontSize = 18.sp, fontFamily = ThemeRed.fontFamilyPopinsRegular)
        }) { padding ->


        }

    }
}
