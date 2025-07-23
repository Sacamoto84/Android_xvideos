package com.example.ui.screens.explorer.tab.saved.tab.collection

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import com.client.common.connectivityObserver.ConnectivityObserver
import com.client.common.preference.PreferencesRepository
import com.client.common.urlVideImage.UrlImage
import com.composeunstyled.Text
import com.example.ui.screens.profile.ScreenRedProfileSM
import com.example.ui.screens.ui.lazyrow123.LazyRow123
import com.example.ui.screens.ui.lazyrow123.LazyRow123Host
import com.example.ui.screens.ui.lazyrow123.TypePager
import com.redgifs.common.ThemeRed
import com.redgifs.common.block.BlockRed
import com.redgifs.common.block.ui.DialogBlock
import com.redgifs.common.di.HostDI
import com.redgifs.common.saved.collection.ui.DaialogNewCollection
import com.redgifs.model.GifsInfo
import com.redgifs.model.Order
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

    val column = mutableIntStateOf(2)

    fun addColumn() {
        column.intValue += 1
        if (column.intValue > 3)
            column.intValue = 0
    }

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

        val list: SnapshotStateList<GifsInfo> = emptyList<GifsInfo>().toMutableStateList()

        val listGifs: LazyPagingItems<GifsInfo> = vm.likedHost.pager.collectAsLazyPagingItems() as LazyPagingItems<GifsInfo>

        LaunchedEffect(column.intValue) {
            vm.likedHost.columns = column.intValue
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
               LazyRow123( host = vm.likedHost, modifier = Modifier.padding(padding), onClickOpenProfile = {} )
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



