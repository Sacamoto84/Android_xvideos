package com.client.xvideos.l.ui.screens.explorer.tab.config

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.example.ui.screens.explorer.tab.setting.ScreenRedExplorerSettingSM
import com.redgifs.common.ThemeRed
import com.redgifs.common.di.HostDI
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject


object ScreenLConfigTab : Screen {

    private fun readResolve(): Any = ScreenLConfigTab

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val haptic = LocalHapticFeedback.current

        val vm: ScreenLExplorerSettingSM = getScreenModel()

        Column(
            modifier = Modifier
                .background(ThemeRed.colorCommonBackground)
                .displayCutoutPadding()
                .fillMaxSize()
        ) {
            Text(
                "Настройки", color = Color.White, style = TextStyle( fontSize = 24.sp, fontFamily = ThemeRed.fontFamilyDMsanss, textAlign = TextAlign.Center ),
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(color = Color.DarkGray)







        }



    }

}

class ScreenLExplorerSettingSM @Inject constructor(

) : ScreenModel {

    var sizeXvideos by mutableLongStateOf(0L)
    var sizeRedDownload by mutableLongStateOf(0L)

}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLExplorerSetting {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenLExplorerSettingSM::class)
    abstract fun bindScreenLExplorerSettingSreenModel(hiltListScreenModel: ScreenLExplorerSettingSM): ScreenModel
}