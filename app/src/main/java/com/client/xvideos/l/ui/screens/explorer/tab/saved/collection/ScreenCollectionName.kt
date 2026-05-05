package com.client.xvideos.l.ui.screens.explorer.tab.saved.collection

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.client.xvideos.common.collectionDB.ui.DaialogNewCollection
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.l.featured.saved.SavedL
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.theme.ThemeL
import kotlinx.coroutines.launch
import com.client.xvideos.l.ui.element.expandMenu.ExpandMenuType
import com.client.xvideos.l.ui.element.lazyRowPictureDetails.L_LazyRowPictureDetails
import com.client.xvideos.l.ui.element.lazyRowPictureDetails.LazyRowPictureDetailsHost
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.DelicateCoroutinesApi
import timber.log.Timber

class ScreenCollectionName(val collectionName: String) : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val vm = getScreenModel<ScreenLCollectionNameSM, ScreenLCollectionNameSM.Factory> { factory -> factory.create(collectionName) }

        val savedL = vm.savedL

        // Set the current collection for the collection manager
        LaunchedEffect(collectionName) {
            savedL.collection.setCollection(collectionName)
        }

        val selectedCollection = savedL.collection.currentCollectionName

        BackHandler {
            Timber.i("iii BackHandler SavedCollectionTab")
            savedL.collection.currentCollectionName = null
        }

        val columnSelect = Settings.l_collectionTab_column_current_count.field.collectAsStateWithLifecycle().value

        //Изменение количества отображаемых элементов
        LaunchedEffect(columnSelect) { vm.host.columns = columnSelect }

        Scaffold(topBar = {
            androidx.compose.material3.Text(
                ">Коллекция>$selectedCollection",
                modifier = Modifier.padding(start = 8.dp),
                color = ThemeL.primaryColor,
                fontSize = 18.sp,
                fontFamily = ThemeL.fontFamilyPopinsRegular
            )
        }) { padding ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                L_LazyRowPictureDetails(
                    host = vm.host,
                    expandMenu = ExpandMenuType.LIKES,
                    tag = "lCollection"
                )
            }
        }

    }
}


class ScreenLCollectionNameSM @AssistedInject constructor(
    @Assisted val collectionName: String,
    val savedL: SavedL,
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(collectionName: String): ScreenLCollectionNameSM
    }

    val host = LazyRowPictureDetailsHost(collectionName)

    init {
        loadCollectionItems()
    }

    private fun loadCollectionItems() {
        screenModelScope.launch {
            host.filteredPic.clear()
            host.filteredPic.addAll(savedL.collection.listUrl)
        }
    }

    fun delete(item: PicsDetails) {
        savedL.collection.remove(item.url_to_original ?: "", collectionName)
        loadCollectionItems()
    }

}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLSavedCollectionName {
    @Binds
    @IntoMap
    @ScreenModelFactoryKey(ScreenLCollectionNameSM.Factory::class)
    abstract fun bindScreenLSavedCollectionNameScreenModel(hiltDetailsScreenModelFactory: ScreenLCollectionNameSM.Factory): ScreenModelFactory
}
