package com.client.xvideos.screens_red.niche

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import com.client.xvideos.feature.connectivityObserver.ConnectivityObserver
import com.client.xvideos.feature.redgifs.http.RedGifs
import com.client.xvideos.feature.redgifs.types.NichesInfo
import com.client.xvideos.feature.redgifs.types.NichesResponse
import com.client.xvideos.feature.redgifs.types.TopCreatorsResponse
import com.client.xvideos.screens_red.common.block.BlockRed
import com.client.xvideos.screens_red.common.downloader.DownloadRed
import com.client.xvideos.screens_red.common.expand_menu_video.ExpandMenuVideoModel
import com.client.xvideos.screens_red.common.ui.lazyrow123.LazyRow123Host
import com.client.xvideos.screens_red.common.ui.lazyrow123.TypePager
import com.client.xvideos.screens_red.common.saved.SavedRed
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
    connectivityObserver: ConnectivityObserver
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
            extraString = nicheName, typePager = TypePager.NICHES
        )

    init {
        Timber.d("!!!  ⚠\uFE0F ScreenNicheSM init {...} ")

        lazyHost.columns = 2

        screenModelScope.launch {
            niche = RedGifs.getNiche(nicheName).niche            // Нужно кешировать
            related = RedGifs.getNichesRelated(nicheName)        // Нужно кешировать
            topCreator = RedGifs.getNichesTopCreators(nicheName) // Нужно кешировать
        }
    }



    val expandMenuVideoList =
        listOf(
            ExpandMenuVideoModel("Скачать", Icons.Filled.FileDownload, onClick = {
                if (it == null) return@ExpandMenuVideoModel
                DownloadRed.downloadItem(it)
            }),
            ExpandMenuVideoModel("Поделиться", Icons.Default.Share),
            ExpandMenuVideoModel("Блокировать", Icons.Default.Block, onClick = {
                if (it == null) return@ExpandMenuVideoModel
                BlockRed.blockVisibleDialog = true
            }),

            ExpandMenuVideoModel("Like", Icons.Default.Favorite, onClick = {
                if (it == null) return@ExpandMenuVideoModel
                SavedRed.addLikes(it)
            }),

            ExpandMenuVideoModel("!Like", Icons.Default.Block, onClick = {
                if (it == null) return@ExpandMenuVideoModel
                SavedRed.removeLikes(it)
            }),
        )


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