package com.client.xvideos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import coil3.compose.AsyncImage
import com.client.xvideos.model.GalleryItem
import com.client.xvideos.net.readHtmlFromURL
import com.client.xvideos.parcer.parseHTML5Player
import com.client.xvideos.parcer.parserItemVideo
import com.client.xvideos.parcer.parserListVideo
import com.client.xvideos.screens.ScreenDashBoards
import com.client.xvideos.ui.theme.XvideosTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant


var l = mutableStateListOf<GalleryItem>()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        plant(DebugTree())
        Timber.i("!!! Hello")

        runBlocking {
            val s = readHtmlFromURL("https://www.xvideos.com")//readHtmlFromURL("https://www.xv-ru.com/video.uedlbibe330/shame4k._")
            l = parserListVideo(s).toMutableStateList()
            //val script = parserItemVideo(s)
            //val a = script?.let { parseHTML5Player(it) }
            //a
        }

        setContent {
            XvideosTheme {
                Navigator(ScreenDashBoards())
            }
        }
    }
}
