package com.client.xvideos

import android.Manifest
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import cafe.adriel.voyager.navigator.Navigator
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.client.xvideos.PermissionScreenActivity.PermissionStorage
import com.client.xvideos.screens.videoplayer.video.cache.VideoPlayerCacheManager
import com.client.xvideos.screens_red.profile.ScreenRedProfile
import com.client.xvideos.ui.theme.XvideosTheme
import dagger.hilt.android.AndroidEntryPoint
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
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        //WindowCompat.setDecorFitsSystemWindows(window, false) // Поддержка WindowInsets

        // Прячем системный UI
        // true (по умолчанию): контент не может заходить под системные элементы
        //false: контент может располагаться на весь экран, включая области под системными панелями (ты сам решаешь, где что рисовать).
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

        }

        plant(DebugTree())

        if (!PermissionStorage.hasPermissions(this)) {
            val intent = Intent(this, PermissionScreenActivity::class.java)
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }

        VideoPlayerCacheManager.initialize(this, 1024 * 1024 * 1024)    // 1GB

        setContent {

            KeepScreenOn()

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
                    Navigator(ScreenRedProfile())
                }

            }
        }
    }

}
