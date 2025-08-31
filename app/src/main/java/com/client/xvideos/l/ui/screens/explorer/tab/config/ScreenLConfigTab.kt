package com.client.xvideos.l.ui.screens.explorer.tab.config

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.client.common.R
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.model.enum.AudiencesType
import com.redgifs.common.ThemeRed
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import timber.log.Timber
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
                .background(ThemeL.greyBackground)
                .displayCutoutPadding()
                .fillMaxSize()
        ) {
            Text(
                "Настройки", color = ThemeL.textColor, style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 24.sp, fontFamily = ThemeL.fontFamilyKarla, textAlign = TextAlign.Center ),
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(color = Color.DarkGray)



            ConfigText("I want to see this content:")
            AudiencesType.entries.forEach { it->
                ConfigTextAndCheckBox(it.title, true,{ })
            }
            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = Color.DarkGray)





        }



    }

}






val styleTextConfig = TextStyle(
    fontSize = 20.sp,
    color = ThemeL.textColor,
    fontFamily = ThemeL.fontFamilyKarla
)

@Composable
fun ConfigTextAndCheckBox(text: String, value: Boolean, onValueChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 0.dp)
            .padding(vertical = 0.dp)
            .height(30.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(value, onValueChange, colors = CheckboxDefaults.colors(
            checkmarkColor = ThemeL.primaryColor,
             checkedColor = ThemeL.grey3
            ,uncheckedColor = ThemeL.grey3

        ), modifier = Modifier.width(40.dp))
        Text(text, style = styleTextConfig)
    }
}

@Composable
fun ConfigText(text: String) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .padding(vertical = 2.dp)
            .height(32.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, style = styleTextConfig)
    }
}








class ScreenLExplorerSettingSM @Inject constructor(

) : ScreenModel {

    var sizeXvideos by mutableLongStateOf(0L)
    var sizeRedDownload by mutableLongStateOf(0L)

    init {
        Timber.i("iii ScreenLExplorerSettingSM init")
    }

    override fun onDispose() {
        super.onDispose()
        Timber.i("iii ScreenLExplorerSettingSM onDispose")
    }

}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLExplorerSetting {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenLExplorerSettingSM::class)
    abstract fun bindScreenLExplorerSettingSreenModel(hiltListScreenModel: ScreenLExplorerSettingSM): ScreenModel
}