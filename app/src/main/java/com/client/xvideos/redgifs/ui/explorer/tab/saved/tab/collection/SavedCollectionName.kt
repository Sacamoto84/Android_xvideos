package com.client.xvideos.redgifs.ui.explorer.tab.saved.tab.collection

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.client.xvideos.common.connectivityObserver.ConnectivityObserver
import com.client.xvideos.redgifs.ui.explorer.tab.saved.tab.R_SavedCollectionTab
import com.client.xvideos.redgifs.ui.ui.lazyrow123.LazyRow123
import com.client.xvideos.redgifs.ui.ui.lazyrow123.LazyRow123Host
import com.client.xvideos.redgifs.ui.ui.lazyrow123.TypePager
import com.composeunstyled.Text
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.common.di.HostDI
import com.client.xvideos.redgifs.model.GifsInfo
import com.client.xvideos.redgifs.model.Order
import com.client.xvideos.redgifs.common.block.BlockRed
import com.redgifs.common.block.ui.DialogBlock
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

        val vm = getScreenModel<ScreenRedCollectionNameSM, ScreenRedCollectionNameSM.Factory> { factory -> factory.create(collectionName) }
        var blockItem by rememberSaveable { mutableStateOf<GifsInfo?>(null) }
        val savedRed = vm.hostDI.savedRed

        val selectedCollection = savedRed.collections.selectedCollection.collectAsStateWithLifecycle().value

        BackHandler {
            Timber.i("iii BackHandler SavedCollectionTab")
            savedRed.collections.selectedCollection.value = null
        }

        LaunchedEffect(R_SavedCollectionTab.columnSelect.column) {
            vm.likedHost.columns = R_SavedCollectionTab.columnSelect.column
        }

        /* ---------- /Диалог ---------- */
        val block = vm.block
        //Диалог для блокировки
        if (block.blockVisibleDialog) {
            DialogBlock(
                visible = block.blockVisibleDialog,
                onDismiss = { block.blockVisibleDialog = false },
                onBlockConfirmed = {
                    if ((blockItem != null)) {
                        block.blockItem(blockItem!!)
                        blockItem = null
                    }
                }
            )
        }

        var collectionVisibleDialogCreateNew by remember { mutableStateOf(false) }

        if (collectionVisibleDialogCreateNew) {
            DaialogNewCollection(
                visible = collectionVisibleDialogCreateNew,
                onDismiss = {
                    collectionVisibleDialogCreateNew = false
                },
                onBlockConfirmed = { collection ->
                    if ((collection != "")) {
                        savedRed.collections.createCollection(collection)
                        collectionVisibleDialogCreateNew = false
                    }
                }
            )
        }

        Scaffold(topBar = {
            Text(
                ">Коллекция>" + selectedCollection ?: "---",
                modifier = Modifier.padding(start = 8.dp),
                color = ThemeRed.colorYellow,
                fontSize = 18.sp,
                fontFamily = ThemeRed.fontFamilyPopinsRegular
            )
        }) { padding ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                LazyRow123(
                    host = vm.likedHost,
                    modifier = Modifier.padding(padding),
                    onClickOpenProfile = {})
            }
        }

    }
}


class ScreenRedCollectionNameSM @AssistedInject constructor(
    @Assisted val collectionName: String,
    val hostDI: HostDI,
    connectivityObserver: ConnectivityObserver,
    val block: BlockRed,
) : ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(collectionName: String): ScreenRedCollectionNameSM
    }

    val likedHost = LazyRow123Host(
        connectivityObserver = connectivityObserver,
        scope = screenModelScope,
        typePager = TypePager.SAVED_COLLECTION,
        extraString = collectionName,
        startOrder = Order.LATEST,
        hostDI = hostDI,
        isCollection = true
    )


}


@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedSavedCollectionName {
    @Binds
    @IntoMap
    @ScreenModelFactoryKey(ScreenRedCollectionNameSM.Factory::class)
    abstract fun bindScreenRedSavedCollectionNameScreenModel(hiltDetailsScreenModelFactory: ScreenRedCollectionNameSM.Factory): ScreenModelFactory
}

//@Module
//@InstallIn(SingletonComponent::class)
//abstract class ScreenModuleRedProfile {
//
//    @Binds
//    @IntoMap
//    @ScreenModelFactoryKey(ScreenRedProfileSM.Factory::class)
//    abstract fun bindHiltProfilesScreenModelFactory(
//        hiltDetailsScreenModelFactory: ScreenRedProfileSM.Factory
//    ): ScreenModelFactory
//
//}



