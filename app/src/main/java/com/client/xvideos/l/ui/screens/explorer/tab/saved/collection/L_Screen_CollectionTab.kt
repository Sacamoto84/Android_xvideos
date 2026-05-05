package com.client.xvideos.l.ui.screens.explorer.tab.saved.collection

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
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.Navigator
import com.client.xvideos.common.coil.UrlImage
import com.client.xvideos.l.featured.saved.LCollectionEntity
import com.client.xvideos.l.featured.saved.SavedL
import com.client.xvideos.l.theme.ThemeL
import com.composeunstyled.Text
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.DelicateCoroutinesApi
import timber.log.Timber
import javax.inject.Inject

object L_Screen_CollectionTab : Screen {

    private fun readResolve(): Any = L_Screen_CollectionTab

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val vm = getScreenModel<ScreenSavedCollectionSM>()

        val savedL = vm.savedL

        val selectedCollection = savedL.collection.currentCollectionName

        BackHandler {
            Timber.i("iii BackHandler SavedCollectionTab")
            savedL.collection.currentCollectionName = null
        }

        /**  ➜ сюда запоминаем элемент, который пользователь хочет удалить  */
        var itemPendingDelete by remember { mutableStateOf<String?>(null) }
        /* ---------- Диалог подтверждения ---------- */
        itemPendingDelete?.let { pending ->
            AlertDialog(

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
                            savedL.collection.deleteCollection(pending)
                            itemPendingDelete = null
                        }
                    ) { Text("Удалить", fontSize = 16.sp, color = Color(0xFF6552A5)) }
                },
                dismissButton = {
                    TextButton(
                        onClick = { itemPendingDelete = null }
                    ) { Text("Отмена", fontSize = 16.sp, color = Color(0xFF6552A5)) }
                },

                containerColor = Color(0xFFEBE6EE)
            )
        }

        L_SavedCollectionTabContent(
            selectedCollection = selectedCollection,
            collectionList = savedL.collection.collectionList,
            gridState = vm.gridState,
            onCollectionClick = { savedL.collection.setCollection(it) },
            onCollectionLongClick = { itemPendingDelete = it },
            onCreateNewCollectionClick = { savedL.collection.visibleDialogCreateNew = true },
            navigationContent = {
                if (selectedCollection != null) {
                    Navigator(ScreenCollectionName(selectedCollection))
                }
            }
        )
    }
}

@Composable
fun L_SavedCollectionTabContent(
    selectedCollection: String?,
    collectionList: List<LCollectionEntity>,
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
            color = ThemeL.primaryColor,
            fontSize = 18.sp,
            fontFamily = ThemeL.fontFamilyPopinsRegular
        )
    }) { padding ->


        if (selectedCollection == null) {

            LazyVerticalGrid( modifier = Modifier.padding(padding), state = gridState, columns = GridCells.Fixed(2) )
            {
                items(collectionList) { collection ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 8.dp).padding(vertical = 4.dp)
                            .combinedClickable(
                                onClick = { onCollectionClick(collection.collection) },
                                onLongClick = { onCollectionLongClick(collection.collection) }),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (collection.previewUrl != null) {
                            UrlImage(
                                url = collection.previewUrl,
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)).size(72.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)).size(72.dp).background(Color.Gray)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(collection.collection, color = Color.White, fontFamily = ThemeL.fontFamilyDMsanss)
                    }
                }

                items(listOf(Unit)) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp).size(72.dp).clip(RoundedCornerShape(8.dp)).background(ThemeL.primaryColor)
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
    val savedL: SavedL,
) : ScreenModel {

    val gridState = LazyGridState()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLSavedCollection {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenSavedCollectionSM::class)
    abstract fun bindScreenLSavedCollectionScreenModel(hiltListScreenModel: ScreenSavedCollectionSM): ScreenModel
}
