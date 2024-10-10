package com.client.xvideos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cafe.adriel.voyager.navigator.Navigator
import com.client.xvideos.screens.dashboards.ScreenDashBoards
import com.client.xvideos.ui.theme.XvideosTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant




@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        plant(DebugTree())
        Timber.i("!!! Hello")



        setContent {
            XvideosTheme {
                Navigator(ScreenDashBoards())
            }
        }
    }
}
