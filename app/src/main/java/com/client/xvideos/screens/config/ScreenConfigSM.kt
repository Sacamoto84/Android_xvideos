package com.client.xvideos.screens.config

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject
import cafe.adriel.voyager.core.model.screenModelScope
import com.client.xvideos.feature.preference.PreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class ScreenConfigSM @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) : ScreenModel {

    /** Количество колонок true-2 false-1 */
    val countRow = preferencesRepository.flowRow2
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), false)

    /** Сохранить количество столбиков */
    fun saveCountRow(enabled: Boolean) {
        screenModelScope.launch {
            preferencesRepository.setRow2(enabled)
        }
    }

    /** Режим Shemale */
    val shemale = preferencesRepository.flowShemale
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), false)

    /** Сохранить режим shemale */
    fun saveShemale(enabled: Boolean) {
        screenModelScope.launch {
            preferencesRepository.setShemale(enabled)
        }
    }


}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleConfig {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenConfigSM::class)
    abstract fun bindScreenConfigScreenModel(hiltListScreenModel: ScreenConfigSM): ScreenModel
}