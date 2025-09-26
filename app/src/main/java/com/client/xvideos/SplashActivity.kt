package com.client.xvideos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.client.xvideos.PermissionScreenActivity.PermissionStorage
import com.client.xvideos.common.room.AppDatabase
import com.client.xvideos.redgifs.common.block.BlockRed
import com.client.xvideos.redgifs.common.saved.SavedRed
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    @Inject
    lateinit var db: javax.inject.Provider<AppDatabase>

    @Inject
    lateinit var blockRed: javax.inject.Provider<BlockRed>

    @Inject
    lateinit var savedRed: javax.inject.Provider<SavedRed>

    private var isReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // включаем API
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // держим сплэш пока инициализация не завершена
        splashScreen.setKeepOnScreenCondition { !isReady }

        // Запускаем инициализацию
        lifecycleScope.launch {
            // имитация тяжёлой работы
            withContext(Dispatchers.IO) {
                initApp()
            }
            isReady = true
            // когда закончили — запускаем MainActivity
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }
    }

    suspend fun initApp() = coroutineScope {
        if (PermissionStorage.hasPermissions(this@SplashActivity)) {

            val savedRedInstance = savedRed.get()
            val blockRedInstance = blockRed.get()
            val dbInstance = db.get()

            val jobs = listOf(
                async { savedRedInstance.refreshTagList() },
                async { blockRedInstance.refresh() },
                async { savedRedInstance.likes.refresh() },
                async { savedRedInstance.niches.refresh() },
                async { savedRedInstance.creators.refresh() },
                async { savedRedInstance.collections.refreshCollectionList() },
                async { dbInstance.cacheUrlStringRamDao().deleteAll() }
            )

            // ждём все задачи
            jobs.awaitAll()
        }
    }

}