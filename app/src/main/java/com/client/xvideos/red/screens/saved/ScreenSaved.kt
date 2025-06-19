package com.client.xvideos.red.screens.saved

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.client.xvideos.red.ThemeRed
import com.client.xvideos.red.common.ui.lazyrow123.LazyRow123
import com.client.xvideos.red.screens.profile.ScreenRedProfile


class ScreenRedSaved : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: ScreenSavedSM = getScreenModel()


        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = ThemeRed.colorCommonBackground2) {


            LazyRow123(
                host = vm.likedHost,
                modifier = Modifier.fillMaxWidth(),
                onClickOpenProfile = {
                    vm.likedHost.currentIndexGoto = vm.likedHost.currentIndex
                    navigator.push(ScreenRedProfile(it))
                },
                gotoPosition = vm.likedHost.currentIndexGoto,
                option = emptyList(),//vm.expandMenuVideoList,
                contentPadding = PaddingValues(0.dp),
                contentBeforeList = { }
            )

        }

    }
}