package com.client.xvideos.l.ui.screens.explorer.tab.saved.crypto

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.l.featured.saved.SavedL
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.ui.element.expandMenu.ExpandMenuType
import com.client.xvideos.l.ui.element.lazyRowPictureDetails.L_LazyRowPictureDetails
import com.client.xvideos.l.ui.element.lazyRowPictureDetails.LazyRowPictureDetailsHost
import com.client.xvideos.redgifs.ui.explorer.tab.gifs.ColumnSelect
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.launch
import javax.inject.Inject

object ScreenLSavedLCryptoTab : Screen {

    private fun readResolve(): Any = ScreenLSavedLCryptoTab

    override val key: ScreenKey = uniqueScreenKey

    @Transient
    val columnSelect = ColumnSelect(Settings.current_count_likesTab)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: ScreenSavedLCryptoSM = getScreenModel()

        Scaffold(modifier = Modifier.fillMaxSize()) {
            L_LazyRowPictureDetails( vm.host, expandMenu =  ExpandMenuType.CRYPTO )
        }

    }

}

class ScreenSavedLCryptoSM @Inject constructor(
    val savedL: SavedL
) : ScreenModel {

    val host = LazyRowPictureDetailsHost("crypto")

    init {
        host.filteredPic = savedL.crypto.listUrl
    }

    fun delete(item: PicsDetails) {
        screenModelScope.launch {
            savedL.crypto.remove(item.url_to_original!!)
        }
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLSavedCrypto {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenSavedLCryptoSM::class)
    abstract fun bindScreenLSavedCryptoScreenModel(hiltListScreenModel: ScreenSavedLCryptoSM): ScreenModel
}
