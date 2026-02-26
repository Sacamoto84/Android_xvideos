package com.client.xvideos.redgifs.ui.explorer.tab.saved.tab

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.Navigator
import com.client.xvideos.common.coil.UrlImage
import com.client.xvideos.common.collectionDB.model.CollectionEntity
import com.client.xvideos.common.collectionDB.ui.DaialogNewCollection
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.redgifs.common.ThemeRed
import com.client.xvideos.redgifs.common.block.BlockRed
import com.client.xvideos.redgifs.common.di.HostDI
import com.client.xvideos.redgifs.model.GifsInfo
import com.client.xvideos.redgifs.model.URL1
import com.client.xvideos.redgifs.ui.explorer.tab.saved.tab.collection.ScreenCollectionName
import com.client.xvideos.ui.theme.XvideosTheme
import com.composeunstyled.Text
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.DelicateCoroutinesApi
import timber.log.Timber
import javax.inject.Inject

object R_SavedCollectionTab : Screen {

    private fun readResolve(): Any = R_SavedCollectionTab

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val vm = getScreenModel<ScreenSavedCollectionSM>()

        val savedRed = vm.hostDI.savedRed

        val selectedCollection = savedRed.collections.selectedCollection.collectAsStateWithLifecycle().value

        val columnSelect  = Settings.r_collectionTab_column_current_count.field.collectAsStateWithLifecycle().value

        BackHandler {
            Timber.i("iii BackHandler SavedCollectionTab")
            savedRed.collections.selectedCollection.value = null
        }

        /**  ➜ сюда запоминаем элемент, который пользователь хочет удалить  */
        var itemPendingDelete by remember { mutableStateOf<String?>(null) }
        /* ---------- Диалог подтверждения ---------- */
        itemPendingDelete?.let { pending ->
            AlertDialog(

                //icon = { UrlImage(pending.thumbnail, modifier = Modifier.size(96.dp)) },

                onDismissRequest = { itemPendingDelete = null },

                title = {
                    Text(
                        "Удалить коллекцию?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },

                text = {
                    Text(buildAnnotatedString {
                        append("Удалить «")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(pending) }
                        append("» из коллекции")
                    }, fontSize = 16.sp)
                },

                confirmButton = {
                    TextButton(
                        onClick = {
                            savedRed.collections.deleteCollection(pending)
                            itemPendingDelete = null
                        }
                    ) { Text("Удалить", fontSize = 16.sp, color = Color(0xFF6552A5)) }
                },
                dismissButton = {
                    TextButton(
                        onClick = { itemPendingDelete = null }
                    ) { Text("Отмена", fontSize = 16.sp, color = Color(0xFF6552A5)) }
                },

                /* Доп. стили при желании */
                containerColor = Color(0xFFEBE6EE)
            )
        }

        R_SavedCollectionTabContent(
            selectedCollection = selectedCollection,
            collectionList = savedRed.collections.collectionList,
            gridState = vm.gridState,
            onCollectionClick = { savedRed.collections.selectedCollection.value = it },
            onCollectionLongClick = { itemPendingDelete = it },
            onCreateNewCollectionClick = { savedRed.collections.visibleDialogCreateNew = true },
            navigationContent = {
                if (selectedCollection != null) {
                    Navigator(ScreenCollectionName(selectedCollection))
                }
            }
        )
    }
}

@Composable
fun R_SavedCollectionTabContent(
    selectedCollection: String?,
    collectionList: List<CollectionEntity<GifsInfo>>,
    gridState: LazyGridState,
    onCollectionClick: (String) -> Unit,
    onCollectionLongClick: (String) -> Unit,
    onCreateNewCollectionClick: () -> Unit,
    navigationContent: @Composable () -> Unit
) {
    Scaffold(topBar = {
        Text(
            ">Коллекция>$selectedCollection",
            modifier = Modifier.padding(start = 8.dp),
            color = ThemeRed.colorYellow,
            fontSize = 18.sp,
            fontFamily = ThemeRed.fontFamilyPopinsRegular
        )
    }) { padding ->


        if (selectedCollection == null) {

            LazyVerticalGrid( modifier = Modifier.padding(padding), state = gridState, columns = GridCells.Fixed(2) )
            {
                items(collectionList) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 8.dp).padding(vertical = 4.dp)
                            .combinedClickable(
                                onClick = { onCollectionClick(it.collection) },
                                onLongClick = { onCollectionLongClick(it.collection) }),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (it.items.isNotEmpty()) {
                            UrlImage(
                                url = it.items.last().urls.thumbnail,
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)).size(72.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)).size(72.dp).background(Color.Gray)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text( it.collection,  color = Color.White, fontFamily = ThemeRed.fontFamilyDMsanss )
                    }
                }

                items(listOf(Unit)) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp).size(72.dp).clip(RoundedCornerShape(8.dp)).background(ThemeRed.colorYellow)
                                .clickable(onClick = { onCreateNewCollectionClick() }), contentAlignment = Alignment.Center )
                        {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(24.dp) )
                        }
                    }
                }
            }
        } else {
            navigationContent()
        }
    }
}


class ScreenSavedCollectionSM @Inject constructor(
    val block: BlockRed,
    hostDIin : javax.inject.Provider<HostDI>,
) : ScreenModel {

    val hostDI = hostDIin.get()

    val gridState = LazyGridState()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleRedSavedCollection {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenSavedCollectionSM::class)
    abstract fun bindScreenRedSavedCollectionScreenModel(hiltListScreenModel: ScreenSavedCollectionSM): ScreenModel
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun R_SavedCollectionTabPreview() {
    XvideosTheme(darkTheme = true) {
        val sampleCollections = listOf(
            CollectionEntity(
                collection = "Favorites",
                items = listOf(GifsInfo(urls = URL1(thumbnail = "")))
            ),
            CollectionEntity(
                collection = "Private",
                items = emptyList<GifsInfo>()
            )
        )
        R_SavedCollectionTabContent(
            selectedCollection = null,
            collectionList = sampleCollections,
            gridState = rememberLazyGridState(),
            onCollectionClick = {},
            onCollectionLongClick = {},
            onCreateNewCollectionClick = {},
            navigationContent = {}
        )
    }
}
