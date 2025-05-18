package com.client.xvideos.screens_red.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.feature.redgifs.types.MediaInfo
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.client.xvideos.screens_red.ThemeRed
import com.client.xvideos.screens_red.profile.atom.RedProfileCreaterInfo
import com.client.xvideos.screens_red.profile.atom.toMinSec
import com.client.xvideos.screens_red.profile.atom.toPrettyCount
import com.composables.core.HorizontalSeparator

class ScreenRedProfile() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val vm: ScreenRedProfileSM = getScreenModel()

        val list = vm.b?.gifs

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



                items(items = list.orEmpty(), key = { it.id }) {

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                    ) {

                        UrlImage(
                            url = it.urls.thumbnail,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                        )

                        Text(
                            it.views?.toPrettyCount() ?: "-",
                            color = Color.White,
                            modifier = Modifier
                                .padding(8.dp)
                                .align(androidx.compose.ui.Alignment.BottomStart),
                            fontFamily = ThemeRed.fontFamilyPopinsMedium
                        )


                        Text(
                            it.duration?.toMinSec() ?: "-",
                            color = Color.White,
                            modifier = Modifier
                                .padding(8.dp)
                                .align(androidx.compose.ui.Alignment.BottomEnd),
                            fontFamily = ThemeRed.fontFamilyPopinsMedium
                        )


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