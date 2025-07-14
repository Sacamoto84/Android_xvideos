package com.example.ui.screens.niche

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import com.client.xvideos.feature.connectivityObserver.ConnectivityObserver
import com.redgifs.model.NichesInfo
import com.redgifs.model.NichesResponse
import com.redgifs.model.TopCreatorsResponse
import com.client.xvideos.redgifs.common.ui.lazyrow123.LazyRow123Host
import com.client.xvideos.redgifs.common.ui.lazyrow123.TypePager
import com.redgifs.common.di.HostDI
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.launch
import timber.log.Timber

class ScreenNicheSM @AssistedInject constructor(
    @Assisted val nicheName: String,
    connectivityObserver: ConnectivityObserver,
    val hostDI: HostDI
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(nicheName: String): ScreenNicheSM
    }

    var niche: NichesInfo by mutableStateOf(NichesInfo())
    var related by mutableStateOf(NichesResponse(emptyList()))
    var topCreator by mutableStateOf(TopCreatorsResponse(emptyList()))

    val lazyHost =
        LazyRow123Host(
            connectivityObserver = connectivityObserver, scope = screenModelScope,
            extraString = nicheName,
            typePager = TypePager.NICHES,
            hostDI = hostDI
        )

    init {
        Timber.d("!!!  ⚠\uFE0F ScreenNicheSM init {...} ")

        lazyHost.columns = 2

        screenModelScope.launch {
            niche = hostDI.redApi.getNiche(nicheName).niche            // Нужно кешировать
            related = hostDI.redApi.getNichesRelated(nicheName)        // Нужно кешировать
            topCreator = hostDI.redApi.getNichesTopCreators(nicheName) // Нужно кешировать
        }
    }


//    val expandMenuVideoList =
//        listOf(
//            ExpandMenuVideoModel("Скачать", Icons.Filled.FileDownload, onClick = {
//                if (it == null) return@ExpandMenuVideoModel
//                DownloadRed.downloadItem(it)
//            }),
//            ExpandMenuVideoModel("Поделиться", Icons.Default.Share),
//            ExpandMenuVideoModel("Блокировать", Icons.Default.Block, onClick = {
//                if (it == null) return@ExpandMenuVideoModel
//                BlockRed.blockVisibleDialog = true
//            }),
//
//            ExpandMenuVideoModel("Like", Icons.Default.Favorite, onClick = {
//                if (it == null) return@ExpandMenuVideoModel
//                SavedRed.addLikes(it)
//            }),
//
//            ExpandMenuVideoModel("!Like", Icons.Default.Block, onClick = {
//                if (it == null) return@ExpandMenuVideoModel
//                SavedRed.removeLikes(it)
//            }),
//        )


}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedNiche {

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(ScreenNicheSM.Factory::class)
    abstract fun bindHiltNicheScreenModelFactory(
        hiltDetailsScreenModelFactory: ScreenNicheSM.Factory
    ): ScreenModelFactory

}