package com.client.xvideos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cafe.adriel.voyager.navigator.Navigator
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.client.xvideos.screens.dashboards.ScreenDashBoards
import com.client.xvideos.search.getSearchResults
import com.client.xvideos.search.parseJson
import com.client.xvideos.ui.theme.XvideosTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant

val urlStart = "https://www.xv-ru.com"

@AndroidEntryPoint
class MainActivity : ComponentActivity(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(1000 * 1024 * 1024)
                    .build()
            }
            //.logger(DebugLogger())
            .respectCacheHeaders(false)
            .allowHardware(true)
            .allowRgb565(true)
            .interceptorDispatcher(Dispatchers.IO)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .dispatcher(Dispatchers.IO)
            .bitmapFactoryMaxParallelism(8)
            //.addLastModifiedToFileCacheKey(false)

            .build()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        plant(DebugTree())
        Timber.i("!!! Hello")

        runBlocking {
            try {
                val a = getSearchResults("sist")
                a
                val b = a?.let { parseJson(it) }
                b
            } catch (e: Exception) {
                Timber.e(e.localizedMessage)
            }
            //b
        }

        setContent {
            XvideosTheme(darkTheme = true) {
                Navigator(ScreenDashBoards())
            }
        }
    }
}
