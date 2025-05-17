package com.client.xvideos

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.room.FtsOptions
import cafe.adriel.voyager.navigator.Navigator
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.client.xvideos.feature.redgifs.API
import com.client.xvideos.feature.redgifs.ApiClient
import com.client.xvideos.feature.redgifs.MediaType
import com.client.xvideos.feature.redgifs.Order
import com.client.xvideos.screens.dashboards.ScreenDashBoards
import com.client.xvideos.screens.videoplayer.video.cache.VideoPlayerCacheManager
import com.client.xvideos.ui.theme.XvideosTheme
import com.google.android.gms.common.api.Api
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant

const val urlStart = "https://www.xv-ru.com"

@AndroidEntryPoint
class MainActivity : ComponentActivity(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .respectCacheHeaders(false)
            .allowHardware(true)
            .allowRgb565(true)
            .interceptorDispatcher(Dispatchers.IO)
            .memoryCachePolicy(CachePolicy.ENABLED)
            //.diskCachePolicy(CachePolicy.ENABLED)
            .dispatcher(Dispatchers.IO)
            .bitmapFactoryMaxParallelism(8)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)


        runBlocking {
            ApiClient.login() // без логина и пароля — временный токен
            val response = API.searchCreator("lilijunex", 1, 80, Order .RECENT, MediaType.GIF)
            println(response)
        }




        //WindowCompat.setDecorFitsSystemWindows(window, false) // Поддержка WindowInsets

        // Прячем системные UI
        // true (по умолчанию): контент не может заходить под системные элементы
        //false: контент может располагаться на весь экран, включая области под системными панелями (ты сам решаешь, где что рисовать).
        WindowCompat.setDecorFitsSystemWindows(window, false)


        window.insetsController?.let {
            it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }


        plant(DebugTree())
        VideoPlayerCacheManager.initialize(this, 1024 * 1024 * 1024)    // 1GB
        setContent {

            KeepScreenOn()

            XvideosTheme(darkTheme = true) {
                //Navigator(ScreenTags("blonde"))
                Box(modifier = Modifier.fillMaxSize()
                    //.displayCutoutPadding()
                    //.systemBarsPadding())
                )
                {
                    Navigator(ScreenDashBoards())
                    //Navigator(ScreenFavorites())
                }

            }
        }
    }
}
