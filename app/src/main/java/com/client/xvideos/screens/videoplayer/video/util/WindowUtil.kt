/*
 * Copyright 2023 Dora Lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.client.xvideos.screens.videoplayer.video.util

import android.app.Activity
import android.view.View
import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Bring the activity to the full screen.
 */
internal fun Activity.setFullScreen(fullscreen: Boolean) {
    window.setFullScreen(fullscreen)
}

/**
 * Bring the window to full screen. (Remove the status bar and navigation bar.)
 */
internal fun Window.setFullScreen(fullscreen: Boolean) {

    if (fullscreen) {
        this.hideSystemBars()
    } else {
        this.showSystemBars()
    }
}

private fun Window.hideSystemBars() {
    WindowCompat.setDecorFitsSystemWindows(this, false)
    // Without this deprecated systemUiVisibility, videos are not completely immersive mode in API 30.
    // ie. navigation bars are visible, if this is removed.
    this.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE

    WindowCompat.getInsetsController(this, this.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

private fun Window.showSystemBars() {
    WindowCompat.setDecorFitsSystemWindows(this, true)
    WindowCompat.getInsetsController(this, this.decorView).show(WindowInsetsCompat.Type.systemBars())
}
