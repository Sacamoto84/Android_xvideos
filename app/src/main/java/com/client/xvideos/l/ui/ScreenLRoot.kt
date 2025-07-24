package com.client.xvideos.l.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScreenLRoot() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val vm: ScreenLRootSM = getScreenModel()

        val album = vm.album.collectAsStateWithLifecycle().value

        val pics = vm.pics.collectAsStateWithLifecycle().value


        Scaffold {


            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(pics) {
                    if (it.url_to_original != null) {
                        UrlImage(it.url_to_original)
                    }
                }


            }


        }


    }


}


class ScreenLRootSM @Inject constructor(
    val luscious: Luscious
) : ScreenModel {

    val album = MutableStateFlow<Album?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val pics = album.filterNotNull().flatMapLatest { it.pics }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    init {
        screenModelScope.launch {
            if (!luscious.loggedIn) {
                luscious.login()
            } else {
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
