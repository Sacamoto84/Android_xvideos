package com.client.xvideos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import cafe.adriel.voyager.navigator.Navigator
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.client.xvideos.screens.dashboards.ScreenDashBoards
import com.client.xvideos.ui.theme.XvideosTheme
import dagger.hilt.android.AndroidEntryPoint
import com.client.xvideos.screens.item.video.cache.VideoPlayerCacheManager
import kotlinx.coroutines.Dispatchers
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false) // Поддержка WindowInsets
        plant(DebugTree())
        VideoPlayerCacheManager.initialize(this, 1024 * 1024 * 1024)    // 1GB
        setContent {
            XvideosTheme(darkTheme = true) {
                //Navigator(ScreenTags("blonde"))
                Navigator(ScreenDashBoards())
            }
        }
    }
}
