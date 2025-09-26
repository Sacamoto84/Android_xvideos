package com.client.xvideos.xvideos.screens.config

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Inject
import cafe.adriel.voyager.core.model.screenModelScope
import com.client.xvideos.common.sharedPref.Settings
import kotlinx.coroutines.launch


class ScreenConfigSM @Inject constructor(

) : ScreenModel {

    /** Количество колонок true-2 false-1 */
    val countRow = Settings.xvideos_row2

    /** Сохранить количество столбиков */
    fun saveCountRow(enabled: Boolean) {
        screenModelScope.launch { countRow.setValue(enabled) }
    }

    /** Режим Shemale */
    val shemale = Settings.xvideos_shemale

    /** Сохранить режим shemale */
    fun saveShemale(enabled: Boolean) { screenModelScope.launch { shemale.setValue(enabled) } }

}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleConfig {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenConfigSM::class)
    abstract fun bindScreenConfigScreenModel(hiltListScreenModel: ScreenConfigSM): ScreenModel
}