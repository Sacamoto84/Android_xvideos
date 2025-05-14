package com.client.xvideos.screens.config

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.client.xvideos.screens.k.ScreenKScreenModel
import com.client.xvideos.screens.tags.ScreenTagsViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject
import androidx.core.content.edit
import cafe.adriel.voyager.core.model.screenModelScope
import com.client.xvideos.repository.PreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class ScreenConfigSM @Inject constructor(
    val pref: SharedPreferences,
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


}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleConfig {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenConfigSM::class)
    abstract fun bindScreenConfigScreenModel(hiltListScreenModel: ScreenConfigSM): ScreenModel
}