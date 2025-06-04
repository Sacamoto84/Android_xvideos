package com.client.xvideos.screens_red.top_this_week

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.client.xvideos.screens_red.ThemeRed
import timber.log.Timber

class ScreenRedTopThisWeek : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: ScreenRedTopThisWeekSM = getScreenModel()

        val list = vm.listGifs.collectAsStateWithLifecycle().value
        val listUsers = vm.listUsers.collectAsStateWithLifecycle().value

        val state = rememberLazyListState()

        Scaffold(containerColor = ThemeRed.colorCommonBackground) { padding ->

            LazyColumn(state = state, modifier = Modifier.fillMaxSize()) {


                items(list, key = {it.id}){

                    UrlImage(it.urls.thumbnail, modifier = Modifier.size(300.dp), contentScale = ContentScale.Fit)


                    Row {

                        val a = listUsers.firstOrNull { it1 -> it1.username == it.userName }
                        if ((a != null) && (a.profileImageUrl != null)) {
                            Box(
                                modifier = Modifier.clip(CircleShape).size(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                UrlImage(
                                    a.profileImageUrl,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }else{
                            Icon(Icons.Default.AssignmentInd, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.Green)
                        }

                        ////////////
                        Text(it.userName, color = Color.White, fontFamily = ThemeRed.fontFamilyPopinsRegular)

                    }

                }

            }


        }

    }


}