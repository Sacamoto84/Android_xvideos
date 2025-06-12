package com.client.xvideos.screens_red.saved

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.client.xvideos.screens_red.ThemeRed
import com.client.xvideos.screens_red.common.saved.SavedRed


class ScreenRedSaved : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: ScreenSavedSM = getScreenModel()

        LaunchedEffect(Unit) {
            SavedRed.refreshLikesList()
        }


        LazyColumn (modifier =Modifier.fillMaxSize().background(ThemeRed.colorCommonBackground2)){

            items(SavedRed.likesList) {

                Row(modifier = Modifier.padding(vertical = 8.dp).height(48.dp).border(1.dp , Color.White).clickable(onClick = { SavedRed.removeLikes(it)})) {
                    UrlImage(it.urls.thumbnail, modifier = Modifier.size(48.dp))
                    Text(it.userName, color = Color.White, modifier = Modifier.height(48.dp))
                }
            }


        }


    }
}