package com.client.xvideos

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import cafe.adriel.voyager.navigator.Navigator
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.client.xvideos.PermissionScreenActivity.PermissionStorage
import com.client.xvideos.red.screens.ScreenRedRoot
import com.client.xvideos.screens.videoplayer.video.cache.VideoPlayerCacheManager
import com.client.xvideos.ui.theme.XvideosTheme
import com.client.xvideos.util.KeepScreenOn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

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
            .diskCachePolicy(CachePolicy.ENABLED)
            .dispatcher(Dispatchers.IO)
            .bitmapFactoryMaxParallelism(8)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge(statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT))
        super.onCreate(savedInstanceState)

        val window = this.window
        val windowInsetsController = window?.let {WindowCompat.getInsetsController(it, it.decorView)}

        windowInsetsController?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        window?.let { WindowCompat.setDecorFitsSystemWindows(window, false) }
        window?.attributes = window.attributes?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }

        windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())

        if (!PermissionStorage.hasPermissions(this)) {
            val intent = Intent(this, PermissionScreenActivity::class.java)
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }

        VideoPlayerCacheManager.initialize(this, 1024 * 1024 * 1024)    // 1GB

        setContent {

            KeepScreenOn()

//            // Remember a SystemUiController
//            val systemUiController = rememberSystemUiController()
//            val useDarkIcons = !isSystemInDarkTheme()
//
//            DisposableEffect(systemUiController, useDarkIcons) {
//
//                systemUiController.setSystemBarsColor(
//                    color = Color.Transparent,
//                    darkIcons = useDarkIcons
//                )
//
//
//                // setStatusBarColor() and setNavigationBarColor() also exist
//                onDispose {}
//            }

            //val startScreen = remember { ScreenRedTopThisWeek() }

            XvideosTheme(darkTheme = true) {



                //Navigator(ScreenTags("blonde"))
                Box(
                    modifier = Modifier.fillMaxSize()
                    //.displayCutoutPadding()
                    //.systemBarsPadding())
                )
                {
                    //Navigator(ScreenDashBoards())
                    //Navigator(ScreenFavorites())
                    //ScreenRedProfile
                    //Navigator(ScreenRedProfile("lilijunex")

                    //Navigator(startScreen, key = "1")
                    Timber.d("!!! Navigator(ScreenRedNiche())")
                    //Navigator(ScreenRedNiche())

                    Navigator(ScreenRedRoot())

                    //Navigator(ScreenRedExplorer())
                }

            }
        }
    }

}
