package com.client.xvideos.screens.itemFullScreen

import android.content.Context
import androidx.media3.common.util.UnstableApi
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext

@UnstableApi
class ScreenVideoPlayerFullScreenSM @AssistedInject constructor(
    @Assisted val inputPosition: Long,
    @ApplicationContext context: Context,
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(inputPosition: Long): ScreenVideoPlayerFullScreenSM
    }

}







