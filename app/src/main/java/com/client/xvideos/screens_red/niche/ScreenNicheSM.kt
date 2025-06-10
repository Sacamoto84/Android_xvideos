package com.client.xvideos.screens_red.niche

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import com.client.xvideos.feature.preference.PreferencesRepository
import com.client.xvideos.feature.redgifs.http.RedGifs
import com.client.xvideos.feature.redgifs.types.NichesInfo
import com.client.xvideos.feature.redgifs.types.NichesResponse
import com.client.xvideos.feature.redgifs.types.TopCreatorsResponse
import com.client.xvideos.feature.room.AppDatabase
import com.client.xvideos.screens_red.profile.ScreenRedProfileSM
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ScreenNicheSM @AssistedInject constructor(
    @Assisted val nicheName: String,
    private val db: AppDatabase,
    private val pref: PreferencesRepository,
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(nicheName: String): ScreenNicheSM
    }

    var niche: NichesInfo by mutableStateOf(NichesInfo())
    var related by mutableStateOf(NichesResponse(emptyList()))
    var topCreator by mutableStateOf(TopCreatorsResponse(emptyList()))

    init {
        screenModelScope.launch {
            niche = RedGifs.getNiche(nicheName)                  //Нужно кешировать

            related = RedGifs.getNichesRelated(nicheName)        //Нужно кешировать
            topCreator = RedGifs.getNichesTopCreators(nicheName) //Нужно кешировать

            //val aa = RedGifs.getNiches(nicheName) //Не кешировать Padding3

        }
    }




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