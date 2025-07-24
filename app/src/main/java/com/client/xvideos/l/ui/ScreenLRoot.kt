package com.client.xvideos.l.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.client.common.urlVideImage.UrlImage
import com.client.xvideos.Secrets
import com.client.xvideos.l.Album
import com.client.xvideos.l.Luscious
import com.example.ui.screens.ScreenRedRootSM
import com.redgifs.common.di.HostDI
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScreenLRoot() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val vm: ScreenLRootSM = getScreenModel()

        val album = vm.album.collectAsStateWithLifecycle().value

        val pics = album?.pics?.collectAsStateWithLifecycle()?.value

        Scaffold {

            if (pics != null) {

                LazyColumn {
                    items(pics) {
                        if (it.url_to_original != null) {
                            UrlImage(it.url_to_original)
                        }
                    }


                }


            }


        }


    }


}

class ScreenLRootSM @Inject constructor(

) : ScreenModel {

    val luscious = Luscious(Secrets.lusciousEmail, Secrets.lusciousPassword)

    val album = MutableStateFlow<Album?>(null)

    init {
        screenModelScope.launch {
            luscious.login()
            if (luscious.loggedIn) {
                album.value = luscious.getAlbum(374481)
            }
        }
    }


}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLRootBlock {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenLRootSM::class)
    abstract fun bindScreenLRootScreenModel(hiltListScreenModel: ScreenLRootSM): ScreenModel
}
