package com.client.xvideos.screens.tags

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.screens.item.ScreenItemScreenModel
import com.client.xvideos.screens.k.ScreenKScreenModel
import com.client.xvideos.screens.tags.atom.TagsPaginatedListScreen

class ScreenTags(val tag: String) : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val vm = getScreenModel<ScreenTagsViewModel, ScreenTagsViewModel.Factory> { factory ->
            factory.create(tag)
        }

        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
            Column {
                Text(tag)
                Row {
                    Text(vm.screen.title0 + " ")
                    Text(vm.screen.title1, color = Color(0xFF787878))
                }
            }

        }) {
            TagsPaginatedListScreen(0, vm)
        }


    }

}