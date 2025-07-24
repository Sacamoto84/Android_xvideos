package com.client.xvideos

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.navigator.Navigator
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.client.common.util.KeepScreenOn
import com.client.xvideos.PermissionScreenActivity.PermissionStorage
import com.client.xvideos.l.ui.ScreenLRoot
import com.client.xvideos.screens.videoplayer.video.cache.VideoPlayerCacheManager
import com.client.xvideos.ui.theme.XvideosTheme
import com.example.ui.screens.ScreenRedRoot
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

const val urlStart = "https://www.xv-ru.com"

@AndroidEntryPoint
class MainActivity : ComponentActivity(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.5)
                    .build()
            }
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.5)
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

    @OptIn(ExperimentalVoyagerApi::class, ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        //enableEdgeToEdge(statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT))
        super.onCreate(savedInstanceState)

        val window = this.window

        val windowInsetsController =
            window?.let { WindowCompat.getInsetsController(it, it.decorView) }
//
        windowInsetsController?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//
        window?.let { WindowCompat.setDecorFitsSystemWindows(window, false) }
        window?.attributes = window.attributes?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
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
            XvideosTheme(darkTheme = true) {
                //EdgeToEdgeFix()
                //Navigator(ScreenTags("blonde"))
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        //.windowInsetsPadding(WindowInsets.ime)
                        //.consumeWindowInsets(WindowInsets.ime)
                    //.displayCutoutPadding()
                    //.systemBarsPadding())
                )
                {
                    //Navigator(ScreenDashBoards())
                    //Navigator(ScreenFavorites())
                    //ScreenRedProfile
                    //Navigator(ScreenRedProfile("lilijunex")
                    //Navigator(startScreen, key = "1")
                    //Navigator(ScreenRedNiche())
                    Navigator( screen = ScreenLRoot())
                }

            }
        }
    }

}

@Composable
fun EdgeToEdgeFix() {
    val context = LocalContext.current
    val window = (context as? Activity)?.window ?: return
    val controller = WindowCompat.getInsetsController(window, window.decorView)

    LaunchedEffect(Unit) {
        // Поведение: показывать панели при свайпе
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Не скрываем навигацию — только статус-бар (если нужно)
        controller.hide(WindowInsetsCompat.Type.statusBars())

        // "Пинаем" для корректного обновления insets
        window.decorView.requestApplyInsets()
    }
}